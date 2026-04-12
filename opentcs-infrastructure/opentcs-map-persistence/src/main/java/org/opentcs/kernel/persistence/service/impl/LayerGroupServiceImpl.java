package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LayerGroupEntity;
import org.opentcs.kernel.persistence.mapper.LayerGroupMapper;
import org.opentcs.kernel.persistence.service.LayerGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 图层组领域服务实现
 */
@Service
public class LayerGroupServiceImpl extends ServiceImpl<LayerGroupMapper, LayerGroupEntity> implements LayerGroupRepository {

    @Override
    public TableDataInfo<LayerGroupEntity> selectPageLayerGroup(LayerGroupEntity layerGroup, PageQuery pageQuery) {
        IPage<LayerGroupEntity> page = this.getBaseMapper().selectPageLayerGroup(pageQuery.build(), layerGroup);
        return TableDataInfo.build(page);
    }

    @Override
    public List<LayerGroupEntity> selectByNavigationMapId(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<LayerGroupEntity>()
                .eq(LayerGroupEntity::getNavigationMapId, navigationMapId)
                .orderByAsc(LayerGroupEntity::getName));
    }
}
