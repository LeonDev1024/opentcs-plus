package org.opentcs.kernel.application.runtime;

import java.util.Optional;
import java.util.Collection;
import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceType;

/**
 * 运行时热状态存储端口。
 * <p>
 * 当前可由内存实现承载，生产环境可替换为 Redis 等外部存储。
 * </p>
 */
public interface RuntimeStateStore {

    void saveVehicleSnapshot(VehicleRuntimeSnapshot snapshot);

    Optional<VehicleRuntimeSnapshot> getVehicleSnapshot(String vehicleId);

    boolean tryAcquireOrderDispatchLock(String orderId);

    void releaseOrderDispatchLock(String orderId);

    boolean saveResourceLockIfAbsent(ResourceLock lock);

    Optional<ResourceLock> getResourceLock(ResourceType resourceType, String resourceId);

    void removeResourceLock(ResourceType resourceType, String resourceId, String lockId);

    Collection<ResourceLock> getResourceLocks();
}
