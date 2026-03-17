package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LocationEntity;
import org.opentcs.kernel.persistence.mapper.LocationMapper;
import org.opentcs.kernel.persistence.service.LocationDomainService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 位置领域服务实现
 */
@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, LocationEntity> implements LocationDomainService {

    @Override
    public TableDataInfo<LocationEntity> selectPage(LocationEntity location, PageQuery pageQuery) {
        LambdaQueryWrapper<LocationEntity> wrapper = new LambdaQueryWrapper<>();

        if (location != null) {
            if (StringUtils.hasText(location.getName())) {
                wrapper.like(LocationEntity::getName, location.getName());
            }
            if (location.getLocationTypeId() != null) {
                wrapper.eq(LocationEntity::getLocationTypeId, location.getLocationTypeId());
            }
            if (location.getNavigationMapId() != null) {
                wrapper.eq(LocationEntity::getNavigationMapId, location.getNavigationMapId());
            }
        }

        wrapper.orderByDesc(LocationEntity::getCreateTime);

        Page<LocationEntity> page = this.page(
            new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
            wrapper
        );

        return TableDataInfo.build(page);
    }

    @Override
    public List<LocationEntity> selectByNavigationMapId(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<LocationEntity>()
                .eq(LocationEntity::getNavigationMapId, navigationMapId)
                .orderByAsc(LocationEntity::getName));
    }

    @Override
    public List<LocationEntity> selectByMapIds(List<Long> mapIds) {
        if (mapIds == null || mapIds.isEmpty()) {
            return List.of();
        }
        return this.list(new LambdaQueryWrapper<LocationEntity>()
                .in(LocationEntity::getNavigationMapId, mapIds)
                .orderByAsc(LocationEntity::getName));
    }

    @Override
    public LocationEntity selectById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<LocationEntity> selectAllLocationByPlantModelId(Long plantModelId) {
        return this.list(new LambdaQueryWrapper<LocationEntity>()
                .eq(LocationEntity::getNavigationMapId, plantModelId)
        );
    }
}
