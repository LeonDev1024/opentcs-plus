package org.opentcs.map.routing;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.persistence.entity.CrossLayerConnectionEntity;

import java.util.*;

/**
 * 跨楼层路径规划器
 */
@Slf4j
public class CrossFloorRouter {

    private final GlobalRoutingGraph globalGraph;

    public CrossFloorRouter(GlobalRoutingGraph globalGraph) {
        this.globalGraph = globalGraph;
    }

    /**
     * 跨楼层路径规划
     * @param sourcePointId 起点点位ID
     * @param destPointId 终点点位ID
     * @return 路径点序列，如果无法到达返回空列表
     */
    public List<String> planRoute(String sourcePointId, String destPointId) {
        // 1. 获取起点和终点所在的地图
        String sourceMapId = globalGraph.getMapIdByPoint(sourcePointId);
        String destMapId = globalGraph.getMapIdByPoint(destPointId);

        if (sourceMapId == null || destMapId == null) {
            log.warn("起点或终点不存在: sourcePointId={}, destPointId={}", sourcePointId, destPointId);
            return Collections.emptyList();
        }

        // 2. 同楼层路径规划
        if (sourceMapId.equals(destMapId)) {
            return planSameFloorRoute(sourcePointId, destPointId, sourceMapId);
        }

        // 3. 跨楼层路径规划
        return planCrossFloorRoute(sourcePointId, destPointId, sourceMapId, destMapId);
    }

    /**
     * 同楼层路径规划
     */
    private List<String> planSameFloorRoute(String sourcePointId, String destPointId, String mapId) {
        LocalRoutingGraph localGraph = globalGraph.getLocalGraph(mapId);
        if (localGraph == null) {
            log.warn("局部路由图不存在: mapId={}", mapId);
            return Collections.emptyList();
        }

        AStarRouter router = new AStarRouter();
        return router.findRouteByGraph(localGraph, sourcePointId, destPointId);
    }

    /**
     * 跨楼层路径规划
     */
    private List<String> planCrossFloorRoute(String sourcePointId, String destPointId,
                                             String sourceMapId, String destMapId) {
        // 1. 获取起点到电梯口的路径
        List<CrossLayerConnectionEntity> connections = globalGraph.getCrossLayerConnections(sourceMapId, destMapId);
        if (connections.isEmpty()) {
            log.warn("无可用跨层连接: sourceMapId={}, destMapId={}", sourceMapId, destMapId);
            return Collections.emptyList();
        }

        List<String> bestRoute = Collections.emptyList();
        double minCost = Double.MAX_VALUE;

        // 2. 遍历所有可能的电梯组合，找最优解
        for (CrossLayerConnectionEntity connection : connections) {
            List<String> route = buildCrossFloorRoute(sourcePointId, destPointId, connection);
            if (!route.isEmpty()) {
                double cost = calculateRouteCost(route);
                if (cost < minCost) {
                    minCost = cost;
                    bestRoute = route;
                }
            }
        }

        return bestRoute;
    }

    /**
     * 构建跨楼层路径
     */
    private List<String> buildCrossFloorRoute(String sourcePointId, String destPointId,
                                               CrossLayerConnectionEntity connection) {
        List<String> route = new ArrayList<>();

        String elevatorSourcePoint = connection.getSourcePointId();
        String elevatorDestPoint = connection.getDestPointId();

        // 段1: 起点 → 电梯等待点
        LocalRoutingGraph sourceGraph = globalGraph.getLocalGraph(
                globalGraph.getMapIdByPoint(sourcePointId));
        if (sourceGraph == null) {
            return Collections.emptyList();
        }

        AStarRouter router = new AStarRouter();
        List<String> segment1 = router.findRouteByGraph(sourceGraph, sourcePointId, elevatorSourcePoint);
        if (segment1.isEmpty()) {
            return Collections.emptyList();
        }
        route.addAll(segment1);

        // 段2: 电梯调度（添加电梯节点）
        route.add(elevatorSourcePoint + "_ELEVATOR_ENTER");
        route.add(elevatorDestPoint + "_ELEVATOR_EXIT");

        // 段3: 电梯到达点 → 终点
        LocalRoutingGraph destGraph = globalGraph.getLocalGraph(
                globalGraph.getMapIdByPoint(destPointId));
        if (destGraph == null) {
            return Collections.emptyList();
        }

        List<String> segment3 = router.findRouteByGraph(destGraph, elevatorDestPoint, destPointId);
        if (segment3.isEmpty()) {
            return Collections.emptyList();
        }
        // 去掉第一个点（避免重复）
        if (segment3.size() > 1) {
            route.addAll(segment3.subList(1, segment3.size()));
        }

        return route;
    }

    /**
     * 计算路径成本
     */
    private double calculateRouteCost(List<String> routePoints) {
        double totalCost = 0;

        for (int i = 0; i < routePoints.size() - 1; i++) {
            String currentPointId = routePoints.get(i);
            String nextPointId = routePoints.get(i + 1);

            // 检查是否是电梯节点
            if (nextPointId.contains("_ELEVATOR_")) {
                // 电梯成本（固定时间）
                totalCost += 10; // 假设电梯运行成本为10
                continue;
            }

            String mapId = globalGraph.getMapIdByPoint(currentPointId);
            if (mapId == null) {
                continue;
            }

            LocalRoutingGraph localGraph = globalGraph.getLocalGraph(mapId);
            if (localGraph == null) {
                continue;
            }

            // 从邻接表获取边成本
            List<LocalRoutingGraph.Edge> edges = localGraph.getNeighbors(currentPointId);
            for (LocalRoutingGraph.Edge edge : edges) {
                if (edge.getDestPointId().equals(nextPointId)) {
                    totalCost += edge.getCost();
                    break;
                }
            }
        }

        return totalCost;
    }

    /**
     * 验证路径连通性
     */
    public boolean validateConnectivity(String sourcePointId, String destPointId) {
        List<String> route = planRoute(sourcePointId, destPointId);
        return !route.isEmpty();
    }
}
