package org.opentcs.kernel.domain.vehicle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 车辆聚合根
 */
public class Vehicle {

    private final String vehicleId;
    private String name;
    private String vehicleType;
    private VehicleState state;
    private VehiclePosition position;
    private String currentOrderId;
    private double energyLevel;
    private String intendedVehicle;
    private final Map<String, String> properties;
    private final long createTime;
    private long updateTime;

    public Vehicle(String vehicleId) {
        this.vehicleId = Objects.requireNonNull(vehicleId, "vehicleId不能为空");
        this.name = vehicleId;
        this.state = VehicleState.UNKNOWN;
        this.position = VehiclePosition.unknown();
        this.properties = new HashMap<>();
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    // 领域行为

    /**
     * 激活车辆
     */
    public void activate() {
        if (this.state == VehicleState.UNAVAILABLE) {
            throw new IllegalStateException("车辆不可用，无法激活");
        }
        this.state = VehicleState.IDLE;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 使车辆不可用
     */
    public void disable() {
        this.state = VehicleState.UNAVAILABLE;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 更新位置
     */
    public void updatePosition(VehiclePosition position) {
        this.position = position;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 更新状态
     */
    public void updateState(VehicleState newState) {
        this.state = newState;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 分配订单
     */
    public void assignOrder(String orderId) {
        if (!canAcceptOrder()) {
            throw new IllegalStateException("车辆当前状态不能接收订单: " + state);
        }
        this.currentOrderId = orderId;
        this.state = VehicleState.EXECUTING;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 完成订单
     */
    public void completeOrder() {
        this.currentOrderId = null;
        this.state = VehicleState.IDLE;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 取消订单
     */
    public void cancelOrder() {
        this.currentOrderId = null;
        this.state = VehicleState.IDLE;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 更新能量
     */
    public void updateEnergy(double energyLevel) {
        this.energyLevel = Math.max(0, Math.min(100, energyLevel));
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 检查是否可以接收订单
     */
    public boolean canAcceptOrder() {
        return state == VehicleState.IDLE || state == VehicleState.WAITING;
    }

    /**
     * 检查是否有订单在执行
     */
    public boolean hasActiveOrder() {
        return currentOrderId != null && state.isActive();
    }

    // Getters
    public String getVehicleId() {
        return vehicleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public VehicleState getState() {
        return state;
    }

    public VehiclePosition getPosition() {
        return position;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public double getEnergyLevel() {
        return energyLevel;
    }

    public String getIntendedVehicle() {
        return intendedVehicle;
    }

    public void setIntendedVehicle(String intendedVehicle) {
        this.intendedVehicle = intendedVehicle;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(vehicleId, vehicle.vehicleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId='" + vehicleId + '\'' +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", position=" + position +
                ", currentOrderId='" + currentOrderId + '\'' +
                ", energyLevel=" + energyLevel +
                '}';
    }

    /**
     * 车辆构建器
     */
    public static class Builder {
        private final Vehicle vehicle;

        public Builder(String vehicleId) {
            this.vehicle = new Vehicle(vehicleId);
        }

        public Builder name(String name) {
            vehicle.name = name;
            return this;
        }

        public Builder vehicleType(String vehicleType) {
            vehicle.vehicleType = vehicleType;
            return this;
        }

        public Builder state(VehicleState state) {
            vehicle.state = state;
            return this;
        }

        public Builder position(VehiclePosition position) {
            vehicle.position = position;
            return this;
        }

        public Builder energyLevel(double energyLevel) {
            vehicle.energyLevel = energyLevel;
            return this;
        }

        public Builder property(String key, String value) {
            vehicle.properties.put(key, value);
            return this;
        }

        public Vehicle build() {
            return vehicle;
        }
    }
}
