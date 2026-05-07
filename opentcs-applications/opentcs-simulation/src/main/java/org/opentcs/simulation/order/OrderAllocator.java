package org.opentcs.simulation.order;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.simulation.map.SimMapGraph;
import org.opentcs.simulation.map.SimMapPoint;
import org.opentcs.simulation.vehicle.SimulatedVehicle;
import org.opentcs.simulation.vehicle.VehicleSimulator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 订单分配器：贪心策略 + 拓扑路径规划（有地图时）
 */
@Slf4j
public class OrderAllocator {

    @Setter
    private VehicleSimulator vehicleSimulator;

    /** 当前地图拓扑图；null 表示随机坐标模式（直线移动） */
    @Setter
    private SimMapGraph mapGraph;

    // ─── 分配入口 ─────────────────────────────────────────────────

    public void allocateOrders(List<SimulatedOrder> orders,
                               List<SimulatedVehicle> vehicles,
                               Map<String, SimulatedOrder> allOrders) {
        if (orders.isEmpty() || vehicles.isEmpty()) return;

        for (SimulatedOrder order : orders) {
            if (order.getState() != SimulatedOrder.OrderState.CREATED) continue;

            // 找距离订单起点最近的空闲车辆
            SimulatedVehicle best = null;
            double minDist = Double.MAX_VALUE;
            for (SimulatedVehicle v : vehicles) {
                if (v.getState() != SimulatedVehicle.VehicleState.IDLE) continue;
                double d = dist(v.getX(), v.getY(), order.getStartX(), order.getStartY());
                if (d < minDist) {
                    minDist = d;
                    best = v;
                }
            }

            if (best != null) {
                assignOrderToVehicle(order, best);
                vehicles.remove(best);
                log.info("Assigned order {} to vehicle {}", order.getOrderId(), best.getName());
            }
        }
    }

    // ─── 分配逻辑 ─────────────────────────────────────────────────

    private void assignOrderToVehicle(SimulatedOrder order, SimulatedVehicle vehicle) {
        order.setState(SimulatedOrder.OrderState.ASSIGNED);
        order.setAssignedVehicleId(vehicle.getVehicleId());
        order.setAssignedVehicle(vehicle);
        order.setAssignedTime(System.currentTimeMillis());
        vehicle.setCurrentOrder(order);

        if (mapGraph != null && !mapGraph.isEmpty()) {
            planWithGraph(order, vehicle);
        } else {
            // 随机坐标模式：直线移动
            order.setRouteToStart(Collections.emptyList());
            order.setRouteToEnd(Collections.emptyList());
            vehicle.moveTo(order.getStartX(), order.getStartY(), 0.0);
        }
    }

    /**
     * 利用地图拓扑图规划路径：
     *   车辆当前位置 → 起点（ASSIGNED 阶段）
     *   起点 → 终点（IN_EXECUTION 阶段）
     */
    private void planWithGraph(SimulatedOrder order, SimulatedVehicle vehicle) {
        SimMapPoint vehiclePoint = mapGraph.findNearest(vehicle.getX(), vehicle.getY());
        SimMapPoint startPoint   = mapGraph.findNearest(order.getStartX(), order.getStartY());
        SimMapPoint endPoint     = mapGraph.findNearest(order.getEndX(), order.getEndY());

        // 路径：当前位置→起点
        List<SimMapPoint> routeToStart = vehiclePoint.getPointId().equals(startPoint.getPointId())
                ? List.of(startPoint)
                : mapGraph.findPath(vehiclePoint.getPointId(), startPoint.getPointId());

        // 路径：起点→终点
        List<SimMapPoint> routeToEnd = startPoint.getPointId().equals(endPoint.getPointId())
                ? List.of(endPoint)
                : mapGraph.findPath(startPoint.getPointId(), endPoint.getPointId());

        order.setRouteToStart(routeToStart.isEmpty() ? Collections.emptyList() : routeToStart);
        order.setRouteToEnd(routeToEnd.isEmpty() ? Collections.emptyList() : routeToEnd);

        // 更新订单坐标为最近图节点（使 IN_EXECUTION 判断一致）
        order.setStartX(startPoint.getX());
        order.setStartY(startPoint.getY());
        order.setEndX(endPoint.getX());
        order.setEndY(endPoint.getY());

        if (!routeToStart.isEmpty()) {
            vehicle.moveByRoute(routeToStart);
        } else {
            // 回退：直接吸附到起点
            vehicle.moveTo(startPoint.getX(), startPoint.getY(), 0.0);
        }

        log.info("Order {} graph route: vehiclePoint={}, startPoint={}, endPoint={}, " +
                        "routeToStart.size={}, routeToEnd.size={}",
                order.getOrderId(),
                vehiclePoint.getPointId(), startPoint.getPointId(), endPoint.getPointId(),
                routeToStart.size(), routeToEnd.size());
    }

    // ─── 工具 ─────────────────────────────────────────────────────

    private static double dist(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1, dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
