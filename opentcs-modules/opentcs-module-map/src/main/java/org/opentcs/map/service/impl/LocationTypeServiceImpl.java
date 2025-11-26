package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public List<LocationType> selectAllLocationTypeByPlantModelId(Long plantModelId) {
        return this.list(new LambdaQueryWrapper<>(LocationType.class)
                .eq(LocationType::getPlantModelId, plantModelId)
        );
    }
}