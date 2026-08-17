package org.opentcs.vehicle.application;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.domain.port.ResourceLockPort;
import org.opentcs.kernel.domain.event.ResourceLockChangedEvent;
import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceLockStatus;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.opentcs.vehicle.persistence.entity.ResourceLockAuditEntity;
import org.opentcs.vehicle.persistence.entity.ResourceLockEntity;
import org.opentcs.vehicle.persistence.service.ResourceLockAuditRepository;
import org.opentcs.vehicle.persistence.service.ResourceLockRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 资源锁事件 → DB 当前态 + 审计；启动时恢复 HELD 锁。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceLockPersistenceListener {

    private final ResourceLockRepository resourceLockRepository;
    private final ResourceLockAuditRepository auditRepository;
    private final ResourceLockPort resourceLockService;

    @PostConstruct
    public void restoreHeldLocks() {
        List<ResourceLockEntity> held = resourceLockRepository.listHeld();
        int restored = 0;
        for (ResourceLockEntity entity : held) {
            try {
                ResourceLock lock = new ResourceLock(
                        entity.getLockId(),
                        entity.getResourceId(),
                        ResourceType.valueOf(entity.getResourceType()),
                        entity.getVehicleId(),
                        entity.getOrderId(),
                        toInstant(entity.getCreatedAt()),
                        toInstant(entity.getExpiresAt()),
                        ResourceLockStatus.HELD
                );
                if (lock.isExpired(Instant.now())) {
                    resourceLockRepository.deleteByResource(entity.getResourceType(), entity.getResourceId());
                    continue;
                }
                if (resourceLockService.restoreHeldLock(lock)) {
                    restored++;
                }
            } catch (Exception e) {
                log.warn("恢复资源锁失败: lockId={}, err={}", entity.getLockId(), e.getMessage());
            }
        }
        if (restored > 0) {
            log.info("已从 DB 恢复资源锁: count={}", restored);
        }
    }

    @EventListener
    public void onResourceLockChanged(ResourceLockChangedEvent event) {
        writeAudit(event);
        syncCurrentState(event);
    }

    private void writeAudit(ResourceLockChangedEvent event) {
        ResourceLockAuditEntity audit = new ResourceLockAuditEntity();
        audit.setLockId(event.getLockId());
        audit.setResourceType(event.getResourceType() == null ? null : event.getResourceType().name());
        audit.setResourceId(event.getResourceId());
        audit.setVehicleId(event.getVehicleId());
        audit.setOrderId(event.getOrderId());
        audit.setEventReason(event.getReason());
        audit.setStatus(event.getStatus() == null ? null : event.getStatus().name());
        audit.setOperatorName("FORCE_RELEASED".equals(event.getReason()) ? "ops" : "system");
        audit.setDetail(event.getReason());
        audit.setEventTime(LocalDateTime.now());
        auditRepository.save(audit);
    }

    private void syncCurrentState(ResourceLockChangedEvent event) {
        String reason = event.getReason();
        if ("ACQUIRED".equals(reason) || "RENEWED".equals(reason) || "RESTORED".equals(reason)) {
            ResourceLockEntity entity = resourceLockRepository
                    .findByResource(event.getResourceType().name(), event.getResourceId())
                    .orElseGet(ResourceLockEntity::new);
            entity.setLockId(event.getLockId());
            entity.setResourceType(event.getResourceType().name());
            entity.setResourceId(event.getResourceId());
            entity.setVehicleId(event.getVehicleId());
            entity.setOrderId(event.getOrderId());
            entity.setStatus("HELD");
            if (entity.getCreatedAt() == null) {
                entity.setCreatedAt(LocalDateTime.now());
            }
            entity.setExpiresAt(event.getExpiresAt() == null
                    ? LocalDateTime.now().plusMinutes(10)
                    : LocalDateTime.ofInstant(event.getExpiresAt(), ZoneOffset.UTC));
            resourceLockRepository.saveOrUpdate(entity);
            return;
        }
        if ("RELEASED".equals(reason) || "EXPIRED".equals(reason) || "FORCE_RELEASED".equals(reason)) {
            resourceLockRepository.deleteByResource(event.getResourceType().name(), event.getResourceId());
        }
    }

    private Instant toInstant(LocalDateTime time) {
        if (time == null) {
            return Instant.now();
        }
        return time.toInstant(ZoneOffset.UTC);
    }
}
