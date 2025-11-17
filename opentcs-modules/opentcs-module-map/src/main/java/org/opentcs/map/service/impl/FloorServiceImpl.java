package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.Floor;
import org.opentcs.map.mapper.FloorMapper;
import org.opentcs.map.service.FloorService;
import org.springframework.stereotype.Service;

/**
 * 楼层服务实现类
 *
 * @author lyc
 */
@Service
public class FloorServiceImpl extends ServiceImpl<FloorMapper, Floor> implements FloorService {

}