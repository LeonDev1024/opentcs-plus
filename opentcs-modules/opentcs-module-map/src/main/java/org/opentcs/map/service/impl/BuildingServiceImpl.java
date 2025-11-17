package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.Building;
import org.opentcs.map.mapper.BuildingMapper;
import org.opentcs.map.service.BuildingService;
import org.springframework.stereotype.Service;

/**
 * 建筑物服务实现类
 *
 * @author lyc
 */
@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {

}