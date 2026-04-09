package org.opentcs.kernel.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DispatcherService 单元测试
 */
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
                eventPublisher
        );
    }

    @Test
    void shouldInitializeOnConstruction() {
        // 验证初始化状态（通过 @PostConstruct 自动初始化）
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
        order.activate();  // 变为 ACTIVE 状态

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
}