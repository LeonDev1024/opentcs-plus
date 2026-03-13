package org.opentcs.kernel.api.dto;

/**
 * 订单步骤状态枚举
 */
public enum StepStateDTO {
    PENDING("待执行"),
    ACTIVE("执行中"),
    COMPLETED("已完成"),
    FAILED("失败");

    private final String description;

    StepStateDTO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
