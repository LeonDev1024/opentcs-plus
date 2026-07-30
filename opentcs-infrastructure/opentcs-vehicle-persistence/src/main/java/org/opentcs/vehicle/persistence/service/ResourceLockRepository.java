package org.opentcs.vehicle.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.vehicle.persistence.entity.ResourceLockEntity;

import java.util.List;
import java.util.Optional;

public interface ResourceLockRepository extends IService<ResourceLockEntity> {
    Optional<ResourceLockEntity> findByResource(String resourceType, String resourceId);
    List<ResourceLockEntity> listHeld();
    void deleteByResource(String resourceType, String resourceId);
}
