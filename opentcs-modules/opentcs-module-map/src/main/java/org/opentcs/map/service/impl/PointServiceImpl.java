package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.Point;
import org.opentcs.map.mapper.PointMapper;
import org.opentcs.map.service.PointService;
import org.springframework.stereotype.Service;

/**
 * 点位 Service 实现类
 */
@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, Point> implements PointService {

    @Override
    public TableDataInfo<Point> selectPagePoint(Point point, PageQuery pageQuery) {
        return this.getBaseMapper().selectPagePoint(point, pageQuery);
    }
}