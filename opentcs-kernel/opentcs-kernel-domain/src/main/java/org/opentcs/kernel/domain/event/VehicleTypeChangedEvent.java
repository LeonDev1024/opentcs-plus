package org.opentcs.kernel.domain.event;

/**
 * 车辆类型变更领域事件。
 * 当车辆被重新分配到不同类型时发布。
 */
public class VehicleTypeChangedEvent extends DomainEvent {

    private final String vehicleId;
    private final String oldTypeId;
    private final String newTypeId;

    public VehicleTypeChangedEvent(String vehicleId, String oldTypeId, String newTypeId) {
        super(vehicleId);
        this.vehicleId = vehicleId;
        this.oldTypeId = oldTypeId;
        this.newTypeId = newTypeId;
    }

    public String getVehicleId() { return vehicleId; }
    public String getOldTypeId() { return oldTypeId; }
    public String getNewTypeId() { return newTypeId; }
}
