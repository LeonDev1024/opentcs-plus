package org.opentcs.simulation.map;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 仿真地图拓扑图：点位 + 有向路径（边）
 * 提供最近点查找（O(n)）和 Dijkstra 最短路径规划
 */
@Slf4j
public class SimMapGraph {

    /** pointId → SimMapPoint */
    private final Map<String, SimMapPoint> pointById = new HashMap<>();

    /** pointId → 出边列表 */
    private final Map<String, List<SimMapEdge>> adjacency = new HashMap<>();

    // ─── 构建 ──────────────────────────────────────────────────

    public void addPoint(SimMapPoint point) {
        pointById.put(point.getPointId(), point);
        adjacency.putIfAbsent(point.getPointId(), new ArrayList<>());
    }

    public void addEdge(SimMapEdge edge) {
        adjacency.computeIfAbsent(edge.getFromId(), k -> new ArrayList<>())
                 .add(edge);
    }

    public boolean isEmpty() {
        return pointById.isEmpty();
    }

    public int pointCount() {
        return pointById.size();
    }

    public int edgeCount() {
        return adjacency.values().stream().mapToInt(List::size).sum();
    }

    public Collection<SimMapPoint> getPoints() {
        return pointById.values();
    }

    public List<SimMapEdge> getAllEdges() {
        List<SimMapEdge> all = new ArrayList<>();
        adjacency.values().forEach(all::addAll);
        return all;
    }

    // ─── 查询 ──────────────────────────────────────────────────

    /**
     * 找到坐标 (x, y) 最近的图节点
     */
    public SimMapPoint findNearest(double x, double y) {
        SimMapPoint best = null;
        double bestDist = Double.MAX_VALUE;
        for (SimMapPoint p : pointById.values()) {
            double d = Math.hypot(p.getX() - x, p.getY() - y);
            if (d < bestDist) {
                bestDist = d;
                best = p;
            }
        }
        return best;
    }

    /**
     * Dijkstra 最短路径规划，返回从 fromId 到 toId 的完整路径（含起终点）。
     * 若无路径可达，返回空列表。
     */
    public List<SimMapPoint> findPath(String fromId, String toId) {
        if (fromId == null || toId == null) return Collections.emptyList();
        if (fromId.equals(toId)) {
            SimMapPoint p = pointById.get(fromId);
            return p != null ? List.of(p) : Collections.emptyList();
        }

        // dist[id] = current best cost to reach id
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        // priority queue: (cost, pointId)
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[0]));

        for (String id : pointById.keySet()) {
            dist.put(id, Double.MAX_VALUE);
        }
        dist.put(fromId, 0.0);
        pq.offer(new double[]{0.0, fromId.hashCode()});
        // Store string keys separately to avoid hashCode collision issues
        Map<Integer, String> hashToId = new HashMap<>();
        for (String id : pointById.keySet()) {
            hashToId.put(id.hashCode(), id);
        }

        // Use string-keyed priority queue instead
        PriorityQueue<Map.Entry<Double, String>> queue = new PriorityQueue<>(
                Comparator.comparingDouble(Map.Entry::getKey));
        queue.offer(Map.entry(0.0, fromId));

        Set<String> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            Map.Entry<Double, String> curr = queue.poll();
            String uid = curr.getValue();
            double cost = curr.getKey();

            if (visited.contains(uid)) continue;
            visited.add(uid);

            if (uid.equals(toId)) break;

            List<SimMapEdge> edges = adjacency.getOrDefault(uid, Collections.emptyList());
            for (SimMapEdge edge : edges) {
                String vid = edge.getToId();
                if (!pointById.containsKey(vid)) continue;
                double newCost = cost + edge.getLength();
                if (newCost < dist.getOrDefault(vid, Double.MAX_VALUE)) {
                    dist.put(vid, newCost);
                    prev.put(vid, uid);
                    queue.offer(Map.entry(newCost, vid));
                }
            }
        }

        // Reconstruct path
        if (!prev.containsKey(toId) && !fromId.equals(toId)) {
            // No path found; fall back to BFS ignoring edge direction (for disconnected graphs)
            log.warn("[SimMapGraph] No directed path from {} to {}, trying undirected BFS", fromId, toId);
            return bfsFallback(fromId, toId);
        }

        LinkedList<SimMapPoint> path = new LinkedList<>();
        String cur = toId;
        while (cur != null) {
            SimMapPoint p = pointById.get(cur);
            if (p == null) return Collections.emptyList();
            path.addFirst(p);
            cur = prev.get(cur);
        }
        return new ArrayList<>(path);
    }

    /**
     * 无向 BFS 回退（图不连通时使用，确保车辆总能找到路）
     */
    private List<SimMapPoint> bfsFallback(String fromId, String toId) {
        // Build undirected adjacency
        Map<String, Set<String>> undirected = new HashMap<>();
        for (Map.Entry<String, List<SimMapEdge>> e : adjacency.entrySet()) {
            for (SimMapEdge edge : e.getValue()) {
                undirected.computeIfAbsent(edge.getFromId(), k -> new HashSet<>()).add(edge.getToId());
                undirected.computeIfAbsent(edge.getToId(), k -> new HashSet<>()).add(edge.getFromId());
            }
        }

        Map<String, String> prev = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(fromId);
        prev.put(fromId, null);

        while (!queue.isEmpty()) {
            String cur = queue.poll();
            if (cur.equals(toId)) break;
            for (String nb : undirected.getOrDefault(cur, Collections.emptySet())) {
                if (!prev.containsKey(nb)) {
                    prev.put(nb, cur);
                    queue.add(nb);
                }
            }
        }

        if (!prev.containsKey(toId)) {
            log.warn("[SimMapGraph] No path at all from {} to {}", fromId, toId);
            return Collections.emptyList();
        }

        LinkedList<SimMapPoint> path = new LinkedList<>();
        String cur = toId;
        while (cur != null) {
            SimMapPoint p = pointById.get(cur);
            if (p == null) return Collections.emptyList();
            path.addFirst(p);
            cur = prev.get(cur);
        }
        return new ArrayList<>(path);
    }
}
