package org.opentcs.strategies.builtin;

import org.opentcs.algorithm.spi.AlgorithmMeta;
import org.opentcs.algorithm.spi.RoutingAlgorithmPlugin;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;

import java.util.*;

/**
 * A* 路径规划算法实现（内置默认插件）。
 * <p>
 * 实现 {@link RoutingAlgorithmPlugin} SPI 接口，可被更高优先级的自定义算法插件替换。
 * 通过配置 {@code opentcs.algorithm.routing.provider=astar} 激活（默认值）。
 * </p>
 * <p>Bean 由 {@code BuiltinStrategiesAutoConfiguration} 显式注册，不依赖组件扫描。</p>
 */
@AlgorithmMeta(
        name = "astar",
        version = "1.0.0",
        description = "A* 启发式路径规划算法（内置默认）",
        author = "OpenTCS Plus"
)
public class AStarRoutingAlgorithm implements RoutingAlgorithmPlugin {

    @Override
    public List<Point> findRoute(Map<String, Point> points, Map<String, Path> paths,
                                 Point start, Point end) {
        if (start == null || end == null) {
            return Collections.emptyList();
        }

        Map<String, List<Path>> adjacencyList = buildAdjacencyList(paths);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getF));
        Set<String> closedSet = new HashSet<>();

        Node startNode = new Node(start.getPointId(), 0, heuristic(start, end));
        openSet.add(startNode);

        Map<String, String> cameFrom = new HashMap<>();
        Map<String, Double> gScore = new HashMap<>();
        gScore.put(start.getPointId(), 0.0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.getPointId().equals(end.getPointId())) {
                return reconstructPath(points, cameFrom, current.getPointId());
            }

            closedSet.add(current.getPointId());

            for (Path path : adjacencyList.getOrDefault(current.getPointId(), Collections.emptyList())) {
                String neighborId = path.getDestPointId();

                if (closedSet.contains(neighborId) || !path.isTraversable()) {
                    continue;
                }

                double tentativeG = gScore.get(current.getPointId()) + path.getLength();

                if (!gScore.containsKey(neighborId) || tentativeG < gScore.get(neighborId)) {
                    cameFrom.put(neighborId, current.getPointId());
                    gScore.put(neighborId, tentativeG);

                    Point neighbor = points.get(neighborId);
                    if (neighbor != null) {
                        double f = tentativeG + heuristic(neighbor, end);
                        openSet.add(new Node(neighborId, tentativeG, f));
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    private Map<String, List<Path>> buildAdjacencyList(Map<String, Path> paths) {
        Map<String, List<Path>> adjacency = new HashMap<>();
        for (Path path : paths.values()) {
            adjacency.computeIfAbsent(path.getSourcePointId(), k -> new ArrayList<>()).add(path);
            Path reverse = new Path(
                    path.getPathId() + "_reverse",
                    path.getDestPointId(),
                    path.getSourcePointId(),
                    path.getLength()
            );
            adjacency.computeIfAbsent(reverse.getSourcePointId(), k -> new ArrayList<>()).add(reverse);
        }
        return adjacency;
    }

    private List<Point> reconstructPath(Map<String, Point> points, Map<String, String> cameFrom, String current) {
        List<Point> path = new ArrayList<>();
        path.add(points.get(current));
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, points.get(current));
        }
        return path;
    }

    private double heuristic(Point p1, Point p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static class Node {
        private final String pointId;
        private final double gScore;
        private final double fScore;

        Node(String pointId, double gScore, double fScore) {
            this.pointId = pointId;
            this.gScore = gScore;
            this.fScore = fScore;
        }

        String getPointId() { return pointId; }
        double getF() { return fScore; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            return Objects.equals(pointId, ((Node) o).pointId);
        }

        @Override
        public int hashCode() { return Objects.hash(pointId); }
    }
}
