package org.opentcs.kernel.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.api.dto.OrderSpecDTO;
import org.opentcs.kernel.api.dto.OrderStateDTO;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.kernel.api.dto.VehicleStateDTO;
import org.opentcs.kernel.domain.event.OrderStateChangedEvent;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.api.map.MapSceneApi;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 运输订单服务测试。
 */
@Tag("dev")
class TransportOrderServiceTest {

    private TransportOrderRegistry registry;
    private RoutePlannerImpl routePlanner;
    private MapRuntimeService mapRuntimeService;
    private ApplicationEventPublisher eventPublisher;
    private TransportOrderService transportOrderService;

    @BeforeEach
    void setUp() {
        registry = new TransportOrderRegistry();
        routePlanner = new RoutePlannerImpl(new PassThroughRoutingAlgorithm());
        MapSceneApi mapSceneApi = mock(MapSceneApi.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        mapRuntimeService = new MapRuntimeService(mapSceneApi, routePlanner);
        transportOrderService = new TransportOrderService(
                registry,
                mock(DispatcherService.class),
                routePlanner,
                mapRuntimeService,
                eventPublisher
        );

        when(mapSceneApi.getNavigationMapByMapId("map-1")).thenReturn(publishedMap());
        when(mapSceneApi.listPointsByMap(100L)).thenReturn(List.of(
                point("P1", 0, 0),
                point("P2", 10, 0)
        ));
        when(mapSceneApi.listPathsByMap(100L)).thenReturn(List.of(path("PATH-1", "P1", "P2")));
    }

    @Test
    void shouldBindCreatedOrderToActiveMapVersion() {
        mapRuntimeService.loadPublishedMap("map-1");

        OrderSpecDTO spec = new OrderSpecDTO();
        spec.setName("order-1");
        spec.setSourcePointId("P1");
        spec.setDestPointId("P2");

        String orderId = transportOrderService.createOrder(spec);
        TransportOrder order = registry.getOrder(orderId);

        assertEquals("map-1", order.getProperties().get("mapId"));
        assertEquals("v1", order.getProperties().get("mapVersion"));
    }

    @Test
    void shouldRejectOrderWhenNoRuntimeMapLoaded() {
        OrderSpecDTO spec = new OrderSpecDTO();
        spec.setName("order-1");
        spec.setSourcePointId("P1");
        spec.setDestPointId("P2");

        assertThrows(IllegalStateException.class, () -> transportOrderService.createOrder(spec));
    }

    @Test
    void shouldRestoreActiveOrderWithOriginalKernelOrderId() {
        mapRuntimeService.loadPublishedMap("map-1");

        OrderSpecDTO spec = new OrderSpecDTO();
        spec.setName("order-1");
        spec.setSourcePointId("P1");
        spec.setDestPointId("P2");

        transportOrderService.restoreOrder("kernel-order-1", spec, OrderStateDTO.ACTIVE, "vehicle-1");

        TransportOrder order = registry.getOrder("kernel-order-1");
        assertEquals("kernel-order-1", order.getOrderId());
        assertEquals(OrderState.ACTIVE, order.getState());
        assertEquals("vehicle-1", order.getProcessingVehicle());
        assertEquals("map-1", order.getProperties().get("mapId"));
        assertEquals("v1", order.getProperties().get("mapVersion"));
    }

    @Test
    void shouldRestoreRecoveringOrderWithoutDispatchingItAsActive() {
        mapRuntimeService.loadPublishedMap("map-1");

        OrderSpecDTO spec = new OrderSpecDTO();
        spec.setName("order-1");
        spec.setSourcePointId("P1");
        spec.setDestPointId("P2");

        transportOrderService.restoreOrder(
                "kernel-order-1", spec, OrderStateDTO.RECOVERING, "vehicle-1");

        TransportOrder order = registry.getOrder("kernel-order-1");
        assertEquals(OrderState.RECOVERING, order.getState());
        assertEquals("vehicle-1", order.getProcessingVehicle());
        assertEquals(0, registry.getAssignedOrders().size());
    }

    @Test
    void shouldConfirmRecoveringOrderWhenVehicleReportsSameActiveOrder() {
        mapRuntimeService.loadPublishedMap("map-1");
        OrderSpecDTO spec = orderSpec();
        transportOrderService.restoreOrder(
                "kernel-order-1", spec, OrderStateDTO.RECOVERING, "vehicle-1");

        transportOrderService.reconcileVehicleRuntimeState(
                "vehicle-1", "kernel-order-1", VehicleStateDTO.EXECUTING,
                List.of("kernel-order-1"), false);

        TransportOrder order = registry.getOrder("kernel-order-1");
        assertEquals(OrderState.ACTIVE, order.getState());
        assertEquals("vehicle-1", order.getProcessingVehicle());
        ArgumentCaptor<OrderStateChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(OrderStateChangedEvent.class);
        verify(eventPublisher, org.mockito.Mockito.atLeastOnce()).publishEvent(eventCaptor.capture());
        OrderStateChangedEvent event = eventCaptor.getAllValues().get(eventCaptor.getAllValues().size() - 1);
        assertEquals(OrderState.RECOVERING, event.getOldState());
        assertEquals(OrderState.ACTIVE, event.getNewState());
        assertEquals("RECOVERY_CONFIRMED", event.getReason());
    }

    @Test
    void shouldFailRecoveringOrderWhenVehicleReportsIdleWithoutOrder() {
        mapRuntimeService.loadPublishedMap("map-1");
        OrderSpecDTO spec = orderSpec();
        transportOrderService.restoreOrder(
                "kernel-order-1", spec, OrderStateDTO.RECOVERING, "vehicle-1");

        transportOrderService.reconcileVehicleRuntimeState(
                "vehicle-1", null, VehicleStateDTO.IDLE, List.of(), false);

        TransportOrder order = registry.getOrder("kernel-order-1");
        assertEquals(OrderState.FAILED, order.getState());
        ArgumentCaptor<OrderStateChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(OrderStateChangedEvent.class);
        verify(eventPublisher, org.mockito.Mockito.atLeastOnce()).publishEvent(eventCaptor.capture());
        OrderStateChangedEvent event = eventCaptor.getAllValues().get(eventCaptor.getAllValues().size() - 1);
        assertEquals("RECOVERY_VEHICLE_IDLE", event.getReason());
    }

    @Test
    void shouldFailRecoveringOrderWhenVehicleReportsFault() {
        mapRuntimeService.loadPublishedMap("map-1");
        transportOrderService.restoreOrder(
                "kernel-order-1", orderSpec(), OrderStateDTO.RECOVERING, "vehicle-1");

        transportOrderService.reconcileVehicleRuntimeState(
                "vehicle-1", "kernel-order-1", VehicleStateDTO.EXECUTING,
                List.of("kernel-order-1"), true);

        TransportOrder order = registry.getOrder("kernel-order-1");
        assertEquals(OrderState.FAILED, order.getState());
    }

    @Test
    void shouldFailRecoveringOrderWhenReportedOrderIsNotActiveOnVehicle() {
        mapRuntimeService.loadPublishedMap("map-1");
        transportOrderService.restoreOrder(
                "kernel-order-1", orderSpec(), OrderStateDTO.RECOVERING, "vehicle-1");

        transportOrderService.reconcileVehicleRuntimeState(
                "vehicle-1", "kernel-order-1", VehicleStateDTO.EXECUTING,
                List.of("another-order"), false);

        TransportOrder order = registry.getOrder("kernel-order-1");
        assertEquals(OrderState.FAILED, order.getState());
    }

    @Test
    void shouldCompleteRecoveringOrderWhenVehicleReportsLastOrderAsIdle() {
        mapRuntimeService.loadPublishedMap("map-1");
        transportOrderService.restoreOrder(
                "kernel-order-1", orderSpec(), OrderStateDTO.RECOVERING, "vehicle-1");

        transportOrderService.reconcileVehicleRuntimeState(
                "vehicle-1", "kernel-order-1", VehicleStateDTO.IDLE, List.of(), false);

        TransportOrder order = registry.getOrder("kernel-order-1");
        assertEquals(OrderState.FINISHED, order.getState());
        ArgumentCaptor<OrderStateChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(OrderStateChangedEvent.class);
        verify(eventPublisher, org.mockito.Mockito.atLeastOnce()).publishEvent(eventCaptor.capture());
        OrderStateChangedEvent event = eventCaptor.getAllValues().get(eventCaptor.getAllValues().size() - 1);
        assertEquals("RECOVERY_COMPLETED_BY_VEHICLE_SNAPSHOT", event.getReason());
    }

    private OrderSpecDTO orderSpec() {
        OrderSpecDTO spec = new OrderSpecDTO();
        spec.setName("order-1");
        spec.setSourcePointId("P1");
        spec.setDestPointId("P2");
        return spec;
    }

    private NavigationMapDTO publishedMap() {
        NavigationMapDTO map = new NavigationMapDTO();
        map.setId(100L);
        map.setMapId("map-1");
        map.setMapVersion("v1");
        map.setStatus("1");
        return map;
    }

    private PointDTO point(String pointId, double x, double y) {
        PointDTO point = new PointDTO();
        point.setPointId(pointId);
        point.setName(pointId);
        point.setXPosition(BigDecimal.valueOf(x));
        point.setYPosition(BigDecimal.valueOf(y));
        return point;
    }

    private PathDTO path(String pathId, String sourcePointId, String destPointId) {
        PathDTO path = new PathDTO();
        path.setPathId(pathId);
        path.setSourcePointId(sourcePointId);
        path.setDestPointId(destPointId);
        path.setLength(BigDecimal.TEN);
        return path;
    }

    private static class PassThroughRoutingAlgorithm implements RoutingAlgorithm {
        @Override
        public List<Point> findRoute(Map<String, Point> points,
                                     Map<String, Path> paths,
                                     Point start,
                                     Point end) {
            return List.of(start, end);
        }
    }
}
