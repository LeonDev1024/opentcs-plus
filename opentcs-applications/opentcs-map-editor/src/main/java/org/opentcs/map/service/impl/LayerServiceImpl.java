package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LayerEntity;
import org.opentcs.map.mapper.LayerMapper;
import org.opentcs.map.service.LayerService;
import org.springframework.stereotype.Service;

/**
 * 图层 Service 实现类
 */
@Service
public class LayerServiceImpl extends ServiceImpl<LayerMapper, LayerEntity> implements LayerService {

    @Override
    public TableDataInfo<LayerEntity> selectPageLayer(LayerEntity layer, PageQuery pageQuery) {
        IPage<LayerEntity> page = this.getBaseMapper().selectPageLayer(pageQuery.build(), layer);
        return TableDataInfo.build(page);
    }
}