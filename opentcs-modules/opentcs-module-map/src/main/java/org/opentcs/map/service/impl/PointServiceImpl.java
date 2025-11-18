package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.Point;
import org.opentcs.map.mapper.PointMapper;
import org.opentcs.map.service.PointService;
import org.springframework.stereotype.Service;

/**
 * 点位 Service 实现类
 */
@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, Point> implements PointService {

}