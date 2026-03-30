package org.opentcs.kernel.domain.event;

import lombok.Getter;
import org.opentcs.kernel.domain.vehicle.VehicleState;

/**
 * 车辆状态变更事件
 */
@Getter
public class VehicleStateChangedEvent extends DomainEvent {

    private final VehicleState oldState;
    private final VehicleState newState;
    private final String currentOrderId;

    public VehicleStateChangedEvent(String vehicleId, VehicleState oldState,
                                    VehicleState newState, String currentOrderId) {
        super(vehicleId);
        this.oldState = oldState;
        this.newState = newState;
        this.currentOrderId = currentOrderId;
    }

    @Override
    public String toString() {
        return "VehicleStateChangedEvent{" +
                "vehicleId='" + getAggregateId() + '\'' +
                ", oldState=" + oldState +
                ", newState=" + newState +
                ", currentOrderId='" + currentOrderId + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
