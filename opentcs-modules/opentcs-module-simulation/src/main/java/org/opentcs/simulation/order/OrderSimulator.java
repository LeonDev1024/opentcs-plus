package org.opentcs.simulation.order;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.simulation.core.SimulationModule;
import org.opentcs.simulation.vehicle.SimulatedVehicle;
import org.opentcs.simulation.vehicle.VehicleSimulator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单模拟器
 */
@Slf4j
@Component
public class OrderSimulator implements SimulationModule {
    
    private final Map<String, SimulatedOrder> orders = new ConcurrentHashMap<>();
    private final OrderGenerator orderGenerator = new OrderGenerator();
    private final OrderAllocator orderAllocator = new OrderAllocator();
    private VehicleSimulator vehicleSimulator;
    private boolean initialized = false;
    
    // 配置参数
    private double orderCreationRate = 0.1; // 订单创建率（订单/秒）
    private int orderMaxDistance = 100; // 订单最大距离（m）
    private int orderMinDistance = 10; // 订单最小距离（m）
    
    @Override
    public void initialize() {
        if (initialized) {
            return;
        }
        
        log.info("Initializing order simulator...");
        // 初始化订单模拟器
        initialized = true;
    }
    
    @Override
    public void start() {
        log.info("Starting order simulator...");
        // 启动订单模拟器
    }
    
    @Override
    public void stop() {
        log.info("Stopping order simulator...");
        // 停止订单模拟器
        orders.clear();
        initialized = false;
    }
    
    @Override
    public void tick(long tick) {
        // 生成新订单
        generateOrders(tick);
        
        // 分配订单
        allocateOrders();
        
        // 更新订单状态
        updateOrders(tick);
    }
    
    @Override
    public String getName() {
        return "Order Simulator";
    }
    
    /**
     * 生成新订单
     * @param tick 当前仿真 tick
     */
    private void generateOrders(long tick) {
        // 根据订单创建率生成新订单
        if (Math.random() < orderCreationRate / 10.0) { // 假设每秒10个tick
            SimulatedOrder order = orderGenerator.generateOrder();
            orders.put(order.getOrderId(), order);
            log.info("Generated new order: {}", order.getOrderId());
        }
    }
    
    /**
     * 分配订单
     */
    private void allocateOrders() {
        if (vehicleSimulator == null) {
            return;
        }
        
        // 获取待分配的订单
        List<SimulatedOrder> unallocatedOrders = new ArrayList<>();
        for (SimulatedOrder order : orders.values()) {
            if (order.getState() == SimulatedOrder.OrderState.CREATED) {
                unallocatedOrders.add(order);
            }
        }
        
        // 获取空闲车辆
        List<SimulatedVehicle> idleVehicles = new ArrayList<>();
        for (SimulatedVehicle vehicle : vehicleSimulator.getVehicles()) {
            if (vehicle.getState() == SimulatedVehicle.VehicleState.IDLE) {
                idleVehicles.add(vehicle);
            }
        }
        
        // 分配订单
        orderAllocator.allocateOrders(unallocatedOrders, idleVehicles, orders);
    }
    
    /**
     * 更新订单状态
     * @param tick 当前仿真 tick
     */
    private void updateOrders(long tick) {
        for (SimulatedOrder order : orders.values()) {
            order.update(tick);
            
            // 检查订单是否完成
            if (order.getState() == SimulatedOrder.OrderState.COMPLETED) {
                log.info("Order {} completed", order.getOrderId());
                // 可以在这里添加订单完成后的处理逻辑
            }
            
            // 检查订单是否超时
            if (order.getState() == SimulatedOrder.OrderState.TIMED_OUT) {
                log.warn("Order {} timed out", order.getOrderId());
                // 可以在这里添加订单超时后的处理逻辑
            }
        }
    }
    
    /**
     * 设置车辆模拟器
     * @param vehicleSimulator 车辆模拟器
     */
    public void setVehicleSimulator(VehicleSimulator vehicleSimulator) {
        this.vehicleSimulator = vehicleSimulator;
        orderAllocator.setVehicleSimulator(vehicleSimulator);
    }
    
    /**
     * 获取所有订单
     * @return 订单列表
     */
    public List<SimulatedOrder> getOrders() {
        return new ArrayList<>(orders.values());
    }
    
    /**
     * 获取指定状态的订单
     * @param state 订单状态
     * @return 订单列表
     */
    public List<SimulatedOrder> getOrdersByState(SimulatedOrder.OrderState state) {
        List<SimulatedOrder> result = new ArrayList<>();
        for (SimulatedOrder order : orders.values()) {
            if (order.getState() == state) {
                result.add(order);
            }
        }
        return result;
    }
    
    /**
     * 设置订单创建率
     * @param orderCreationRate 订单创建率
     */
    public void setOrderCreationRate(double orderCreationRate) {
        this.orderCreationRate = orderCreationRate;
        orderGenerator.setOrderCreationRate(orderCreationRate);
    }
    
    /**
     * 设置订单最大距离
     * @param orderMaxDistance 订单最大距离
     */
    public void setOrderMaxDistance(int orderMaxDistance) {
        this.orderMaxDistance = orderMaxDistance;
        orderGenerator.setOrderMaxDistance(orderMaxDistance);
    }
    
    /**
     * 设置订单最小距离
     * @param orderMinDistance 订单最小距离
     */
    public void setOrderMinDistance(int orderMinDistance) {
        this.orderMinDistance = orderMinDistance;
        orderGenerator.setOrderMinDistance(orderMinDistance);
    }
}