package org.opentcs.kernel.persistence.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.FactoryModelEntity;
import org.opentcs.kernel.persistence.entity.NavigationMapEntity;
import org.opentcs.kernel.persistence.mapper.FactoryModelMapper;
import org.opentcs.kernel.persistence.service.FactoryModelDomainService;
import org.opentcs.kernel.persistence.service.NavigationMapDomainService;
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
        implements FactoryModelDomainService {

    private final NavigationMapDomainService navigationMapDomainService;

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
            List<NavigationMapEntity> maps = navigationMapDomainService.selectByFactoryModelId(id);
            factoryModel.setParams(java.util.Map.of("maps", maps));
        }
        return factoryModel;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateFactoryModel(FactoryModelEntity factoryModel) {
        return this.updateById(factoryModel);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean deleteFactoryModel(Long id) {
        return this.removeById(id);
    }
}
