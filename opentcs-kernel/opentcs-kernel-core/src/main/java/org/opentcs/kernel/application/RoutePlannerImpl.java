package org.opentcs.kernel.application;

import org.opentcs.kernel.api.RoutePlannerApi;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.dto.RouteDTO;
import org.opentcs.kernel.domain.port.RouteTopologyPort;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 路径规划器，同时实现 {@link RoutePlannerApi} / {@link RouteTopologyPort}。
 * <p>
 * 通过 {@link RoutingAlgorithm} 接口注入具体算法（由 opentcs-algorithm 模块提供），
 * kernel-core 对算法实现完全解耦。
 * </p>
 */
public class RoutePlannerImpl implements RoutePlannerApi, RouteTopologyPort {

    private final Map<String, Point> points = new ConcurrentHashMap<>();
    private final Map<String, Path> paths = new ConcurrentHashMap<>();
    private static final String UNKNOWN_LOCK_OWNER = "<unknown>";

    /**
     * 运行时资源锁：resourceKey -> vehicleId。
     *
     * 路由需要知道锁的持有车辆，才能允许车辆从自己占用的当前位置驶离，
     * 同时继续避让其他车辆占用的点位/路径。
     */
    private final Map<String, String> lockedResources = new ConcurrentHashMap<>();
    private final RoutingAlgorithm router;

    public RoutePlannerImpl(RoutingAlgorithm router) {
        this.router = router;
    }

    // ===== 内部点位/路径注册 =====

    public void registerPoint(Point point) {
        points.put(point.getPointId(), point);
    }

    public void registerPath(Path path) {
        paths.put(path.getPathId(), path);
    }

    public void unregisterPoint(String pointId) {
        points.remove(pointId);
    }

    public void unregisterPath(String pathId) {
        paths.remove(pathId);
    }

    public Point getPoint(String pointId) {
        return points.get(pointId);
    }

    public Path getPath(String pathId) {
        return paths.get(pathId);
    }

    public Collection<Point> getAllPoints() { return Collections.unmodifiableCollection(points.values()); }
    public Collection<Path> getAllPaths()   { return Collections.unmodifiableCollection(paths.values()); }
    public int getPointCount()              { return points.size(); }
    public int getPathCount()               { return paths.size(); }

    public void clear() {
        points.clear();
        paths.clear();
        lockedResources.clear();
    }

    public void setResourceLocked(ResourceType resourceType, String resourceId, boolean locked) {
        setResourceLocked(resourceType, resourceId, null, locked);
    }

    public void setResourceLocked(ResourceType resourceType,
                                  String resourceId,
                                  String vehicleId,
                                  boolean locked) {
        String key = resourceKey(resourceType, resourceId);
        if (locked) {
            lockedResources.put(key, normalizeLockOwner(vehicleId));
        } else {
            lockedResources.remove(key);
        }
    }

    // ===== 内部规划方法（kernel-core 内部使用，返回领域对象）=====

    public List<Point> findRouteDomain(String sourcePointId, String destPointId) {
        return findRouteDomain(sourcePointId, destPointId, null);
    }

    /**
     * 为指定车辆规划路径。该车辆自己持有的资源锁不会被视为障碍。
     */
    public List<Point> findRouteDomain(String sourcePointId, String destPointId, String vehicleId) {
        Point source = points.get(sourcePointId);
        Point dest = points.get(destPointId);
        if (source == null || dest == null) return Collections.emptyList();
        if (isResourceLockedByOtherVehicle(ResourceType.POINT, sourcePointId, vehicleId)
                || isResourceLockedByOtherVehicle(ResourceType.POINT, destPointId, vehicleId)) {
            return Collections.emptyList();
        }
        Map<String, Path> availablePaths = paths.values().stream()
                .filter(path -> isRoutePathAvailable(path, vehicleId))
                .collect(Collectors.toMap(Path::getPathId, p -> p));
        return router.findRoute(points, availablePaths, source, dest);
    }

    public List<Path> findPath(String sourcePointId, String destPointId) {
        return findPath(sourcePointId, destPointId, null);
    }

    /**
     * 为指定车辆规划路径。用于寻车/空驶规划，避免车辆被自己的当前位置锁挡住。
     */
    public List<Path> findPath(String sourcePointId, String destPointId, String vehicleId) {
        List<Point> route = findRouteDomain(sourcePointId, destPointId, vehicleId);
        if (route.isEmpty()) return Collections.emptyList();

        List<Path> result = new ArrayList<>();
        for (int i = 0; i < route.size() - 1; i++) {
            String from = route.get(i).getPointId();
            String to   = route.get(i + 1).getPointId();
            findPathSegment(from, to, vehicleId).ifPresent(result::add);
        }
        return result;
    }

