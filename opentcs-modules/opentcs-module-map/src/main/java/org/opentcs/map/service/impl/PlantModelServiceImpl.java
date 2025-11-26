package org.opentcs.map.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.Layer;
import org.opentcs.map.domain.entity.LayerGroup;
import org.opentcs.map.domain.entity.PlantModel;
import org.opentcs.map.domain.entity.VisualLayout;
import org.opentcs.map.mapper.PlantModelMapper;
import org.opentcs.map.service.LayerGroupService;
import org.opentcs.map.service.LayerService;
import org.opentcs.map.service.PlantModelService;
import org.opentcs.map.service.VisualLayoutService;
import org.springframework.stereotype.Service;

/**
 * 地图模型 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class PlantModelServiceImpl extends ServiceImpl<PlantModelMapper, PlantModel> implements PlantModelService {

    private final VisualLayoutService visualLayoutService;
    private final LayerGroupService layerGroupService;
    private final LayerService layerService;

    @Override
    public boolean createPlantModel(PlantModel plantModel) {
        // 校验地图名称是否存在
        boolean isExist = this.getBaseMapper().selectCount(new LambdaQueryWrapper<>(PlantModel.class)
                        .eq(PlantModel::getName, plantModel.getName())
                        .eq(PlantModel::getModelVersion, "1.0")
                        .eq(PlantModel::getDelFlag, "0")) > 0;
        if (isExist) {
            throw new RuntimeException("地图名称已存在");
        }
        plantModel.setMapId(IdUtil.fastSimpleUUID());
        plantModel.setModelVersion("1.0");

        this.save(plantModel);

        // 创建默认的VisualLayout， LayerGroup和Layer
        VisualLayout visualLayout = new VisualLayout();
        visualLayout.setPlantModelId(plantModel.getId());
        visualLayout.setName(plantModel.getName() + "布局");
        visualLayoutService.save(visualLayout);

        LayerGroup layerGroup = new LayerGroup();
        layerGroup.setVisualLayoutId(visualLayout.getId());
        layerGroup.setName("默认图层组");
        layerGroupService.save(layerGroup);

        Layer layer = new Layer();
        layer.setVisualLayoutId(visualLayout.getId());
        layer.setLayerGroupId(layerGroup.getId());
        layer.setName("默认图层");
        layer.setVisible(true);
        layerService.save(layer);

        return true;

    }

    @Override
    public TableDataInfo<PlantModel> selectPagePlantModel(PlantModel plantModel, PageQuery pageQuery) {
        IPage<PlantModel> page = this.getBaseMapper().selectPagePlantModel(pageQuery.build(), plantModel);
        return TableDataInfo.build(page);
    }
}