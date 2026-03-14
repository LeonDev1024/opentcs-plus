package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.kernel.persistence.entity.LayerGroupEntity;

/**
 * 图层组 Mapper 接口
 */
public interface LayerGroupMapper extends BaseMapper<LayerGroupEntity> {

    /**
     * 分页查询图层组列表
     * @param page 分页参数
     * @param layerGroup 查询条件
     * @return 分页结果
     */
    IPage<LayerGroupEntity> selectPageLayerGroup(IPage<LayerGroupEntity> page, LayerGroupEntity layerGroup);
}