    // ===== RoutePlannerApi 端口实现 =====

    @Override
    public Optional<RouteDTO> findRoute(String sourcePointId, String destPointId) {
        List<Path> pathList = findPath(sourcePointId, destPointId);
        if (pathList.isEmpty()) return Optional.empty();
        return Optional.of(buildRouteDTO(sourcePointId, destPointId, pathList));
    }

    @Override
    public List<RouteDTO> findMultiPointRoute(List<String> pointIds) {
        if (pointIds == null || pointIds.size() < 2) return Collections.emptyList();
        List<RouteDTO> result = new ArrayList<>();
        for (int i = 0; i < pointIds.size() - 1; i++) {
            findRoute(pointIds.get(i), pointIds.get(i + 1)).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public List<RouteDTO> getAllRoutes() {
        // 返回所有路径段作为单跳路线（供地图展示）
        return paths.values().stream()
                .map(p -> buildRouteDTO(p.getSourcePointId(), p.getDestPointId(), List.of(p)))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isReachable(String sourcePointId, String destPointId) {
        return !findPath(sourcePointId, destPointId).isEmpty();
    }

    @Override
    public double getDistance(String sourcePointId, String destPointId) {
        return findPath(sourcePointId, destPointId).stream()
                .mapToDouble(Path::getLength)
                .sum();
    }

    public double getTravelCost(String sourcePointId, String destPointId) {
        List<Path> pathList = findPath(sourcePointId, destPointId);
        if (pathList.isEmpty()) {
            return Double.MAX_VALUE;
        }
        return pathList.stream()
                .mapToDouble(Path::travelCost)
                .sum();
    }

    // ===== 内部工具 =====

    private RouteDTO buildRouteDTO(String sourceId, String destId, List<Path> pathList) {
        RouteDTO dto = new RouteDTO();
        dto.setRouteId(UUID.randomUUID().toString());
        dto.setSourcePointId(sourceId);
        dto.setDestPointId(destId);
        dto.setTotalDistance(pathList.stream().mapToDouble(Path::getLength).sum());
        dto.setPaths(pathList.stream().map(this::toPathDTO).collect(Collectors.toList()));
        return dto;
    }

    private Optional<Path> findPathSegment(String from, String to, String vehicleId) {
        Optional<Path> forward = paths.values().stream()
                .filter(p -> p.getSourcePointId().equals(from) && p.getDestPointId().equals(to))
                .filter(path -> isRoutePathAvailable(path, vehicleId))
                .findFirst();
        if (forward.isPresent()) {
            return forward;
        }
        return paths.values().stream()
                .filter(Path::isBidirectional)
                .filter(p -> p.getSourcePointId().equals(to) && p.getDestPointId().equals(from))
                .filter(path -> isRoutePathAvailable(path, vehicleId))
                .findFirst()
                .map(Path::reverseCopy);
    }

    private boolean isRoutePathAvailable(Path path, String vehicleId) {
        return path.isTraversable()
                && !isResourceLockedByOtherVehicle(ResourceType.PATH, path.getPathId(), vehicleId)
                && !isResourceLockedByOtherVehicle(ResourceType.POINT, path.getSourcePointId(), vehicleId)
                && !isResourceLockedByOtherVehicle(ResourceType.POINT, path.getDestPointId(), vehicleId);
    }

    private boolean isResourceLockedByOtherVehicle(ResourceType resourceType,
                                                   String resourceId,
                                                   String vehicleId) {
        String lockOwner = lockedResources.get(resourceKey(resourceType, resourceId));
        return lockOwner != null
                && (vehicleId == null || !lockOwner.equals(vehicleId));
    }

    private String normalizeLockOwner(String vehicleId) {
        return vehicleId == null || vehicleId.isBlank() ? UNKNOWN_LOCK_OWNER : vehicleId;
    }

    private String resourceKey(ResourceType resourceType, String resourceId) {
        return resourceType + ":" + resourceId;
    }

    private PathDTO toPathDTO(Path p) {
        PathDTO dto = new PathDTO();
        dto.setPathId(p.getPathId());
        dto.setSourcePointId(p.getSourcePointId());
        dto.setDestPointId(p.getDestPointId());
        dto.setLength(BigDecimal.valueOf(p.getLength()));
        return dto;
    }
}
