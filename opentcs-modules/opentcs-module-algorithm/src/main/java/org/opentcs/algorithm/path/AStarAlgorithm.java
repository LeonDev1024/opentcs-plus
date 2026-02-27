package org.opentcs.algorithm.path;

import java.util.*;

/**
 * A* 路径规划算法
 */
public class AStarAlgorithm {

    /**
     * 计算从起点到终点的最短路径
     * @param start 起点
     * @param goal 终点
     * @param graph 图结构
     * @return 最短路径
     */
    public List<Node> findPath(Node start, Node goal, Graph graph) {
        // 开放列表，存储待评估的节点
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(node -> node.getF()));
        // 关闭列表，存储已评估的节点
        Set<Node> closedList = new HashSet<>();

        // 初始化起点
        start.setG(0);
        start.setH(calculateHeuristic(start, goal));
        start.setF(start.getG() + start.getH());
        openList.add(start);

        while (!openList.isEmpty()) {
            // 获取F值最小的节点
            Node current = openList.poll();

            // 如果到达终点，回溯路径
            if (current.equals(goal)) {
                return reconstructPath(current);
            }

            closedList.add(current);

            // 遍历所有邻居节点
            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTarget();

                // 如果邻居已在关闭列表中，跳过
                if (closedList.contains(neighbor)) {
                    continue;
                }

                // 计算从起点到邻居的距离
                double tentativeG = current.getG() + edge.getWeight();

                // 如果邻居不在开放列表中，或者找到了更短的路径
                if (!openList.contains(neighbor) || tentativeG < neighbor.getG()) {
                    // 更新邻居节点的信息
                    neighbor.setParent(current);
                    neighbor.setG(tentativeG);
                    neighbor.setH(calculateHeuristic(neighbor, goal));
                    neighbor.setF(neighbor.getG() + neighbor.getH());

                    // 如果邻居不在开放列表中，添加到开放列表
                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        // 没有找到路径
        return null;
    }

    /**
     * 计算启发函数（曼哈顿距离）
     * @param node 当前节点
     * @param goal 目标节点
     * @return 启发值
     */
    private double calculateHeuristic(Node node, Node goal) {
        // 曼哈顿距离
        return Math.abs(node.getX() - goal.getX()) + Math.abs(node.getY() - goal.getY());
    }

    /**
     * 回溯路径
     * @param node 终点节点
     * @return 路径列表
     */
    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
    }
}
