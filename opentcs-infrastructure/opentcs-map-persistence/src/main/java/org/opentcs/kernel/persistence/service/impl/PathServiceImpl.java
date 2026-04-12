package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.persistence.service.DTOConverter;
import org.opentcs.kernel.persistence.entity.PathEntity;
import org.opentcs.kernel.persistence.mapper.PathMapper;
import org.opentcs.kernel.persistence.service.PathRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 路径领域服务实现
 */
@Service
@RequiredArgsConstructor
public class PathServiceImpl extends ServiceImpl<PathMapper, PathEntity> implements PathRepository {

    private final ObjectMapper objectMapper;

    @Override
    public TableDataInfo<PathEntity> selectPagePath(PathEntity path, PageQuery pageQuery) {
        return this.getBaseMapper().selectPagePath(path, pageQuery);
    }

    @Override
    public TableDataInfo<PathDTO> selectPageDTO(PathEntity path, PageQuery pageQuery) {
        TableDataInfo<PathEntity> entityResult = this.getBaseMapper().selectPagePath(path, pageQuery);
        List<PathDTO> dtoList = DTOConverter.toPathDTOList(entityResult.getRows());

        TableDataInfo<PathDTO> result = TableDataInfo.build();
        result.setRows(dtoList);
        result.setTotal(entityResult.getTotal());
        return result;
    }

    @Override
    public TableDataInfo<PathDTO> selectPageDTO(PathDTO path, PageQuery pageQuery) {
        return selectPageDTO(toEntity(path), pageQuery);
    }

    @Override
    public boolean saveDTO(PathDTO path) {
        return this.save(toEntity(path));
    }


    @Override
    public List<PathEntity> listByMap(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<PathEntity>()
                .eq(PathEntity::getNavigationMapId, navigationMapId)
                .eq(PathEntity::getDelFlag, "0")
        );
    }

    @Override
    public List<PathDTO> listByMapDTO(Long navigationMapId) {
        List<PathEntity> entities = this.listByMap(navigationMapId);
        entities.forEach(this::hydratePathLayout);
        return DTOConverter.toPathDTOList(entities);
    }

    @Override
    public List<PathEntity> listByMapIds(List<Long> mapIds) {
        if (mapIds == null || mapIds.isEmpty()) {
            return List.of();
        }
        return this.list(new LambdaQueryWrapper<PathEntity>()
                .in(PathEntity::getNavigationMapId, mapIds)
                .eq(PathEntity::getDelFlag, "0")
                .orderByAsc(PathEntity::getName));
    }

    @Override
    public List<PathDTO> listByMapIdsDTO(List<Long> mapIds) {
        List<PathEntity> entities = this.listByMapIds(mapIds);
        entities.forEach(this::hydratePathLayout);
        return DTOConverter.toPathDTOList(entities);
    }

    @Override
    public PathDTO getByIdDTO(Long id) {
        PathEntity entity = this.getById(id);
        hydratePathLayout(entity);
        return DTOConverter.toPathDTO(entity);
    }

    @Override
    public boolean updateByIdDTO(PathDTO path) {
        return this.updateById(toEntity(path));
    }

    @Override
    public int removeByMap(Long navigationMapId) {
        return this.baseMapper.delete(new LambdaQueryWrapper<PathEntity>()
                .eq(PathEntity::getNavigationMapId, navigationMapId));
    }

    private PathEntity toEntity(PathDTO dto) {
        PathEntity entity = new PathEntity();
        entity.setId(dto.getId());
        entity.setNavigationMapId(dto.getNavigationMapId());
        entity.setLayerId(dto.getLayerId());
        entity.setPathId(dto.getPathId());
        entity.setName(dto.getName());
        entity.setSourcePointId(dto.getSourcePointId());
        entity.setDestPointId(dto.getDestPointId());
        entity.setLength(dto.getLength());
        entity.setMaxVelocity(dto.getMaxVelocity());
        entity.setMaxReverseVelocity(dto.getMaxReverseVelocity());
        entity.setLocked(dto.getLocked());
        entity.setIsBlocked(dto.getIsBlocked());
        entity.setProperties(dto.getProperties());
        entity.setLayout(dto.getLayout());
        // 从 layout 回填关键派生字段（兼容仅传 layout 的场景）
        PathLayoutPersist parsed = parseLayout(dto.getLayout());
        if (entity.getLayerId() == null && parsed.layerId != null) {
            entity.setLayerId(parsed.layerId);
        }
        if ((entity.getLayout() == null || entity.getLayout().isBlank()) && parsed.controlPoints != null) {
            try {
                PathLayoutPersist persist = new PathLayoutPersist();
                persist.layerId = entity.getLayerId();
                persist.connectionType = parsed.connectionType != null ? parsed.connectionType : "DIRECT";
                persist.controlPoints = parsed.controlPoints;
                entity.setLayout(objectMapper.writeValueAsString(persist));
            } catch (Exception e) {
                throw new RuntimeException("序列化 path.layout 失败, pathId=" + dto.getPathId(), e);
            }
        }
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }

    private static class PathLayoutPersist {
        public Long layerId;
        public String connectionType;
        public List<java.util.Map<String, Object>> controlPoints;
    }

    private PathLayoutPersist parseLayout(String layoutJson) {
        if (layoutJson == null || layoutJson.isBlank()) {
            return new PathLayoutPersist();
        }
        try {
            if (layoutJson.trim().startsWith("[")) {
                PathLayoutPersist persist = new PathLayoutPersist();
                persist.controlPoints = objectMapper.readValue(
                    layoutJson,
                    new TypeReference<List<java.util.Map<String, Object>>>() {
                    }
                );
                return persist;
            }
            return objectMapper.readValue(layoutJson, PathLayoutPersist.class);
        } catch (Exception ignored) {
            return new PathLayoutPersist();
        }
    }

    private void hydratePathLayout(PathEntity path) {
        if (path == null) {
            return;
        }
        if (path.getLayout() == null || path.getLayout().isBlank()) {
            return;
        }
        try {
            String layoutJson = path.getLayout().trim();
            if (layoutJson.startsWith("[")) {
                List<java.util.Map<String, Object>> controlPoints = objectMapper.readValue(
                    layoutJson,
                    new TypeReference<List<java.util.Map<String, Object>>>() {
                    }
                );
                PathLayoutPersist persist = new PathLayoutPersist();
                persist.layerId = path.getLayerId();
                persist.connectionType = "DIRECT";
                persist.controlPoints = controlPoints;
                path.setLayout(objectMapper.writeValueAsString(persist));
            } else {
                PathLayoutPersist persist = objectMapper.readValue(layoutJson, PathLayoutPersist.class);
                if (persist.layerId != null) {
                    path.setLayerId(persist.layerId);
                }
            }
        } catch (Exception ignored) {
            // ignore malformed legacy layout payload
        }
    }
}
