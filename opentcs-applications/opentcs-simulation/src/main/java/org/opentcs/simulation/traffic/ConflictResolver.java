package org.opentcs.simulation.traffic;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.simulation.vehicle.SimulatedVehicle;
import org.opentcs.simulation.vehicle.VehicleSimulator;

import java.util.List;

/**
 * 冲突解决器
 */
@Slf4j
public class ConflictResolver {
    
    private VehicleSimulator vehicleSimulator;
    
    /**
     * 解决交通冲突
     * @param conflicts 冲突列表
     * @param vehicleSimulator 车辆模拟器
     */
    public void resolveConflicts(List<Conflict> conflicts, VehicleSimulator vehicleSimulator) {
        this.vehicleSimulator = vehicleSimulator;
        
        for (Conflict conflict : conflicts) {
            resolveConflict(conflict);
        }
    }
    
    /**
     * 解决单个冲突
     * @param conflict 冲突
     */
    private void resolveConflict(Conflict conflict) {
        SimulatedVehicle vehicle1 = vehicleSimulator.getVehicle(conflict.getVehicleId1());
        SimulatedVehicle vehicle2 = vehicleSimulator.getVehicle(conflict.getVehicleId2());
        
        if (vehicle1 == null || vehicle2 == null) {
            return;
        }
        
        switch (conflict.getType()) {
            case CURRENT:
                // 处理当前冲突：让两辆车都停止
                handleCurrentConflict(vehicle1, vehicle2);
                break;
            case PREDICTED:
                // 处理预测冲突：让其中一辆车减速或停止
                handlePredictedConflict(vehicle1, vehicle2);
                break;
        }
    }
    
    /**
     * 处理当前冲突
     * @param vehicle1 车辆1
     * @param vehicle2 车辆2
     */
    private void handleCurrentConflict(SimulatedVehicle vehicle1, SimulatedVehicle vehicle2) {
        // MVP: 仅记录冲突日志，不中断车辆运行
        log.debug("Traffic conflict detected between {} and {}, continuing", vehicle1.getName(), vehicle2.getName());
    }

    /**
     * 处理预测冲突
     * @param vehicle1 车辆1
     * @param vehicle2 车辆2
     */
    private void handlePredictedConflict(SimulatedVehicle vehicle1, SimulatedVehicle vehicle2) {
        // MVP: 仅记录冲突日志，不中断车辆运行
        log.debug("Predicted traffic conflict between {} and {}, continuing", vehicle1.getName(), vehicle2.getName());
    }
    
    /**
     * 设置车辆模拟器
     * @param vehicleSimulator 车辆模拟器
     */
    public void setVehicleSimulator(VehicleSimulator vehicleSimulator) {
        this.vehicleSimulator = vehicleSimulator;
    }
}