package org.opentcs.map.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public boolean createPlantModel(PlantModel plantModel) {
        // 校验地图名称是否存在
        boolean isExist = this.getBaseMapper().selectCount(new LambdaQueryWrapper<>(PlantModel.class)
                        .eq(PlantModel::getName, plantModel.getName())
                        .eq(PlantModel::getDelFlag, "0")) > 0;
        if (isExist) {
            throw new RuntimeException("地图名称已存在");
        }
        plantModel.setMapId(IdUtil.fastSimpleUUID());
        return this.save(plantModel);
    }

    @Override
    public TableDataInfo<PlantModel> selectPagePlantModel(PlantModel plantModel, PageQuery pageQuery) {
        IPage<PlantModel> page = this.getBaseMapper().selectPagePlantModel(pageQuery.build(), plantModel);
        return TableDataInfo.build(page);
    }
}