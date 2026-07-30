package org.opentcs.kernel.application.traffic;

import org.opentcs.kernel.application.ResourceLockService;
import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.vehicle.Vehicle;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 最小拓扑冲突检测：站点占用 / 路径段占用。
 */
public class TopologyConflictDetector {

    private final ResourceLockService resourceLockService;
    private final RoutePlannerImpl routePlanner;

    public TopologyConflictDetector(ResourceLockService resourceLockService,
                                    RoutePlannerImpl routePlanner) {
        this.resourceLockService = resourceLockService;
        this.routePlanner = routePlanner;
    }

    /**
     * @return 冲突原因；empty 表示可通过
     */
    public Optional<String> findAssignConflict(Vehicle vehicle, TransportOrder order) {
        if (vehicle == null || order == null) {
            return Optional.of("INVALID_ARGS");
        }
        String dest = order.getDestPointId();
        if (dest == null || dest.isBlank()) {
            return Optional.empty();
        }

        Collection<ResourceLock> locks = resourceLockService.listHeldLocks();

        Optional<String> pointConflict = conflictOnResource(locks, ResourceType.POINT, dest, vehicle.getVehicleId());
        if (pointConflict.isPresent()) {
            return Optional.of("STATION_OCCUPIED:" + dest + " by " + pointConflict.get());
        }

        String source = vehicle.getPosition() != null ? vehicle.getPosition().getPointId() : order.getSourcePointId();
        if (source != null && !source.isBlank()) {
            List<Path> path = routePlanner.findPath(source, dest);
            for (Path segment : path) {
                Optional<String> pathHolder = conflictOnResource(locks, ResourceType.PATH, segment.getPathId(),
                        vehicle.getVehicleId());
                if (pathHolder.isPresent()) {
                    return Optional.of("PATH_OCCUPIED:" + segment.getPathId() + " by " + pathHolder.get());
                }
                // 对向窄道简化：双向路径若对端点被其他车占用，视为交叉口冲突
                Optional<String> srcHold = conflictOnResource(locks, ResourceType.POINT, segment.getSourcePointId(),
                        vehicle.getVehicleId());
                Optional<String> dstHold = conflictOnResource(locks, ResourceType.POINT, segment.getDestPointId(),
                        vehicle.getVehicleId());
                if (srcHold.isPresent() && dstHold.isPresent() && !srcHold.get().equals(dstHold.get())) {
                    return Optional.of("INTERSECTION_CONFLICT:" + segment.getPathId());
                }
            }
        }
        return Optional.empty();
    }

    private Optional<String> conflictOnResource(Collection<ResourceLock> locks,
                                                ResourceType type,
                                                String resourceId,
                                                String vehicleId) {
        return locks.stream()
                .filter(l -> l.getResourceType() == type && resourceId.equals(l.getResourceId()))
                .filter(l -> !vehicleId.equals(l.getVehicleId()))
                .map(ResourceLock::getVehicleId)
                .findFirst();
    }
}
