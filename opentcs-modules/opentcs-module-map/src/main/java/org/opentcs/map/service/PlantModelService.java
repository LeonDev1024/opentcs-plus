package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.PlantModel;

/**
 * 地图模型 Service 接口
 */
public interface PlantModelService extends IService<PlantModel> {

    /**
     * 分页查询地图模型列表
     * @param plantModel 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PlantModel> selectPagePlantModel(PlantModel plantModel, PageQuery pageQuery);
}