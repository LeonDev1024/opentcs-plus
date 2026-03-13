package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.kernel.persistence.entity.PlantModelEntity;

/**
 * 地图模型 Mapper 接口
 */
public interface PlantModelMapper extends BaseMapper<PlantModelEntity> {

    /**
     * 分页查询地图模型列表
     * @param page 分页参数
     * @param plantModel 查询条件
     * @return 分页结果
     */
    IPage<PlantModelEntity> selectPagePlantModel(IPage<PlantModelEntity> page, PlantModelEntity plantModel);
}