package org.opentcs.vehicle.application;

import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.kernel.api.OrderTraceKeys;
import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.application.VehicleRegistry;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 将内核运输订单转换为驱动层 {@link DriverOrder}。
 */
@Component
public class DriverOrderFactory {

    private final RoutePlannerImpl routePlanner;
    private final VehicleRegistry vehicleRegistry;

    public DriverOrderFactory(RoutePlannerImpl routePlanner, VehicleRegistry vehicleRegistry) {
        this.routePlanner = routePlanner;
        this.vehicleRegistry = vehicleRegistry;
    }

    public DriverOrder fromTransportOrder(TransportOrder order) {
        return fromTransportOrder(order, null);
    }

    public DriverOrder fromTransportOrder(TransportOrder order, String vehicleId) {
        List<Path> completeRoute = buildCompleteRoute(order, vehicleId);
        DriverOrder driverOrder = new DriverOrder();
        driverOrder.setOrderId(order.getOrderId());
        driverOrder.setOrderVersion(0);
        driverOrder.setNodes(buildNodes(order, completeRoute));
        driverOrder.setEdges(buildEdges(completeRoute));

        Map<String, String> parameters = new HashMap<>();
        String traceId = order.getProperties().get(OrderTraceKeys.TRACE_ID);
        if (traceId != null) {
            parameters.put(OrderTraceKeys.TRACE_ID, traceId);
        }
        parameters.put("sourcePointId", order.getSourcePointId());
        parameters.put("destPointId", order.getDestPointId());
        driverOrder.setParameters(parameters);
        return driverOrder;
    }

    private List<Path> buildCompleteRoute(TransportOrder order, String vehicleId) {
        List<Path> route = new ArrayList<>();
        Vehicle vehicle = vehicleId == null ? null : vehicleRegistry.getVehicleDomain(vehicleId);
        String currentPointId = vehicle != null && vehicle.getPosition() != null
                ? vehicle.getPosition().getPointId()
                : null;
        if (currentPointId != null
                && order.getSourcePointId() != null
                && !currentPointId.equals(order.getSourcePointId())) {
            route.addAll(routePlanner.findPath(
                    currentPointId, order.getSourcePointId(), vehicleId));
        }
        route.addAll(order.getRoute());
        return route;
    }

    private List<DriverOrder.Node> buildNodes(TransportOrder order, List<Path> completeRoute) {
        List<DriverOrder.Node> nodes = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();

        for (Path path : completeRoute) {
            if (path.getSourcePointId() != null) {
                seen.add(path.getSourcePointId());
            }
            if (path.getDestPointId() != null) {
                seen.add(path.getDestPointId());
            }
        }
        if (seen.isEmpty() && order.getSourcePointId() != null) {
            seen.add(order.getSourcePointId());
        }
        if (order.getDestPointId() != null) {
            seen.add(order.getDestPointId());
        }

        int seq = 0;
        for (String pointId : seen) {
            DriverOrder.Node node = new DriverOrder.Node();
            node.setNodeId(pointId);
            node.setSequenceId(String.valueOf(seq++));
            Point point = routePlanner.getPoint(pointId);
            if (point != null) {
                node.setX(point.getX());
                node.setY(point.getY());
                node.setTheta(point.getOrientation());
            }
            nodes.add(node);
        }
        return nodes;
    }

    private List<DriverOrder.Edge> buildEdges(List<Path> route) {
        List<DriverOrder.Edge> edges = new ArrayList<>();
        if (route == null || route.isEmpty()) {
            return edges;
        }
        int seq = 0;
        for (Path path : route) {
            DriverOrder.Edge edge = new DriverOrder.Edge();
            edge.setEdgeId(path.getPathId());
            edge.setSequenceId(String.valueOf(seq++));
            edge.setStartNodeId(path.getSourcePointId());
            edge.setEndNodeId(path.getDestPointId());
            edge.setMaxVelocity(path.getMaxVelocity());
            edge.setMaxReverseVelocity(path.getMaxReverseVelocity());
            edges.add(edge);
        }
        return edges;
    }
}
