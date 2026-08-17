package org.opentcs.vehicle.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.vehicle.persistence.entity.ResourceLockAuditEntity;

import java.util.List;

public interface ResourceLockAuditRepository extends IService<ResourceLockAuditEntity> {
    List<ResourceLockAuditEntity> listRecent(int limit);
}
