package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.Path;
import org.opentcs.map.mapper.PathMapper;
import org.opentcs.map.service.PathService;
import org.springframework.stereotype.Service;

/**
 * 路径 Service 实现类
 */
@Service
public class PathServiceImpl extends ServiceImpl<PathMapper, Path> implements PathService {

}