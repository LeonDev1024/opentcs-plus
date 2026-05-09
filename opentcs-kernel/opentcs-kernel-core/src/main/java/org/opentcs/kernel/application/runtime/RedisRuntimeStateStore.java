package org.opentcs.kernel.application.runtime;

import org.opentcs.common.redis.utils.RedisUtils;
import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceType;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Redis 运行时热状态存储。
 */
public class RedisRuntimeStateStore implements RuntimeStateStore {

    private static final String PREFIX = "opentcs:runtime:";
    private static final Duration DISPATCH_LOCK_TTL = Duration.ofMinutes(5);

    @Override
    public void saveVehicleSnapshot(VehicleRuntimeSnapshot snapshot) {
        if (snapshot == null || snapshot.vehicleId() == null || snapshot.vehicleId().isBlank()) {
            return;
        }
        RedisUtils.setCacheObject(vehicleSnapshotKey(snapshot.vehicleId()), snapshot);
    }

    @Override
    public Optional<VehicleRuntimeSnapshot> getVehicleSnapshot(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(RedisUtils.getCacheObject(vehicleSnapshotKey(vehicleId)));
    }

    @Override
    public boolean tryAcquireOrderDispatchLock(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            return false;
        }
        return RedisUtils.setObjectIfAbsent(orderDispatchLockKey(orderId), Boolean.TRUE, DISPATCH_LOCK_TTL);
    }

    @Override
    public void releaseOrderDispatchLock(String orderId) {
        if (orderId != null && !orderId.isBlank()) {
            RedisUtils.deleteObject(orderDispatchLockKey(orderId));
        }
    }

    @Override
    public boolean saveResourceLockIfAbsent(ResourceLock lock) {
        if (lock == null) {
            return false;
        }
        Duration ttl = Duration.between(Instant.now(), lock.getExpiresAt());
        if (ttl.isNegative() || ttl.isZero()) {
            return false;
        }
        return RedisUtils.setObjectIfAbsent(resourceLockKey(lock.getResourceType(), lock.getResourceId()),
                lock, ttl);
    }

    @Override
    public Optional<ResourceLock> getResourceLock(ResourceType resourceType, String resourceId) {
        if (resourceType == null || resourceId == null || resourceId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(RedisUtils.getCacheObject(resourceLockKey(resourceType, resourceId)));
    }

    @Override
    public void removeResourceLock(ResourceType resourceType, String resourceId, String lockId) {
        getResourceLock(resourceType, resourceId)
                .filter(lock -> Objects.equals(lock.getLockId(), lockId))
                .ifPresent(lock -> RedisUtils.deleteObject(resourceLockKey(resourceType, resourceId)));
    }

    @Override
    public Collection<ResourceLock> getResourceLocks() {
        return RedisUtils.keys(PREFIX + "resource-lock:*")
                .stream()
                .map(key -> RedisUtils.<ResourceLock>getCacheObject(key))
                .filter(Objects::nonNull)
                .toList();
    }

    private String vehicleSnapshotKey(String vehicleId) {
        return PREFIX + "vehicle:" + vehicleId;
    }

    private String orderDispatchLockKey(String orderId) {
        return PREFIX + "dispatch-lock:" + orderId;
    }

    private String resourceLockKey(ResourceType resourceType, String resourceId) {
        return PREFIX + "resource-lock:" + resourceType + ":" + resourceId;
    }
}
