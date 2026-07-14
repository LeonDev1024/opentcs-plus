package org.opentcs.kernel.application;

import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.kernel.api.map.MapSceneApi;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 地图运行时加载服务。
 * <p>
 * 当前实现维护单张活动运行地图：发布地图后将点位/路径加载到
 * {@link RoutePlannerImpl}，供路径规划与调度使用。多地图并行运行时，
 * 这里应升级为按 mapId/version 分区的运行态图仓库。
 * </p>
 */
public class MapRuntimeService {

    private static final Logger log = LoggerFactory.getLogger(MapRuntimeService.class);

    private static final String STATUS_PUBLISHED = "1";

    private final MapSceneApi mapSceneApi;
    private final RoutePlannerImpl routePlanner;
    private volatile String activeMapId;
    private volatile String activeMapVersion;

    public MapRuntimeService(MapSceneApi mapSceneApi, RoutePlannerImpl routePlanner) {
        this.mapSceneApi = mapSceneApi;
        this.routePlanner = routePlanner;
    }

    /**
     * 加载已发布地图到运行时路由图。
     *
     * @param mapId 地图业务标识
     * @return 加载结果摘要
     */
    public LoadedMap loadPublishedMap(String mapId) {
        if (mapId == null || mapId.isBlank()) {
            throw new IllegalArgumentException("mapId 不能为空");
        }

        NavigationMapDTO map = mapSceneApi.getNavigationMapByMapId(mapId);
        if (map == null) {
            throw new IllegalArgumentException("地图不存在: " + mapId);
        }
        if (!STATUS_PUBLISHED.equals(map.getStatus())) {
            throw new IllegalStateException("地图尚未发布，不能加载到运行时: " + mapId);
        }
        if (map.getId() == null) {
            throw new IllegalStateException("地图缺少数据库 ID，不能加载到运行时: " + mapId);
        }

        List<PointDTO> points = mapSceneApi.listPointsByMap(map.getId());
        List<PathDTO> paths = mapSceneApi.listPathsByMap(map.getId());

        validateRuntimeGraph(map, points, paths);

        routePlanner.clear();
        for (PointDTO point : points) {
            routePlanner.registerPoint(toDomainPoint(point));
        }
        for (PathDTO path : paths) {
            routePlanner.registerPath(toDomainPath(path));
        }

        activeMapId = map.getMapId();
        activeMapVersion = map.getMapVersion();

        log.info("运行时地图加载完成: mapId={}, version={}, points={}, paths={}",
                activeMapId, activeMapVersion, points.size(), paths.size());

        return new LoadedMap(activeMapId, activeMapVersion, points.size(), paths.size());
    }

    public String getActiveMapId() {
        return activeMapId;
    }

    public String getActiveMapVersion() {
        return activeMapVersion;
    }

    private void validateRuntimeGraph(NavigationMapDTO map,
                                      List<PointDTO> points,
                                      List<PathDTO> paths) {
        if (points == null || points.isEmpty()) {
            throw new IllegalStateException("发布地图缺少点位数据，不能加载到运行时: " + map.getMapId());
        }
        if (paths == null || paths.isEmpty()) {
            throw new IllegalStateException("发布地图缺少路径数据，不能加载到运行时: " + map.getMapId());
        }
        var pointIds = points.stream()
                .map(PointDTO::getPointId)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
        if (pointIds.size() != points.size()) {
            throw new IllegalStateException("发布地图存在空或重复点位 ID，不能加载到运行时: " + map.getMapId());
        }

        Set<String> pathIds = new HashSet<>();
        Map<String, Set<String>> graph = new HashMap<>();
        for (String pointId : pointIds) {
            graph.put(pointId, new HashSet<>());
        }
        for (PathDTO path : paths) {
            if (path.getPathId() == null || path.getPathId().isBlank() || !pathIds.add(path.getPathId())) {
                throw new IllegalStateException("发布地图存在空或重复路径 ID，不能加载到运行时: " + map.getMapId());
            }
            if (path.getSourcePointId() == null || !pointIds.contains(path.getSourcePointId())) {
                throw new IllegalStateException("路径起点不存在，不能加载到运行时: " + path.getPathId());
            }
            if (path.getDestPointId() == null || !pointIds.contains(path.getDestPointId())) {
                throw new IllegalStateException("路径终点不存在，不能加载到运行时: " + path.getPathId());
            }
            validatePathDirection(path);
            graph.get(path.getSourcePointId()).add(path.getDestPointId());
            if (isBidirectional(path)) {
                graph.get(path.getDestPointId()).add(path.getSourcePointId());
            }
        }
        validateConnected(map, pointIds, graph);
    }

