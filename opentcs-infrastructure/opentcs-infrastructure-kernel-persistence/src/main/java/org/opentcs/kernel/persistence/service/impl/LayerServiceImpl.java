package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LayerEntity;
import org.opentcs.kernel.persistence.mapper.LayerMapper;
import org.opentcs.kernel.persistence.service.LayerDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 图层领域服务实现
 */
@Service
public class LayerServiceImpl extends ServiceImpl<LayerMapper, LayerEntity> implements LayerDomainService {

    @Override
    public TableDataInfo<LayerEntity> selectPageLayer(LayerEntity layer, PageQuery pageQuery) {
        IPage<LayerEntity> page = this.getBaseMapper().selectPageLayer(pageQuery.build(), layer);
        return TableDataInfo.build(page);
    }

    @Override
    public List<LayerEntity> selectByNavigationMapId(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<LayerEntity>()
                .eq(LayerEntity::getNavigationMapId, navigationMapId)
                .orderByAsc(LayerEntity::getName));
    }
}
