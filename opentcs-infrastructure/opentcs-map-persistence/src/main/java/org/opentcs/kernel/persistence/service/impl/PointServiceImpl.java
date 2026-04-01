package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.kernel.persistence.entity.PointEntity;
import org.opentcs.kernel.persistence.mapper.PointMapper;
import org.opentcs.kernel.persistence.service.DTOConverter;
import org.opentcs.kernel.persistence.service.PointDomainService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 点位领域服务实现
 */
@Service
@RequiredArgsConstructor
public class PointServiceImpl extends ServiceImpl<PointMapper, PointEntity> implements PointDomainService {

    private final ObjectMapper objectMapper;

    @Override
    public TableDataInfo<PointEntity> selectPagePoint(PointEntity point, PageQuery pageQuery) {
        return this.getBaseMapper().selectPagePoint(point, pageQuery);
    }

    @Override
    public boolean saveDTO(PointDTO point) {
        return this.save(toEntity(point));
    }


    @Override
    public List<PointEntity> listByMap(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<PointEntity>()
                .eq(PointEntity::getNavigationMapId, navigationMapId)
                .eq(PointEntity::getDelFlag, "0")
        );
    }

    @Override
    public List<PointDTO> listByMapDTO(Long navigationMapId) {
        return DTOConverter.toPointDTOList(this.listByMap(navigationMapId));
    }

    @Override
    public List<PointEntity> listByMapIds(List<Long> mapIds) {
        if (mapIds == null || mapIds.isEmpty()) {
            return List.of();
        }
        return this.list(new LambdaQueryWrapper<PointEntity>()
                .in(PointEntity::getNavigationMapId, mapIds)
                .eq(PointEntity::getDelFlag, "0")
                .orderByAsc(PointEntity::getName));
    }

    @Override
    public int removeByMap(Long navigationMapId) {
        return this.baseMapper.delete(new LambdaQueryWrapper<PointEntity>()
                .eq(PointEntity::getNavigationMapId, navigationMapId));
    }

    @Override
    public boolean updateByIdDTO(PointDTO point) {
        return this.updateById(toEntity(point));
    }

    private PointEntity toEntity(PointDTO dto) {
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
        normalizePointLayout(dto, entity);
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }

    private void normalizePointLayout(PointDTO dto, PointEntity entity) {
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

        Object editorProps = layout.get("editorProps");
        if (entity.getRadius() == null) {
            entity.setRadius(toDecimal(layout.get("radius")));
            if (entity.getRadius() == null && editorProps instanceof Map<?, ?> editor) {
                entity.setRadius(toDecimal(editor.get("radius")));
            }
        }
        if (entity.getLabel() == null && editorProps instanceof Map<?, ?> editor) {
            Object label = editor.get("label");
            if (label != null) {
                entity.setLabel(String.valueOf(label));
            }
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
        Map<String, Object> canonicalEditor = new LinkedHashMap<>();
        canonicalEditor.put("radius", entity.getRadius());
        canonicalEditor.put("label", entity.getLabel());
        canonical.put("editorProps", canonicalEditor);
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
