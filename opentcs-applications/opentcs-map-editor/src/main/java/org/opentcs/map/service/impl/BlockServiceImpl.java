package org.opentcs.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.kernel.persistence.entity.BlockEntity;
import org.opentcs.map.mapper.BlockMapper;
import org.opentcs.map.service.BlockService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 区块 Service 实现类
 */
@Service
public class BlockServiceImpl extends ServiceImpl<BlockMapper, BlockEntity> implements BlockService {

    @Override
    public List<BlockEntity> selectAllBlockByPlantModelId(Long plantModelId) {
        return this.list(new LambdaQueryWrapper<>(BlockEntity.class)
                .eq(BlockEntity::getPlantModelId, plantModelId)
        );
    }
}