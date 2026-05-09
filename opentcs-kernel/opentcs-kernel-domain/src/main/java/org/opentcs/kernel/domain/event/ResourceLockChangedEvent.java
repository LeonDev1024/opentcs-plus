package org.opentcs.kernel.domain.event;

import lombok.Getter;
import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceLockStatus;
import org.opentcs.kernel.domain.resource.ResourceType;

import java.time.Instant;

/**
 * 资源锁状态变化事件。
 */
@Getter
public class ResourceLockChangedEvent extends DomainEvent {

    private final String lockId;
    private final String resourceId;
    private final ResourceType resourceType;
    private final String vehicleId;
    private final String orderId;
    private final ResourceLockStatus status;
    private final Instant expiresAt;
    private final String reason;

    public ResourceLockChangedEvent(ResourceLock lock, String reason) {
        super(lock.getResourceId());
        this.lockId = lock.getLockId();
        this.resourceId = lock.getResourceId();
        this.resourceType = lock.getResourceType();
        this.vehicleId = lock.getVehicleId();
        this.orderId = lock.getOrderId();
        this.status = lock.getStatus();
        this.expiresAt = lock.getExpiresAt();
        this.reason = reason;
    }
}
