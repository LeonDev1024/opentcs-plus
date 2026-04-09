package org.opentcs.kernel.application;

import org.opentcs.kernel.api.VehicleRegistryApi;
import org.opentcs.kernel.api.dto.PositionDTO;
import org.opentcs.kernel.api.dto.VehicleDTO;
import org.opentcs.kernel.api.dto.VehicleStateDTO;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.vehicle.VehicleState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 车辆运行时注册表，同时实现 {@link VehicleRegistryApi} 端口接口。
 * <p>
 * 持有所有在线车辆的运行时状态（位置、状态、当前订单），
 * 数据仅存于内存，服务重启后由驱动层重新上报恢复。
 * </p>
 */
public class VehicleRegistry implements VehicleRegistryApi {

    private final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();

    // ===== 内部操作（kernel-core 内使用）=====

    public Vehicle registerVehicleDomain(Vehicle vehicle) {
        vehicles.put(vehicle.getVehicleId(), vehicle);
        return vehicle;
    }

    public void unregisterVehicleDomain(String vehicleId) {
        vehicles.remove(vehicleId);
    }

    public Vehicle getVehicleDomain(String vehicleId) {
        return vehicles.get(vehicleId);
    }

    public List<Vehicle> getAvailableVehicleDomains() {
        return vehicles.values().stream()
                .filter(Vehicle::canAcceptOrder)
                .collect(Collectors.toList());
    }

    public void updateVehicleStateDomain(String vehicleId, VehicleState state) {
        Vehicle v = vehicles.get(vehicleId);
        if (v != null) v.updateState(state);
    }

    public void updateVehiclePositionDomain(String vehicleId, VehiclePosition position) {
        Vehicle v = vehicles.get(vehicleId);
        if (v != null) v.updatePosition(position);
    }

    public void updateVehicleEnergyDomain(String vehicleId, double energyLevel) {
        Vehicle v = vehicles.get(vehicleId);
        if (v != null) v.updateEnergy(energyLevel);
    }

    public String getVehicleCurrentOrder(String vehicleId) {
        Vehicle v = vehicles.get(vehicleId);
        return v != null ? v.getCurrentOrderId() : null;
    }

    public boolean isOnline(String vehicleId) {
        Vehicle v = vehicles.get(vehicleId);
        return v != null && v.getState() != VehicleState.OFFLINE;
    }

    public List<Vehicle> getAllVehicleDomains() {
        return new java.util.ArrayList<>(vehicles.values());
    }

    // ===== VehicleRegistryApi 端口实现 =====

    @Override
    public VehicleDTO registerVehicle(VehicleDTO dto) {
        Vehicle vehicle = fromDTO(dto);
        vehicles.put(vehicle.getVehicleId(), vehicle);
        return toDTO(vehicle);
    }

    @Override
    public void unregisterVehicle(String vehicleId) {
        vehicles.remove(vehicleId);
    }

    @Override
    public void updateVehicleState(String vehicleId, VehicleStateDTO stateDTO) {
        Vehicle v = vehicles.get(vehicleId);
        if (v != null) v.updateState(toState(stateDTO));
    }

    @Override
    public void updateVehiclePosition(String vehicleId, PositionDTO posDTO) {
        Vehicle v = vehicles.get(vehicleId);
        if (v != null) {
            v.updatePosition(new VehiclePosition(
                    posDTO.getPointId(),
                    posDTO.getMapId(),
                    posDTO.getX() != null ? posDTO.getX() : 0,
                    posDTO.getY() != null ? posDTO.getY() : 0,
                    posDTO.getZ() != null ? posDTO.getZ() : 0,
                    posDTO.getOrientation() != null ? posDTO.getOrientation() : 0
            ));
        }
    }

    @Override
    public Optional<VehicleDTO> getVehicle(String vehicleId) {
        return Optional.ofNullable(vehicles.get(vehicleId)).map(this::toDTO);
    }

    @Override
    public List<VehicleDTO> getAllVehicles() {
        return vehicles.values().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> getAvailableVehicles() {
        return vehicles.values().stream()
                .filter(Vehicle::canAcceptOrder)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getOnlineVehicleIds() {
        return vehicles.values().stream()
                .filter(v -> v.getState() != VehicleState.OFFLINE)
                .map(Vehicle::getVehicleId)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean vehicleExists(String vehicleId) {
        return vehicles.containsKey(vehicleId);
    }

    // ===== DTO 映射 =====

    public VehicleDTO toDTO(Vehicle v) {
        VehicleDTO dto = new VehicleDTO();
        dto.setVehicleId(v.getVehicleId());
        dto.setName(v.getName());
        dto.setTypeId(v.getTypeId());
        dto.setState(toStateDTO(v.getState()));
        dto.setCurrentOrderId(v.getCurrentOrderId());
        dto.setEnergyLevel(v.getEnergyLevel());
        dto.setCreateTime(v.getCreateTime());
        dto.setUpdateTime(v.getUpdateTime());
        if (v.getPosition() != null) {
            PositionDTO pos = new PositionDTO();
            pos.setPointId(v.getPosition().getPointId());
            pos.setMapId(v.getPosition().getMapId());
            pos.setX(v.getPosition().getX());
            pos.setY(v.getPosition().getY());
            pos.setZ(v.getPosition().getZ());
            pos.setOrientation(v.getPosition().getOrientation());
            dto.setPosition(pos);
        }
        return dto;
    }

    private Vehicle fromDTO(VehicleDTO dto) {
        Vehicle v = new Vehicle(dto.getVehicleId());
        if (dto.getName() != null) v.setName(dto.getName());
        if (dto.getTypeId() != null) v.setTypeId(dto.getTypeId());
        if (dto.getState() != null) v.updateState(toState(dto.getState()));
        return v;
    }

    private VehicleStateDTO toStateDTO(VehicleState state) {
        if (state == null) return VehicleStateDTO.UNKNOWN;
        return VehicleStateDTO.valueOf(state.name());
    }

    private VehicleState toState(VehicleStateDTO dto) {
        if (dto == null) return VehicleState.UNKNOWN;
        return VehicleState.valueOf(dto.name());
    }
}
