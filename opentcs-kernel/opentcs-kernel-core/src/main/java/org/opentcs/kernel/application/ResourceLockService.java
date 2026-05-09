package org.opentcs.kernel.application;

import org.opentcs.kernel.application.runtime.RuntimeStateStore;
import org.opentcs.kernel.domain.event.ResourceLockChangedEvent;
import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceLockStatus;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * 调度资源锁应用服务。
 */
public class ResourceLockService {

    private final RuntimeStateStore runtimeStateStore;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    public ResourceLockService(RuntimeStateStore runtimeStateStore,
                               ApplicationEventPublisher eventPublisher) {
        this(runtimeStateStore, eventPublisher, Clock.systemUTC());
    }

    ResourceLockService(RuntimeStateStore runtimeStateStore,
                        ApplicationEventPublisher eventPublisher,
                        Clock clock) {
        this.runtimeStateStore = runtimeStateStore;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    public Optional<ResourceLock> tryAcquire(ResourceType resourceType,
                                             String resourceId,
                                             String vehicleId,
                                             String orderId,
                                             Duration ttl) {
        expireOverdueLocks();
        Instant now = Instant.now(clock);
        ResourceLock lock = new ResourceLock(UUID.randomUUID().toString(), resourceId,
                resourceType, vehicleId, orderId, now, now.plus(ttl),
                ResourceLockStatus.HELD);
        if (!runtimeStateStore.saveResourceLockIfAbsent(lock)) {
            return Optional.empty();
        }
        publish(lock, "ACQUIRED");
        return Optional.of(lock);
    }

    public boolean renew(String lockId,
                         ResourceType resourceType,
                         String resourceId,
                         String vehicleId,
                         String orderId,
                         Duration ttl) {
        Optional<ResourceLock> existing = runtimeStateStore.getResourceLock(resourceType, resourceId);
        if (existing.isEmpty() || !existing.get().isHeldBy(vehicleId, orderId)
                || !existing.get().getLockId().equals(lockId)) {
            return false;
        }

        ResourceLock lock = existing.get();
        if (lock.isExpired(Instant.now(clock))) {
            expire(lock);
            return false;
        }
        lock.renew(Instant.now(clock).plus(ttl));
        publish(lock, "RENEWED");
        return true;
    }

    public boolean release(String lockId, ResourceType resourceType, String resourceId) {
        Optional<ResourceLock> existing = runtimeStateStore.getResourceLock(resourceType, resourceId);
        if (existing.isEmpty() || !existing.get().getLockId().equals(lockId)) {
            return false;
        }
        ResourceLock lock = existing.get();
        lock.release();
        runtimeStateStore.removeResourceLock(resourceType, resourceId, lockId);
        publish(lock, "RELEASED");
        return true;
    }

    public int expireOverdueLocks() {
        Collection<ResourceLock> locks = new ArrayList<>(runtimeStateStore.getResourceLocks());
        int expired = 0;
        Instant now = Instant.now(clock);
        for (ResourceLock lock : locks) {
            if (lock.isExpired(now)) {
                expire(lock);
                expired++;
            }
        }
        return expired;
    }

    @Scheduled(fixedDelayString = "${opentcs.resource-lock.expire-scan-ms:1000}")
    public void scheduledExpireOverdueLocks() {
        expireOverdueLocks();
    }

    public Collection<ResourceLock> listHeldLocks() {
        expireOverdueLocks();
        return runtimeStateStore.getResourceLocks();
    }

    private void expire(ResourceLock lock) {
        lock.expire();
        runtimeStateStore.removeResourceLock(lock.getResourceType(), lock.getResourceId(), lock.getLockId());
        publish(lock, "EXPIRED");
    }

    private void publish(ResourceLock lock, String reason) {
        eventPublisher.publishEvent(new ResourceLockChangedEvent(lock, reason));
    }
}
