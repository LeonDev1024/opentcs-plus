package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.LocationType;
import org.opentcs.map.mapper.LocationTypeMapper;
import org.opentcs.map.service.LocationTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 位置类型 Service 实现类
 */
@Service
public class LocationTypeServiceImpl extends ServiceImpl<LocationTypeMapper, LocationType> implements LocationTypeService {


    @Override
    public TableDataInfo<LocationType> selectPage(LocationType locationType, PageQuery pageQuery) {
        LambdaQueryWrapper<LocationType> lqw = new LambdaQueryWrapper<>();
        if (locationType != null && locationType.getName() != null && !locationType.getName().isEmpty()) {
            lqw.like(LocationType::getName, locationType.getName());
        }
        Page<LocationType> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page = this.page(page, lqw);
        return TableDataInfo.build(page);
    }
}