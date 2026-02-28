package org.opentcs.simulation.traffic;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 冲突检测器
 */
@Slf4j
public class ConflictDetector {
    
    private static final double MIN_DISTANCE = 2.0; // 最小安全距离（m）
    private static final long PREDICTION_TIME = 1000; // 预测时间（毫秒）
    
    /**
     * 检测交通冲突
     * @param vehiclePaths 车辆路径映射
     * @return 冲突列表
     */
    public List<Conflict> detectConflicts(Map<String, VehiclePath> vehiclePaths) {
        List<Conflict> conflicts = new ArrayList<>();
        
        // 遍历所有车辆对
        List<VehiclePath> paths = new ArrayList<>(vehiclePaths.values());
        for (int i = 0; i < paths.size(); i++) {
            VehiclePath path1 = paths.get(i);
            if (path1.getState() != org.opentcs.simulation.vehicle.SimulatedVehicle.VehicleState.MOVING) {
                continue;
            }
            
            for (int j = i + 1; j < paths.size(); j++) {
                VehiclePath path2 = paths.get(j);
                if (path2.getState() != org.opentcs.simulation.vehicle.SimulatedVehicle.VehicleState.MOVING) {
                    continue;
                }
                
                // 检测当前冲突
                if (detectCurrentConflict(path1, path2)) {
                    Conflict conflict = new Conflict(path1.getVehicleId(), path2.getVehicleId(), Conflict.ConflictType.CURRENT);
                    conflicts.add(conflict);
                    log.warn("Detected current conflict between vehicle {} and {}", path1.getVehicleId(), path2.getVehicleId());
                }
                
                // 检测预测冲突
                if (detectPredictedConflict(path1, path2)) {
                    Conflict conflict = new Conflict(path1.getVehicleId(), path2.getVehicleId(), Conflict.ConflictType.PREDICTED);
                    conflicts.add(conflict);
                    log.warn("Detected predicted conflict between vehicle {} and {}", path1.getVehicleId(), path2.getVehicleId());
                }
            }
        }
        
        return conflicts;
    }
    
    /**
     * 检测当前冲突
     * @param path1 车辆1的路径
     * @param path2 车辆2的路径
     * @return 是否存在冲突
     */
    private boolean detectCurrentConflict(VehiclePath path1, VehiclePath path2) {
        double distance = path1.distanceTo(path2.getCurrentX(), path2.getCurrentY());
        return distance < MIN_DISTANCE;
    }
    
    /**
     * 检测预测冲突
     * @param path1 车辆1的路径
     * @param path2 车辆2的路径
     * @return 是否存在冲突
     */
    private boolean detectPredictedConflict(VehiclePath path1, VehiclePath path2) {
        long futureTime = System.currentTimeMillis() + PREDICTION_TIME;
        VehiclePath.PathPoint pred1 = path1.predictPosition(futureTime);
        VehiclePath.PathPoint pred2 = path2.predictPosition(futureTime);
        
        double distance = Math.sqrt(Math.pow(pred1.getX() - pred2.getX(), 2) + 
                                   Math.pow(pred1.getY() - pred2.getY(), 2));
        
        return distance < MIN_DISTANCE;
    }
}