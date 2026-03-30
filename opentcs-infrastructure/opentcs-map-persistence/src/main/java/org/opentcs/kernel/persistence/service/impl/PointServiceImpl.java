package org.opentcs.kernel.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.kernel.persistence.entity.PointEntity;
import org.opentcs.kernel.persistence.mapper.PointMapper;
import org.opentcs.kernel.persistence.service.DTOConverter;
import org.opentcs.kernel.persistence.service.PointDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 点位领域服务实现
 */
@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, PointEntity> implements PointDomainService {

    @Override
    public TableDataInfo<PointEntity> selectPagePoint(PointEntity point, PageQuery pageQuery) {
        return this.getBaseMapper().selectPagePoint(point, pageQuery);
    }

    @Override
    public boolean saveDTO(PointDTO point) {
        return this.save(toEntity(point));
    }


    @Override
    public List<PointEntity> listByMap(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<PointEntity>()
                .eq(PointEntity::getNavigationMapId, navigationMapId)
                .eq(PointEntity::getDelFlag, "0")
        );
    }

    @Override
    public List<PointDTO> listByMapDTO(Long navigationMapId) {
        return DTOConverter.toPointDTOList(this.listByMap(navigationMapId));
    }

    @Override
    public List<PointEntity> listByMapIds(List<Long> mapIds) {
        if (mapIds == null || mapIds.isEmpty()) {
            return List.of();
        }
        return this.list(new LambdaQueryWrapper<PointEntity>()
                .in(PointEntity::getNavigationMapId, mapIds)
                .eq(PointEntity::getDelFlag, "0")
                .orderByAsc(PointEntity::getName));
    }

    @Override
    public int removeByMap(Long navigationMapId) {
        return this.baseMapper.delete(new LambdaQueryWrapper<PointEntity>()
                .eq(PointEntity::getNavigationMapId, navigationMapId));
    }

    @Override
    public boolean updateByIdDTO(PointDTO point) {
        return this.updateById(toEntity(point));
    }

    private PointEntity toEntity(PointDTO dto) {
        PointEntity entity = new PointEntity();
        entity.setId(dto.getId());
        entity.setNavigationMapId(dto.getNavigationMapId());
        entity.setLayerId(dto.getLayerId());
        entity.setPointId(dto.getPointId());
        entity.setName(dto.getName());
        entity.setXPosition(dto.getXPosition());
        entity.setYPosition(dto.getYPosition());
        entity.setZPosition(dto.getZPosition());
        entity.setVehicleOrientation(dto.getVehicleOrientation());
        entity.setType(dto.getType());
        entity.setRadius(dto.getRadius());
        entity.setLocked(dto.getLocked());
        entity.setIsBlocked(dto.getIsBlocked());
        entity.setIsOccupied(dto.getIsOccupied());
        entity.setLabel(dto.getLabel());
        entity.setProperties(dto.getProperties());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }
}
