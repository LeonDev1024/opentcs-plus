package org.opentcs.kernel.domain.vehicle;

/**
 * 车辆状态枚举
 */
public enum VehicleState {
    UNKNOWN("未知状态"),
    UNAVAILABLE("不可用"),
    IDLE("空闲"),
    CHARGING("充电中"),
    EXECUTING("执行订单中"),
    PAUSED("订单暂停"),
    WAITING("等待中"),
    ERROR("错误"),
    OFFLINE("离线");

    private final String description;

    VehicleState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 是否可以接收新订单
     */
    public boolean canAcceptOrder() {
        return this == IDLE || this == WAITING;
    }

    /**
     * 是否处于活跃状态
     */
    public boolean isActive() {
        return this == EXECUTING || this == PAUSED || this == WAITING;
    }
}
