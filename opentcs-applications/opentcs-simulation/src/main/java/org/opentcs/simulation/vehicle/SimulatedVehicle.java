package org.opentcs.simulation.vehicle;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.simulation.map.SimMapPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 模拟车辆：支持沿地图拓扑路径（多航点）行驶，无地图时退回直线运动
 */
@Slf4j
@Data
public class SimulatedVehicle {

    private String vehicleId;
    private String name;
    private double maxSpeed;       // m/s
    private double acceleration;   // m/s²
    private double deceleration;   // m/s²
    private double batteryCapacity;

    // 车辆状态
    private VehicleState state = VehicleState.IDLE;
    private double currentSpeed = 0.0;
    private double currentBattery = 100.0;
    private double x = 0.0;
    private double y = 0.0;
    private double theta = 0.0;

    // 最终目标（用于 UI 显示和直线模式）
    private double targetX = 0.0;
    private double targetY = 0.0;
    private double targetTheta = 0.0;

    // 航点路径（有地图时使用）
    private List<SimMapPoint> route = new ArrayList<>();
    /** 当前正在驶向的航点下标 */
    private int routeIndex = 0;

    // 运动参数
    private double batteryConsumptionRate = 0.1; // %/s
    private double chargingRate = 2.0;           // %/s
    private double distanceToTarget = 0.0;
    private double angleToTarget = 0.0;

    private Object currentOrder;
    private boolean running = false;

    // ─── 到达判定阈值（m）────────────────────────────────────────
    private static final double ARRIVAL_THRESHOLD = 0.15;

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

    // ─── 生命周期 ────────────────────────────────────────────────

    public void start() {
        running = true;
        state = VehicleState.IDLE;
    }

    public void stop() {
        running = false;
        state = VehicleState.STOPPED;
        currentSpeed = 0.0;
    }

    // ─── Tick 更新 ───────────────────────────────────────────────

    public void update(long tick) {
        if (!running) return;

        switch (state) {
            case MOVING -> move();
            case CHARGING -> charge();
            default -> { /* IDLE / ERROR / STOPPED: do nothing */ }
        }

        if (state == VehicleState.MOVING) {
            currentBattery -= batteryConsumptionRate / 10.0;
            if (currentBattery < 0) {
                currentBattery = 0;
                state = VehicleState.ERROR;
                log.warn("Vehicle {} battery depleted", name);
            }
        }
    }

    // ─── 移动逻辑 ────────────────────────────────────────────────

    private void move() {
        if (!route.isEmpty()) {
            moveAlongRoute();
        } else {
            moveStraight(targetX, targetY, targetTheta, true);
        }
    }

    /**
     * 沿航点路径行驶：每次朝当前航点前进，到达后切换到下一个
     */
    private void moveAlongRoute() {
        if (routeIndex >= route.size()) {
            // 所有航点已走完
            arriveAtFinal();
            return;
        }

        SimMapPoint wp = route.get(routeIndex);
        double wpX = wp.getX();
        double wpY = wp.getY();

        double dx = wpX - x;
        double dy = wpY - y;
        double distToWp = Math.sqrt(dx * dx + dy * dy);

        if (distToWp <= ARRIVAL_THRESHOLD) {
            // 到达当前航点，吸附并推进
            x = wpX;
            y = wpY;
            currentSpeed = 0;
            routeIndex++;
            if (routeIndex >= route.size()) {
                arriveAtFinal();
            }
            return;
        }

        // 更新 distanceToTarget 为到下一个航点的距离（供 UI 显示）
        distanceToTarget = distToWp;

        // 向当前航点前进
        moveStraight(wpX, wpY, Math.atan2(dy, dx), false);
    }

