package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.kernel.persistence.entity.BlockEntity;

import java.util.List;

/**
 * 区块 Service 接口
 */
public interface BlockService extends IService<BlockEntity> {

    List<BlockEntity> selectAllBlockByPlantModelId(Long plantModelId);
}