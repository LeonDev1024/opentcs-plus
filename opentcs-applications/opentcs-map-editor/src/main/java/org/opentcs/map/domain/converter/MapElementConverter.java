package org.opentcs.map.domain.converter;

import org.opentcs.kernel.persistence.entity.PointEntity;
import org.opentcs.kernel.persistence.entity.PathEntity;
import org.opentcs.kernel.persistence.entity.LocationEntity;
import org.opentcs.map.domain.dto.PointDTO;
import org.opentcs.map.domain.dto.PathDTO;
import org.opentcs.map.domain.dto.LocationDTO;
import org.opentcs.common.core.dto.PathLayoutControlPointTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图元素转换器
 */
public class MapElementConverter {

    /**
     * 点位 Entity 转 DTO
     */
    public static PointDTO toPointDTO(PointEntity entity) {
        if (entity == null) {
            return null;
        }
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
        return dto;
    }

    /**
     * 点位 DTO 转 Entity
     */
    public static PointEntity toPointEntity(PointDTO dto) {
        if (dto == null) {
            return null;
        }
        PointEntity entity = new PointEntity();
        entity.setId(dto.getId());
        entity.setNavigationMapId(dto.getNavigationMapId());
        entity.setLayerId(dto.getLayerId());
        entity.setPointId(dto.getPointId());
        entity.setName(dto.getName());
        entity.setXPosition(dto.getXPosition());
        entity.setYPosition(dto.getYPosition());
        entity.setZPosition(dto.getZPosition());
        entity.setVehicleOrientation(dto.getVehicleOrientation());
        entity.setType(dto.getType());
        entity.setRadius(dto.getRadius());
        entity.setLocked(dto.getLocked());
        entity.setIsBlocked(dto.getIsBlocked());
        entity.setIsOccupied(dto.getIsOccupied());
        entity.setLabel(dto.getLabel());
        entity.setProperties(dto.getProperties());
        return entity;
    }

    /**
     * 点位列表 Entity 转 DTO
     */
    public static List<PointDTO> toPointDTOList(List<PointEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(MapElementConverter::toPointDTO)
                .toList();
    }

    /**
     * 路径 Entity 转 DTO
     */
    public static PathDTO toPathDTO(PathEntity entity) {
        if (entity == null) {
            return null;
        }
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

        // 转换布局控制点
        if (entity.getLayoutControlPoints() != null) {
            List<PathDTO.LayoutControlPointDTO> controlPoints = entity.getLayoutControlPoints().stream()
                    .map(cp -> {
                        PathDTO.LayoutControlPointDTO controlPointDTO = new PathDTO.LayoutControlPointDTO();
                        controlPointDTO.setX(cp.getX());
                        controlPointDTO.setY(cp.getY());
                        return controlPointDTO;
                    })
                    .toList();
            dto.setLayoutControlPoints(controlPoints);
        }

        return dto;
    }

    /**
     * 路径 DTO 转 Entity
     */
    public static PathEntity toPathEntity(PathDTO dto) {
        if (dto == null) {
            return null;
        }
        PathEntity entity = new PathEntity();
        entity.setId(dto.getId());
        entity.setNavigationMapId(dto.getNavigationMapId());
        entity.setPathId(dto.getPathId());
        entity.setName(dto.getName());
        entity.setSourcePointId(dto.getSourcePointId());
        entity.setDestPointId(dto.getDestPointId());
        entity.setLength(dto.getLength());
        entity.setMaxVelocity(dto.getMaxVelocity());
        entity.setMaxReverseVelocity(dto.getMaxReverseVelocity());
        entity.setRoutingType(dto.getRoutingType());
        entity.setLocked(dto.getLocked());
        entity.setIsBlocked(dto.getIsBlocked());
        entity.setProperties(dto.getProperties());

        // 转换布局控制点
        if (dto.getLayoutControlPoints() != null) {
            List<PathLayoutControlPointTO> controlPoints = dto.getLayoutControlPoints().stream()
                    .map(cp -> {
                        PathLayoutControlPointTO controlPoint = new PathLayoutControlPointTO();
                        controlPoint.setX(cp.getX());
                        controlPoint.setY(cp.getY());
                        return controlPoint;
                    })
                    .toList();
            entity.setLayoutControlPoints(controlPoints);
        }

        return entity;
    }

    /**
     * 路径列表 Entity 转 DTO
     */
    public static List<PathDTO> toPathDTOList(List<PathEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(MapElementConverter::toPathDTO)
                .toList();
    }

    /**
     * 位置 Entity 转 DTO
     */
    public static LocationDTO toLocationDTO(LocationEntity entity) {
        if (entity == null) {
            return null;
        }
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
        return dto;
    }

    /**
     * 位置 DTO 转 Entity
     */
    public static LocationEntity toLocationEntity(LocationDTO dto) {
        if (dto == null) {
            return null;
        }
        LocationEntity entity = new LocationEntity();
        entity.setId(dto.getId());
        entity.setNavigationMapId(dto.getNavigationMapId());
        entity.setLocationTypeId(dto.getLocationTypeId());
        entity.setLocationId(dto.getLocationId());
        entity.setName(dto.getName());
        entity.setXPosition(dto.getXPosition());
        entity.setYPosition(dto.getYPosition());
        entity.setZPosition(dto.getZPosition());
        entity.setLocked(dto.getLocked());
        entity.setIsOccupied(dto.getIsOccupied());
        entity.setProperties(dto.getProperties());
        return entity;
    }

    /**
     * 位置列表 Entity 转 DTO
     */
    public static List<LocationDTO> toLocationDTOList(List<LocationEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(MapElementConverter::toLocationDTO)
                .toList();
    }
}