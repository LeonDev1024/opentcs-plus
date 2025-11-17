package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.FloorConnection;
import org.opentcs.map.mapper.FloorConnectionMapper;
import org.opentcs.map.service.FloorConnectionService;
import org.springframework.stereotype.Service;

/**
 * 跨楼层连接服务实现类
 *
 * @author lyc
 */
@Service
public class FloorConnectionServiceImpl extends ServiceImpl<FloorConnectionMapper, FloorConnection> implements FloorConnectionService {

}