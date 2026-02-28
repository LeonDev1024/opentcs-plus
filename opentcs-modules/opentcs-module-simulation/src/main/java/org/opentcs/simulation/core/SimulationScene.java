package org.opentcs.simulation.core;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 仿真场景
 */
@Data
public class SimulationScene implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private long creationTime;
    private long lastModifiedTime;
    
    // 场景配置
    private int vehicleCount = 10; // 车辆数量
    private int orderCount = 20; // 订单数量
    private int simulationDuration = 3600; // 仿真持续时间（秒）
    private int tickRate = 10; // 仿真速率（ticks/秒）
    
    // 车辆配置
    private double vehicleMaxSpeed = 1.0; // 车辆最大速度（m/s）
    private double vehicleAcceleration = 0.5; // 车辆加速度（m/s²）
    private double vehicleDeceleration = 0.5; // 车辆减速度（m/s²）
    private double vehicleBatteryCapacity = 100.0; // 车辆电池容量（%）
    private double vehicleBatteryConsumptionRate = 0.1; // 车辆电池消耗率（%/s）
    private double vehicleChargingRate = 2.0; // 车辆充电率（%/s）
    
    // 订单配置
    private double orderCreationRate = 0.1; // 订单创建率（订单/秒）
    private int orderMaxDistance = 100; // 订单最大距离（m）
    private int orderMinDistance = 10; // 订单最小距离（m）
    
    // 地图配置
    private String mapId;
    
    // 场景状态
    private transient boolean isRunning;
    private transient long currentTick;
    private transient long startTime;
    
    // 构造函数
    public SimulationScene() {
        this.creationTime = System.currentTimeMillis();
        this.lastModifiedTime = System.currentTimeMillis();
    }
    
    /**
     * 更新最后修改时间
     */
    public void updateLastModifiedTime() {
        this.lastModifiedTime = System.currentTimeMillis();
    }
}