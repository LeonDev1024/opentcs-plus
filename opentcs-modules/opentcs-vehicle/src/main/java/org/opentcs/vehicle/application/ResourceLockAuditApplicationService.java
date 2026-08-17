package org.opentcs.vehicle.application;

import lombok.RequiredArgsConstructor;
import org.opentcs.vehicle.persistence.entity.ResourceLockAuditEntity;
import org.opentcs.vehicle.persistence.service.ResourceLockAuditRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资源锁审计查询（表归属 vehicle；监控模块只调用此公开服务）。
 */
@Service
@RequiredArgsConstructor
public class ResourceLockAuditApplicationService {

    private final ResourceLockAuditRepository resourceLockAuditRepository;

    public List<Map<String, Object>> listRecent(int limit) {
        int size = Math.max(1, Math.min(limit, 500));
        return resourceLockAuditRepository.listRecent(size).stream()
                .map(this::toAuditMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toAuditMap(ResourceLockAuditEntity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("lockId", entity.getLockId());
        map.put("resourceType", entity.getResourceType());
        map.put("resourceId", entity.getResourceId());
        map.put("vehicleId", entity.getVehicleId());
        map.put("orderId", entity.getOrderId());
        map.put("eventReason", entity.getEventReason());
        map.put("status", entity.getStatus());
        map.put("operatorName", entity.getOperatorName());
        map.put("detail", entity.getDetail());
        map.put("eventTime", entity.getEventTime() == null ? null : entity.getEventTime().toString());
        return map;
    }
}
