package org.opentcs.kernel.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.application.dispatch.RouteCostDispatchStrategy;
import org.opentcs.kernel.application.runtime.InMemoryRuntimeStateStore;
import org.opentcs.kernel.domain.event.OrderWithdrawalRequestedEvent;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DispatcherService 单元测试
 */
@Tag("dev")
class DispatcherServiceTest {

    private VehicleRegistry vehicleRegistry;
    private TransportOrderRegistry orderRegistry;
    private RoutePlannerImpl routePlanner;
    private ApplicationEventPublisher eventPublisher;
    private DispatcherService dispatcherService;

    @BeforeEach
    void setUp() {
        vehicleRegistry = mock(VehicleRegistry.class);
        orderRegistry = mock(TransportOrderRegistry.class);
        routePlanner = mock(RoutePlannerImpl.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        dispatcherService = new DispatcherService(
                vehicleRegistry,
                orderRegistry,
                routePlanner,
                eventPublisher,
                new InMemoryRuntimeStateStore(),
                new RouteCostDispatchStrategy()
        );
        dispatcherService.initialize();
    }

    @Test
    void shouldInitializeOnConstruction() {
        // 普通单元测试不会由 Spring 调用 @PostConstruct，setUp 中显式初始化。
        assertTrue(dispatcherService.isInitialized());
    }

    @Test
    void shouldReturnFalseForDispatchWhenNoOrders() {
        when(orderRegistry.getWaitingOrders()).thenReturn(Collections.emptyList());

        dispatcherService.dispatch();

        // 无订单时不应抛出异常
        verify(orderRegistry, times(1)).getWaitingOrders();
    }

    @Test
    void shouldRunScheduledDispatchWhenInitialized() {
        when(orderRegistry.getWaitingOrders()).thenReturn(Collections.emptyList());

        dispatcherService.scheduledDispatch();

        verify(orderRegistry, times(1)).getWaitingOrders();
    }

    @Test
    void shouldSkipScheduledDispatchWhenTerminated() {
        dispatcherService.terminate();

        dispatcherService.scheduledDispatch();

        verify(orderRegistry, never()).getWaitingOrders();
    }

    @Test
    void shouldReturnFalseForDispatchWhenNoVehicles() {
        TransportOrder order = new TransportOrder("test-order");
        when(orderRegistry.getWaitingOrders()).thenReturn(List.of(order));
        when(vehicleRegistry.getAvailableVehicleDomains()).thenReturn(Collections.emptyList());

        boolean result = dispatcherService.dispatchOrder(order);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForDispatchWhenOrderNotInCorrectState() {
        TransportOrder order = new TransportOrder("test-order");
        order.fail();

        boolean result = dispatcherService.dispatchOrder(order);

        assertFalse(result);
    }

    @Test
    void shouldNotThrowWhenVehicleNotFound() {
        // withdrawOrderByVehicle 返回 void，不应抛出异常
        assertDoesNotThrow(() ->
            dispatcherService.withdrawOrderByVehicle("unknown-vehicle", true));
    }

    @Test
    void shouldSetInitializedToTrue() {
        dispatcherService.terminate();
        assertFalse(dispatcherService.isInitialized());

        dispatcherService.initialize();
        assertTrue(dispatcherService.isInitialized());
    }

    @Test
    void shouldSetInitializedToFalse() {
        assertTrue(dispatcherService.isInitialized());

        dispatcherService.terminate();
        assertFalse(dispatcherService.isInitialized());
    }

    @Test
    void shouldExposeCurrentDispatchStrategyName() {
        assertEquals("route-cost", dispatcherService.getCurrentStrategyName());
    }

    @Test
    void shouldSelectVehicleByRouteCostInsteadOfEuclideanDistance() {
        TransportOrder order = new TransportOrder(
                "order-1", "order-1", "SRC", "DST",
                List.of(new Path("ORDER-PATH", "SRC", "DST", 10)));
        Vehicle farByRoute = vehicle("far-by-route", "FAR");
        Vehicle nearByRoute = vehicle("near-by-route", "NEAR");

        when(vehicleRegistry.getAvailableVehicleDomains())
                .thenReturn(List.of(farByRoute, nearByRoute));
        when(routePlanner.findRouteDomain("FAR", "SRC"))
                .thenReturn(List.of(new Point("FAR", "FAR", 0, 0), new Point("SRC", "SRC", 0, 0)));
        when(routePlanner.findRouteDomain("NEAR", "SRC"))
                .thenReturn(List.of(new Point("NEAR", "NEAR", 0, 0), new Point("SRC", "SRC", 0, 0)));
        when(routePlanner.findPath("FAR", "SRC"))
                .thenReturn(List.of(new Path("FAR-PATH", "FAR", "SRC", 100)));
        when(routePlanner.findPath("NEAR", "SRC"))
                .thenReturn(List.of(new Path("NEAR-PATH", "NEAR", "SRC", 10)));

        boolean result = dispatcherService.dispatchOrder(order);

        assertTrue(result);
        assertEquals("near-by-route", order.getProcessingVehicle());
        assertEquals(order.getOrderId(), nearByRoute.getCurrentOrderId());
        assertNull(farByRoute.getCurrentOrderId());
    }

    @Test
    void shouldRespectIntendedVehicleAsHardConstraint() {
        TransportOrder order = new TransportOrder(
                "order-1", "order-1", "SRC", "DST",
                List.of(new Path("ORDER-PATH", "SRC", "DST", 10)));
        order.setIntendedVehicle("intended-vehicle");
        Vehicle intendedVehicle = vehicle("intended-vehicle", "FAR");
        Vehicle closerVehicle = vehicle("closer-vehicle", "NEAR");

        when(vehicleRegistry.getAvailableVehicleDomains())
                .thenReturn(List.of(intendedVehicle, closerVehicle));
        when(routePlanner.findRouteDomain("FAR", "SRC"))
                .thenReturn(List.of(new Point("FAR", "FAR", 0, 0), new Point("SRC", "SRC", 0, 0)));
        when(routePlanner.findRouteDomain("NEAR", "SRC"))
                .thenReturn(List.of(new Point("NEAR", "NEAR", 0, 0), new Point("SRC", "SRC", 0, 0)));
        when(routePlanner.findPath("FAR", "SRC"))
                .thenReturn(List.of(new Path("FAR-PATH", "FAR", "SRC", 100)));
        when(routePlanner.findPath("NEAR", "SRC"))
                .thenReturn(List.of(new Path("NEAR-PATH", "NEAR", "SRC", 10)));

        boolean result = dispatcherService.dispatchOrder(order);

        assertTrue(result);
        assertEquals("intended-vehicle", order.getProcessingVehicle());
        assertEquals(order.getOrderId(), intendedVehicle.getCurrentOrderId());
        assertNull(closerVehicle.getCurrentOrderId());
    }

    @Test
    void shouldNotAssignOtherVehicleWhenIntendedVehicleUnavailable() {
        TransportOrder order = new TransportOrder(
                "order-1", "order-1", "SRC", "DST",
                List.of(new Path("ORDER-PATH", "SRC", "DST", 10)));
        order.setIntendedVehicle("intended-vehicle");
        Vehicle otherVehicle = vehicle("other-vehicle", "NEAR");

        when(vehicleRegistry.getAvailableVehicleDomains()).thenReturn(List.of(otherVehicle));
        when(routePlanner.findRouteDomain("NEAR", "SRC"))
                .thenReturn(List.of(new Point("NEAR", "NEAR", 0, 0), new Point("SRC", "SRC", 0, 0)));

        boolean result = dispatcherService.dispatchOrder(order);

        assertFalse(result);
        assertNull(order.getProcessingVehicle());
        assertNull(otherVehicle.getCurrentOrderId());
    }

    @Test
    void shouldPublishWithdrawalRequestWhenAssignedOrderIsWithdrawn() {
        TransportOrder order = new TransportOrder(
                "order-1", "order-1", "SRC", "DST",
                List.of(new Path("ORDER-PATH", "SRC", "DST", 10)));
        order.activate();
        order.assignTo("vehicle-1");
        Vehicle vehicle = vehicle("vehicle-1", "SRC");
        vehicle.assignOrder(order.getOrderId());

        when(orderRegistry.getOrder("order-1")).thenReturn(order);
        when(vehicleRegistry.getVehicleDomain("vehicle-1")).thenReturn(vehicle);

        dispatcherService.withdrawOrder("order-1", true);

        verify(eventPublisher, atLeastOnce()).publishEvent((Object) argThat(event ->
                event instanceof OrderWithdrawalRequestedEvent withdrawal
                && withdrawal.getOrderId().equals("order-1")
                && withdrawal.getVehicleId().equals("vehicle-1")
                && withdrawal.isImmediateAbort()
                && withdrawal.getActionType().equals("ABORT_ORDER")));
        assertTrue(order.isCancelled());
        assertNull(vehicle.getCurrentOrderId());
    }

    @Test
    void shouldPenalizeLowEnergyVehicleWhenRouteCostsAreClose() {
        TransportOrder order = new TransportOrder(
                "order-1", "order-1", "SRC", "DST",
                List.of(new Path("ORDER-PATH", "SRC", "DST", 10)));
        Vehicle lowEnergyVehicle = vehicle("low-energy", "NEAR", 5);
        Vehicle healthyVehicle = vehicle("healthy-energy", "FAR", 80);

        when(vehicleRegistry.getAvailableVehicleDomains())
                .thenReturn(List.of(lowEnergyVehicle, healthyVehicle));
        when(routePlanner.findRouteDomain("NEAR", "SRC"))
                .thenReturn(List.of(new Point("NEAR", "NEAR", 0, 0), new Point("SRC", "SRC", 0, 0)));
        when(routePlanner.findRouteDomain("FAR", "SRC"))
                .thenReturn(List.of(new Point("FAR", "FAR", 0, 0), new Point("SRC", "SRC", 0, 0)));
        when(routePlanner.findPath("NEAR", "SRC"))
                .thenReturn(List.of(new Path("NEAR-PATH", "NEAR", "SRC", 10)));
        when(routePlanner.findPath("FAR", "SRC"))
                .thenReturn(List.of(new Path("FAR-PATH", "FAR", "SRC", 20)));

        boolean result = dispatcherService.dispatchOrder(order);

        assertTrue(result);
        assertEquals("healthy-energy", order.getProcessingVehicle());
        assertEquals(order.getOrderId(), healthyVehicle.getCurrentOrderId());
        assertNull(lowEnergyVehicle.getCurrentOrderId());
    }

    @Test
    void shouldRespectRequiredVehicleTypeConstraint() {
        TransportOrder order = new TransportOrder(
                "order-1", "order-1", "SRC", "DST",
                List.of(new Path("ORDER-PATH", "SRC", "DST", 10)));
        order.getProperties().put("requiredVehicleType", "forklift");
        Vehicle forklift = vehicle("forklift-1", "FAR");
        forklift.setTypeId("forklift");
        Vehicle tugger = vehicle("tugger-1", "NEAR");
        tugger.setTypeId("tugger");

        when(vehicleRegistry.getAvailableVehicleDomains()).thenReturn(List.of(tugger, forklift));
        when(routePlanner.findRouteDomain("NEAR", "SRC"))
                .thenReturn(List.of(new Point("NEAR", "NEAR", 0, 0), new Point("SRC", "SRC", 0, 0)));
        when(routePlanner.findRouteDomain("FAR", "SRC"))
                .thenReturn(List.of(new Point("FAR", "FAR", 0, 0), new Point("SRC", "SRC", 0, 0)));
        when(routePlanner.findPath("FAR", "SRC"))
                .thenReturn(List.of(new Path("FAR-PATH", "FAR", "SRC", 100)));
        when(routePlanner.findPath("NEAR", "SRC"))
                .thenReturn(List.of(new Path("NEAR-PATH", "NEAR", "SRC", 10)));

        boolean result = dispatcherService.dispatchOrder(order);

        assertTrue(result);
        assertEquals("forklift-1", order.getProcessingVehicle());
        assertEquals(order.getOrderId(), forklift.getCurrentOrderId());
        assertNull(tugger.getCurrentOrderId());
    }

    @Test
    void shouldRespectAllowedOrderTypesCapabilityConstraint() {
        TransportOrder order = new TransportOrder(
                "order-1", "order-1", "SRC", "DST",
                List.of(new Path("ORDER-PATH", "SRC", "DST", 10)));
        order.getProperties().put("orderType", "pallet");
        Vehicle compatibleVehicle = vehicle("compatible", "FAR");
        compatibleVehicle.getProperties().put("allowedOrderTypes", "pallet, tote");
        Vehicle incompatibleVehicle = vehicle("incompatible", "NEAR");
        incompatibleVehicle.getProperties().put("allowedOrderTypes", "tote");

        when(vehicleRegistry.getAvailableVehicleDomains())
                .thenReturn(List.of(incompatibleVehicle, compatibleVehicle));
        when(routePlanner.findRouteDomain("NEAR", "SRC"))
                .thenReturn(List.of(new Point("NEAR", "NEAR", 0, 0), new Point("SRC", "SRC", 0, 0)));
        when(routePlanner.findRouteDomain("FAR", "SRC"))
                .thenReturn(List.of(new Point("FAR", "FAR", 0, 0), new Point("SRC", "SRC", 0, 0)));
        when(routePlanner.findPath("FAR", "SRC"))
                .thenReturn(List.of(new Path("FAR-PATH", "FAR", "SRC", 100)));
        when(routePlanner.findPath("NEAR", "SRC"))
                .thenReturn(List.of(new Path("NEAR-PATH", "NEAR", "SRC", 10)));

        boolean result = dispatcherService.dispatchOrder(order);

        assertTrue(result);
        assertEquals("compatible", order.getProcessingVehicle());
        assertEquals(order.getOrderId(), compatibleVehicle.getCurrentOrderId());
        assertNull(incompatibleVehicle.getCurrentOrderId());
    }

    private Vehicle vehicle(String vehicleId, String pointId) {
        return vehicle(vehicleId, pointId, 100);
    }

    private Vehicle vehicle(String vehicleId, String pointId, double energyLevel) {
        Vehicle vehicle = new Vehicle(vehicleId);
        vehicle.updateState(VehicleState.IDLE);
        vehicle.updatePosition(new VehiclePosition(pointId, "map-1", 0, 0, 0, 0));
        vehicle.updateEnergy(energyLevel);
        return vehicle;
    }
}
