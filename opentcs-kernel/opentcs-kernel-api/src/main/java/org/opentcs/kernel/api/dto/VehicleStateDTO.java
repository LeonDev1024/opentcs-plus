package org.opentcs.kernel.api.dto;

/**
 * 车辆状态枚举
 */
public enum VehicleStateDTO {
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

    VehicleStateDTO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
