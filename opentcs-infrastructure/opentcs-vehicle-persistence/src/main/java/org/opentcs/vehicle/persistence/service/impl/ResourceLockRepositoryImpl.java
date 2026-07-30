package org.opentcs.vehicle.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.vehicle.persistence.entity.ResourceLockEntity;
import org.opentcs.vehicle.persistence.mapper.ResourceLockMapper;
import org.opentcs.vehicle.persistence.service.ResourceLockRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceLockRepositoryImpl extends ServiceImpl<ResourceLockMapper, ResourceLockEntity>
        implements ResourceLockRepository {

    @Override
    public Optional<ResourceLockEntity> findByResource(String resourceType, String resourceId) {
        if (!StringUtils.hasText(resourceType) || !StringUtils.hasText(resourceId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<ResourceLockEntity>()
                .eq(ResourceLockEntity::getResourceType, resourceType)
                .eq(ResourceLockEntity::getResourceId, resourceId)
                .last("LIMIT 1")));
    }

    @Override
    public List<ResourceLockEntity> listHeld() {
        return list(new LambdaQueryWrapper<ResourceLockEntity>()
                .eq(ResourceLockEntity::getStatus, "HELD"));
    }

    @Override
    public void deleteByResource(String resourceType, String resourceId) {
        remove(new LambdaQueryWrapper<ResourceLockEntity>()
                .eq(ResourceLockEntity::getResourceType, resourceType)
                .eq(ResourceLockEntity::getResourceId, resourceId));
    }
}
