package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.PointEntity;
import org.opentcs.map.mapper.PointMapper;
import org.opentcs.map.service.PointService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 点位 Service 实现类
 */
@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, PointEntity> implements PointService {

    @Override
    public TableDataInfo<PointEntity> selectPagePoint(PointEntity point, PageQuery pageQuery) {
        return this.getBaseMapper().selectPagePoint(point, pageQuery);
    }

    @Override
    public List<PointEntity> selectAllPointByPlantModelId(Long plantModelId) {
        return this.list(new LambdaQueryWrapper<PointEntity>()
                .eq(PointEntity::getPlantModelId, plantModelId)
        );
    }
}