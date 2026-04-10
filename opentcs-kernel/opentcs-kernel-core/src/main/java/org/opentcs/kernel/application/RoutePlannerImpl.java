package org.opentcs.kernel.application;

import org.opentcs.kernel.api.RoutePlannerApi;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.dto.RouteDTO;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 路径规划器，同时实现 {@link RoutePlannerApi} 端口接口。
 * <p>
 * 通过 {@link RoutingAlgorithm} 接口注入具体算法（由 opentcs-algorithm 模块提供），
 * kernel-core 对算法实现完全解耦。
 * </p>
 */
public class RoutePlannerImpl implements RoutePlannerApi {

    private final Map<String, Point> points = new ConcurrentHashMap<>();
    private final Map<String, Path> paths = new ConcurrentHashMap<>();
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
    }

    // ===== 内部规划方法（kernel-core 内部使用，返回领域对象）=====

    public List<Point> findRouteDomain(String sourcePointId, String destPointId) {
        Point source = points.get(sourcePointId);
        Point dest = points.get(destPointId);
        if (source == null || dest == null) return Collections.emptyList();
        return router.findRoute(points, paths, source, dest);
    }

    public List<Path> findPath(String sourcePointId, String destPointId) {
        List<Point> route = findRouteDomain(sourcePointId, destPointId);
        if (route.isEmpty()) return Collections.emptyList();

        List<Path> result = new ArrayList<>();
        for (int i = 0; i < route.size() - 1; i++) {
            String from = route.get(i).getPointId();
            String to   = route.get(i + 1).getPointId();
            paths.values().stream()
                    .filter(p -> p.getSourcePointId().equals(from) && p.getDestPointId().equals(to))
                    .findFirst()
                    .ifPresent(result::add);
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

    private PathDTO toPathDTO(Path p) {
        PathDTO dto = new PathDTO();
        dto.setPathId(p.getPathId());
        dto.setSourcePointId(p.getSourcePointId());
        dto.setDestPointId(p.getDestPointId());
        dto.setLength(BigDecimal.valueOf(p.getLength()));
        return dto;
    }
}
