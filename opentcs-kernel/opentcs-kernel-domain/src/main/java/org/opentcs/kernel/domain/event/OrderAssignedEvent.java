package org.opentcs.kernel.domain.event;

/**
 * 订单已分配给车辆，请求驱动层下发执行指令。
 */
public class OrderAssignedEvent extends DomainEvent {

    private final String orderId;
    private final String vehicleId;

    public OrderAssignedEvent(String orderId, String vehicleId) {
        super(orderId);
        this.orderId = orderId;
        this.vehicleId = vehicleId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    @Override
    public String toString() {
        return "OrderAssignedEvent{" +
                "orderId='" + orderId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
