package org.opentcs.kernel.api.dto;

/**
 * 订单状态枚举
 */
public enum OrderStateDTO {
    RAW("待激活"),
    ACTIVE("执行中"),
    FINISHED("已完成"),
    FAILED("失败"),
    CANCELLED("已取消");

    private final String description;

    OrderStateDTO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
