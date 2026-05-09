package org.opentcs.kernel.domain.event;

import lombok.Getter;
import org.opentcs.kernel.domain.order.OrderState;

/**
 * 订单运行状态变化事件。
 */
@Getter
public class OrderStateChangedEvent extends DomainEvent {

    private final OrderState oldState;
    private final OrderState newState;
    private final String processingVehicle;
    private final String reason;

    public OrderStateChangedEvent(String orderId,
                                  OrderState oldState,
                                  OrderState newState,
                                  String processingVehicle,
                                  String reason) {
        super(orderId);
        this.oldState = oldState;
        this.newState = newState;
        this.processingVehicle = processingVehicle;
        this.reason = reason;
    }
}
