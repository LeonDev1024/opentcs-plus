package org.opentcs.simulation.traffic;

import lombok.Data;
import org.opentcs.simulation.vehicle.SimulatedVehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * 车辆路径
 */
@Data
public class VehiclePath {
    
    private String vehicleId;
    private double currentX;
    private double currentY;
    private double currentTheta;
    private double targetX;
    private double targetY;
    private SimulatedVehicle.VehicleState state;
    
    private List<PathPoint> pathPoints = new ArrayList<>();
    private static final int MAX_PATH_POINTS = 100; // 最大路径点数量
    
    /**
     * 构造函数
     * @param vehicleId 车辆ID
     */
    public VehiclePath(String vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    /**
     * 更新车辆位置
     * @param x X坐标
     * @param y Y坐标
     * @param theta 方向角
     * @param targetX 目标X坐标
     * @param targetY 目标Y坐标
     * @param state 车辆状态
     */
    public void updatePosition(double x, double y, double theta, double targetX, double targetY, 
                              SimulatedVehicle.VehicleState state) {
        this.currentX = x;
        this.currentY = y;
        this.currentTheta = theta;
        this.targetX = targetX;
        this.targetY = targetY;
        this.state = state;
        
        // 添加路径点
        PathPoint point = new PathPoint(x, y, theta, System.currentTimeMillis());
        pathPoints.add(point);
        
        // 保持路径点数量在限制范围内
        if (pathPoints.size() > MAX_PATH_POINTS) {
            pathPoints.remove(0);
        }
    }
    
    /**
     * 预测车辆未来位置
     * @param time 未来时间（毫秒）
     * @return 预测位置
     */
    public PathPoint predictPosition(long time) {
        if (pathPoints.isEmpty()) {
            return new PathPoint(currentX, currentY, currentTheta, System.currentTimeMillis());
        }
        
        // 简单的线性预测
        // 计算平均速度
        double avgSpeed = 0.0;
        int count = 0;
        for (int i = 1; i < pathPoints.size(); i++) {
            PathPoint prev = pathPoints.get(i - 1);
            PathPoint curr = pathPoints.get(i);
            double distance = Math.sqrt(Math.pow(curr.getX() - prev.getX(), 2) + 
                                       Math.pow(curr.getY() - prev.getY(), 2));
            long timeDiff = curr.getTime() - prev.getTime();
            if (timeDiff > 0) {
                avgSpeed += distance / (timeDiff / 1000.0); // m/s
                count++;
            }
        }
        
        if (count > 0) {
            avgSpeed /= count;
        }
        
        // 计算预测位置
        double direction = currentTheta;
        long currentTime = System.currentTimeMillis();
        double timeDiff = (time - currentTime) / 1000.0; // 秒
        double distance = avgSpeed * timeDiff;
        
        double predictedX = currentX + Math.cos(direction) * distance;
        double predictedY = currentY + Math.sin(direction) * distance;
        
        return new PathPoint(predictedX, predictedY, currentTheta, time);
    }
    
    /**
     * 计算到指定点的距离
     * @param x X坐标
     * @param y Y坐标
     * @return 距离
     */
    public double distanceTo(double x, double y) {
        return Math.sqrt(Math.pow(x - currentX, 2) + Math.pow(y - currentY, 2));
    }
    
    /**
     * 路径点
     */
    @Data
    public static class PathPoint {
        private double x;
        private double y;
        private double theta;
        private long time;
        
        public PathPoint(double x, double y, double theta, long time) {
            this.x = x;
            this.y = y;
            this.theta = theta;
            this.time = time;
        }
    }
}