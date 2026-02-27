package org.opentcs.simulation.traffic;

import lombok.Data;

/**
 * 交通冲突
 */
@Data
public class Conflict {
    
    private String vehicleId1;
    private String vehicleId2;
    private ConflictType type;
    private long detectedTime;
    
    /**
     * 构造函数
     * @param vehicleId1 车辆1的ID
     * @param vehicleId2 车辆2的ID
     * @param type 冲突类型
     */
    public Conflict(String vehicleId1, String vehicleId2, ConflictType type) {
        this.vehicleId1 = vehicleId1;
        this.vehicleId2 = vehicleId2;
        this.type = type;
        this.detectedTime = System.currentTimeMillis();
    }
    
    /**
     * 冲突类型
     */
    public enum ConflictType {
        CURRENT,    // 当前冲突
        PREDICTED   // 预测冲突
    }
}