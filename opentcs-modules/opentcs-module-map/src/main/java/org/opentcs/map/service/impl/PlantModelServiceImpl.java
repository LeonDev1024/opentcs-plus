package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.PlantModel;
import org.opentcs.map.mapper.PlantModelMapper;
import org.opentcs.map.service.PlantModelService;
import org.springframework.stereotype.Service;

/**
 * 地图模型服务实现类
 *
 * @author lyc
 */
@Service
public class PlantModelServiceImpl extends ServiceImpl<PlantModelMapper, PlantModel> implements PlantModelService {

}