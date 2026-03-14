package org.opentcs.kernel.domain.event;

/**
 * 订单创建事件
 */
public class OrderCreatedEvent extends DomainEvent {

    private final String orderName;
    private final String intendedVehicle;
    private final int stepCount;

    public OrderCreatedEvent(String orderId, String orderName, String intendedVehicle, int stepCount) {
        super(orderId);
        this.orderName = orderName;
        this.intendedVehicle = intendedVehicle;
        this.stepCount = stepCount;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getIntendedVehicle() {
        return intendedVehicle;
    }

    public int getStepCount() {
        return stepCount;
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "orderId='" + getAggregateId() + '\'' +
                ", orderName='" + orderName + '\'' +
                ", intendedVehicle='" + intendedVehicle + '\'' +
                ", stepCount=" + stepCount +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
