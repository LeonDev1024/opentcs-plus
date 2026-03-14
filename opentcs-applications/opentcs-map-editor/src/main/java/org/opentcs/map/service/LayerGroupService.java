package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LayerGroupEntity;

/**
 * 图层组 Service 接口
 */
public interface LayerGroupService extends IService<LayerGroupEntity> {

    /**
     * 分页查询图层组列表
     * @param layerGroup 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<LayerGroupEntity> selectPageLayerGroup(LayerGroupEntity layerGroup, PageQuery pageQuery);
}