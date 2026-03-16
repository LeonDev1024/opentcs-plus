package org.opentcs.map.routing;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A* 路径规划算法
 */
public class AStarRouter {

    /**
     * 查找最短路径（使用LocalRoutingGraph）
     */
    public List<String> findRouteByGraph(LocalRoutingGraph graph, String startPointId, String endPointId) {
        if (!graph.hasPoint(startPointId) || !graph.hasPoint(endPointId)) {
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
            List<LocalRoutingGraph.Edge> outgoingEdges = graph.getNeighbors(current.getPointId());
            for (LocalRoutingGraph.Edge edge : outgoingEdges) {
                String neighborId = edge.getDestPointId();

                if (closedSet.contains(neighborId)) {
                    continue;
                }

                double tentativeGScore = gScore.get(current.getPointId()) + edge.getCost();

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
     * 计算启发式函数（欧几里得距离）
     */
    private double heuristic(LocalRoutingGraph graph, String pointId1, String pointId2) {
        var p1 = graph.getPoint(pointId1);
        var p2 = graph.getPoint(pointId2);

        if (p1 == null || p2 == null) {
            return Double.MAX_VALUE;
        }

        double dx = p1.getXPosition().doubleValue() - p2.getXPosition().doubleValue();
        double dy = p1.getYPosition().doubleValue() - p2.getYPosition().doubleValue();
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
            path.add(0, current);
        }

        return path;
    }

    /**
     * A* 节点
     */
    private static class Node {
        private final String pointId;
        private final double gScore;
        private final double fScore;

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
