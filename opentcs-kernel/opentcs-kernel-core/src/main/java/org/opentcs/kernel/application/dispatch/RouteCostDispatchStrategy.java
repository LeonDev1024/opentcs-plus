package org.opentcs.kernel.application.dispatch;

import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认派车策略：先执行指定车辆硬约束，再按车辆到订单起点的真实路径成本选择。
 */
public class RouteCostDispatchStrategy implements DispatchStrategy {

    private static final Logger log = LoggerFactory.getLogger(RouteCostDispatchStrategy.class);
    private static final double LOW_ENERGY_THRESHOLD = 30.0;
    private static final double LOW_ENERGY_PENALTY_WEIGHT = 10.0;
    private static final double CHARGING_PENALTY = 1000.0;
    private static final double ACTIVE_ORDER_PENALTY = 10_000.0;
    private static final double DEADLINE_LATENESS_PENALTY_WEIGHT = 2.0;

    @Override
    public String getName() {
        return "route-cost";
    }

    @Override
    public Optional<Vehicle> selectVehicle(TransportOrder order,
                                           List<Vehicle> candidates,
                                           RoutePlannerImpl routePlanner) {
        List<Vehicle> constrainedCandidates = applyIntendedVehicleConstraint(order, candidates);
        constrainedCandidates = applyVehicleCapabilityConstraint(order, constrainedCandidates);
        return constrainedCandidates.stream()
                .map(vehicle -> score(vehicle, order, routePlanner))
                .peek(score -> log.debug("订单 {} 候选车辆 {} 派车评分: {}",
                        order.getOrderId(), score.vehicle().getVehicleId(), score.total()))
                .min(Comparator.comparingDouble(DispatchScore::total))
                .map(DispatchScore::vehicle);
    }

    private List<Vehicle> applyIntendedVehicleConstraint(TransportOrder order, List<Vehicle> candidates) {
        String intendedVehicle = order.getIntendedVehicle();
        if (intendedVehicle == null || intendedVehicle.isBlank()) {
            return candidates;
        }

        List<Vehicle> matched = candidates.stream()
                .filter(vehicle -> intendedVehicle.equals(vehicle.getVehicleId()))
                .collect(Collectors.toList());
        if (matched.isEmpty()) {
            log.warn("订单 {} 指定车辆 {} 当前不可用或不可达，不允许改派其他车辆",
                    order.getOrderId(), intendedVehicle);
        }
        return matched;
    }

    private List<Vehicle> applyVehicleCapabilityConstraint(TransportOrder order, List<Vehicle> candidates) {
        List<Vehicle> vehicleTypeMatched = filterByRequiredVehicleType(order, candidates);
        return filterByAllowedOrderTypes(order, vehicleTypeMatched);
    }

    private List<Vehicle> filterByRequiredVehicleType(TransportOrder order, List<Vehicle> candidates) {
        String requiredType = firstNonBlank(
                order.getProperties().get("requiredVehicleType"),
                order.getProperties().get("vehicleType"));
        if (requiredType == null) {
            return candidates;
        }

        Set<String> allowedTypes = splitCsv(requiredType);
        List<Vehicle> matched = candidates.stream()
                .filter(vehicle -> allowedTypes.contains(vehicle.getTypeId()))
                .collect(Collectors.toList());
        if (matched.isEmpty()) {
            log.warn("订单 {} 要求车型 {}，当前无匹配可用车辆", order.getOrderId(), requiredType);
        }
        return matched;
    }

    private List<Vehicle> filterByAllowedOrderTypes(TransportOrder order, List<Vehicle> candidates) {
        String orderType = order.getProperties().get("orderType");
        if (orderType == null || orderType.isBlank()) {
            return candidates;
        }

        List<Vehicle> matched = candidates.stream()
                .filter(vehicle -> {
                    String allowedOrderTypes = vehicle.getProperties().get("allowedOrderTypes");
                    return allowedOrderTypes == null
                            || allowedOrderTypes.isBlank()
                            || splitCsv(allowedOrderTypes).contains(orderType);
                })
                .collect(Collectors.toList());
        if (matched.isEmpty()) {
            log.warn("订单 {} 类型 {} 当前无能力匹配车辆", order.getOrderId(), orderType);
        }
        return matched;
    }

    private double routeCost(String sourcePointId, String targetPointId, RoutePlannerImpl routePlanner) {
        if (sourcePointId == null || targetPointId == null) {
            return Double.MAX_VALUE;
        }
        if (sourcePointId.equals(targetPointId)) {
            return 0;
        }

        var paths = routePlanner.findPath(sourcePointId, targetPointId);
        if (paths.isEmpty()) {
            return Double.MAX_VALUE;
        }
        return paths.stream()
                .mapToDouble(PathCost::of)
                .sum();
    }

    private DispatchScore score(Vehicle vehicle, TransportOrder order, RoutePlannerImpl routePlanner) {
        double routeCost = routeCost(vehicle.getPosition().getPointId(), order.getSourcePointId(), routePlanner);
        double lowEnergyPenalty = Math.max(0, LOW_ENERGY_THRESHOLD - vehicle.getEnergyLevel())
                * LOW_ENERGY_PENALTY_WEIGHT;
        double chargingPenalty = vehicle.getState() == VehicleState.CHARGING ? CHARGING_PENALTY : 0;
        double activeOrderPenalty = vehicle.getCurrentOrderId() == null ? 0 : ACTIVE_ORDER_PENALTY;
        double deadlinePenalty = deadlinePenalty(order, routeCost);
        return new DispatchScore(vehicle,
                routeCost + lowEnergyPenalty + chargingPenalty + activeOrderPenalty + deadlinePenalty);
    }

    private double deadlinePenalty(TransportOrder order, double routeCost) {
        if (order.getDeadline() == null || routeCost == Double.MAX_VALUE) {
            return 0;
        }
        long estimatedArrivalTime = System.currentTimeMillis() + Math.round(routeCost * 1000);
        long latenessMillis = Math.max(0, estimatedArrivalTime - order.getDeadline());
        return (latenessMillis / 1000.0) * DEADLINE_LATENESS_PENALTY_WEIGHT;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private Set<String> splitCsv(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .collect(Collectors.toSet());
    }

    private record DispatchScore(Vehicle vehicle, double total) {
    }

    private static class PathCost {
        private static double of(org.opentcs.kernel.domain.routing.Path path) {
            return path.travelCost();
        }
    }
}
