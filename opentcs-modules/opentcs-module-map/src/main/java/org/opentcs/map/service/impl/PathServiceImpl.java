package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.Path;
import org.opentcs.map.mapper.PathMapper;
import org.opentcs.map.service.PathService;
import org.springframework.stereotype.Service;

/**
 * 路径服务实现类
 *
 * @author lyc
 */
@Service
public class PathServiceImpl extends ServiceImpl<PathMapper, Path> implements PathService {

}