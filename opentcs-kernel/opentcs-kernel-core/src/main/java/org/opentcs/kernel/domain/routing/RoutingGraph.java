package org.opentcs.kernel.domain.routing;

import java.util.*;

/**
 * 路由图
 */
public class RoutingGraph {

    private final Map<String, Point> points = new HashMap<>();
    private final Map<String, Path> paths = new HashMap<>();
    private final Map<String, List<Path>> adjacencyList = new HashMap<>();

    /**
     * 添加点位
     */
    public void addPoint(Point point) {
        points.put(point.getPointId(), point);
        adjacencyList.putIfAbsent(point.getPointId(), new ArrayList<>());
    }

    /**
     * 添加路径
     */
    public void addPath(Path path) {
        paths.put(path.getPathId(), path);
        // 添加正向边
        adjacencyList.computeIfAbsent(path.getSourcePointId(), k -> new ArrayList<>()).add(path);
        // 添加反向边（如果是双向路径）
        if (isBidirectional(path)) {
            Path reversePath = createReversePath(path);
            adjacencyList.computeIfAbsent(reversePath.getSourcePointId(), k -> new ArrayList<>()).add(reversePath);
        }
    }

    /**
     * 获取点位
     */
    public Point getPoint(String pointId) {
        return points.get(pointId);
    }

    /**
     * 获取路径
     */
    public Path getPath(String pathId) {
        return paths.get(pathId);
    }

    /**
     * 获取所有点位
     */
    public Collection<Point> getAllPoints() {
        return points.values();
    }

    /**
     * 获取所有路径
     */
    public Collection<Path> getAllPaths() {
        return paths.values();
    }

    /**
     * 获取从某点出发的所有路径
     */
    public List<Path> getOutgoingPaths(String pointId) {
        return adjacencyList.getOrDefault(pointId, Collections.emptyList());
    }

    /**
     * 检查路径是否存在
     */
    public boolean hasPath(String sourcePointId, String destPointId) {
        return getOutgoingPaths(sourcePointId).stream()
                .anyMatch(path -> path.getDestPointId().equals(destPointId));
    }

    private boolean isBidirectional(Path path) {
        // 默认假设所有路径都是双向的
        return true;
    }

    private Path createReversePath(Path path) {
        return new Path(
                path.getPathId() + "_reverse",
                path.getDestPointId(),
                path.getSourcePointId(),
                path.getLength()
        );
    }
}
