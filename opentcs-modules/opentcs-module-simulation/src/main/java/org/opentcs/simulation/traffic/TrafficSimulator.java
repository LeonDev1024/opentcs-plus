package org.opentcs.simulation.traffic;

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
 * 交通模拟器
 */
@Slf4j
@Component
public class TrafficSimulator implements SimulationModule {
    
    private final Map<String, VehiclePath> vehiclePaths = new ConcurrentHashMap<>();
    private final ConflictDetector conflictDetector = new ConflictDetector();
    private final ConflictResolver conflictResolver = new ConflictResolver();
    private VehicleSimulator vehicleSimulator;
    private boolean initialized = false;
    
    @Override
    public void initialize() {
        if (initialized) {
            return;
        }
        
        log.info("Initializing traffic simulator...");
        // 初始化交通模拟器
        initialized = true;
    }
    
    @Override
    public void start() {
        log.info("Starting traffic simulator...");
        // 启动交通模拟器
    }
    
    @Override
    public void stop() {
        log.info("Stopping traffic simulator...");
        // 停止交通模拟器
        vehiclePaths.clear();
        initialized = false;
    }
    
    @Override
    public void tick(long tick) {
        if (vehicleSimulator == null) {
            return;
        }
        
        // 更新车辆路径
        updateVehiclePaths();
        
        // 检测交通冲突
        List<Conflict> conflicts = conflictDetector.detectConflicts(vehiclePaths);
        
        // 解决交通冲突
        conflictResolver.resolveConflicts(conflicts, vehicleSimulator);
    }
    
    @Override
    public String getName() {
        return "Traffic Simulator";
    }
    
    /**
     * 更新车辆路径
     */
    private void updateVehiclePaths() {
        for (SimulatedVehicle vehicle : vehicleSimulator.getVehicles()) {
            VehiclePath path = vehiclePaths.get(vehicle.getVehicleId());
            if (path == null) {
                path = new VehiclePath(vehicle.getVehicleId());
                vehiclePaths.put(vehicle.getVehicleId(), path);
            }
            
            // 更新车辆位置
            path.updatePosition(vehicle.getX(), vehicle.getY(), vehicle.getTheta(), 
                              vehicle.getTargetX(), vehicle.getTargetY(), vehicle.getState());
        }
    }
    
    /**
     * 设置车辆模拟器
     * @param vehicleSimulator 车辆模拟器
     */
    public void setVehicleSimulator(VehicleSimulator vehicleSimulator) {
        this.vehicleSimulator = vehicleSimulator;
        conflictResolver.setVehicleSimulator(vehicleSimulator);
    }
    
    /**
     * 获取所有车辆路径
     * @return 车辆路径映射
     */
    public Map<String, VehiclePath> getVehiclePaths() {
        return vehiclePaths;
    }
    
    /**
     * 获取车辆路径
     * @param vehicleId 车辆ID
     * @return 车辆路径
     */
    public VehiclePath getVehiclePath(String vehicleId) {
        return vehiclePaths.get(vehicleId);
    }
}