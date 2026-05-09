package org.opentcs.kernel.domain.event;

/**
 * 请求车辆侧撤回订单的领域事件。
 */
public class OrderWithdrawalRequestedEvent extends DomainEvent {

    private final String orderId;
    private final String vehicleId;
    private final boolean immediateAbort;

    public OrderWithdrawalRequestedEvent(String orderId, String vehicleId, boolean immediateAbort) {
        super(orderId);
        this.orderId = orderId;
        this.vehicleId = vehicleId;
        this.immediateAbort = immediateAbort;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public boolean isImmediateAbort() {
        return immediateAbort;
    }

    public String getActionType() {
        return immediateAbort ? "ABORT_ORDER" : "CANCEL_ORDER";
    }
}
