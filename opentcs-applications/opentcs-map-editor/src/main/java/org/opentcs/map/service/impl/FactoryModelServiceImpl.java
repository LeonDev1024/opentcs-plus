package org.opentcs.map.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.FactoryModelEntity;
import org.opentcs.kernel.persistence.entity.NavigationMapEntity;
import org.opentcs.map.mapper.FactoryModelMapper;
import org.opentcs.map.service.FactoryModelService;
import org.opentcs.map.service.NavigationMapService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工厂模型 Service 实现类
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "factoryModel")
public class FactoryModelServiceImpl extends ServiceImpl<FactoryModelMapper, FactoryModelEntity>
        implements FactoryModelService {

    private final NavigationMapService navigationMapService;

    @Override
    @CacheEvict(allEntries = true)
    public boolean createFactoryModel(FactoryModelEntity factoryModel) {
        // 校验工厂名称是否存在
        long count = this.count(new LambdaQueryWrapper<FactoryModelEntity>()
                .eq(FactoryModelEntity::getName, factoryModel.getName())
                .eq(FactoryModelEntity::getDelFlag, "0"));
        if (count > 0) {
            throw new RuntimeException("工厂名称已存在");
        }

        // 生成唯一标识符
        factoryModel.setFactoryId(IdUtil.fastSimpleUUID());
        factoryModel.setModelVersion("1.0");

        return this.save(factoryModel);
    }

    @Override
    public TableDataInfo<FactoryModelEntity> selectPageFactoryModel(FactoryModelEntity factoryModel,
                                                                    PageQuery pageQuery) {
        IPage<FactoryModelEntity> page = this.getBaseMapper().selectPageFactoryModel(
                pageQuery.build(), factoryModel);
        return TableDataInfo.build(page);
    }

    @Override
    @Cacheable(key = "#id")
    public FactoryModelEntity getFactoryModelDetail(Long id) {
        FactoryModelEntity factoryModel = this.getById(id);
        if (factoryModel != null) {
            // 获取关联的导航地图列表
            List<NavigationMapEntity> maps = navigationMapService.selectByFactoryModelId(id);
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
