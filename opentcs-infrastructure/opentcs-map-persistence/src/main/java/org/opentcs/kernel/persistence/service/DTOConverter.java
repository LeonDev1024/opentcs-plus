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

    public static BlockEntity toBlockEntity(BlockDTO dto) {
        if (dto == null) return null;
        BlockEntity entity = new BlockEntity();
        entity.setId(dto.getId());
        entity.setFactoryModelId(dto.getFactoryModelId());
        entity.setNavigationMapId(dto.getNavigationMapId());
        entity.setBlockId(dto.getBlockId());
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setMembers(dto.getMembers());
        entity.setColor(dto.getColor());
        entity.setProperties(dto.getProperties());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
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
        dto.setLocked(entity.getLocked());
        dto.setIsBlocked(entity.getIsBlocked());
        dto.setProperties(entity.getProperties());
        dto.setConnectionType(entity.getConnectionType());
        dto.setLayoutControlPoints(entity.getLayoutControlPoints());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public static List<PathDTO> toPathDTOList(List<PathEntity> entities) {
        return entities.stream().map(DTOConverter::toPathDTO).collect(Collectors.toList());
    }

    /**
     * PointEntity 转 PointDTO
     */
    public static PointDTO toPointDTO(PointEntity entity) {
        if (entity == null) return null;
        PointDTO dto = new PointDTO();
        dto.setId(entity.getId());
        dto.setNavigationMapId(entity.getNavigationMapId());
        dto.setLayerId(entity.getLayerId());
        dto.setPointId(entity.getPointId());
        dto.setName(entity.getName());
        dto.setXPosition(entity.getXPosition());
        dto.setYPosition(entity.getYPosition());
        dto.setZPosition(entity.getZPosition());
        dto.setVehicleOrientation(entity.getVehicleOrientation());
        dto.setType(entity.getType());
        dto.setRadius(entity.getRadius());
        dto.setLocked(entity.getLocked());
        dto.setIsBlocked(entity.getIsBlocked());
        dto.setIsOccupied(entity.getIsOccupied());
        dto.setLabel(entity.getLabel());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    public static List<PointDTO> toPointDTOList(List<PointEntity> entities) {
        return entities.stream().map(DTOConverter::toPointDTO).collect(Collectors.toList());
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

    public static CrossLayerConnectionEntity toCrossLayerConnectionEntity(CrossLayerConnectionDTO dto) {
        if (dto == null) return null;
        CrossLayerConnectionEntity entity = new CrossLayerConnectionEntity();
        entity.setId(dto.getId());
        entity.setFactoryModelId(dto.getFactoryModelId());
        entity.setConnectionId(dto.getConnectionId());
        entity.setName(dto.getName());
        entity.setConnectionType(dto.getConnectionType());
        entity.setSourceNavigationMapId(dto.getSourceNavigationMapId());
        entity.setSourcePointId(dto.getSourcePointId());
        entity.setSourceFloor(dto.getSourceFloor());
        entity.setDestNavigationMapId(dto.getDestNavigationMapId());
        entity.setDestPointId(dto.getDestPointId());
        entity.setDestFloor(dto.getDestFloor());
        entity.setCapacity(dto.getCapacity());
        entity.setMaxWeight(dto.getMaxWeight());
        entity.setTravelTime(dto.getTravelTime());
        entity.setAvailable(dto.getAvailable());
        entity.setCurrentLoad(dto.getCurrentLoad());
        entity.setProperties(dto.getProperties());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }

    /**
     * NavigationMapEntity 转 NavigationMapDTO
     */
    public static NavigationMapDTO toNavigationMapDTO(NavigationMapEntity entity) {
        if (entity == null) return null;
        NavigationMapDTO dto = new NavigationMapDTO();
        dto.setId(entity.getId());
        dto.setFactoryModelId(entity.getFactoryModelId());
        dto.setMapId(entity.getMapId());
        dto.setName(entity.getName());
        dto.setFloorNumber(entity.getFloorNumber());
        dto.setVehicleTypeId(entity.getVehicleTypeId());
        dto.setOriginX(entity.getOriginX());
        dto.setOriginY(entity.getOriginY());
        dto.setRotation(entity.getRotation());
        dto.setProperties(entity.getProperties());
        dto.setStatus(entity.getStatus());
        dto.setMapVersion(entity.getMapVersion());
        dto.setRasterUrl(entity.getRasterUrl());
        dto.setRasterVersion(entity.getRasterVersion());
        dto.setRasterWidth(entity.getRasterWidth());
        dto.setRasterHeight(entity.getRasterHeight());
        dto.setRasterResolution(entity.getRasterResolution());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}
