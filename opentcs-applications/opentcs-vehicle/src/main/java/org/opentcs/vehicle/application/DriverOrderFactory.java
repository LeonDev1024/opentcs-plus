package org.opentcs.vehicle.application;

import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.kernel.api.OrderTraceKeys;
import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.domain.order.OrderStep;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
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

    public DriverOrderFactory(RoutePlannerImpl routePlanner) {
        this.routePlanner = routePlanner;
    }

    public DriverOrder fromTransportOrder(TransportOrder order) {
        DriverOrder driverOrder = new DriverOrder();
        driverOrder.setOrderId(order.getOrderId());
        driverOrder.setOrderVersion(0);
        driverOrder.setNodes(buildNodes(order));
        driverOrder.setEdges(buildEdges(order));

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

    private List<DriverOrder.Node> buildNodes(TransportOrder order) {
        List<DriverOrder.Node> nodes = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();

        if (order.getSourcePointId() != null) {
            seen.add(order.getSourcePointId());
        }
        for (OrderStep step : order.getSteps()) {
            if (step.getSourcePointId() != null) {
                seen.add(step.getSourcePointId());
            }
            if (step.getDestinationPointId() != null) {
                seen.add(step.getDestinationPointId());
            }
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

    private List<DriverOrder.Edge> buildEdges(TransportOrder order) {
        List<DriverOrder.Edge> edges = new ArrayList<>();
        List<Path> route = order.getRoute();
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
