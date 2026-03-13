package org.opentcs.kernel.domain.routing;

import java.util.Objects;

/**
 * 点位
 */
public class Point {

    public enum PointType {
        HALT_POSITION("停车点"),
        PARK_POSITION("停放点"),
        REPORT_POSITION("报告点");

        private final String description;

        PointType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final String pointId;
    private final String name;
    private final double x;
    private final double y;
    private final double z;
    private final double orientation;
    private final PointType type;
    private final double radius;
    private boolean locked;
    private boolean blocked;
    private boolean occupied;

    public Point(String pointId, String name, double x, double y) {
        this.pointId = Objects.requireNonNull(pointId, "pointId不能为空");
        this.name = name != null ? name : pointId;
        this.x = x;
        this.y = y;
        this.z = 0;
        this.orientation = 0;
        this.type = PointType.HALT_POSITION;
        this.radius = 0;
    }

    public Point(String pointId, String name, double x, double y, double z) {
        this.pointId = Objects.requireNonNull(pointId, "pointId不能为空");
        this.name = name != null ? name : pointId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.orientation = 0;
        this.type = PointType.HALT_POSITION;
        this.radius = 0;
    }

    /**
     * 检查是否可通行
     */
    public boolean isTraversable() {
        return !locked && !blocked;
    }

    /**
     * 锁定点位
     */
    public void lock() {
        this.locked = true;
    }

    /**
     * 解锁点位
     */
    public void unlock() {
        this.locked = false;
    }

    /**
     * 占用点位
     */
    public void occupy() {
        this.occupied = true;
    }

    /**
     * 释放点位
     */
    public void release() {
        this.occupied = false;
    }

    // Getters
    public String getPointId() {
        return pointId;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getOrientation() {
        return orientation;
    }

    public PointType getType() {
        return type;
    }

    public double getRadius() {
        return radius;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isOccupied() {
        return occupied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Objects.equals(pointId, point.pointId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointId);
    }

    @Override
    public String toString() {
        return "Point{" +
                "pointId='" + pointId + '\'' +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", type=" + type +
                '}';
    }
}