    private void validateConnected(NavigationMapDTO map,
                                   Set<String> pointIds,
                                   Map<String, Set<String>> graph) {
        String start = pointIds.iterator().next();
        Set<String> visited = new HashSet<>();
        ArrayDeque<String> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String next : graph.getOrDefault(current, Set.of())) {
                if (visited.add(next)) {
                    queue.add(next);
                }
            }
        }
        if (visited.size() != pointIds.size()) {
            throw new IllegalStateException("发布地图存在不可达点位，不能加载到运行时: "
                    + map.getMapId() + ", reachable=" + visited.size() + "/" + pointIds.size());
        }
    }

    private void validatePathDirection(PathDTO path) {
        Map<String, String> properties = parseProperties(path.getProperties());
        String routingType = path.getRoutingType();
        if (routingType == null || routingType.isBlank()) {
            return;
        }
        String normalized = routingType.trim().toUpperCase();
        if (!Set.of("BIDIRECTIONAL", "TWO_WAY", "ONE_WAY", "FORWARD").contains(normalized)) {
            throw new IllegalStateException("路径方向类型非法，pathId="
                    + path.getPathId() + ", routingType=" + routingType);
        }
        if ("ONE_WAY".equals(normalized) && "true".equalsIgnoreCase(properties.get("bidirectional"))) {
            throw new IllegalStateException("路径方向冲突，ONE_WAY 不能声明 bidirectional=true，pathId="
                    + path.getPathId());
        }
    }

    private boolean isBidirectional(PathDTO path) {
        Map<String, String> properties = parseProperties(path.getProperties());
        String bidirectional = properties.get("bidirectional");
        if (bidirectional != null) {
            return Boolean.parseBoolean(bidirectional);
        }
        String routingType = path.getRoutingType();
        if (routingType == null || routingType.isBlank()) {
            return true;
        }
        String normalized = routingType.trim().toUpperCase();
        return "BIDIRECTIONAL".equals(normalized) || "TWO_WAY".equals(normalized);
    }

    private Point toDomainPoint(PointDTO dto) {
        Point point = new Point(
                requireText(dto.getPointId(), "pointId"),
                dto.getName(),
                toDouble(dto.getXPosition()),
                toDouble(dto.getYPosition()),
                toDouble(dto.getZPosition())
        );
        if (Boolean.TRUE.equals(dto.getLocked())) {
            point.lock();
        }
        if (Boolean.TRUE.equals(dto.getIsOccupied())) {
            point.occupy();
        }
        return point;
    }

    private Path toDomainPath(PathDTO dto) {
        Path path = new Path(
                requireText(dto.getPathId(), "pathId"),
                requireText(dto.getSourcePointId(), "sourcePointId"),
                requireText(dto.getDestPointId(), "destPointId"),
                dto.getLength() == null ? 0.0 : dto.getLength().doubleValue(),
                toNullableDouble(dto.getMaxVelocity()),
                toNullableDouble(dto.getMaxReverseVelocity())
        );
        if (dto.getRoutingType() != null && !dto.getRoutingType().isBlank()) {
            path.getProperties().put("routingType", dto.getRoutingType());
        }
        path.getProperties().putAll(parseProperties(dto.getProperties()));
        if (Boolean.TRUE.equals(dto.getLocked())) {
            path.lock();
        }
        if (Boolean.TRUE.equals(dto.getIsBlocked())) {
            path.block();
        }
        return path;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("地图运行时数据缺少字段: " + fieldName);
        }
        return value;
    }

    private double toDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private Double toNullableDouble(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }

    private Map<String, String> parseProperties(String properties) {
        Map<String, String> parsed = new HashMap<>();
        if (properties == null || properties.isBlank()) {
            return parsed;
        }

        String content = properties.trim();
        if (content.startsWith("{") && content.endsWith("}")) {
            content = content.substring(1, content.length() - 1);
        }
        for (String entry : content.split(",")) {
            String[] pair = entry.split(":", 2);
            if (pair.length != 2) {
                pair = entry.split("=", 2);
            }
            if (pair.length == 2) {
                String key = cleanupPropertyToken(pair[0]);
                String value = cleanupPropertyToken(pair[1]);
                if (!key.isBlank()) {
                    parsed.put(key, value);
                }
            }
        }
        return parsed;
    }

    private String cleanupPropertyToken(String token) {
        return token == null ? "" : token.trim()
                .replaceAll("^\"|\"$", "")
                .replaceAll("^'|'$", "");
    }

    public record LoadedMap(String mapId, String version, int pointCount, int pathCount) {
    }
}
