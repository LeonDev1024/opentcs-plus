package org.opentcs.kernel.domain.vehicle;

import java.util.Objects;

/**
 * 车辆位置值对象
 */
public class VehiclePosition {

    private final String pointId;
    private final String mapId;
    private final double x;
    private final double y;
    private final double z;
    private final double orientation;

    public VehiclePosition(String pointId, String mapId, double x, double y, double z, double orientation) {
        this.pointId = pointId;
        this.mapId = mapId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.orientation = orientation;
    }

    public String getPointId() {
        return pointId;
    }

    public String getMapId() {
        return mapId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehiclePosition that = (VehiclePosition) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0 &&
                Double.compare(that.orientation, orientation) == 0 &&
                Objects.equals(pointId, that.pointId) &&
                Objects.equals(mapId, that.mapId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointId, mapId, x, y, z, orientation);
    }

    @Override
    public String toString() {
        return "VehiclePosition{" +
                "pointId='" + pointId + '\'' +
                ", mapId='" + mapId + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", orientation=" + orientation +
                '}';
    }

    /**
     * 创建一个默认位置
     */
    public static VehiclePosition unknown() {
        return new VehiclePosition(null, null, 0, 0, 0, 0);
    }
}
