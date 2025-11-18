package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.PlantModel;

/**
 * 地图模型 Mapper 接口
 */
public interface PlantModelMapper extends BaseMapper<PlantModel> {

    /**
     * 分页查询地图模型列表
     * @param plantModel 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PlantModel> selectPagePlantModel(PlantModel plantModel, PageQuery pageQuery);
}