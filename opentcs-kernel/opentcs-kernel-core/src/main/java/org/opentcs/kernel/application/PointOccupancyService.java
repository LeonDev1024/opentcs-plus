package org.opentcs.kernel.application;

import org.opentcs.kernel.domain.port.PointOccupancyPort;
import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 点位占用：进入站点时抢占 POINT 锁，离开时释放。
 */
public class PointOccupancyService implements PointOccupancyPort {

    private static final Logger log = LoggerFactory.getLogger(PointOccupancyService.class);
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

    private final ResourceLockService resourceLockService;

    /** vehicleId -> (resourceKey -> lockId) */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> holdings = new ConcurrentHashMap<>();
    /** vehicleId -> last reported resource name/id */
    private final ConcurrentHashMap<String, String> lastPosition = new ConcurrentHashMap<>();

    public PointOccupancyService(ResourceLockService resourceLockService) {
        this.resourceLockService = resourceLockService;
    }

    /**
     * 车辆位置变更：释放旧占用，尝试占用新位置 POINT。
     *
     * @return true 表示新位置占用成功（或无需占用）；false 表示被其他车辆占用冲突
     */
    @Override
    public boolean onVehicleMoved(String vehicleId, String orderId, String resourceName) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return true;
        }
        String previous = lastPosition.get(vehicleId);
        if (resourceName != null && resourceName.equals(previous)) {
            renewHoldings(vehicleId, orderId);
            return true;
        }
        releaseAll(vehicleId);

        if (resourceName == null || resourceName.isBlank()) {
            lastPosition.remove(vehicleId);
            return true;
        }

        boolean ok = tryOccupy(vehicleId, blankToUnknown(orderId), resourceName);
        if (ok) {
            lastPosition.put(vehicleId, resourceName);
        } else {
            log.warn("车辆占用冲突: vehicleId={}, resource={}", vehicleId, resourceName);
        }
        return ok;
    }

    public boolean tryOccupy(String vehicleId, String orderId, String resourceName) {
        Optional<ResourceLock> pointLock = resourceLockService.tryAcquire(
                ResourceType.POINT, resourceName, vehicleId, orderId, DEFAULT_TTL);
        if (pointLock.isEmpty()) {
            Optional<ResourceLock> existing = resourceLockService.listHeldLocks().stream()
                    .filter(l -> l.getResourceType() == ResourceType.POINT
                            && resourceName.equals(l.getResourceId()))
                    .findFirst();
            if (existing.isPresent() && !vehicleId.equals(existing.get().getVehicleId())) {
                return false;
            }
        } else {
            remember(vehicleId, ResourceType.POINT, resourceName, pointLock.get().getLockId());
        }
        return true;
    }

    public void releaseAll(String vehicleId) {
        Map<String, String> held = holdings.remove(vehicleId);
        if (held == null || held.isEmpty()) {
            lastPosition.remove(vehicleId);
            return;
        }
        for (Map.Entry<String, String> e : held.entrySet()) {
            String[] parts = e.getKey().split(":", 2);
            if (parts.length != 2) {
                continue;
            }
            ResourceType type = ResourceType.valueOf(parts[0]);
            resourceLockService.release(e.getValue(), type, parts[1]);
        }
        lastPosition.remove(vehicleId);
    }

    private void renewHoldings(String vehicleId, String orderId) {
        Map<String, String> held = holdings.get(vehicleId);
        if (held == null) {
            return;
        }
        String oid = blankToUnknown(orderId);
        for (Map.Entry<String, String> e : held.entrySet()) {
            String[] parts = e.getKey().split(":", 2);
            if (parts.length != 2) {
                continue;
            }
            ResourceType type = ResourceType.valueOf(parts[0]);
            resourceLockService.renew(e.getValue(), type, parts[1], vehicleId, oid, DEFAULT_TTL);
        }
    }

    private void remember(String vehicleId, ResourceType type, String resourceId, String lockId) {
        holdings.computeIfAbsent(vehicleId, k -> new ConcurrentHashMap<>())
                .put(type + ":" + resourceId, lockId);
    }

    private String blankToUnknown(String orderId) {
        return orderId == null || orderId.isBlank() ? "NO_ORDER" : orderId;
    }
}
