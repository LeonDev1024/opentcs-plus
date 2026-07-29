package org.opentcs.kernel.application;

import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.opentcs.kernel.domain.resource.RuntimeBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Block / 站点占用：进入成员资源时抢占 BLOCK/POINT 锁，离开时释放。
 */
public class BlockOccupancyService {

    private static final Logger log = LoggerFactory.getLogger(BlockOccupancyService.class);
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

    private final ResourceLockService resourceLockService;
    private final BlockRegistry blockRegistry;

    /** vehicleId -> (resourceKey -> lockId) */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> holdings = new ConcurrentHashMap<>();
    /** vehicleId -> last reported resource name/id */
    private final ConcurrentHashMap<String, String> lastPosition = new ConcurrentHashMap<>();

    public BlockOccupancyService(ResourceLockService resourceLockService, BlockRegistry blockRegistry) {
        this.resourceLockService = resourceLockService;
        this.blockRegistry = blockRegistry;
    }

    /**
     * 车辆位置变更：释放旧占用，尝试占用新位置所属 Block 与 POINT。
     *
     * @return true 表示新位置占用成功（或无需占用）；false 表示被其他车辆占用冲突
     */
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
        // 站点/点位锁
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

        for (RuntimeBlock block : blockRegistry.findByMember(resourceName)) {
            // SAME_DIRECTION_ONLY：同 Block 内允许多车共处（点位仍互斥）；完整方向语义后续迭代
            if (!block.isSingleVehicleOnly()) {
                continue;
            }
            Optional<ResourceLock> blockLock = resourceLockService.tryAcquire(
                    ResourceType.BLOCK, block.getBlockId(), vehicleId, orderId, DEFAULT_TTL);
            if (blockLock.isEmpty()) {
                Optional<ResourceLock> held = resourceLockService.listHeldLocks().stream()
                        .filter(l -> l.getResourceType() == ResourceType.BLOCK
                                && block.getBlockId().equals(l.getResourceId()))
                        .findFirst();
                if (held.isPresent() && !vehicleId.equals(held.get().getVehicleId())) {
                    releaseAll(vehicleId);
                    return false;
                }
            } else {
                remember(vehicleId, ResourceType.BLOCK, block.getBlockId(), blockLock.get().getLockId());
            }
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
