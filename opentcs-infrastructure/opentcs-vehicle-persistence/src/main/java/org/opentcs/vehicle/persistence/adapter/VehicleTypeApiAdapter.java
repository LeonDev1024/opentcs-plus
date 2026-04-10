package org.opentcs.vehicle.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.opentcs.kernel.api.VehicleTypeApi;
import org.opentcs.kernel.api.dto.VehicleTypeDTO;
import org.opentcs.vehicle.persistence.entity.VehicleTypeEntity;
import org.opentcs.vehicle.persistence.service.VehicleTypeDomainService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * VehicleTypeApi 的持久化适配器实现（基础设施层）。
 */
@Component
@RequiredArgsConstructor
public class VehicleTypeApiAdapter implements VehicleTypeApi {

    private final VehicleTypeDomainService vehicleTypeDomainService;

    @Override
    public List<VehicleTypeDTO> findAll() {
        return vehicleTypeDomainService.list().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleTypeDTO> findByBrandId(Long brandId) {
        return vehicleTypeDomainService.lambdaQuery()
                .eq(VehicleTypeEntity::getBrandId, brandId)
                .eq(VehicleTypeEntity::getDelFlag, "0")
                .list()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<VehicleTypeDTO> findById(Long id) {
        return Optional.ofNullable(vehicleTypeDomainService.getById(id)).map(this::toDTO);
    }

    @Override
    public Optional<VehicleTypeDTO> findByTypeId(String typeId) {
        // typeId 对应 name 字段（唯一名称作为领域标识）
        return vehicleTypeDomainService.lambdaQuery()
                .eq(VehicleTypeEntity::getName, typeId)
                .oneOpt()
                .map(this::toDTO);
    }

    @Override
    public VehicleTypeDTO create(VehicleTypeDTO dto) {
        VehicleTypeEntity entity = toEntity(dto);
        vehicleTypeDomainService.save(entity);
        return toDTO(entity);
    }

    @Override
    public VehicleTypeDTO update(VehicleTypeDTO dto) {
        VehicleTypeEntity entity = toEntity(dto);
        vehicleTypeDomainService.updateById(entity);
        return toDTO(vehicleTypeDomainService.getById(entity.getId()));
    }

    @Override
    public void delete(Long id) {
        vehicleTypeDomainService.removeById(id);
    }

    // ===== 转换方法 =====

    private VehicleTypeDTO toDTO(VehicleTypeEntity e) {
        VehicleTypeDTO dto = new VehicleTypeDTO();
        dto.setId(e.getId());
        dto.setTypeId(e.getName());           // name 作为领域 typeId
        dto.setBrandId(e.getBrandId());
        dto.setBrandName(e.getBrandName());
        dto.setName(e.getName());
        dto.setLength(e.getLength());
        dto.setWidth(e.getWidth());
        dto.setHeight(e.getHeight());
        dto.setMaxVelocity(e.getMaxVelocity());
        dto.setMaxReverseVelocity(e.getMaxReverseVelocity());
        dto.setEnergyLevel(e.getEnergyLevel());
        dto.setAllowedOrders(e.getAllowedOrders());
        dto.setAllowedPeripheralOperations(e.getAllowedPeripheralOperations());
        return dto;
    }

    private VehicleTypeEntity toEntity(VehicleTypeDTO dto) {
        VehicleTypeEntity e = new VehicleTypeEntity();
        e.setId(dto.getId());
        e.setBrandId(dto.getBrandId());
        e.setName(dto.getName());
        e.setLength(dto.getLength());
        e.setWidth(dto.getWidth());
        e.setHeight(dto.getHeight());
        e.setMaxVelocity(dto.getMaxVelocity());
        e.setMaxReverseVelocity(dto.getMaxReverseVelocity());
        e.setEnergyLevel(dto.getEnergyLevel());
        if (dto.getAllowedOrders() != null) e.setAllowedOrders(dto.getAllowedOrders());
        if (dto.getAllowedPeripheralOperations() != null) e.setAllowedPeripheralOperations(dto.getAllowedPeripheralOperations());
        return e;
    }
}
