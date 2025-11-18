package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.LocationType;
import org.opentcs.map.mapper.LocationTypeMapper;
import org.opentcs.map.service.LocationTypeService;
import org.springframework.stereotype.Service;

/**
 * 位置类型 Service 实现类
 */
@Service
public class LocationTypeServiceImpl extends ServiceImpl<LocationTypeMapper, LocationType> implements LocationTypeService {

}