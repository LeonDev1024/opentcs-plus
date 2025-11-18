package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.PlantModel;
import org.opentcs.map.mapper.PlantModelMapper;
import org.opentcs.map.service.PlantModelService;
import org.springframework.stereotype.Service;

/**
 * 地图模型 Service 实现类
 */
@Service
public class PlantModelServiceImpl extends ServiceImpl<PlantModelMapper, PlantModel> implements PlantModelService {

    @Override
    public TableDataInfo<PlantModel> selectPagePlantModel(PlantModel plantModel, PageQuery pageQuery) {
        return this.getBaseMapper().selectPagePlantModel(plantModel, pageQuery);
    }
}