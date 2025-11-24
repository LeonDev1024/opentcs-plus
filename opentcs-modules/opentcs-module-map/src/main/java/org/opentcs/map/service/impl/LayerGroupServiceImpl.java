package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.LayerGroup;
import org.opentcs.map.mapper.LayerGroupMapper;
import org.opentcs.map.service.LayerGroupService;
import org.springframework.stereotype.Service;

/**
 * 图层组 Service 实现类
 */
@Service
public class LayerGroupServiceImpl extends ServiceImpl<LayerGroupMapper, LayerGroup> implements LayerGroupService {

    @Override
    public TableDataInfo<LayerGroup> selectPageLayerGroup(LayerGroup layerGroup, PageQuery pageQuery) {
        IPage<LayerGroup> page = this.getBaseMapper().selectPageLayerGroup(pageQuery.build(), layerGroup);
        return TableDataInfo.build(page);
    }
}