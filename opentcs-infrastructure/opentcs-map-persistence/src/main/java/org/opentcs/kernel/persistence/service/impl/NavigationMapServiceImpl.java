package org.opentcs.kernel.persistence.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.persistence.service.DTOConverter;
import org.opentcs.kernel.persistence.entity.*;
import org.opentcs.kernel.persistence.mapper.*;
import org.opentcs.kernel.persistence.service.NavigationMapDomainService;
import org.opentcs.kernel.persistence.service.LayerDomainService;
import org.opentcs.kernel.persistence.service.LayerGroupDomainService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 导航地图领域服务实现
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "navigationMap")
public class NavigationMapServiceImpl extends ServiceImpl<NavigationMapMapper, NavigationMapEntity>
        implements NavigationMapDomainService {

    private final LayerDomainService layerDomainService;
    private final LayerGroupDomainService layerGroupDomainService;

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean createNavigationMap(NavigationMapEntity navigationMap) {
        long count = this.count(new LambdaQueryWrapper<NavigationMapEntity>()
                .eq(NavigationMapEntity::getFactoryModelId, navigationMap.getFactoryModelId())
                .eq(NavigationMapEntity::getMapId, navigationMap.getMapId())
                .eq(NavigationMapEntity::getDelFlag, "0"));
        if (count > 0) {
            throw new RuntimeException("地图标识已存在");
        }

        if (navigationMap.getMapId() == null || navigationMap.getMapId().isEmpty()) {
            navigationMap.setMapId(IdUtil.fastSimpleUUID());
        }

        boolean saved = this.save(navigationMap);

        if (saved) {
            LayerGroupEntity layerGroup = new LayerGroupEntity();
            layerGroup.setNavigationMapId(navigationMap.getId());
            layerGroup.setName("默认图层组");
            layerGroupDomainService.save(layerGroup);

            LayerEntity layer = new LayerEntity();
            layer.setNavigationMapId(navigationMap.getId());
            layer.setLayerGroupId(layerGroup.getId());
            layer.setName("默认图层");
            layer.setVisible(true);
            layerDomainService.save(layer);
        }

        return saved;
    }

    @Override
    public TableDataInfo<NavigationMapDTO> selectPageNavigationMap(NavigationMapEntity navigationMap, PageQuery pageQuery) {
        IPage<NavigationMapDTO> page = this.getBaseMapper().selectPageNavigationMap(
                pageQuery.build(), navigationMap);
        return TableDataInfo.build(page);
    }

    @Override
    public List<NavigationMapDTO> selectByFactoryModelId(Long factoryModelId) {
        return this.getBaseMapper().selectByFactoryModelId(factoryModelId);
    }

    @Override
    public NavigationMapDTO selectByFactoryModelIdAndFloor(Long factoryModelId, Integer floorNumber) {
        return this.getBaseMapper().selectByFactoryModelIdAndFloor(factoryModelId, floorNumber);
    }

    @Override
    public NavigationMapDTO selectByMapId(String mapId) {
        return this.getBaseMapper().selectByMapId(mapId);
    }

    @Override
    @Cacheable(key = "#id")
    public NavigationMapEntity getNavigationMapDetail(Long id) {
        return this.getById(id);
    }

    @Override
    public NavigationMapDTO getNavigationMapDetailDTO(Long id) {
        return DTOConverter.toNavigationMapDTO(this.getById(id));
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateNavigationMap(NavigationMapEntity navigationMap) {
        return this.updateById(navigationMap);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateNavigationMapDTO(NavigationMapDTO navigationMap) {
        NavigationMapEntity entity = new NavigationMapEntity();
        entity.setId(navigationMap.getId());
        entity.setFactoryModelId(navigationMap.getFactoryModelId());
        entity.setMapId(navigationMap.getMapId());
        entity.setName(navigationMap.getName());
        entity.setFloorNumber(navigationMap.getFloorNumber());
        entity.setVehicleTypeId(navigationMap.getVehicleTypeId());
        entity.setOriginX(navigationMap.getOriginX());
        entity.setOriginY(navigationMap.getOriginY());
        entity.setRotation(navigationMap.getRotation());
        entity.setProperties(navigationMap.getProperties());
        entity.setStatus(navigationMap.getStatus());
        entity.setMapVersion(navigationMap.getMapVersion());
        entity.setRasterUrl(navigationMap.getRasterUrl());
        entity.setRasterVersion(navigationMap.getRasterVersion());
        entity.setRasterWidth(navigationMap.getRasterWidth());
        entity.setRasterHeight(navigationMap.getRasterHeight());
        entity.setRasterResolution(navigationMap.getRasterResolution());
        entity.setYamlOrigin(navigationMap.getYamlOrigin());
        entity.setYamlUrl(navigationMap.getYamlUrl());
        entity.setMapOrigin(navigationMap.getMapOrigin());
        entity.setCreateTime(navigationMap.getCreateTime());
        entity.setUpdateTime(navigationMap.getUpdateTime());
        return this.updateById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean deleteNavigationMap(Long id) {
        return this.removeById(id);
    }
}
