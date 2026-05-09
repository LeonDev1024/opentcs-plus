package org.opentcs.simulation.task;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.simulation.core.SimulationEngine;
import org.opentcs.simulation.order.OrderSimulator;
import org.opentcs.simulation.order.SimulatedOrder;
import org.opentcs.simulation.traffic.TrafficSimulator;
import org.opentcs.simulation.vehicle.VehicleSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 仿真定时任务：每隔固定周期自动启动一轮仿真测试（地图 + 车辆 + 订单），
 * 运行指定时长后统计结果并记录日志。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "simulation.schedule.enabled", havingValue = "true", matchIfMissing = false)
public class SimulationScheduledTask {

    /** 每轮仿真运行时长（秒），默认 270s，留 30s 给下一轮准备 */
    @Value("${simulation.schedule.run-duration-seconds:270}")
    private int runDurationSeconds;

    /** 每轮添加的测试车辆数 */
    @Value("${simulation.schedule.vehicle-count:2}")
    private int vehicleCount;

    /** 车辆最大速度（m/s） */
    @Value("${simulation.schedule.vehicle-max-speed:2.0}")
    private double vehicleMaxSpeed;

    @Autowired
    private SimulationEngine simulationEngine;

    @Autowired
    private VehicleSimulator vehicleSimulator;

    @Autowired
    private OrderSimulator orderSimulator;

    @Autowired
    private TrafficSimulator trafficSimulator;

    /** 累计完成订单总数（跨轮次） */
    private final AtomicLong totalCompleted = new AtomicLong(0);

    /** 已执行轮次 */
    private final AtomicLong cycleCount = new AtomicLong(0);

    /**
     * 每 5 分钟自动执行一轮仿真测试。
     * fixedDelay 保证上一轮完全结束后再计时，避免并发执行。
     */
    @Scheduled(fixedDelayString = "${simulation.schedule.interval-ms:300000}",
               initialDelayString = "${simulation.schedule.initial-delay-ms:60000}")
    public void runCycle() {
        long cycle = cycleCount.incrementAndGet();
        log.info("===== 仿真定时任务 第 {} 轮 开始 =====", cycle);

        try {
            // 1. 停止上一轮（如果还在跑）
            if (simulationEngine.getStatus() != SimulationEngine.SimulationStatus.STOPPED) {
                simulationEngine.stop();
                vehicleSimulator.stop();
                orderSimulator.stop();
                trafficSimulator.stop();
                Thread.sleep(500);
            }

            // 2. 初始化 & 注册模块
            vehicleSimulator.initialize();
            orderSimulator.initialize();
            trafficSimulator.initialize();

            orderSimulator.setVehicleSimulator(vehicleSimulator);
            trafficSimulator.setVehicleSimulator(vehicleSimulator);

            simulationEngine.clearModules();
            simulationEngine.addModule(vehicleSimulator);
            simulationEngine.addModule(orderSimulator);
            simulationEngine.addModule(trafficSimulator);

            simulationEngine.start();

            // 3. 添加测试车辆
            for (int i = 1; i <= vehicleCount; i++) {
                vehicleSimulator.createVehicle(
                        "scheduled-v" + i,
                        "TestVehicle-" + i,
                        vehicleMaxSpeed, 0.5, 0.5, 100.0
                );
            }
            log.info("已添加 {} 辆测试车辆，仿真运行中（{}s）...", vehicleCount, runDurationSeconds);

            // 4. 运行指定时长
            Thread.sleep(runDurationSeconds * 1000L);

            // 5. 统计本轮结果
            List<SimulatedOrder> orders = orderSimulator.getOrders();
            Map<SimulatedOrder.OrderState, Long> stats = orders.stream()
                    .collect(Collectors.groupingBy(SimulatedOrder::getState, Collectors.counting()));

            long completed = stats.getOrDefault(SimulatedOrder.OrderState.COMPLETED, 0L);
            long timedOut  = stats.getOrDefault(SimulatedOrder.OrderState.TIMED_OUT, 0L);
            long total     = orders.size();

            totalCompleted.addAndGet(completed);

            log.info("===== 仿真定时任务 第 {} 轮 完成 | 本轮：总={} 完成={} 超时={} | 累计完成={} =====",
                    cycle, total, completed, timedOut, totalCompleted.get());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("仿真定时任务第 {} 轮被中断", cycle);
        } catch (Exception e) {
            log.error("仿真定时任务第 {} 轮执行异常: {}", cycle, e.getMessage(), e);
        }
    }
}
