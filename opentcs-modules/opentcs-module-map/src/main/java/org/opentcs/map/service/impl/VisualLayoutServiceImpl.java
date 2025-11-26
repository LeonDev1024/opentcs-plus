package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.bo.VisualLayoutBO;
import org.opentcs.map.domain.entity.Layer;
import org.opentcs.map.domain.entity.LayerGroup;
import org.opentcs.map.domain.entity.VisualLayout;
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
public class VisualLayoutServiceImpl extends ServiceImpl<VisualLayoutMapper, VisualLayout> implements VisualLayoutService {

    private final LayerGroupService layerGroupService;
    private final LayerService layerService;

    @Override
    public VisualLayoutBO getVisualLayoutByPlantModelId(Long plantModelId) {
        VisualLayoutBO visualLayoutBO = new VisualLayoutBO();
        VisualLayout visualLayout = this.getOne(new LambdaQueryWrapper<VisualLayout>()
                .eq(VisualLayout::getPlantModelId, plantModelId));
        visualLayoutBO.setVisualLayout(visualLayout);

        List<LayerGroup> layerGroups = layerGroupService.list(new LambdaQueryWrapper<LayerGroup>()
                .eq(LayerGroup::getVisualLayoutId, visualLayout.getId()));
        visualLayoutBO.setLayerGroups(layerGroups);

        List<Layer> layers = layerService.list(new LambdaQueryWrapper<Layer>()
                .eq(Layer::getVisualLayoutId, visualLayout.getId()));
        visualLayoutBO.setLayers(layers);
        return visualLayoutBO;
    }

    @Override
    public TableDataInfo<VisualLayout> selectPageVisualLayout(VisualLayout visualLayout, PageQuery pageQuery) {
        IPage<VisualLayout> page = this.getBaseMapper().selectPageVisualLayout(pageQuery.build(), visualLayout);
        return TableDataInfo.build(page);
    }
}