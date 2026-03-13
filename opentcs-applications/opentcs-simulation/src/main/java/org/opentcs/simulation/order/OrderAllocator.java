package org.opentcs.simulation.order;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.simulation.vehicle.SimulatedVehicle;
import org.opentcs.simulation.vehicle.VehicleSimulator;

import java.util.List;
import java.util.Map;

/**
 * 订单分配器
 */
@Slf4j
public class OrderAllocator {
    
    private VehicleSimulator vehicleSimulator;
    
    /**
     * 分配订单
     * @param orders 待分配的订单列表
     * @param vehicles 空闲车辆列表
     * @param allOrders 所有订单映射
     */
    public void allocateOrders(List<SimulatedOrder> orders, List<SimulatedVehicle> vehicles, 
                             Map<String, SimulatedOrder> allOrders) {
        if (orders.isEmpty() || vehicles.isEmpty()) {
            return;
        }
        
        // 简单的贪心算法：为每个订单选择距离起点最近的空闲车辆
        for (SimulatedOrder order : orders) {
            if (order.getState() != SimulatedOrder.OrderState.CREATED) {
                continue;
            }
            
            SimulatedVehicle bestVehicle = null;
            double minDistance = Double.MAX_VALUE;
            
            // 找到距离订单起点最近的空闲车辆
            for (SimulatedVehicle vehicle : vehicles) {
                if (vehicle.getState() != SimulatedVehicle.VehicleState.IDLE) {
                    continue;
                }
                
                double distance = calculateDistance(vehicle.getX(), vehicle.getY(), order.getStartX(), order.getStartY());
                if (distance < minDistance) {
                    minDistance = distance;
                    bestVehicle = vehicle;
                }
            }
            
            // 如果找到合适的车辆，分配订单
            if (bestVehicle != null) {
                assignOrderToVehicle(order, bestVehicle);
                vehicles.remove(bestVehicle); // 从空闲车辆列表中移除
                log.info("Assigned order {} to vehicle {}", order.getOrderId(), bestVehicle.getName());
            }
        }
    }
    
    /**
     * 计算两点之间的距离
     * @param x1 第一个点的X坐标
     * @param y1 第一个点的Y坐标
     * @param x2 第二个点的X坐标
     * @param y2 第二个点的Y坐标
     * @return 两点之间的距离
     */
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * 将订单分配给车辆
     * @param order 订单
     * @param vehicle 车辆
     */
    private void assignOrderToVehicle(SimulatedOrder order, SimulatedVehicle vehicle) {
        // 更新订单状态
        order.setState(SimulatedOrder.OrderState.ASSIGNED);
        order.setAssignedVehicleId(vehicle.getVehicleId());
        order.setAssignedTime(System.currentTimeMillis());
        
        // 指挥车辆移动到订单起点
        vehicle.moveTo(order.getStartX(), order.getStartY(), 0.0);
        
        // 设置车辆的当前订单
        vehicle.setCurrentOrder(order);
    }
    
    /**
     * 设置车辆模拟器
     * @param vehicleSimulator 车辆模拟器
     */
    public void setVehicleSimulator(VehicleSimulator vehicleSimulator) {
        this.vehicleSimulator = vehicleSimulator;
    }
}