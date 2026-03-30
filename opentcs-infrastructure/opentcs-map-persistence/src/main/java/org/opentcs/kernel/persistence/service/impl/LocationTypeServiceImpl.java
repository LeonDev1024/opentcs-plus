package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.LocationTypeDTO;
import org.opentcs.kernel.persistence.entity.LocationTypeEntity;
import org.opentcs.kernel.persistence.mapper.LocationTypeMapper;
import org.opentcs.kernel.persistence.service.LocationTypeDomainService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 位置类型领域服务实现
 */
@Service
public class LocationTypeServiceImpl extends ServiceImpl<LocationTypeMapper, LocationTypeEntity> implements LocationTypeDomainService {

    @Override
    public TableDataInfo<LocationTypeEntity> selectPage(LocationTypeEntity locationType, PageQuery pageQuery) {
        LambdaQueryWrapper<LocationTypeEntity> lqw = new LambdaQueryWrapper<>();
        if (locationType != null && locationType.getName() != null && !locationType.getName().isEmpty()) {
            lqw.like(LocationTypeEntity::getName, locationType.getName());
        }
        Page<LocationTypeEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = this.page(page, lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<LocationTypeDTO> selectPageDTO(LocationTypeEntity locationType, PageQuery pageQuery) {
        LambdaQueryWrapper<LocationTypeEntity> lqw = new LambdaQueryWrapper<>();
        if (locationType != null && locationType.getName() != null && !locationType.getName().isEmpty()) {
            lqw.like(LocationTypeEntity::getName, locationType.getName());
        }
        Page<LocationTypeEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = this.page(page, lqw);

        // Convert to DTO
        List<LocationTypeDTO> dtoList = convertToDTOList(page.getRecords());
        TableDataInfo<LocationTypeDTO> result = TableDataInfo.build();
        result.setRows(dtoList);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public TableDataInfo<LocationTypeDTO> selectPageDTO(LocationTypeDTO locationType, PageQuery pageQuery) {
        return selectPageDTO(toEntity(locationType), pageQuery);
    }

    @Override
    public List<LocationTypeEntity> selectAll() {
        return this.list();
    }

    @Override
    public List<LocationTypeDTO> listDTO() {
        return convertToDTOList(this.list());
    }

    @Override
    public LocationTypeEntity selectById(Long id) {
        return this.getById(id);
    }

    @Override
    public LocationTypeDTO getByIdDTO(Long id) {
        return convertToDTO(this.getById(id));
    }

    @Override
    public boolean saveDTO(LocationTypeDTO locationType) {
        return this.save(toEntity(locationType));
    }

    @Override
    public boolean updateByIdDTO(LocationTypeDTO locationType) {
        return this.updateById(toEntity(locationType));
    }

    /**
     * Entity转DTO
     */
    private LocationTypeDTO convertToDTO(LocationTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        LocationTypeDTO dto = new LocationTypeDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAllowedOperations(entity.getAllowedOperations());
        dto.setAllowedPeripheralOperations(entity.getAllowedPeripheralOperations());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    /**
     * Entity列表转DTO列表
     */
    private List<LocationTypeDTO> convertToDTOList(List<LocationTypeEntity> entities) {
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LocationTypeEntity toEntity(LocationTypeDTO dto) {
        if (dto == null) {
            return null;
        }
        LocationTypeEntity entity = new LocationTypeEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setAllowedOperations(dto.getAllowedOperations());
        entity.setAllowedPeripheralOperations(dto.getAllowedPeripheralOperations());
        entity.setProperties(dto.getProperties());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }
}
