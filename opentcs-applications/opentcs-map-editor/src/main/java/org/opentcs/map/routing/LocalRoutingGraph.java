package org.opentcs.map.routing;

import lombok.Getter;
import org.opentcs.kernel.persistence.entity.PathEntity;
import org.opentcs.kernel.persistence.entity.PointEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 局部路由图 - 单个导航地图内的路由图
 */
@Getter
public class LocalRoutingGraph {

    /**
     * 邻接表：pointId -> 出边列表
     */
    private final Map<String, List<Edge>> adjacency = new HashMap<>();

    /**
     * 点位Map：pointId -> PointEntity
     */
    private final Map<String, PointEntity> points = new HashMap<>();

    /**
     * 路径Map：pathId -> PathEntity
     */
    private final Map<String, PathEntity> paths = new HashMap<>();

    /**
     * 导航地图ID
     */
    private final String mapId;

    public LocalRoutingGraph(String mapId) {
        this.mapId = mapId;
    }

    /**
     * 添加点位
     */
    public void addPoint(PointEntity point) {
        points.put(point.getPointId(), point);
        adjacency.putIfAbsent(point.getPointId(), new ArrayList<>());
    }

    /**
     * 添加路径
     */
    public void addPath(PathEntity path) {
        paths.put(path.getPathId(), path);

        double length = path.getLength() != null ? path.getLength().doubleValue() : 0;
        Double maxVel = path.getMaxVelocity() != null ? path.getMaxVelocity().doubleValue() : null;
        Double maxReverseVel = path.getMaxReverseVelocity() != null ? path.getMaxReverseVelocity().doubleValue() : null;

        // 添加正向边
        Edge forward = new Edge(path.getDestPointId(), length, maxVel, path.getPathId());
        adjacency.computeIfAbsent(path.getSourcePointId(), k -> new ArrayList<>()).add(forward);

        // 如果是双向路径，添加反向边
        if ("BIDIRECTIONAL".equals(path.getRoutingType())) {
            Edge backward = new Edge(path.getSourcePointId(), length, maxReverseVel, path.getPathId());
            adjacency.computeIfAbsent(path.getDestPointId(), k -> new ArrayList<>()).add(backward);
        }
    }

    /**
     * 获取指定点的所有邻居
     */
    public List<Edge> getNeighbors(String pointId) {
        return adjacency.getOrDefault(pointId, Collections.emptyList());
    }

    /**
     * 获取指定点位
     */
    public PointEntity getPoint(String pointId) {
        return points.get(pointId);
    }

    /**
     * 获取指定路径
     */
    public PathEntity getPath(String pathId) {
        return paths.get(pathId);
    }

    /**
     * 检查点是否存在
     */
    public boolean hasPoint(String pointId) {
        return points.containsKey(pointId);
    }

    /**
     * 获取所有点位ID
     */
    public Set<String> getAllPointIds() {
        return points.keySet();
    }

    /**
     * 边（路径）
     */
    @Getter
    public static class Edge {
        private final String destPointId;
        private final double length;
        private final Double maxVelocity;
        private final String pathId;

        public Edge(String destPointId, double length, Double maxVelocity, String pathId) {
            this.destPointId = destPointId;
            this.length = length;
            this.maxVelocity = maxVelocity;
            this.pathId = pathId;
        }

        /**
         * 计算边的成本（根据速度限制）
         */
        public double getCost() {
            if (maxVelocity != null && maxVelocity > 0) {
                return length / maxVelocity;
            }
            return length;
        }
    }
}
