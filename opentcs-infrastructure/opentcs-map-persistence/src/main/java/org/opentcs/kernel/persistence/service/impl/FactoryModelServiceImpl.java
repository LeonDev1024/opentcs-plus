package org.opentcs.kernel.persistence.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.FactoryModelDTO;
import org.opentcs.kernel.persistence.service.DTOConverter;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.persistence.entity.FactoryModelEntity;
import org.opentcs.kernel.persistence.entity.NavigationMapEntity;
import org.opentcs.kernel.persistence.mapper.FactoryModelMapper;
import org.opentcs.kernel.persistence.service.FactoryModelRepository;
import org.opentcs.kernel.persistence.service.NavigationMapRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工厂模型领域服务实现
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "factoryModel")
public class FactoryModelServiceImpl extends ServiceImpl<FactoryModelMapper, FactoryModelEntity>
        implements FactoryModelRepository {

    private final NavigationMapRepository navigationMapRepository;

    @Override
    @CacheEvict(allEntries = true)
    public boolean createFactoryModel(FactoryModelEntity factoryModel) {
        long count = this.count(new LambdaQueryWrapper<FactoryModelEntity>()
                .eq(FactoryModelEntity::getName, factoryModel.getName())
                .eq(FactoryModelEntity::getDelFlag, "0"));
        if (count > 0) {
            throw new RuntimeException("工厂名称已存在");
        }

        // 自动生成工厂编号（雪花算法）
        factoryModel.setFactoryId(String.valueOf(IdUtil.getSnowflakeNextId()));
        // 设置默认比例尺
        if (factoryModel.getScale() == null) {
            factoryModel.setScale(java.math.BigDecimal.ONE);
        }

        return this.save(factoryModel);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean createFactoryModelDTO(FactoryModelDTO factoryModel) {
        return createFactoryModel(toEntity(factoryModel));
    }

    @Override
    public TableDataInfo<FactoryModelEntity> selectPage(FactoryModelEntity factoryModel, PageQuery pageQuery) {
        IPage<FactoryModelEntity> page = this.getBaseMapper().selectPageFactoryModel(
                pageQuery.build(), factoryModel);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<FactoryModelEntity> selectPageFactoryModel(FactoryModelEntity factoryModel, PageQuery pageQuery) {
        return selectPage(factoryModel, pageQuery);
    }

    @Override
    public TableDataInfo<FactoryModelDTO> selectPageFactoryModelDTO(FactoryModelEntity factoryModel, PageQuery pageQuery) {
        IPage<FactoryModelEntity> page = this.getBaseMapper().selectPageFactoryModel(
                pageQuery.build(), factoryModel);

        List<FactoryModelDTO> dtoList = DTOConverter.toFactoryModelDTOList(page.getRecords());
        TableDataInfo<FactoryModelDTO> result = TableDataInfo.build();
        result.setRows(dtoList);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public TableDataInfo<FactoryModelDTO> selectPageFactoryModelDTO(FactoryModelDTO factoryModel, PageQuery pageQuery) {
        return selectPageFactoryModelDTO(toEntity(factoryModel), pageQuery);
    }

    @Override
    public List<FactoryModelEntity> selectAll() {
        return this.list();
    }

    @Override
    public FactoryModelEntity selectById(Long id) {
        return this.getById(id);
    }

    @Override
    public FactoryModelEntity selectByName(String name) {
        return this.getOne(new LambdaQueryWrapper<FactoryModelEntity>()
                .eq(FactoryModelEntity::getName, name)
                .eq(FactoryModelEntity::getDelFlag, "0"));
    }

    @Override
    @Cacheable(key = "#id")
    public FactoryModelEntity getFactoryModelDetail(Long id) {
        FactoryModelEntity factoryModel = this.getById(id);
        if (factoryModel != null) {
            List<NavigationMapDTO> maps = navigationMapRepository.selectByFactoryModelId(id);
            factoryModel.setParams(java.util.Map.of("maps", maps));
        }
        return factoryModel;
    }

    @Override
    public FactoryModelDTO getFactoryModelDetailDTO(Long id) {
        FactoryModelDTO dto = DTOConverter.toFactoryModelDTO(this.getById(id));
        if (dto != null) {
            List<NavigationMapDTO> maps = navigationMapRepository.selectByFactoryModelId(id);
            dto.setProperties(maps != null ? maps.toString() : null);
        }
        return dto;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateFactoryModel(FactoryModelEntity factoryModel) {
        return this.updateById(factoryModel);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateFactoryModelDTO(FactoryModelDTO factoryModel) {
        return updateFactoryModel(toEntity(factoryModel));
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean deleteFactoryModel(Long id) {
        return this.removeById(id);
    }

    private FactoryModelEntity toEntity(FactoryModelDTO dto) {
        if (dto == null) {
            return null;
        }
        FactoryModelEntity entity = new FactoryModelEntity();
        entity.setId(dto.getId());
        entity.setFactoryId(dto.getFactoryId());
        entity.setName(dto.getName());
        entity.setScale(dto.getScale());
        entity.setProperties(dto.getProperties());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }
}
