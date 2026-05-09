package org.opentcs.kernel.application.runtime;

import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceType;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 内存运行时热状态存储。
 */
public class InMemoryRuntimeStateStore implements RuntimeStateStore {

    private final Map<String, VehicleRuntimeSnapshot> vehicleSnapshots = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Boolean> orderDispatchLocks = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ResourceLock> resourceLocks = new ConcurrentHashMap<>();

    @Override
    public void saveVehicleSnapshot(VehicleRuntimeSnapshot snapshot) {
        if (snapshot == null || snapshot.vehicleId() == null || snapshot.vehicleId().isBlank()) {
            return;
        }
        vehicleSnapshots.put(snapshot.vehicleId(), snapshot);
    }

    @Override
    public Optional<VehicleRuntimeSnapshot> getVehicleSnapshot(String vehicleId) {
        return Optional.ofNullable(vehicleSnapshots.get(vehicleId));
    }

    @Override
    public boolean tryAcquireOrderDispatchLock(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            return false;
        }
        return orderDispatchLocks.putIfAbsent(orderId, Boolean.TRUE) == null;
    }

    @Override
    public void releaseOrderDispatchLock(String orderId) {
        if (orderId != null) {
            orderDispatchLocks.remove(orderId);
        }
    }

    @Override
    public boolean saveResourceLockIfAbsent(ResourceLock lock) {
        if (lock == null) {
            return false;
        }
        return resourceLocks.putIfAbsent(resourceKey(lock.getResourceType(), lock.getResourceId()), lock) == null;
    }

    @Override
    public Optional<ResourceLock> getResourceLock(ResourceType resourceType, String resourceId) {
        return Optional.ofNullable(resourceLocks.get(resourceKey(resourceType, resourceId)));
    }

    @Override
    public void removeResourceLock(ResourceType resourceType, String resourceId, String lockId) {
        if (lockId == null) {
            return;
        }
        String key = resourceKey(resourceType, resourceId);
        resourceLocks.computeIfPresent(key,
                (ignored, current) -> lockId.equals(current.getLockId()) ? null : current);
    }

    @Override
    public Collection<ResourceLock> getResourceLocks() {
        return resourceLocks.values();
    }

    private String resourceKey(ResourceType resourceType, String resourceId) {
        return resourceType + ":" + resourceId;
    }
}
