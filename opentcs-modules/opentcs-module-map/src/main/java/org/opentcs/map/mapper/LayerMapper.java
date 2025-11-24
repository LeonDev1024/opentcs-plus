package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.map.domain.entity.Layer;

/**
 * 图层 Mapper 接口
 */
public interface LayerMapper extends BaseMapper<Layer> {

    /**
     * 分页查询图层列表
     * @param page 分页参数
     * @param layer 查询条件
     * @return 分页结果
     */
    IPage<Layer> selectPageLayer(IPage<Layer> page, Layer layer);
}