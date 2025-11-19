package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.map.domain.entity.PlantModel;

/**
 * 地图模型 Mapper 接口
 */
public interface PlantModelMapper extends BaseMapper<PlantModel> {

    /**
     * 分页查询地图模型列表
     * @param page 分页参数
     * @param plantModel 查询条件
     * @return 分页结果
     */
    IPage<PlantModel> selectPagePlantModel(IPage<PlantModel> page, PlantModel plantModel);
}