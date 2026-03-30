package org.opentcs.kernel.domain.routing;

import java.util.*;

/**
 * A* 路径规划算法
 */
public class AStarRouter {

    /**
     * 查找最短路径
     *
     * @param graph       路由图
     * @param startPointId 起点ID
     * @param endPointId  终点ID
     * @return 路径列表（包含所有经过的点位），如果无法到达返回空列表
     */
    public List<String> findRoute(RoutingGraph graph, String startPointId, String endPointId) {
        if (graph.getPoint(startPointId) == null || graph.getPoint(endPointId) == null) {
            return Collections.emptyList();
        }

        // 优先队列：(f(n)) = g(n) + h(n)
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getF));
        Set<String> closedSet = new HashSet<>();

        // 起点
        Node startNode = new Node(startPointId, 0, heuristic(graph, startPointId, endPointId));
        openSet.add(startNode);

        // 记录每个节点的父节点
        Map<String, String> cameFrom = new HashMap<>();
        // 记录从起点到当前节点的实际成本
        Map<String, Double> gScore = new HashMap<>();
        gScore.put(startPointId, 0.0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.getPointId().equals(endPointId)) {
                return reconstructPath(cameFrom, current.getPointId());
            }

            closedSet.add(current.getPointId());

            // 获取当前点的所有出边
            List<Path> outgoingPaths = graph.getOutgoingPaths(current.getPointId());
            for (Path path : outgoingPaths) {
                String neighborId = path.getDestPointId();

                if (closedSet.contains(neighborId)) {
                    continue;
                }

                // 检查路径是否可通行
                if (!path.isTraversable()) {
                    continue;
                }

                double tentativeGScore = gScore.get(current.getPointId()) + path.getLength();

                if (!gScore.containsKey(neighborId) || tentativeGScore < gScore.get(neighborId)) {
                    cameFrom.put(neighborId, current.getPointId());
                    gScore.put(neighborId, tentativeGScore);

                    double fScore = tentativeGScore + heuristic(graph, neighborId, endPointId);
                    openSet.add(new Node(neighborId, tentativeGScore, fScore));
                }
            }
        }

        // 无法到达终点
        return Collections.emptyList();
    }

    /**
     * 查找最短路径（使用Map）
     *
     * @param points 点位Map
     * @param paths 路径Map
     * @param start 起点
     * @param end   终点
     * @return 路径列表（包含所有经过的点位），如果无法到达返回空列表
     */
    public List<Point> findRoute(Map<String, Point> points, Map<String, Path> paths,
                                 Point start, Point end) {
        if (start == null || end == null) {
            return Collections.emptyList();
        }

        // 构建临时的adjacency list
        Map<String, List<Path>> adjacencyList = buildAdjacencyList(paths);

        // 优先队列：(f(n)) = g(n) + h(n)
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getF));
        Set<String> closedSet = new HashSet<>();

        // 起点
        Node startNode = new Node(start.getPointId(), 0, heuristic(points, start, end));
        openSet.add(startNode);

        // 记录每个节点的父节点
        Map<String, String> cameFrom = new HashMap<>();
        // 记录从起点到当前节点的实际成本
        Map<String, Double> gScore = new HashMap<>();
        gScore.put(start.getPointId(), 0.0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.getPointId().equals(end.getPointId())) {
                return reconstructPointPath(points, cameFrom, current.getPointId());
            }

            closedSet.add(current.getPointId());

            // 获取当前点的所有出边
            List<Path> outgoingPaths = adjacencyList.getOrDefault(current.getPointId(), Collections.emptyList());
            for (Path path : outgoingPaths) {
                String neighborId = path.getDestPointId();

                if (closedSet.contains(neighborId)) {
                    continue;
                }

                // 检查路径是否可通行
                if (!path.isTraversable()) {
                    continue;
                }

                double tentativeGScore = gScore.get(current.getPointId()) + path.getLength();

                if (!gScore.containsKey(neighborId) || tentativeGScore < gScore.get(neighborId)) {
                    cameFrom.put(neighborId, current.getPointId());
                    gScore.put(neighborId, tentativeGScore);

                    Point neighborPoint = points.get(neighborId);
                    if (neighborPoint != null) {
                        double fScore = tentativeGScore + heuristic(points, neighborPoint, end);
                        openSet.add(new Node(neighborId, tentativeGScore, fScore));
                    }
                }
            }
        }

        // 无法到达终点
        return Collections.emptyList();
    }

    /**
     * 构建邻接表
     */
    private Map<String, List<Path>> buildAdjacencyList(Map<String, Path> paths) {
        Map<String, List<Path>> adjacencyList = new HashMap<>();

        for (Path path : paths.values()) {
            // 添加正向边
            adjacencyList.computeIfAbsent(path.getSourcePointId(), k -> new ArrayList<>()).add(path);
            // 添加反向边（假设所有路径都是双向的）
            Path reversePath = new Path(
                    path.getPathId() + "_reverse",
                    path.getDestPointId(),
                    path.getSourcePointId(),
                    path.getLength()
            );
            adjacencyList.computeIfAbsent(reversePath.getSourcePointId(), k -> new ArrayList<>()).add(reversePath);
        }

        return adjacencyList;
    }

    /**
     * 重建点路径
     */
    private List<Point> reconstructPointPath(Map<String, Point> points, Map<String, String> cameFrom, String current) {
        List<Point> path = new ArrayList<>();
        path.add(points.get(current));

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, points.get(current));
        }

        return path;
    }

    /**
     * 计算启发式函数（欧几里得距离）
     */
    private double heuristic(RoutingGraph graph, String pointId1, String pointId2) {
        Point p1 = graph.getPoint(pointId1);
        Point p2 = graph.getPoint(pointId2);

        if (p1 == null || p2 == null) {
            return Double.MAX_VALUE;
        }

        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算启发式函数（欧几里得距离）- 使用Map
     */
    private double heuristic(Map<String, Point> points, Point p1, Point p2) {
        if (p1 == null || p2 == null) {
            return Double.MAX_VALUE;
        }

        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 重建路径
     */
    private List<String> reconstructPath(Map<String, String> cameFrom, String current) {
        List<String> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);  // 从前面插入
        }

        return path;
    }

    /**
     * A* 节点
     */
    private static class Node {
        private final String pointId;
        private final double gScore;  // 从起点到当前节点的实际成本
        private final double fScore;  // 估计总成本

        public Node(String pointId, double gScore, double fScore) {
            this.pointId = pointId;
            this.gScore = gScore;
            this.fScore = fScore;
        }

        public String getPointId() {
            return pointId;
        }

        public double getF() {
            return fScore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(pointId, node.pointId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pointId);
        }
    }
}
