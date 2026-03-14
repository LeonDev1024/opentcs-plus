package org.opentcs.kernel.application;

import org.opentcs.kernel.domain.routing.AStarRouter;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由规划器实现
 */
public class RoutePlannerImpl {

    private final Map<String, Point> points = new ConcurrentHashMap<>();
    private final Map<String, Path> paths = new ConcurrentHashMap<>();
    private final AStarRouter router = new AStarRouter();

    /**
     * 注册点位
     */
    public void registerPoint(Point point) {
        points.put(point.getPointId(), point);
    }

    /**
     * 注册路径
     */
    public void registerPath(Path path) {
        paths.put(path.getPathId(), path);
    }

    /**
     * 移除点位
     */
    public void unregisterPoint(String pointId) {
        points.remove(pointId);
    }

    /**
     * 移除路径
     */
    public void unregisterPath(String pathId) {
        paths.remove(pathId);
    }

    /**
     * 查找最短路径
     */
    public List<Point> findRoute(String sourcePointId, String destPointId) {
        Point source = points.get(sourcePointId);
        Point dest = points.get(destPointId);

        if (source == null || dest == null) {
            return Collections.emptyList();
        }

        return router.findRoute(points, paths, source, dest);
    }

    /**
     * 查找最短路径（包含详细路径信息）
     */
    public List<Path> findPath(String sourcePointId, String destPointId) {
        Point source = points.get(sourcePointId);
        Point dest = points.get(destPointId);

        if (source == null || dest == null) {
            return Collections.emptyList();
        }

        List<Point> route = router.findRoute(points, paths, source, dest);
        if (route.isEmpty()) {
            return Collections.emptyList();
        }

        // 将点位序列转换为路径序列
        List<Path> result = new ArrayList<>();
        for (int i = 0; i < route.size() - 1; i++) {
            String fromPointId = route.get(i).getPointId();
            String toPointId = route.get(i + 1).getPointId();

            // 查找从fromPoint到toPoint的路径
            Optional<Path> path = paths.values().stream()
                    .filter(p -> p.getSourcePointId().equals(fromPointId)
                            && p.getDestPointId().equals(toPointId))
                    .findFirst();

            path.ifPresent(result::add);
        }

        return result;
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
     * 清空路由图
     */
    public void clear() {
        points.clear();
        paths.clear();
    }

    /**
     * 获取点位数量
     */
    public int getPointCount() {
        return points.size();
    }

    /**
     * 获取路径数量
     */
    public int getPathCount() {
        return paths.size();
    }
}
