package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.bo.VisualLayoutBO;
import org.opentcs.kernel.persistence.entity.LayerEntity;
import org.opentcs.kernel.persistence.entity.LayerGroupEntity;
import org.opentcs.kernel.persistence.entity.VisualLayoutEntity;
import org.opentcs.map.mapper.VisualLayoutMapper;
import org.opentcs.map.service.LayerGroupService;
import org.opentcs.map.service.LayerService;
import org.opentcs.map.service.VisualLayoutService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 视觉布局 Service 实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VisualLayoutServiceImpl extends ServiceImpl<VisualLayoutMapper, VisualLayoutEntity> implements VisualLayoutService {

    private final LayerGroupService layerGroupService;
    private final LayerService layerService;

    @Override
    public VisualLayoutBO getVisualLayoutByPlantModelId(Long plantModelId) {
        VisualLayoutBO visualLayoutBO = new VisualLayoutBO();
        VisualLayoutEntity visualLayout = this.getOne(new LambdaQueryWrapper<VisualLayoutEntity>()
                .eq(VisualLayoutEntity::getPlantModelId, plantModelId));
        if (visualLayout == null) {
            return visualLayoutBO;
        }
        visualLayoutBO.setVisualLayoutId(visualLayout.getId());
        visualLayoutBO.setName(visualLayout.getName());
        visualLayoutBO.setScaleX(visualLayout.getScaleX());
        visualLayoutBO.setScaleY(visualLayout.getScaleY());

        List<LayerGroupEntity> layerGroups = layerGroupService.list(new LambdaQueryWrapper<LayerGroupEntity>()
                .eq(LayerGroupEntity::getVisualLayoutId, visualLayout.getId()));
        visualLayoutBO.setLayerGroups(layerGroups);

        List<LayerEntity> layers = layerService.list(new LambdaQueryWrapper<LayerEntity>()
                .eq(LayerEntity::getVisualLayoutId, visualLayout.getId()));
        visualLayoutBO.setLayers(layers);
        return visualLayoutBO;
    }

    @Override
    public TableDataInfo<VisualLayoutEntity> selectPageVisualLayout(VisualLayoutEntity visualLayout, PageQuery pageQuery) {
        IPage<VisualLayoutEntity> page = this.getBaseMapper().selectPageVisualLayout(pageQuery.build(), visualLayout);
        return TableDataInfo.build(page);
    }
}