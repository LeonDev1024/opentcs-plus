package org.opentcs.algorithm.path;

import java.util.*;

/**
 * 图结构类
 */
public class Graph {
    private Map<Node, List<Edge>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    /**
     * 添加节点
     * @param node 节点
     */
    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    /**
     * 添加边
     * @param source 源节点
     * @param target 目标节点
     * @param weight 权重
     */
    public void addEdge(Node source, Node target, double weight) {
        adjacencyList.putIfAbsent(source, new ArrayList<>());
        adjacencyList.putIfAbsent(target, new ArrayList<>());
        adjacencyList.get(source).add(new Edge(source, target, weight));
        // 如果是双向边，添加反向边
        adjacencyList.get(target).add(new Edge(target, source, weight));
    }

    /**
     * 获取节点的邻居边
     * @param node 节点
     * @return 邻居边列表
     */
    public List<Edge> getNeighbors(Node node) {
        return adjacencyList.getOrDefault(node, Collections.emptyList());
    }

    /**
     * 获取所有节点
     * @return 节点集合
     */
    public Set<Node> getNodes() {
        return adjacencyList.keySet();
    }

    /**
     * 根据ID查找节点
     * @param id 节点ID
     * @return 节点
     */
    public Node findNodeById(String id) {
        for (Node node : adjacencyList.keySet()) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    /**
     * 清除所有节点和边
     */
    public void clear() {
        adjacencyList.clear();
    }
}
