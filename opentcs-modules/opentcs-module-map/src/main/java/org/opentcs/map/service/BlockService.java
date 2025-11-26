package org.opentcs.map.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.map.domain.entity.Block;

import java.util.List;

/**
 * 区块 Service 接口
 */
public interface BlockService extends IService<Block> {

    List<Block> selectAllBlockByPlantModelId(Long plantModelId);
}