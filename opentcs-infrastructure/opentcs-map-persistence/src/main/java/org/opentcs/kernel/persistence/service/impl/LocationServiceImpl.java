package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.LocationDTO;
import org.opentcs.kernel.persistence.service.DTOConverter;
import org.opentcs.kernel.persistence.entity.LocationEntity;
import org.opentcs.kernel.persistence.mapper.LocationMapper;
import org.opentcs.kernel.persistence.service.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 位置领域服务实现
 */
@Service
@RequiredArgsConstructor
public class LocationServiceImpl extends ServiceImpl<LocationMapper, LocationEntity> implements LocationRepository {

    private final ObjectMapper objectMapper;

    @Override
    public TableDataInfo<LocationEntity> selectPage(LocationEntity location, PageQuery pageQuery) {
        LambdaQueryWrapper<LocationEntity> wrapper = new LambdaQueryWrapper<>();

        if (location != null) {
            if (StringUtils.hasText(location.getName())) {
                wrapper.like(LocationEntity::getName, location.getName());
            }
            if (location.getLocationTypeId() != null) {
                wrapper.eq(LocationEntity::getLocationTypeId, location.getLocationTypeId());
            }
            if (location.getNavigationMapId() != null) {
                wrapper.eq(LocationEntity::getNavigationMapId, location.getNavigationMapId());
            }
        }

        wrapper.orderByDesc(LocationEntity::getCreateTime);

        Page<LocationEntity> page = this.page(
            new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
            wrapper
        );

        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<LocationDTO> selectPageDTO(LocationEntity location, PageQuery pageQuery) {
        LambdaQueryWrapper<LocationEntity> wrapper = new LambdaQueryWrapper<>();

        if (location != null) {
            if (StringUtils.hasText(location.getName())) {
                wrapper.like(LocationEntity::getName, location.getName());
            }
            if (location.getLocationTypeId() != null) {
                wrapper.eq(LocationEntity::getLocationTypeId, location.getLocationTypeId());
            }
            if (location.getNavigationMapId() != null) {
                wrapper.eq(LocationEntity::getNavigationMapId, location.getNavigationMapId());
            }
        }

        wrapper.orderByDesc(LocationEntity::getCreateTime);

        Page<LocationEntity> page = this.page(
            new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
            wrapper
        );

        // Convert to DTO
        List<LocationDTO> dtoList = DTOConverter.toLocationDTOList(page.getRecords());
        TableDataInfo<LocationDTO> result = TableDataInfo.build();
        result.setRows(dtoList);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public TableDataInfo<LocationDTO> selectPageDTO(LocationDTO location, PageQuery pageQuery) {
        return selectPageDTO(toEntity(location), pageQuery);
    }

    @Override
    public boolean saveDTO(LocationDTO location) {
        return this.save(toEntity(location));
    }

    @Override
    public List<LocationEntity> selectByNavigationMapId(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<LocationEntity>()
                .eq(LocationEntity::getNavigationMapId, navigationMapId)
                .orderByAsc(LocationEntity::getName));
    }

    @Override
    public List<LocationDTO> selectByNavigationMapIdDTO(Long navigationMapId) {
        return DTOConverter.toLocationDTOList(this.selectByNavigationMapId(navigationMapId));
    }

    @Override
    public List<LocationEntity> selectByMapIds(List<Long> mapIds) {
        if (mapIds == null || mapIds.isEmpty()) {
            return List.of();
        }
        return this.list(new LambdaQueryWrapper<LocationEntity>()
                .in(LocationEntity::getNavigationMapId, mapIds)
                .orderByAsc(LocationEntity::getName));
    }

    @Override
    public List<LocationDTO> selectByMapIdsDTO(List<Long> mapIds) {
        return DTOConverter.toLocationDTOList(this.selectByMapIds(mapIds));
    }

    @Override
    public LocationEntity selectById(Long id) {
        return this.getById(id);
    }

    @Override
    public LocationDTO selectByIdDTO(Long id) {
        return DTOConverter.toLocationDTO(this.getById(id));
    }

    @Override
    public boolean updateByIdDTO(LocationDTO location) {
        return this.updateById(toEntity(location));
    }

    @Override
    public List<LocationEntity> selectAllLocationByPlantModelId(Long plantModelId) {
        return this.list(new LambdaQueryWrapper<LocationEntity>()
                .eq(LocationEntity::getNavigationMapId, plantModelId)
        );
    }

    @Override
    public int removeByMap(Long navigationMapId) {
        return this.baseMapper.delete(new LambdaQueryWrapper<LocationEntity>()
                .eq(LocationEntity::getNavigationMapId, navigationMapId));
    }

    private LocationEntity toEntity(LocationDTO dto) {
        LocationEntity entity = new LocationEntity();
        entity.setId(dto.getId());
        entity.setNavigationMapId(dto.getNavigationMapId());
        entity.setLayerId(dto.getLayerId());
        entity.setLocationTypeId(dto.getLocationTypeId());
        entity.setLocationId(dto.getLocationId());
        entity.setName(dto.getName());
        entity.setXPosition(dto.getXPosition());
        entity.setYPosition(dto.getYPosition());
        entity.setZPosition(dto.getZPosition());
        entity.setLocked(dto.getLocked());
        entity.setIsOccupied(dto.getIsOccupied());
        entity.setProperties(dto.getProperties());
        normalizeLocationLayout(dto, entity);
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }

    private void normalizeLocationLayout(LocationDTO dto, LocationEntity entity) {
        Map<String, Object> layout = parseLayout(dto.getLayout());
        if (entity.getLayerId() == null) {
            entity.setLayerId(toLong(layout.get("layerId")));
        }
        if (entity.getXPosition() == null) {
            entity.setXPosition(toDecimal(layout.get("x")));
        }
        if (entity.getYPosition() == null) {
            entity.setYPosition(toDecimal(layout.get("y")));
        }
        if (entity.getZPosition() == null) {
            entity.setZPosition(toDecimal(layout.get("z")));
        }

        if (dto.getLayout() != null && !dto.getLayout().isBlank()) {
            entity.setLayout(dto.getLayout());
            return;
        }

        Map<String, Object> canonical = new LinkedHashMap<>();
        canonical.put("layerId", entity.getLayerId());
        canonical.put("x", entity.getXPosition());
        canonical.put("y", entity.getYPosition());
        canonical.put("z", entity.getZPosition());
        entity.setLayout(writeLayout(canonical));
    }

    private Map<String, Object> parseLayout(String raw) {
        if (raw == null || raw.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(raw, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    private String writeLayout(Map<String, Object> layout) {
        try {
            return objectMapper.writeValueAsString(layout);
        } catch (Exception e) {
            return "{}";
        }
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (Exception ignored) {
            return null;
        }
    }

    private BigDecimal toDecimal(Object value) {
        if (value == null) return null;
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (Exception ignored) {
            return null;
        }
    }
}
