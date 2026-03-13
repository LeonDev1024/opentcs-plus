package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.kernel.persistence.entity.LocationEntity;
import org.opentcs.map.mapper.LocationMapper;
import org.opentcs.map.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 位置 Service 实现类
 */
@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, LocationEntity> implements LocationService {

    @Override
    public List<LocationEntity> selectAllLocationByPlantModelId(Long plantModelId) {
        return this.list(new LambdaQueryWrapper<>(LocationEntity.class)
                .eq(LocationEntity::getPlantModelId, plantModelId)
        );
    }
}