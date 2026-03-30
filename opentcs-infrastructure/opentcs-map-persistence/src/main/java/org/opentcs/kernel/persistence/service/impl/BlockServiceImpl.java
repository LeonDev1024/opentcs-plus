package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.kernel.persistence.service.DTOConverter;
import org.opentcs.kernel.persistence.entity.BlockEntity;
import org.opentcs.kernel.persistence.mapper.BlockMapper;
import org.opentcs.kernel.persistence.service.BlockDomainService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 区域领域服务实现
 */
@Slf4j
@Service
public class BlockServiceImpl extends ServiceImpl<BlockMapper, BlockEntity> implements BlockDomainService {

    @Override
    public TableDataInfo<BlockEntity> selectPage(BlockEntity block, PageQuery pageQuery) {
        LambdaQueryWrapper<BlockEntity> wrapper = new LambdaQueryWrapper<>();

        if (block != null) {
            if (StringUtils.hasText(block.getName())) {
                wrapper.like(BlockEntity::getName, block.getName());
            }
            if (StringUtils.hasText(block.getType())) {
                wrapper.eq(BlockEntity::getType, block.getType());
            }
            if (block.getFactoryModelId() != null) {
                wrapper.eq(BlockEntity::getFactoryModelId, block.getFactoryModelId());
            }
            if (block.getNavigationMapId() != null) {
                wrapper.eq(BlockEntity::getNavigationMapId, block.getNavigationMapId());
            }
        }

        wrapper.orderByDesc(BlockEntity::getCreateTime);

        Page<BlockEntity> page = this.page(
                new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
                wrapper
        );

        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<BlockDTO> selectPageDTO(BlockEntity block, PageQuery pageQuery) {
        LambdaQueryWrapper<BlockEntity> wrapper = new LambdaQueryWrapper<>();

        if (block != null) {
            if (StringUtils.hasText(block.getName())) {
                wrapper.like(BlockEntity::getName, block.getName());
            }
            if (StringUtils.hasText(block.getType())) {
                wrapper.eq(BlockEntity::getType, block.getType());
            }
            if (block.getFactoryModelId() != null) {
                wrapper.eq(BlockEntity::getFactoryModelId, block.getFactoryModelId());
            }
            if (block.getNavigationMapId() != null) {
                wrapper.eq(BlockEntity::getNavigationMapId, block.getNavigationMapId());
            }
        }

        wrapper.orderByDesc(BlockEntity::getCreateTime);

        Page<BlockEntity> page = this.page(
                new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
                wrapper
        );

        // Convert to DTO
        List<BlockDTO> dtoList = DTOConverter.toBlockDTOList(page.getRecords());
        TableDataInfo<BlockDTO> result = TableDataInfo.build();
        result.setRows(dtoList);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public List<BlockEntity> selectByFactoryModelId(Long factoryModelId) {
        return this.list(new LambdaQueryWrapper<BlockEntity>()
                .eq(BlockEntity::getFactoryModelId, factoryModelId)
                .orderByAsc(BlockEntity::getName));
    }

    @Override
    public List<BlockEntity> selectByFactoryModelIdAndType(Long factoryModelId, String type) {
        return this.list(new LambdaQueryWrapper<BlockEntity>()
                .eq(BlockEntity::getFactoryModelId, factoryModelId)
                .eq(BlockEntity::getType, type)
                .orderByAsc(BlockEntity::getName));
    }

    @Override
    public List<BlockEntity> selectByNavigationMapId(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<BlockEntity>()
                .eq(BlockEntity::getNavigationMapId, navigationMapId)
                .orderByAsc(BlockEntity::getName));
    }

    @Override
    public BlockEntity selectById(Long id) {
        return this.getById(id);
    }

    @Override
    public BlockDTO selectByIdDTO(Long id) {
        return DTOConverter.toBlockDTO(this.getById(id));
    }

    @Override
    public List<BlockDTO> selectByFactoryModelIdDTO(Long factoryModelId) {
        return DTOConverter.toBlockDTOList(this.list(new LambdaQueryWrapper<BlockEntity>()
                .eq(BlockEntity::getFactoryModelId, factoryModelId)
                .orderByAsc(BlockEntity::getName)));
    }

    @Override
    public List<BlockDTO> selectByFactoryModelIdAndTypeDTO(Long factoryModelId, String type) {
        return DTOConverter.toBlockDTOList(this.list(new LambdaQueryWrapper<BlockEntity>()
                .eq(BlockEntity::getFactoryModelId, factoryModelId)
                .eq(BlockEntity::getType, type)
                .orderByAsc(BlockEntity::getName)));
    }

    @Override
    public boolean create(BlockEntity block) {
        return this.save(block);
    }

    @Override
    public boolean update(BlockEntity block) {
        return this.updateById(block);
    }

    @Override
    public boolean delete(Long id) {
        return this.removeById(id);
    }

    @Override
    public List<BlockEntity> selectAllBlockByPlantModelId(Long plantModelId) {
        return this.list(new LambdaQueryWrapper<BlockEntity>()
                .eq(BlockEntity::getFactoryModelId, plantModelId)
                .orderByAsc(BlockEntity::getName));
    }
}
