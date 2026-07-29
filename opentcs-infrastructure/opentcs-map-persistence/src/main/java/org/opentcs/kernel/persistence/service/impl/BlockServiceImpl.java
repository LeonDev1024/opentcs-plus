package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.kernel.persistence.entity.BlockEntity;
import org.opentcs.kernel.persistence.mapper.BlockMapper;
import org.opentcs.kernel.persistence.service.BlockRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Block 持久化实现。
 */
@Service
@RequiredArgsConstructor
public class BlockServiceImpl extends ServiceImpl<BlockMapper, BlockEntity> implements BlockRepository {

    private final ObjectMapper objectMapper;

    @Override
    public List<BlockDTO> listByMapDTO(Long navigationMapId) {
        if (navigationMapId == null) {
            return List.of();
        }
        List<BlockEntity> entities = this.list(new LambdaQueryWrapper<BlockEntity>()
                .eq(BlockEntity::getNavigationMapId, navigationMapId));
        return entities.stream().map(this::toDTO).toList();
    }

    @Override
    public boolean saveDTO(BlockDTO block) {
        return this.save(toEntity(block));
    }

    @Override
    public int removeByMap(Long navigationMapId) {
        return this.baseMapper.delete(new LambdaQueryWrapper<BlockEntity>()
                .eq(BlockEntity::getNavigationMapId, navigationMapId));
    }

    private BlockEntity toEntity(BlockDTO dto) {
        BlockEntity entity = new BlockEntity();
        entity.setId(dto.getId());
        entity.setNavigationMapId(dto.getNavigationMapId());
        entity.setFactoryModelId(dto.getFactoryModelId());
        entity.setBlockId(dto.getBlockId());
        entity.setName(dto.getName());
        entity.setType(dto.getType() != null ? dto.getType() : "SINGLE_VEHICLE_ONLY");
        entity.setColor(dto.getColor() != null ? dto.getColor() : "#F44336");
        entity.setMembers(writeJson(dto.getMembers() != null ? dto.getMembers() : List.of()));
        entity.setProperties(writeJson(dto.getProperties() != null ? dto.getProperties() : Map.of()));
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }

    private BlockDTO toDTO(BlockEntity entity) {
        BlockDTO dto = new BlockDTO();
        dto.setId(entity.getId());
        dto.setNavigationMapId(entity.getNavigationMapId());
        dto.setFactoryModelId(entity.getFactoryModelId());
        dto.setBlockId(entity.getBlockId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setColor(entity.getColor());
        dto.setMembers(readStringList(entity.getMembers()));
        dto.setProperties(readStringMap(entity.getProperties()));
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("序列化 Block JSON 失败", e);
        }
    }

    private List<String> readStringList(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private Map<String, String> readStringMap(String json) {
        if (json == null || json.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            Map<String, String> map = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {
            });
            return map != null ? map : new LinkedHashMap<>();
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }
}
