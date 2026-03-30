package org.opentcs.kernel.domain.event;

import lombok.Getter;

/**
 * 订单创建事件
 */
@Getter
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
