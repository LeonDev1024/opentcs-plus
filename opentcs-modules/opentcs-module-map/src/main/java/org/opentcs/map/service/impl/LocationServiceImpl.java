package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.Location;
import org.opentcs.map.mapper.LocationMapper;
import org.opentcs.map.service.LocationService;
import org.springframework.stereotype.Service;

/**
 * 位置 Service 实现类
 */
@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements LocationService {

}