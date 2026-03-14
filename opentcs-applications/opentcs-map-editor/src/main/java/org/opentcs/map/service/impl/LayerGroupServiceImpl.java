package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LayerGroupEntity;
import org.opentcs.map.mapper.LayerGroupMapper;
import org.opentcs.map.service.LayerGroupService;
import org.springframework.stereotype.Service;

/**
 * 图层组 Service 实现类
 */
@Service
public class LayerGroupServiceImpl extends ServiceImpl<LayerGroupMapper, LayerGroupEntity> implements LayerGroupService {

    @Override
    public TableDataInfo<LayerGroupEntity> selectPageLayerGroup(LayerGroupEntity layerGroup, PageQuery pageQuery) {
        IPage<LayerGroupEntity> page = this.getBaseMapper().selectPageLayerGroup(pageQuery.build(), layerGroup);
        return TableDataInfo.build(page);
    }
}