package org.opentcs.kernel.persistence.service;

import org.opentcs.kernel.api.dto.*;
import org.opentcs.kernel.persistence.entity.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO 转换工具类
 */
public class DTOConverter {

    /**
     * BlockEntity 转 BlockDTO
     */
    public static BlockDTO toBlockDTO(BlockEntity entity) {
        if (entity == null) return null;
        BlockDTO dto = new BlockDTO();
        dto.setId(entity.getId());
        dto.setFactoryModelId(entity.getFactoryModelId());
        dto.setNavigationMapId(entity.getNavigationMapId());
        dto.setBlockId(entity.getBlockId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setMembers(entity.getMembers());
        dto.setColor(entity.getColor());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public static List<BlockDTO> toBlockDTOList(List<BlockEntity> entities) {
        return entities.stream().map(DTOConverter::toBlockDTO).collect(Collectors.toList());
    }

    /**
     * LocationEntity 转 LocationDTO
     */
    public static LocationDTO toLocationDTO(LocationEntity entity) {
        if (entity == null) return null;
        LocationDTO dto = new LocationDTO();
        dto.setId(entity.getId());
        dto.setNavigationMapId(entity.getNavigationMapId());
        dto.setLocationTypeId(entity.getLocationTypeId());
        dto.setLocationId(entity.getLocationId());
        dto.setName(entity.getName());
        dto.setXPosition(entity.getXPosition());
        dto.setYPosition(entity.getYPosition());
        dto.setZPosition(entity.getZPosition());
        dto.setLocked(entity.getLocked());
        dto.setIsOccupied(entity.getIsOccupied());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public static List<LocationDTO> toLocationDTOList(List<LocationEntity> entities) {
        return entities.stream().map(DTOConverter::toLocationDTO).collect(Collectors.toList());
    }

    /**
     * PathEntity 转 PathDTO
     */
    public static PathDTO toPathDTO(PathEntity entity) {
        if (entity == null) return null;
        PathDTO dto = new PathDTO();
        dto.setId(entity.getId());
        dto.setNavigationMapId(entity.getNavigationMapId());
        dto.setPathId(entity.getPathId());
        dto.setName(entity.getName());
        dto.setSourcePointId(entity.getSourcePointId());
        dto.setDestPointId(entity.getDestPointId());
        dto.setLength(entity.getLength());
        dto.setMaxVelocity(entity.getMaxVelocity());
        dto.setMaxReverseVelocity(entity.getMaxReverseVelocity());
        dto.setRoutingType(entity.getRoutingType());
        dto.setLocked(entity.getLocked());
        dto.setIsBlocked(entity.getIsBlocked());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public static List<PathDTO> toPathDTOList(List<PathEntity> entities) {
        return entities.stream().map(DTOConverter::toPathDTO).collect(Collectors.toList());
    }

    /**
     * FactoryModelEntity 转 FactoryModelDTO
     */
    public static FactoryModelDTO toFactoryModelDTO(FactoryModelEntity entity) {
        if (entity == null) return null;
        FactoryModelDTO dto = new FactoryModelDTO();
        dto.setId(entity.getId());
        dto.setFactoryId(entity.getFactoryId());
        dto.setName(entity.getName());
        dto.setScale(entity.getScale());
        dto.setProperties(entity.getProperties());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public static List<FactoryModelDTO> toFactoryModelDTOList(List<FactoryModelEntity> entities) {
        return entities.stream().map(DTOConverter::toFactoryModelDTO).collect(Collectors.toList());
    }

    /**
     * VehicleEntity 转 VehicleEntityDTO
     */
    public static VehicleEntityDTO toVehicleEntityDTO(VehicleEntity entity) {
        if (entity == null) return null;
        VehicleEntityDTO dto = new VehicleEntityDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setVinCode(entity.getVinCode());
        dto.setVehicleTypeId(entity.getVehicleTypeId());
        dto.setCurrentPosition(entity.getCurrentPosition());
        dto.setNextPosition(entity.getNextPosition());
        dto.setState(entity.getState());
        dto.setIntegrationLevel(entity.getIntegrationLevel());
        dto.setEnergyLevel(entity.getEnergyLevel());
        dto.setCurrentTransportOrder(entity.getCurrentTransportOrder());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public static List<VehicleEntityDTO> toVehicleEntityDTOList(List<VehicleEntity> entities) {
        return entities.stream().map(DTOConverter::toVehicleEntityDTO).collect(Collectors.toList());
    }

    /**
     * VehicleTypeEntity 转 VehicleTypeDTO
     */
    public static VehicleTypeDTO toVehicleTypeDTO(VehicleTypeEntity entity) {
        if (entity == null) return null;
        VehicleTypeDTO dto = new VehicleTypeDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLength(entity.getLength());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setMaxVelocity(entity.getMaxVelocity());
        dto.setMaxReverseVelocity(entity.getMaxReverseVelocity());
        dto.setEnergyLevel(entity.getEnergyLevel());
        dto.setAllowedOrders(entity.getAllowedOrders());
        dto.setAllowedPeripheralOperations(entity.getAllowedPeripheralOperations());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public static List<VehicleTypeDTO> toVehicleTypeDTOList(List<VehicleTypeEntity> entities) {
        return entities.stream().map(DTOConverter::toVehicleTypeDTO).collect(Collectors.toList());
    }

    /**
     * TransportOrderEntity 转 TransportOrderEntityDTO
     */
    public static TransportOrderEntityDTO toTransportOrderEntityDTO(TransportOrderEntity entity) {
        if (entity == null) return null;
        TransportOrderEntityDTO dto = new TransportOrderEntityDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setOrderNo(entity.getOrderNo());
        dto.setState(entity.getState());
        dto.setIntendedVehicle(entity.getIntendedVehicle());
        dto.setProcessingVehicle(entity.getProcessingVehicle());
        dto.setDestinations(entity.getDestinations());
        dto.setCreationTime(entity.getCreationTime());
        dto.setFinishedTime(entity.getFinishedTime());
        dto.setDeadline(entity.getDeadline());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public static List<TransportOrderEntityDTO> toTransportOrderEntityDTOList(List<TransportOrderEntity> entities) {
        return entities.stream().map(DTOConverter::toTransportOrderEntityDTO).collect(Collectors.toList());
    }

    /**
     * CrossLayerConnectionEntity 转 CrossLayerConnectionDTO
     */
    public static CrossLayerConnectionDTO toCrossLayerConnectionDTO(CrossLayerConnectionEntity entity) {
        if (entity == null) return null;
        CrossLayerConnectionDTO dto = new CrossLayerConnectionDTO();
        dto.setId(entity.getId());
        dto.setFactoryModelId(entity.getFactoryModelId());
        dto.setConnectionId(entity.getConnectionId());
        dto.setName(entity.getName());
        dto.setConnectionType(entity.getConnectionType());
        dto.setSourceNavigationMapId(entity.getSourceNavigationMapId());
        dto.setSourcePointId(entity.getSourcePointId());
        dto.setSourceFloor(entity.getSourceFloor());
        dto.setDestNavigationMapId(entity.getDestNavigationMapId());
        dto.setDestPointId(entity.getDestPointId());
        dto.setDestFloor(entity.getDestFloor());
        dto.setCapacity(entity.getCapacity());
        dto.setMaxWeight(entity.getMaxWeight());
        dto.setTravelTime(entity.getTravelTime());
        dto.setAvailable(entity.getAvailable());
        dto.setCurrentLoad(entity.getCurrentLoad());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public static List<CrossLayerConnectionDTO> toCrossLayerConnectionDTOList(List<CrossLayerConnectionEntity> entities) {
        return entities.stream().map(DTOConverter::toCrossLayerConnectionDTO).collect(Collectors.toList());
    }
}
