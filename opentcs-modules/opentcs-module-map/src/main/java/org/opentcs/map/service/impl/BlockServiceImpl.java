package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.map.domain.entity.Block;
import org.opentcs.map.mapper.BlockMapper;
import org.opentcs.map.service.BlockService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 区块 Service 实现类
 */
@Service
public class BlockServiceImpl extends ServiceImpl<BlockMapper, Block> implements BlockService {

    @Override
    public List<Block> selectAllBlockByPlantModelId(Long plantModelId) {
        return this.list(new LambdaQueryWrapper<>(Block.class)
                .eq(Block::getPlantModelId, plantModelId)
        );
    }
}