package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.Location;
import org.opentcs.map.mapper.LocationMapper;
import org.opentcs.map.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 位置 Service 实现类
 */
@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements LocationService {

    @Override
    public List<Location> selectAllLocationByPlantModelId(Long plantModelId) {
        return this.list(new LambdaQueryWrapper<>(Location.class)
                .eq(Location::getPlantModelId, plantModelId)
        );
    }
}