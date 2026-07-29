package org.opentcs.kernel.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.kernel.persistence.entity.BlockEntity;

import java.util.List;

/**
 * Block 持久化端口。
 */
public interface BlockRepository extends IService<BlockEntity> {

    List<BlockDTO> listByMapDTO(Long navigationMapId);

    boolean saveDTO(BlockDTO block);

    int removeByMap(Long navigationMapId);
}
