package org.opentcs.kernel.application;

import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.vehicle.VehicleState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 车辆注册表（内存实现）
 */
public class VehicleRegistry {

    private final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();
    private final Map<String, VehicleState> vehicleStates = new ConcurrentHashMap<>();
    private final Map<String, VehiclePosition> vehiclePositions = new ConcurrentHashMap<>();

    /**
     * 注册车辆
     */
    public Vehicle registerVehicle(Vehicle vehicle) {
        vehicles.put(vehicle.getVehicleId(), vehicle);
        vehicleStates.put(vehicle.getVehicleId(), vehicle.getState());
        vehiclePositions.put(vehicle.getVehicleId(), vehicle.getPosition());
        return vehicle;
    }

    /**
     * 注销车辆
     */
    public void unregisterVehicle(String vehicleId) {
        vehicles.remove(vehicleId);
        vehicleStates.remove(vehicleId);
        vehiclePositions.remove(vehicleId);
    }

    /**
     * 更新车辆状态
     */
    public void updateVehicleState(String vehicleId, VehicleState state) {
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) {
            vehicle.updateState(state);
            vehicleStates.put(vehicleId, state);
        }
    }

    /**
     * 更新车辆位置
     */
    public void updateVehiclePosition(String vehicleId, VehiclePosition position) {
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) {
            vehicle.updatePosition(position);
            vehiclePositions.put(vehicleId, position);
        }
    }

    /**
     * 获取车辆
     */
    public Vehicle getVehicle(String vehicleId) {
        return vehicles.get(vehicleId);
    }

    /**
     * 获取所有车辆
     */
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }

    /**
     * 获取可用车辆（空闲且未绑定订单）
     */
    public List<Vehicle> getAvailableVehicles() {
        return vehicles.values().stream()
                .filter(Vehicle::canAcceptOrder)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有在线车辆ID
     */
    public Set<String> getOnlineVehicleIds() {
        return vehicles.values().stream()
                .filter(v -> v.getState() != VehicleState.OFFLINE)
                .map(Vehicle::getVehicleId)
                .collect(Collectors.toSet());
    }

    /**
     * 检查车辆是否存在
     */
    public boolean vehicleExists(String vehicleId) {
        return vehicles.containsKey(vehicleId);
    }

    /**
     * 检查车辆是否在线
     */
    public boolean isOnline(String vehicleId) {
        Vehicle vehicle = vehicles.get(vehicleId);
        return vehicle != null && vehicle.getState() != VehicleState.OFFLINE;
    }

    /**
     * 获取车辆当前订单
     */
    public String getVehicleCurrentOrder(String vehicleId) {
        Vehicle vehicle = vehicles.get(vehicleId);
        return vehicle != null ? vehicle.getCurrentOrderId() : null;
    }

    /**
     * 分配订单给车辆
     */
    public void assignOrderToVehicle(String vehicleId, String orderId) {
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) {
            vehicle.assignOrder(orderId);
        }
    }

    /**
     * 车辆完成订单
     */
    public void completeVehicleOrder(String vehicleId) {
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) {
            vehicle.completeOrder();
        }
    }

    /**
     * 车辆取消订单
     */
    public void cancelVehicleOrder(String vehicleId) {
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) {
            vehicle.cancelOrder();
        }
    }

    /**
     * 更新车辆能量
     */
    public void updateVehicleEnergy(String vehicleId, double energyLevel) {
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) {
            vehicle.updateEnergy(energyLevel);
        }
    }

    /**
     * 清空所有车辆
     */
    public void clear() {
        vehicles.clear();
        vehicleStates.clear();
        vehiclePositions.clear();
    }

    /**
     * 获取车辆数量
     */
    public int size() {
        return vehicles.size();
    }
}
