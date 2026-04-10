package org.opentcs.kernel.domain.vehicle;

/**
 * 车辆类型规格（值对象，不可变）。
 * <p>
 * 描述某一车辆类型的物理和运动参数。
 * 作为值对象，通过比较所有字段判断相等性。
 * </p>
 */
public final class VehicleTypeSpec {

    /** 车辆长度（mm） */
    private final double length;

    /** 车辆宽度（mm） */
    private final double width;

    /** 车辆高度（mm） */
    private final double height;

    /** 最大前进速度（mm/s） */
    private final double maxVelocity;

    /** 最大后退速度（mm/s） */
    private final double maxReverseVelocity;

    /** 临界能量阈值（%，低于此值需充电） */
    private final double criticalEnergyLevel;

    public VehicleTypeSpec(double length, double width, double height,
                           double maxVelocity, double maxReverseVelocity,
                           double criticalEnergyLevel) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.maxVelocity = maxVelocity;
        this.maxReverseVelocity = maxReverseVelocity;
        this.criticalEnergyLevel = criticalEnergyLevel;
    }

    public static VehicleTypeSpec defaults() {
        return new VehicleTypeSpec(1000, 600, 1200, 1000, 500, 20.0);
    }

    public double getLength() { return length; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getMaxVelocity() { return maxVelocity; }
    public double getMaxReverseVelocity() { return maxReverseVelocity; }
    public double getCriticalEnergyLevel() { return criticalEnergyLevel; }

    /** 判断当前能量是否低于临界阈值 */
    public boolean isCriticalEnergy(double currentEnergyLevel) {
        return currentEnergyLevel <= criticalEnergyLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleTypeSpec)) return false;
        VehicleTypeSpec that = (VehicleTypeSpec) o;
        return Double.compare(length, that.length) == 0
                && Double.compare(width, that.width) == 0
                && Double.compare(height, that.height) == 0
                && Double.compare(maxVelocity, that.maxVelocity) == 0
                && Double.compare(maxReverseVelocity, that.maxReverseVelocity) == 0
                && Double.compare(criticalEnergyLevel, that.criticalEnergyLevel) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(length, width, height, maxVelocity, maxReverseVelocity, criticalEnergyLevel);
    }

    @Override
    public String toString() {
        return "VehicleTypeSpec{" +
                "length=" + length + "mm, width=" + width + "mm, height=" + height + "mm" +
                ", maxVelocity=" + maxVelocity + "mm/s, criticalEnergy=" + criticalEnergyLevel + "%" + '}';
    }
}
