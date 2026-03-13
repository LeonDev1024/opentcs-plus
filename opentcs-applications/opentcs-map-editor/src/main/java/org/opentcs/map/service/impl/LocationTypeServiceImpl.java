package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.LocationTypeEntity;
import org.opentcs.map.mapper.LocationTypeMapper;
import org.opentcs.map.service.LocationTypeService;
import org.springframework.stereotype.Service;

/**
 * 位置类型 Service 实现类
 */
@Service
public class LocationTypeServiceImpl extends ServiceImpl<LocationTypeMapper, LocationTypeEntity> implements LocationTypeService {


    @Override
    public TableDataInfo<LocationTypeEntity> selectPage(LocationTypeEntity locationType, PageQuery pageQuery) {
        LambdaQueryWrapper<LocationTypeEntity> lqw = new LambdaQueryWrapper<>();
        if (locationType != null && locationType.getName() != null && !locationType.getName().isEmpty()) {
            lqw.like(LocationTypeEntity::getName, locationType.getName());
        }
        Page<LocationTypeEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = this.page(page, lqw);
        return TableDataInfo.build(page);
    }
}