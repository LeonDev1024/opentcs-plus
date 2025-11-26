package org.opentcs.map.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.map.domain.bo.PlantModelBO;
import org.opentcs.map.domain.entity.PlantModel;
import org.opentcs.map.domain.vo.LoadModelVO;
import org.opentcs.map.service.IMapEditorService;
import org.opentcs.map.service.PlantModelService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapEditorServiceImpl implements IMapEditorService {
    private final PlantModelService plantModelService;
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
        return plantModelBO;
    }
}