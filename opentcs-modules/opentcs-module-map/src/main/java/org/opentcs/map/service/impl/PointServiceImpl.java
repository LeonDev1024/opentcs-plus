package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.Point;
import org.opentcs.map.mapper.PointMapper;
import org.opentcs.map.service.PointService;
import org.springframework.stereotype.Service;

/**
 * 导航点服务实现类
 *
 * @author lyc
 */
@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, Point> implements PointService {

}