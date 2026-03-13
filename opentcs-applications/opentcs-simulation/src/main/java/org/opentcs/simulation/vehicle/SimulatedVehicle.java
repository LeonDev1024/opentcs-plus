package org.opentcs.simulation.vehicle;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 模拟车辆
 */
@Slf4j
@Data
public class SimulatedVehicle {
    
    private String vehicleId;
    private String name;
    private double maxSpeed; // 最大速度（m/s）
    private double acceleration; // 加速度（m/s²）
    private double deceleration; // 减速度（m/s²）
    private double batteryCapacity; // 电池容量（%）
    
    // 车辆状态
    private VehicleState state = VehicleState.IDLE;
    private double currentSpeed = 0.0; // 当前速度（m/s）
    private double currentBattery = 100.0; // 当前电池电量（%）
    private double x = 0.0; // 当前X坐标（m）
    private double y = 0.0; // 当前Y坐标（m）
    private double theta = 0.0; // 当前方向角（rad）
    
    // 目标状态
    private double targetX = 0.0;
    private double targetY = 0.0;
    private double targetTheta = 0.0;
    
    // 运动参数
    private double batteryConsumptionRate = 0.1; // 电池消耗率（%/s）
    private double chargingRate = 2.0; // 充电率（%/s）
    private double distanceToTarget = 0.0;
    private double angleToTarget = 0.0;
    
    // 当前订单
    private Object currentOrder;
    
    private boolean running = false;
    
    /**
     * 构造函数
     */
    public SimulatedVehicle(String vehicleId, String name, double maxSpeed, double acceleration, 
                          double deceleration, double batteryCapacity) {
        this.vehicleId = vehicleId;
        this.name = name;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
        this.batteryCapacity = batteryCapacity;
        this.currentBattery = batteryCapacity;
    }
    
    /**
     * 启动车辆
     */
    public void start() {
        running = true;
        state = VehicleState.IDLE;
        log.info("Started simulated vehicle: {}", name);
    }
    
    /**
     * 停止车辆
     */
    public void stop() {
        running = false;
        state = VehicleState.STOPPED;
        currentSpeed = 0.0;
        log.info("Stopped simulated vehicle: {}", name);
    }
    
    /**
     * 更新车辆状态
     * @param tick 当前仿真 tick
     */
    public void update(long tick) {
        if (!running) {
            return;
        }
        
        switch (state) {
            case IDLE:
                // 空闲状态，不做任何操作
                break;
            case MOVING:
                move();
                break;
            case CHARGING:
                charge();
                break;
            case ERROR:
                // 错误状态，不做任何操作
                break;
            case STOPPED:
                // 停止状态，不做任何操作
                break;
        }
        
        // 更新电池状态
        if (state == VehicleState.MOVING) {
            currentBattery -= batteryConsumptionRate / 10.0; // 假设每秒10个tick
            if (currentBattery < 0) {
                currentBattery = 0;
                state = VehicleState.ERROR;
                log.warn("Vehicle {} battery depleted", name);
            }
        }
    }
    
    /**
     * 移动车辆
     */
    private void move() {
        // 计算到目标点的距离和角度
        double dx = targetX - x;
        double dy = targetY - y;
        distanceToTarget = Math.sqrt(dx * dx + dy * dy);
        angleToTarget = Math.atan2(dy, dx);
        
        // 计算转向角度
        double deltaTheta = angleToTarget - theta;
        // 调整到最小角度
        if (deltaTheta > Math.PI) {
            deltaTheta -= 2 * Math.PI;
        } else if (deltaTheta < -Math.PI) {
            deltaTheta += 2 * Math.PI;
        }
        
        // 转向
        if (Math.abs(deltaTheta) > 0.1) {
            // 以固定角速度转向
            double turnRate = 0.5; // rad/s
            theta += Math.signum(deltaTheta) * turnRate / 10.0; // 假设每秒10个tick
            // 确保theta在[-pi, pi]范围内
            if (theta > Math.PI) {
                theta -= 2 * Math.PI;
            } else if (theta < -Math.PI) {
                theta += 2 * Math.PI;
            }
        } else {
            // 已经朝向目标，可以前进
            if (distanceToTarget > 0.1) {
                // 加速到最大速度
                if (currentSpeed < maxSpeed) {
                    currentSpeed += acceleration / 10.0;
                    if (currentSpeed > maxSpeed) {
                        currentSpeed = maxSpeed;
                    }
                }
                
                // 计算减速距离
                double decelerationDistance = (currentSpeed * currentSpeed) / (2 * deceleration);
                
                // 如果距离目标很近，开始减速
                if (distanceToTarget < decelerationDistance) {
                    currentSpeed -= deceleration / 10.0;
                    if (currentSpeed < 0) {
                        currentSpeed = 0;
                    }
                }
                
                // 前进
                x += Math.cos(theta) * currentSpeed / 10.0;
                y += Math.sin(theta) * currentSpeed / 10.0;
            } else {
                // 到达目标
                currentSpeed = 0;
                x = targetX;
                y = targetY;
                theta = targetTheta;
                state = VehicleState.IDLE;
                log.info("Vehicle {} reached target position", name);
            }
        }
    }
    
    /**
     * 充电
     */
    private void charge() {
        currentBattery += chargingRate / 10.0; // 假设每秒10个tick
        if (currentBattery >= batteryCapacity) {
            currentBattery = batteryCapacity;
            state = VehicleState.IDLE;
            log.info("Vehicle {} fully charged", name);
        }
    }
    
    /**
     * 移动到指定位置
     * @param x X坐标
     * @param y Y坐标
     * @param theta 方向角
     */
    public void moveTo(double x, double y, double theta) {
        this.targetX = x;
        this.targetY = y;
        this.targetTheta = theta;
        this.state = VehicleState.MOVING;
        log.info("Vehicle {} moving to position ({}, {}) with angle {}", name, x, y, theta);
    }
    
    /**
     * 开始充电
     */
    public void startCharging() {
        this.state = VehicleState.CHARGING;
        log.info("Vehicle {} starting to charge", name);
    }
    
    /**
     * 设置错误状态
     */
    public void setError() {
        this.state = VehicleState.ERROR;
        this.currentSpeed = 0;
        log.warn("Vehicle {} entered error state", name);
    }
    
    /**
     * 清除错误状态
     */
    public void clearError() {
        this.state = VehicleState.IDLE;
        log.info("Vehicle {} error cleared", name);
    }
    
    /**
     * 车辆状态
     */
    public enum VehicleState {
        IDLE,        // 空闲
        MOVING,      // 移动中
        CHARGING,    // 充电中
        ERROR,       // 错误
        STOPPED      // 停止
    }
}