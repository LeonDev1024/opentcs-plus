package org.opentcs.map.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.*;
import org.opentcs.map.mapper.NavigationMapMapper;
import org.opentcs.map.service.NavigationMapService;
import org.opentcs.map.service.PointService;
import org.opentcs.map.service.PathService;
import org.opentcs.map.service.LayerService;
import org.opentcs.map.service.LayerGroupService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导航地图 Service 实现类
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "navigationMap")
public class NavigationMapServiceImpl extends ServiceImpl<NavigationMapMapper, NavigationMapEntity>
        implements NavigationMapService {

    private final PointService pointService;
    private final PathService pathService;
    private final LayerService layerService;
    private final LayerGroupService layerGroupService;

    @Override
    @CacheEvict(allEntries = true)
    public boolean createNavigationMap(NavigationMapEntity navigationMap) {
        // 校验地图标识是否已存在
        long count = this.count(new LambdaQueryWrapper<NavigationMapEntity>()
                .eq(NavigationMapEntity::getFactoryModelId, navigationMap.getFactoryModelId())
                .eq(NavigationMapEntity::getMapId, navigationMap.getMapId())
                .eq(NavigationMapEntity::getDelFlag, "0"));
        if (count > 0) {
            throw new RuntimeException("地图标识已存在");
        }

        // 生成唯一标识符
        if (navigationMap.getMapId() == null || navigationMap.getMapId().isEmpty()) {
            navigationMap.setMapId(IdUtil.fastSimpleUUID());
        }

        boolean saved = this.save(navigationMap);

        if (saved) {
            // 创建默认的图层组和图层
            LayerGroupEntity layerGroup = new LayerGroupEntity();
            layerGroup.setNavigationMapId(navigationMap.getId());
            layerGroup.setName("默认图层组");
            layerGroupService.save(layerGroup);

            LayerEntity layer = new LayerEntity();
            layer.setNavigationMapId(navigationMap.getId());
            layer.setLayerGroupId(layerGroup.getId());
            layer.setName("默认图层");
            layer.setVisible(true);
            layerService.save(layer);
        }

        return saved;
    }

    @Override
    public TableDataInfo<NavigationMapEntity> selectPageNavigationMap(NavigationMapEntity navigationMap,
                                                                       PageQuery pageQuery) {
        IPage<NavigationMapEntity> page = this.getBaseMapper().selectPageNavigationMap(
                pageQuery.build(), navigationMap);
        return TableDataInfo.build(page);
    }

    @Override
    public List<NavigationMapEntity> selectByFactoryModelId(Long factoryModelId) {
        return this.getBaseMapper().selectByFactoryModelId(factoryModelId);
    }

    @Override
    public NavigationMapEntity selectByFactoryModelIdAndFloor(Long factoryModelId, Integer floorNumber) {
        return this.getBaseMapper().selectByFactoryModelIdAndFloor(factoryModelId, floorNumber);
    }

    @Override
    @Cacheable(key = "#id")
    public NavigationMapEntity getNavigationMapDetail(Long id) {
        NavigationMapEntity navigationMap = this.getById(id);
        if (navigationMap != null) {
            // 获取关联的点位
            List<PointEntity> points = pointService.list(new LambdaQueryWrapper<PointEntity>()
                    .eq(PointEntity::getNavigationMapId, id)
                    .eq(PointEntity::getDelFlag, "0"));

            // 获取关联的路径
            List<PathEntity> paths = pathService.list(new LambdaQueryWrapper<PathEntity>()
                    .eq(PathEntity::getNavigationMapId, id)
                    .eq(PathEntity::getDelFlag, "0"));

            // 获取关联的图层
            List<LayerEntity> layers = layerService.list(new LambdaQueryWrapper<LayerEntity>()
                    .eq(LayerEntity::getNavigationMapId, id)
                    .eq(LayerEntity::getDelFlag, "0"));

            Map<String, Object> details = new HashMap<>();
            details.put("points", points);
            details.put("paths", paths);
            details.put("layers", layers);
            navigationMap.setParams(details);
        }
        return navigationMap;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateNavigationMap(NavigationMapEntity navigationMap) {
        return this.updateById(navigationMap);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean deleteNavigationMap(Long id) {
        return this.removeById(id);
    }
}
