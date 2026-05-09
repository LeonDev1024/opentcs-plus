package org.opentcs.kernel.domain.resource;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 调度资源锁聚合。
 */
public class ResourceLock implements Serializable {

    private final String lockId;
    private final String resourceId;
    private final ResourceType resourceType;
    private final String vehicleId;
    private final String orderId;
    private final Instant createdAt;
    private Instant expiresAt;
    private ResourceLockStatus status;

    public ResourceLock(String resourceId,
                        ResourceType resourceType,
                        String vehicleId,
                        String orderId,
                        Instant expiresAt) {
        this(UUID.randomUUID().toString(), resourceId, resourceType, vehicleId, orderId,
                Instant.now(), expiresAt, ResourceLockStatus.HELD);
    }

    public ResourceLock(String lockId,
                        String resourceId,
                        ResourceType resourceType,
                        String vehicleId,
                        String orderId,
                        Instant createdAt,
                        Instant expiresAt,
                        ResourceLockStatus status) {
        this.lockId = requireText(lockId, "lockId");
        this.resourceId = requireText(resourceId, "resourceId");
        this.resourceType = Objects.requireNonNull(resourceType, "resourceType");
        this.vehicleId = requireText(vehicleId, "vehicleId");
        this.orderId = requireText(orderId, "orderId");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.status = Objects.requireNonNull(status, "status");
        if (!expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("expiresAt 必须晚于 createdAt");
        }
    }

    public boolean isHeld() {
        return status == ResourceLockStatus.HELD;
    }

    public boolean isExpired(Instant now) {
        return isHeld() && !expiresAt.isAfter(now);
    }

    public boolean isHeldBy(String vehicleId, String orderId) {
        return isHeld()
                && this.vehicleId.equals(vehicleId)
                && this.orderId.equals(orderId);
    }

    public void renew(Instant newExpiresAt) {
        if (!isHeld()) {
            throw new IllegalStateException("只有持有中的资源锁可以续约");
        }
        if (!newExpiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("newExpiresAt 必须晚于创建时间");
        }
        this.expiresAt = newExpiresAt;
    }

    public void release() {
        if (status == ResourceLockStatus.HELD) {
            status = ResourceLockStatus.RELEASED;
        }
    }

    public void expire() {
        if (status == ResourceLockStatus.HELD) {
            status = ResourceLockStatus.EXPIRED;
        }
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " 不能为空");
        }
        return value;
    }

    public String getLockId() {
        return lockId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getOrderId() {
        return orderId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public ResourceLockStatus getStatus() {
        return status;
    }
}
