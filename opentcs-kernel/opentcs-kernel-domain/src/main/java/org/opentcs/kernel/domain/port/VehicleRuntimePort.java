package org.opentcs.kernel.domain.port;

import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.vehicle.VehicleState;

import java.util.List;

/**
 * 车辆运行时协作端口（modules 依赖此接口，不依赖 kernel.core 实现类）。
 */
public interface VehicleRuntimePort {

    Vehicle registerVehicleDomain(Vehicle vehicle);

    void unregisterVehicleDomain(String vehicleId);

    Vehicle getVehicleDomain(String vehicleId);

    List<Vehicle> getAvailableVehicleDomains();

    List<Vehicle> getAllVehicleDomains();

    void updateVehicleStateDomain(String vehicleId, VehicleState state);

    boolean reportVehicleRuntimeStateDomain(String vehicleId,
                                            VehicleState state,
                                            String currentOrderId,
                                            Long statusSequence);

    void updateVehiclePositionDomain(String vehicleId, VehiclePosition position);

    void updateVehicleEnergyDomain(String vehicleId, double energyLevel);

    String getVehicleCurrentOrder(String vehicleId);

    boolean isOnline(String vehicleId);
}
