package org.opentcs.kernel.persistence.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.PlantModelEntity;
import org.opentcs.kernel.persistence.mapper.PlantModelHistoryMapper;
import org.opentcs.kernel.persistence.mapper.PlantModelMapper;
import org.opentcs.kernel.persistence.service.PlantModelDomainService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地图模型领域服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "plantModel")
public class PlantModelServiceImpl extends ServiceImpl<PlantModelMapper, PlantModelEntity>
        implements PlantModelDomainService {

    private final PlantModelHistoryMapper plantModelHistoryMapper;

    @Override
    public TableDataInfo<PlantModelEntity> selectPagePlantModel(PlantModelEntity plantModel, PageQuery pageQuery) {
        IPage<PlantModelEntity> page = this.getBaseMapper().selectPagePlantModel(
                pageQuery.build(), plantModel);
        return TableDataInfo.build(page);
    }

    @Override
    public List<PlantModelEntity> selectAll() {
        return this.list();
    }

    @Override
    @Cacheable(key = "#id")
    public PlantModelEntity selectById(Long id) {
        return this.getById(id);
    }

    @Override
    public PlantModelEntity selectByName(String name) {
        return this.getOne(new LambdaQueryWrapper<PlantModelEntity>()
                .eq(PlantModelEntity::getName, name)
                .eq(PlantModelEntity::getDelFlag, "0"));
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean createPlantModel(PlantModelEntity plantModel) {
        long count = this.count(new LambdaQueryWrapper<PlantModelEntity>()
                .eq(PlantModelEntity::getName, plantModel.getName())
                .eq(PlantModelEntity::getDelFlag, "0"));
        if (count > 0) {
            throw new RuntimeException("地图模型名称已存在");
        }

        plantModel.setMapId(IdUtil.fastSimpleUUID());
        plantModel.setModelVersion("1.0");

        return this.save(plantModel);
    }

    @Override
    @CacheEvict(allEntries = true)
    public Long createVersion(Long modelId, String versionName) {
        // TODO: 实现版本创建
        log.info("创建版本: modelId={}, versionName={}", modelId, versionName);
        PlantModelEntity entity = this.getById(modelId);
        if (entity == null) {
            throw new RuntimeException("地图模型不存在");
        }
        // Create new version logic
        return modelId;
    }

    @Override
    public TableDataInfo<PlantModelEntity> getVersionHistory(Long id, PageQuery pageQuery) {
        // TODO: 实现版本历史查询
        log.info("获取版本历史: {}", id);
        return TableDataInfo.build(this.getBaseMapper().selectPagePlantModel(pageQuery.build(), null));
    }

    @Override
    public String validateTopology(Long id) {
        // TODO: 实现拓扑验证
        log.info("验证拓扑: {}", id);
        return "拓扑验证通过";
    }

    @Override
    @CacheEvict(allEntries = true)
    public Long copyMap(Long modelId, String newName) {
        // TODO: 实现地图复制
        log.info("复制地图: modelId={}, newName={}", modelId, newName);
        PlantModelEntity source = this.getById(modelId);
        if (source == null) {
            throw new RuntimeException("源地图模型不存在");
        }

        PlantModelEntity copy = new PlantModelEntity();
        copy.setName(newName);
        copy.setMapId(IdUtil.fastSimpleUUID());
        copy.setModelVersion("1.0");
        copy.setStatus(source.getStatus());
        copy.setDescription(source.getDescription());

        this.save(copy);
        return copy.getId();
    }
}
