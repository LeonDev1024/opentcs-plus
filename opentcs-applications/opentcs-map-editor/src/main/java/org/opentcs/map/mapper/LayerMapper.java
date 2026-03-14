package org.opentcs.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.kernel.persistence.entity.LayerEntity;

/**
 * 图层 Mapper 接口
 */
public interface LayerMapper extends BaseMapper<LayerEntity> {

    /**
     * 分页查询图层列表
     * @param page 分页参数
     * @param layer 查询条件
     * @return 分页结果
     */
    IPage<LayerEntity> selectPageLayer(IPage<LayerEntity> page, LayerEntity layer);
}