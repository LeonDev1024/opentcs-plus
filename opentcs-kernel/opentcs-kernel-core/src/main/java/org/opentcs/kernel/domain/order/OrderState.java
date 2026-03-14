package org.opentcs.kernel.domain.order;

/**
 * 订单状态枚举
 */
public enum OrderState {
    RAW("待激活"),
    ACTIVE("执行中"),
    FINISHED("已完成"),
    FAILED("失败"),
    CANCELLED("已取消");

    private final String description;

    OrderState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 是否可以激活
     */
    public boolean canActivate() {
        return this == RAW;
    }

    /**
     * 是否可以取消
     */
    public boolean canCancel() {
        return this == RAW || this == ACTIVE;
    }

    /**
     * 是否处于终态
     */
    public boolean isFinal() {
        return this == FINISHED || this == FAILED || this == CANCELLED;
    }
}
