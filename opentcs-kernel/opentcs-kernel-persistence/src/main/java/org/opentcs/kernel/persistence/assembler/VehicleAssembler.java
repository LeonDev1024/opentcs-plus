package org.opentcs.kernel.persistence.assembler;

import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.opentcs.kernel.persistence.entity.VehicleEntity;

/**
 * 车辆领域模型与数据模型转换器
 */
public class VehicleAssembler {

    /**
     * 将数据模型转换为领域模型
     *
     * @param entity 数据模型
     * @return 领域模型
     */
    public Vehicle toDomain(VehicleEntity entity) {
        if (entity == null) {
            return null;
        }

        Vehicle vehicle = new Vehicle.Builder(String.valueOf(entity.getId()))
            .name(entity.getName())
            .vehicleType(String.valueOf(entity.getVehicleTypeId()))
            .state(parseVehicleState(entity.getState()))
            .position(parseVehiclePosition(entity.getCurrentPosition()))
            .energyLevel(entity.getEnergyLevel() != null ? entity.getEnergyLevel().doubleValue() : 100.0)
            .build();

        return vehicle;
    }

    /**
     * 从领域模型转换为数据模型
     *
     * @param domain  领域模型
     * @param entity  目标数据模型
     */
    public void toEntity(Vehicle domain, VehicleEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setName(domain.getName());
        entity.setState(domain.getState() != null ? domain.getState().name() : null);
        entity.setCurrentPosition(domain.getPosition() != null ? domain.getPosition().getPointId() : null);
        entity.setEnergyLevel(domain.getEnergyLevel() > 0 ? java.math.BigDecimal.valueOf(domain.getEnergyLevel()) : null);
    }

    /**
     * 从领域模型复制属性到数据模型
     *
     * @param domain  领域模型
     * @param entity  目标数据模型
     */
    public void copyToDataModel(Vehicle domain, VehicleEntity entity) {
        toEntity(domain, entity);
    }

    /**
     * 创建领域模型
     */
    public Vehicle createDomain(String vehicleId, String name) {
        return new Vehicle.Builder(vehicleId)
            .name(name)
            .state(VehicleState.IDLE)
            .position(VehiclePosition.unknown())
            .build();
    }

    private VehicleState parseVehicleState(String state) {
        if (state == null) {
            return VehicleState.UNKNOWN;
        }
        try {
            return VehicleState.valueOf(state);
        } catch (IllegalArgumentException e) {
            return VehicleState.UNKNOWN;
        }
    }

    private VehiclePosition parseVehiclePosition(String position) {
        if (position == null || position.isEmpty()) {
            return VehiclePosition.unknown();
        }
        return new VehiclePosition(position, null, 0, 0, 0, 0);
    }
}
