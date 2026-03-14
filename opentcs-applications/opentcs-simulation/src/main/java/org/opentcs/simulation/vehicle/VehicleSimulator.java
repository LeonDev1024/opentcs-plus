package org.opentcs.simulation.vehicle;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.simulation.core.SimulationModule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 车辆模拟器
 */
@Slf4j
@Component
public class VehicleSimulator implements SimulationModule {
    
    private final Map<String, SimulatedVehicle> vehicles = new ConcurrentHashMap<>();
    private boolean initialized = false;
    
    @Override
    public void initialize() {
        if (initialized) {
            return;
        }
        
        log.info("Initializing vehicle simulator...");
        // 初始化车辆模拟器
        initialized = true;
    }
    
    @Override
    public void start() {
        log.info("Starting vehicle simulator...");
        // 启动所有车辆
        for (SimulatedVehicle vehicle : vehicles.values()) {
            vehicle.start();
        }
    }
    
    @Override
    public void stop() {
        log.info("Stopping vehicle simulator...");
        // 停止所有车辆
        for (SimulatedVehicle vehicle : vehicles.values()) {
            vehicle.stop();
        }
        vehicles.clear();
        initialized = false;
    }
    
    @Override
    public void tick(long tick) {
        // 更新所有车辆的状态
        for (SimulatedVehicle vehicle : vehicles.values()) {
            vehicle.update(tick);
        }
    }
    
    @Override
    public String getName() {
        return "Vehicle Simulator";
    }
    
    /**
     * 创建模拟车辆
     * @param vehicleId 车辆ID
     * @param name 车辆名称
     * @param maxSpeed 最大速度
     * @param acceleration 加速度
     * @param deceleration 减速度
     * @param batteryCapacity 电池容量
     * @return 模拟车辆
     */
    public SimulatedVehicle createVehicle(String vehicleId, String name, double maxSpeed, double acceleration, 
                                         double deceleration, double batteryCapacity) {
        SimulatedVehicle vehicle = new SimulatedVehicle(vehicleId, name, maxSpeed, acceleration, 
                                                      deceleration, batteryCapacity);
        vehicles.put(vehicleId, vehicle);
        log.info("Created simulated vehicle: {}", name);
        return vehicle;
    }
    
    /**
     * 获取模拟车辆
     * @param vehicleId 车辆ID
     * @return 模拟车辆
     */
    public SimulatedVehicle getVehicle(String vehicleId) {
        return vehicles.get(vehicleId);
    }
    
    /**
     * 获取所有模拟车辆
     * @return 模拟车辆列表
     */
    public List<SimulatedVehicle> getVehicles() {
        return new ArrayList<>(vehicles.values());
    }
    
    /**
     * 移除模拟车辆
     * @param vehicleId 车辆ID
     * @return 是否移除成功
     */
    public boolean removeVehicle(String vehicleId) {
        SimulatedVehicle vehicle = vehicles.remove(vehicleId);
        if (vehicle != null) {
            vehicle.stop();
            log.info("Removed simulated vehicle: {}", vehicle.getName());
            return true;
        }
        return false;
    }
    
    /**
     * 清空所有模拟车辆
     */
    public void clearVehicles() {
        for (SimulatedVehicle vehicle : vehicles.values()) {
            vehicle.stop();
        }
        vehicles.clear();
        log.info("Cleared all simulated vehicles");
    }
}