package org.opentcs.vehicle.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.vehicle.persistence.entity.ResourceLockAuditEntity;
import org.opentcs.vehicle.persistence.mapper.ResourceLockAuditMapper;
import org.opentcs.vehicle.persistence.service.ResourceLockAuditRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceLockAuditRepositoryImpl extends ServiceImpl<ResourceLockAuditMapper, ResourceLockAuditEntity>
        implements ResourceLockAuditRepository {

    @Override
    public List<ResourceLockAuditEntity> listRecent(int limit) {
        return list(new LambdaQueryWrapper<ResourceLockAuditEntity>()
                .orderByDesc(ResourceLockAuditEntity::getEventTime)
                .last("LIMIT " + Math.max(1, Math.min(limit, 500))));
    }
}
