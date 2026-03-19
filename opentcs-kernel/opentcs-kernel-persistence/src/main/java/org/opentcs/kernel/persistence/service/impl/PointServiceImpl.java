package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.PointEntity;
import org.opentcs.kernel.persistence.mapper.PointMapper;
import org.opentcs.kernel.persistence.service.PointDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 点位领域服务实现
 */
@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, PointEntity> implements PointDomainService {

    @Override
    public TableDataInfo<PointEntity> selectPagePoint(PointEntity point, PageQuery pageQuery) {
        return this.getBaseMapper().selectPagePoint(point, pageQuery);
    }


    @Override
    public List<PointEntity> listByMap(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<PointEntity>()
                .eq(PointEntity::getNavigationMapId, navigationMapId)
                .eq(PointEntity::getDelFlag, "0")
        );
    }

    @Override
    public List<PointEntity> listByMapIds(List<Long> mapIds) {
        if (mapIds == null || mapIds.isEmpty()) {
            return List.of();
        }
        return this.list(new LambdaQueryWrapper<PointEntity>()
                .in(PointEntity::getNavigationMapId, mapIds)
                .eq(PointEntity::getDelFlag, "0")
                .orderByAsc(PointEntity::getName));
    }
}
