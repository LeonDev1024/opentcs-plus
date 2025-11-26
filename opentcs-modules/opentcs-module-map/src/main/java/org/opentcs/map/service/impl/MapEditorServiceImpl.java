package org.opentcs.map.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.map.domain.bo.PlantModelBO;
import org.opentcs.map.domain.bo.VisualLayoutBO;
import org.opentcs.map.domain.entity.*;
import org.opentcs.map.domain.vo.LoadModelVO;
import org.opentcs.map.service.*;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
        plantModelBO.setMapInfo(plantModel);
        VisualLayoutBO visualLayoutBO = visualLayoutService.getVisualLayoutByPlantModelId(plantModel.getId());
        plantModelBO.setVisualLayout(visualLayoutBO);

        // 停靠点集合
        List<Point> points = pointService.selectAllPointByPlantModelId(plantModel.getId());
        plantModelBO.setPoints(new HashSet<>(points));
        // 路径集合
        List<Path> paths = pathService.selectAllPathByPlantModelId(plantModel.getId());
        plantModelBO.setPaths(new HashSet<>(paths));
        // 业务点位类型集合
        List<LocationType> locationTypes = locationTypeService.selectAllLocationTypeByPlantModelId(plantModel.getId());
        plantModelBO.setLocationTypes(new HashSet<>(locationTypes));
        // 业务点位集合
        List<Location> locations = locationService.selectAllLocationByPlantModelId(plantModel.getId());
        plantModelBO.setLocations(new HashSet<>(locations));
        // 交通规则区域集合
        List<Block> blocks = blockService.selectAllBlockByPlantModelId(plantModel.getId());
        plantModelBO.setBlocks(new HashSet<>(blocks));
        return plantModelBO;
    }
}