package org.opentcs.map.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.map.domain.bo.PlantModelBO;
import org.opentcs.map.domain.bo.VisualLayoutBO;
import org.opentcs.map.domain.entity.*;
import org.opentcs.map.domain.vo.LoadModelVO;
import org.opentcs.map.service.*;
import org.opentcs.map.utils.ModelVersionUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapEditorServiceImpl implements IMapEditorService {
    private final PlantModelService plantModelService;
    private final VisualLayoutService visualLayoutService;
    private final PointService pointService;
    private final PathService pathService;
    private final LocationService locationService;
    private final LocationTypeService locationTypeService;
    private final BlockService blockService;
    @Override
    public PlantModelBO load(LoadModelVO loadModelVO) {
        LambdaQueryWrapper<PlantModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PlantModel::getMapId, loadModelVO.getMapId());
        PlantModel plantModel = plantModelService.getOne(queryWrapper);
        
        if (Objects.isNull(plantModel)) {
            log.error("地图模型不存在");
            return null;
        }
        PlantModelBO plantModelBO = new PlantModelBO();
        plantModelBO.setPlantModelId(plantModel.getId());
        plantModelBO.setName(plantModel.getName());
        plantModelBO.setMapId(plantModel.getMapId());
        plantModelBO.setModelVersion(plantModel.getModelVersion());
        VisualLayoutBO visualLayoutBO = visualLayoutService.getVisualLayoutByPlantModelId(plantModel.getId());
        plantModelBO.setVisualLayout(visualLayoutBO);

        // 停靠点集合
        List<Point> points = pointService.selectAllPointByPlantModelId(plantModel.getId());
        plantModelBO.setPoints(new HashSet<>(points));
        // 路径集合
        List<Path> paths = pathService.selectAllPathByPlantModelId(plantModel.getId());
        plantModelBO.setPaths(new HashSet<>(paths));
        // 业务点位集合
        List<Location> locations = locationService.selectAllLocationByPlantModelId(plantModel.getId());
        plantModelBO.setLocations(new HashSet<>(locations));
        // 交通规则区域集合
        List<Block> blocks = blockService.selectAllBlockByPlantModelId(plantModel.getId());
        plantModelBO.setBlocks(new HashSet<>(blocks));
        return plantModelBO;
    }

    @Override
    public boolean save(PlantModelBO plantModelBO) {
        // 版本号是1.0时，为第一次新建地图元素不需要复制，直接更新视图元素
        if (StrUtil.equals(plantModelBO.getModelVersion(), "1.0")) {
            pointService.saveBatch(plantModelBO.getPoints());
            pathService.saveBatch(plantModelBO.getPaths());
            locationService.saveBatch(plantModelBO.getLocations());
            locationTypeService.saveBatch(plantModelBO.getLocationTypes());
            blockService.saveBatch(plantModelBO.getBlocks());
        } else {
            // 复制地图元素
            PlantModel plantModel = new PlantModel();
            plantModel.setMapId(plantModelBO.getMapId());
            plantModel.setName(plantModelBO.getName());
            // 模型版本+1
            plantModel.setModelVersion(ModelVersionUtil.getNextModelVersion(plantModelBO.getModelVersion()));
            plantModelService.save(plantModel);

            // 保存地图元素
            Set<Point> points = plantModelBO.getPoints();
            if (CollUtil.isNotEmpty( points)) {
                // 替换地图模型id
                points.forEach(point -> point.setPlantModelId(plantModel.getId()));
                pointService.saveBatch(points);
            }

            Set<Path> paths = plantModelBO.getPaths();
            if (CollUtil.isNotEmpty(paths)) {
                // 替换地图模型id
                paths.forEach(path -> path.setPlantModelId(plantModel.getId()));
                pathService.saveBatch(paths);
            }

            Set<Location> locations = plantModelBO.getLocations();
            if (CollUtil.isNotEmpty(locations)) {
                // 替换地图模型id
                locations.forEach(location -> location.setPlantModelId(plantModel.getId()));
                locationService.saveBatch(locations);
            }

            Set<Block> blocks = plantModelBO.getBlocks();
            if (CollUtil.isNotEmpty(blocks)) {
                // 替换地图模型id
                blocks.forEach(block -> block.setPlantModelId(plantModel.getId()));
                blockService.saveBatch(blocks);
            }
        }
        return true;
    }
}