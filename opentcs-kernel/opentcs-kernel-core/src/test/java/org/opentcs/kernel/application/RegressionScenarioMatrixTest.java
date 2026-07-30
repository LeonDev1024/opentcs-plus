package org.opentcs.kernel.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.application.dispatch.RouteCostDispatchStrategy;
import org.opentcs.kernel.application.runtime.InMemoryRuntimeStateStore;
import org.opentcs.kernel.application.traffic.TopologyConflictDetector;
import org.opentcs.kernel.domain.event.OrderWithdrawalRequestedEvent;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Dev 2.0.1 仿真回归场景矩阵（离线 / 禁行 / 取消 / 冲突）。
 * 由 {@code mvn test -Pdev} 默认执行，纳入 CI。
 */
@Tag("dev")
class RegressionScenarioMatrixTest {

    private VehicleRegistry vehicleRegistry;
    private TransportOrderRegistry orderRegistry;
    private RoutePlannerImpl routePlanner;
    private ResourceLockService lockService;
    private ApplicationEventPublisher eventPublisher;
    private DispatcherService dispatcherService;
    private TopologyConflictDetector conflictDetector;

    @BeforeEach
    void setUp() {
        vehicleRegistry = new VehicleRegistry();
        orderRegistry = new TransportOrderRegistry();
        routePlanner = new RoutePlannerImpl(new SimpleRoutingAlgorithm());
        lockService = new ResourceLockService(new InMemoryRuntimeStateStore(), mock(ApplicationEventPublisher.class));
        eventPublisher = mock(ApplicationEventPublisher.class);
        conflictDetector = new TopologyConflictDetector(lockService, routePlanner);
        dispatcherService = new DispatcherService(
                vehicleRegistry,
                orderRegistry,
                routePlanner,
                eventPublisher,
                new InMemoryRuntimeStateStore(),
                new RouteCostDispatchStrategy(),
                conflictDetector
        );
        dispatcherService.initialize();

        routePlanner.registerPoint(new Point("A", "A", 0, 0));
        routePlanner.registerPoint(new Point("B", "B", 10, 0));
        routePlanner.registerPoint(new Point("C", "C", 20, 0));
        Path ab = new Path("PATH-AB", "A", "B", 10);
        Path bc = new Path("PATH-BC", "B", "C", 10);
        routePlanner.registerPath(ab);
        routePlanner.registerPath(bc);
    }

    @Test
    void offlineVehicleIsSkippedFromDispatch() {
        Vehicle offline = vehicle("v-off", "A", VehicleState.OFFLINE);
        Vehicle idle = vehicle("v-idle", "A", VehicleState.IDLE);
        vehicleRegistry.registerVehicleDomain(offline);
        vehicleRegistry.registerVehicleDomain(idle);

        assertEquals(1, vehicleRegistry.getAvailableVehicleDomains().size());
        assertEquals("v-idle", vehicleRegistry.getAvailableVehicleDomains().get(0).getVehicleId());

        TransportOrder order = new TransportOrder("ord-offline", "ord-offline", "A", "B",
                List.of(routePlanner.getPath("PATH-AB")));
        orderRegistry.createOrder(order);

        assertTrue(dispatcherService.dispatchOrder(order));
        assertEquals("v-idle", order.getProcessingVehicle());
    }

    @Test
    void blockedPathPreventsRoutingAndDispatch() {
        Path ab = routePlanner.getPath("PATH-AB");
        ab.block();

        assertTrue(routePlanner.findRouteDomain("A", "B").isEmpty());
        assertFalse(routePlanner.isReachable("A", "B"));

        // 车辆在 A，订单源点 B：必须经禁行路径到达源点 → 派车失败
        Vehicle idle = vehicle("v1", "A", VehicleState.IDLE);
        vehicleRegistry.registerVehicleDomain(idle);
        TransportOrder order = new TransportOrder("ord-blocked", "ord-blocked", "B", "C",
                List.of(routePlanner.getPath("PATH-BC")));
        orderRegistry.createOrder(order);

        assertFalse(dispatcherService.dispatchOrder(order));
        assertEquals(OrderState.ACTIVE, order.getState());
        assertEquals(null, order.getProcessingVehicle());
    }

    @Test
    void withdrawCancelsAssignedOrderAndEmitsEvent() {
        Vehicle idle = vehicle("v1", "A", VehicleState.IDLE);
        vehicleRegistry.registerVehicleDomain(idle);
        TransportOrder order = new TransportOrder("ord-cancel", "ord-cancel", "A", "B",
                List.of(routePlanner.getPath("PATH-AB")));
        orderRegistry.createOrder(order);
        assertTrue(dispatcherService.dispatchOrder(order));

        dispatcherService.withdrawOrder("ord-cancel", true);

        verify(eventPublisher).publishEvent((Object) argThat(event ->
                event instanceof OrderWithdrawalRequestedEvent withdrawal
                        && "ord-cancel".equals(withdrawal.getOrderId())
                        && "v1".equals(withdrawal.getVehicleId())
                        && withdrawal.isImmediateAbort()));
    }

    @Test
    void stationConflictBlocksSecondVehicleAssignment() {
        lockService.tryAcquire(ResourceType.POINT, "B", "v-other", "o-other", Duration.ofMinutes(1));

        Vehicle idle = vehicle("v1", "A", VehicleState.IDLE);
        vehicleRegistry.registerVehicleDomain(idle);
        TransportOrder order = new TransportOrder("ord-conflict", "ord-conflict", "A", "B",
                List.of(routePlanner.getPath("PATH-AB")));
        orderRegistry.createOrder(order);

        assertTrue(conflictDetector.findAssignConflict(idle, order).isPresent());
        assertFalse(dispatcherService.dispatchOrder(order));
    }

    private Vehicle vehicle(String id, String pointId, VehicleState state) {
        Vehicle v = new Vehicle(id);
        v.setName(id);
        v.updateState(state);
        v.updatePosition(new VehiclePosition(pointId, null, 0, 0, 0, 0));
        return v;
    }

    /** 仅沿路径邻接前进，尊重 isTraversable。 */
    private static class SimpleRoutingAlgorithm implements RoutingAlgorithm {
        @Override
        public List<Point> findRoute(Map<String, Point> points,
                                     Map<String, Path> paths,
                                     Point start,
                                     Point end) {
            if (start.getPointId().equals(end.getPointId())) {
                return List.of(start);
            }
            for (Path path : paths.values()) {
                if (!path.isTraversable()) {
                    continue;
                }
                if (path.getSourcePointId().equals(start.getPointId())
                        && path.getDestPointId().equals(end.getPointId())) {
                    return List.of(start, end);
                }
                if (path.isBidirectional()
                        && path.getDestPointId().equals(start.getPointId())
                        && path.getSourcePointId().equals(end.getPointId())) {
                    return List.of(start, end);
                }
            }
            // 两跳：A→B→C
            for (Path first : paths.values()) {
                if (!first.isTraversable() || !first.getSourcePointId().equals(start.getPointId())) {
                    continue;
                }
                String mid = first.getDestPointId();
                for (Path second : paths.values()) {
                    if (!second.isTraversable()) {
                        continue;
                    }
                    if (second.getSourcePointId().equals(mid) && second.getDestPointId().equals(end.getPointId())) {
                        return List.of(start, points.get(mid), end);
                    }
                }
            }
            return List.of();
        }
    }
}
