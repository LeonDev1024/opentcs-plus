package org.opentcs.simulation.order;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.simulation.vehicle.SimulatedVehicle;

/**
 * 模拟订单
 */
@Slf4j
@Data
public class SimulatedOrder {
    
    private String orderId;
    private double startX; // 起点X坐标
    private double startY; // 起点Y坐标
    private double endX; // 终点X坐标
    private double endY; // 终点Y坐标
    private double distance; // 订单距离
    private int timeout; // 超时时间（秒）
    
    // 订单状态
    private OrderState state = OrderState.CREATED;
    private String assignedVehicleId;
    private long createdAt;
    private long assignedTime;
    private long startedTime;
    private long completedTime;
    private long timedOutTime;
    
    // 构造函数
    public SimulatedOrder() {
        this.createdAt = System.currentTimeMillis();
    }
    
    /**
     * 更新订单状态
     * @param tick 当前仿真 tick
     */
    public void update(long tick) {
        switch (state) {
            case CREATED:
                // 检查是否超时
                if (System.currentTimeMillis() - createdAt > timeout * 1000) {
                    state = OrderState.TIMED_OUT;
                    timedOutTime = System.currentTimeMillis();
                    log.warn("Order {} timed out", orderId);
                }
                break;
            case ASSIGNED:
                // 检查车辆是否到达起点
                SimulatedVehicle vehicle = getAssignedVehicle();
                if (vehicle != null && vehicle.getState() == SimulatedVehicle.VehicleState.IDLE) {
                    // 车辆到达起点，开始执行订单
                    state = OrderState.IN_EXECUTION;
                    startedTime = System.currentTimeMillis();
                    // 指挥车辆移动到终点
                    vehicle.moveTo(endX, endY, 0.0);
                    log.info("Order {} started execution", orderId);
                }
                break;
            case IN_EXECUTION:
                // 检查车辆是否到达终点
                SimulatedVehicle executingVehicle = getAssignedVehicle();
                if (executingVehicle != null && executingVehicle.getState() == SimulatedVehicle.VehicleState.IDLE) {
                    // 车辆到达终点，完成订单
                    state = OrderState.COMPLETED;
                    completedTime = System.currentTimeMillis();
                    // 清除车辆的当前订单
                    executingVehicle.setCurrentOrder(null);
                    log.info("Order {} completed", orderId);
                }
                // 检查是否超时
                if (System.currentTimeMillis() - startedTime > timeout * 1000) {
                    state = OrderState.TIMED_OUT;
                    timedOutTime = System.currentTimeMillis();
                    // 清除车辆的当前订单
                    if (executingVehicle != null) {
                        executingVehicle.setCurrentOrder(null);
                    }
                    log.warn("Order {} timed out during execution", orderId);
                }
                break;
            case COMPLETED:
            case TIMED_OUT:
            case CANCELLED:
                // 这些状态不需要更新
                break;
        }
    }
    
    /**
     * 获取分配的车辆
     * @return 分配的车辆
     */
    private SimulatedVehicle getAssignedVehicle() {
        // 这里需要从车辆模拟器中获取车辆，暂时返回null
        // 实际实现中，应该通过车辆ID从VehicleSimulator中获取车辆
        return null;
    }
    
    /**
     * 取消订单
     */
    public void cancel() {
        state = OrderState.CANCELLED;
        log.info("Order {} cancelled", orderId);
        
        // 清除车辆的当前订单
        SimulatedVehicle vehicle = getAssignedVehicle();
        if (vehicle != null) {
            vehicle.setCurrentOrder(null);
        }
    }
    
    /**
     * 订单状态
     */
    public enum OrderState {
        CREATED,        // 已创建
        ASSIGNED,       // 已分配
        IN_EXECUTION,   // 执行中
        COMPLETED,      // 已完成
        TIMED_OUT,      // 已超时
        CANCELLED       // 已取消
    }
}