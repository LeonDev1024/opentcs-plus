package org.opentcs.kernel.domain.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.opentcs.kernel.domain.routing.Path;

/**
 * 运输订单聚合根
 */
public class TransportOrder {

    private final String orderId;
    private String name;
    private String orderNo;
    private OrderState state;
    private String intendedVehicle;
    private String processingVehicle;
    private final List<OrderStep> steps;
    private int currentStepIndex;
    private final long creationTime;
    private long finishedTime;
    private Long deadline;
    private final java.util.Map<String, String> properties;
    // 订单起点和终点
    private String sourcePointId;
    private String destPointId;
    // 完整路径信息
    private final List<Path> route;

    public TransportOrder(String name) {
        this.orderId = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name, "name不能为空");
        this.orderNo = generateOrderNo();
        this.state = OrderState.RAW;
        this.steps = new ArrayList<>();
        this.currentStepIndex = 0;
        this.creationTime = System.currentTimeMillis();
        this.properties = new java.util.HashMap<>();
        this.route = new ArrayList<>();
    }

    public TransportOrder(String orderId, String name) {
        this.orderId = Objects.requireNonNull(orderId, "orderId不能为空");
        this.name = Objects.requireNonNull(name, "name不能为空");
        this.orderNo = generateOrderNo();
        this.state = OrderState.RAW;
        this.steps = new ArrayList<>();
        this.currentStepIndex = 0;
        this.creationTime = System.currentTimeMillis();
        this.properties = new java.util.HashMap<>();
        this.route = new ArrayList<>();
    }

    /**
     * 带路径信息的构造函数
     */
    public TransportOrder(String orderId, String name, String sourcePointId,
                        String destPointId, List<Path> route) {
        this.orderId = Objects.requireNonNull(orderId, "orderId不能为空");
        this.name = Objects.requireNonNull(name, "name不能为空");
        this.orderNo = generateOrderNo();
        this.state = OrderState.RAW;
        this.sourcePointId = sourcePointId;
        this.destPointId = destPointId;
        this.steps = new ArrayList<>();
        this.currentStepIndex = 0;
        this.creationTime = System.currentTimeMillis();
        this.properties = new java.util.HashMap<>();
        this.route = route != null ? new ArrayList<>(route) : new ArrayList<>();

        // 从路径生成步骤
        if (!this.route.isEmpty()) {
            generateStepsFromRoute();
        }
    }

    /**
     * 从路径生成步骤
     */
    private void generateStepsFromRoute() {
        for (Path path : route) {
            OrderStep step = new OrderStep(
                    path.getSourcePointId(),
                    path.getDestPointId(),
                    path.getPathId()
            );
            steps.add(step);
        }
    }

    /**
     * 释放订单（车辆完成或取消后）
     */
    public void release() {
        this.processingVehicle = null;
        // 如果订单还未完成，可以重新设置为RAW状态等待再次分配
        if (this.state == OrderState.ACTIVE) {
            this.state = OrderState.RAW;
        }
    }

    // 领域行为

    /**
     * 添加步骤
     */
    public void addStep(OrderStep step) {
        if (this.state != OrderState.RAW) {
            throw new IllegalStateException("只有 RAW 状态才能添加步骤");
        }
        this.steps.add(step);
    }

    /**
     * 激活订单
     */
    public void activate() {
        if (!state.canActivate()) {
            throw new IllegalStateException("当前状态不能激活: " + state);
        }
        if (steps.isEmpty()) {
            throw new IllegalStateException("订单没有步骤，无法激活");
        }
        this.state = OrderState.ACTIVE;
    }

    /**
     * 分配车辆
     */
    public void assignTo(String vehicleId) {
        if (this.state != OrderState.ACTIVE) {
            throw new IllegalStateException("只有 ACTIVE 状态才能分配车辆");
        }
        this.processingVehicle = vehicleId;
    }

    /**
     * 完成当前步骤
     */
    public void completeCurrentStep() {
        if (this.state != OrderState.ACTIVE) {
            throw new IllegalStateException("只有 ACTIVE 状态才能完成步骤");
        }
        if (currentStepIndex >= steps.size()) {
            throw new IllegalStateException("没有更多的步骤");
        }

        OrderStep currentStep = steps.get(currentStepIndex);
        currentStep.complete();
        currentStepIndex++;

        // 检查是否所有步骤都完成
        if (currentStepIndex >= steps.size()) {
            finish();
        }
    }

    /**
     * 完成订单
     */
    public void finish() {
        this.state = OrderState.FINISHED;
        this.finishedTime = System.currentTimeMillis();
    }

    /**
     * 完成订单（外部调用）
     */
    public void complete() {
        finish();
    }

    /**
     * 失败
     */
    public void fail() {
        this.state = OrderState.FAILED;
        this.finishedTime = System.currentTimeMillis();
    }

    /**
     * 取消订单
     */
    public void cancel() {
        if (!state.canCancel()) {
            throw new IllegalStateException("当前状态不能取消: " + state);
        }
        this.state = OrderState.CANCELLED;
        this.finishedTime = System.currentTimeMillis();
    }

    /**
     * 获取当前步骤
     */
    public OrderStep getCurrentStep() {
        if (currentStepIndex >= steps.size()) {
            return null;
        }
        return steps.get(currentStepIndex);
    }

    /**
     * 检查订单是否完成
     */
    public boolean isFinished() {
        return state == OrderState.FINISHED;
    }

    /**
     * 检查订单是否失败
     */
    public boolean isFailed() {
        return state == OrderState.FAILED;
    }

    /**
     * 检查订单是否已取消
     */
    public boolean isCancelled() {
        return state == OrderState.CANCELLED;
    }

    /**
     * 检查订单是否在执行
     */
    public boolean isActive() {
        return state == OrderState.ACTIVE;
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis();
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public OrderState getState() {
        return state;
    }

    public String getIntendedVehicle() {
        return intendedVehicle;
    }

    public void setIntendedVehicle(String intendedVehicle) {
        this.intendedVehicle = intendedVehicle;
    }

    public String getProcessingVehicle() {
        return processingVehicle;
    }

    public List<OrderStep> getSteps() {
        return steps;
    }

    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getFinishedTime() {
        return finishedTime;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public java.util.Map<String, String> getProperties() {
        return properties;
    }

    public String getSourcePointId() {
        return sourcePointId;
    }

    public String getDestPointId() {
        return destPointId;
    }

    public List<Path> getRoute() {
        return route;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportOrder that = (TransportOrder) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "TransportOrder{" +
                "orderId='" + orderId + '\'' +
                ", name='" + name + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", state=" + state +
                ", processingVehicle='" + processingVehicle + '\'' +
                ", currentStepIndex=" + currentStepIndex +
                '}';
    }

    /**
     * 订单构建器
     */
    public static class Builder {
        private final TransportOrder order;

        public Builder(String name) {
            this.order = new TransportOrder(name);
        }

        public Builder orderId(String orderId) {
            return this;
        }

        public Builder intendedVehicle(String intendedVehicle) {
            order.intendedVehicle = intendedVehicle;
            return this;
        }

        public Builder deadline(Long deadline) {
            order.deadline = deadline;
            return this;
        }

        public Builder property(String key, String value) {
            order.properties.put(key, value);
            return this;
        }

        public Builder addStep(OrderStep step) {
            order.addStep(step);
            return this;
        }

        public TransportOrder build() {
            return order;
        }
    }
}
