package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.Block;
import org.opentcs.map.mapper.BlockMapper;
import org.opentcs.map.service.BlockService;
import org.springframework.stereotype.Service;

/**
 * 区块 Service 实现类
 */
@Service
public class BlockServiceImpl extends ServiceImpl<BlockMapper, Block> implements BlockService {

}