    /**
     * 朝 (tx, ty) 直线行驶
     * @param finalArrival 是否在到达后切换 IDLE
     */
    private void moveStraight(double tx, double ty, double finalTheta, boolean finalArrival) {
        double dx = tx - x;
        double dy = ty - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        angleToTarget = Math.atan2(dy, dx);

        if (finalArrival) {
            distanceToTarget = dist;
        }

        if (dist <= ARRIVAL_THRESHOLD) {
            currentSpeed = 0;
            x = tx;
            y = ty;
            if (finalArrival) {
                theta = finalTheta;
                arriveAtFinal();
            }
            return;
        }

        // 转向
        double deltaTheta = angleToTarget - theta;
        if (deltaTheta > Math.PI)  deltaTheta -= 2 * Math.PI;
        if (deltaTheta < -Math.PI) deltaTheta += 2 * Math.PI;

        if (Math.abs(deltaTheta) > 0.1) {
            double turnRate = 2.0; // rad/s（路径规划后方向已对，可以快速转向）
            theta += Math.signum(deltaTheta) * turnRate / 10.0;
            if (theta > Math.PI)  theta -= 2 * Math.PI;
            if (theta < -Math.PI) theta += 2 * Math.PI;
        } else {
            // 加速/减速
            if (currentSpeed < maxSpeed) {
                currentSpeed = Math.min(currentSpeed + acceleration / 10.0, maxSpeed);
            }
            double decDist = (currentSpeed * currentSpeed) / (2 * deceleration);
            if (dist < decDist) {
                currentSpeed = Math.max(0, currentSpeed - deceleration / 10.0);
            }
            x += Math.cos(theta) * currentSpeed / 10.0;
            y += Math.sin(theta) * currentSpeed / 10.0;
        }
    }

    private void arriveAtFinal() {
        currentSpeed = 0;
        state = VehicleState.IDLE;
        route = new ArrayList<>();
        routeIndex = 0;
        log.info("Vehicle {} reached final destination ({}, {})", name, targetX, targetY);
    }

    // ─── 充电 ────────────────────────────────────────────────────

    private void charge() {
        currentBattery += chargingRate / 10.0;
        if (currentBattery >= batteryCapacity) {
            currentBattery = batteryCapacity;
            state = VehicleState.IDLE;
        }
    }

    // ─── 指令接口 ────────────────────────────────────────────────

    /**
     * 无地图直线移动
     */
    public void moveTo(double x, double y, double theta) {
        this.targetX = x;
        this.targetY = y;
        this.targetTheta = theta;
        this.route = new ArrayList<>();
        this.routeIndex = 0;
        this.state = VehicleState.MOVING;
    }

    /**
     * 按图拓扑路径移动（航点列表）
     * route 包含从当前最近点到目标的完整路径
     */
    public void moveByRoute(List<SimMapPoint> routePoints) {
        if (routePoints == null || routePoints.isEmpty()) {
            return;
        }
        SimMapPoint last = routePoints.get(routePoints.size() - 1);
        this.targetX = last.getX();
        this.targetY = last.getY();
        this.targetTheta = 0.0;
        this.route = new ArrayList<>(routePoints);
        this.routeIndex = 0;
        this.state = VehicleState.MOVING;
        log.info("Vehicle {} route assigned: {} waypoints → ({}, {})",
                name, routePoints.size(), targetX, targetY);
    }

    /**
     * 获取剩余航点（当前 routeIndex 之后，用于前端绘制虚线路径）
     */
    public List<SimMapPoint> getRemainingRoute() {
        if (route.isEmpty() || routeIndex >= route.size()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(route.subList(routeIndex, route.size()));
    }

    public void startCharging() {
        this.state = VehicleState.CHARGING;
    }

    public void setError() {
        this.state = VehicleState.ERROR;
        this.currentSpeed = 0;
    }

    public void clearError() {
        this.state = VehicleState.IDLE;
    }

    // ─── 枚举 ────────────────────────────────────────────────────

    public enum VehicleState {
        IDLE, MOVING, CHARGING, ERROR, STOPPED
    }
}
