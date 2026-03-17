package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.PathEntity;
import org.opentcs.kernel.persistence.mapper.PathMapper;
import org.opentcs.kernel.persistence.service.PathDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 路径领域服务实现
 */
@Service
public class PathServiceImpl extends ServiceImpl<PathMapper, PathEntity> implements PathDomainService {

    @Override
    public TableDataInfo<PathEntity> selectPagePath(PathEntity path, PageQuery pageQuery) {
        return this.getBaseMapper().selectPagePath(path, pageQuery);
    }

    @Override
    public List<PathEntity> selectAllPathByPlantModelId(Long plantModelId) {
        return this.list(new LambdaQueryWrapper<>(PathEntity.class)
                .eq(PathEntity::getPlantModelId, plantModelId)
        );
    }

    @Override
    public List<PathEntity> listByMap(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<PathEntity>()
                .eq(PathEntity::getNavigationMapId, navigationMapId)
                .eq(PathEntity::getDelFlag, "0")
        );
    }

    @Override
    public List<PathEntity> listByMapIds(List<Long> mapIds) {
        if (mapIds == null || mapIds.isEmpty()) {
            return List.of();
        }
        return this.list(new LambdaQueryWrapper<PathEntity>()
                .in(PathEntity::getNavigationMapId, mapIds)
                .eq(PathEntity::getDelFlag, "0")
                .orderByAsc(PathEntity::getName));
    }
}
