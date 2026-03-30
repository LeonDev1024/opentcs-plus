package org.opentcs.kernel.domain.order;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 订单步骤
 */
@Getter
public class OrderStep {

    // Getters
    private final int stepIndex;
    private final String sourcePointId;
    private final String destinationPointId;
    private final String destinationName;
    private final String pathId;
    private final List<OrderAction> actions;
    private StepState state;
    private final long createTime;
    private long updateTime;

    public OrderStep(int stepIndex, String destinationPointId, String destinationName) {
        this.stepIndex = stepIndex;
        this.destinationPointId = Objects.requireNonNull(destinationPointId, "destinationPointId不能为空");
        this.destinationName = destinationName;
        this.sourcePointId = null;
        this.pathId = null;
        this.actions = new ArrayList<>();
        this.state = StepState.PENDING;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    /**
     * 带路径信息的构造函数
     */
    public OrderStep(String sourcePointId, String destPointId, String pathId) {
        this.stepIndex = 0;
        this.sourcePointId = sourcePointId;
        this.destinationPointId = destPointId;
        this.destinationName = destPointId;
        this.pathId = pathId;
        this.actions = new ArrayList<>();
        this.state = StepState.PENDING;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    /**
     * 添加动作
     */
    public void addAction(OrderAction action) {
        this.actions.add(action);
    }

    /**
     * 开始执行
     */
    public void start() {
        if (this.state != StepState.PENDING) {
            throw new IllegalStateException("只有 PENDING 状态才能开始");
        }
        this.state = StepState.ACTIVE;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 完成步骤
     */
    public void complete() {
        if (this.state != StepState.ACTIVE) {
            throw new IllegalStateException("只有 ACTIVE 状态才能完成");
        }
        this.state = StepState.COMPLETED;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 失败
     */
    public void fail() {
        this.state = StepState.FAILED;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 检查是否完成
     */
    public boolean isCompleted() {
        return state == StepState.COMPLETED;
    }

    /**
     * 检查是否失败
     */
    public boolean isFailed() {
        return state == StepState.FAILED;
    }

    /**
     * 获取终点ID（别名）
     */
    public String getDestPointId() {
        return destinationPointId;
    }

    @Override
    public String toString() {
        return "OrderStep{" +
                "stepIndex=" + stepIndex +
                ", destinationPointId='" + destinationPointId + '\'' +
                ", state=" + state +
                '}';
    }

    /**
     * 步骤状态枚举
     */
    @Getter
    public enum StepState {
        PENDING("待执行"),
        ACTIVE("执行中"),
        COMPLETED("已完成"),
        FAILED("失败");

        private final String description;

        StepState(String description) {
            this.description = description;
        }

    }
}
