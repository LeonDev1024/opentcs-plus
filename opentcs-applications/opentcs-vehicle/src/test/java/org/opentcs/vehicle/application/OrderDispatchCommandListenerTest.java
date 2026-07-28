package org.opentcs.vehicle.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.api.VehicleGateway;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;
import org.opentcs.driver.registry.DriverRegistry;
import org.opentcs.kernel.api.OrderLifecycleApi;
import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.application.TransportOrderRegistry;
import org.opentcs.kernel.domain.event.OrderAssignedEvent;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("dev")
class OrderDispatchCommandListenerTest {

    @Test
    void shouldSendDriverOrderWhenOrderAssigned() {
        CapturingVehicleGateway gateway = new CapturingVehicleGateway();
        DriverRegistry registry = new DriverRegistry(gateway);
        TransportOrderRegistry orderRegistry = new TransportOrderRegistry();
        RoutePlannerImpl routePlanner = new RoutePlannerImpl(unreachableRouter());
        routePlanner.registerPoint(new Point("A", "A", 0, 0));
        routePlanner.registerPoint(new Point("B", "B", 10, 0));

        TransportOrder order = new TransportOrder(
                "order-1", "test", "A", "B",
                List.of(new Path("P1", "A", "B", 10)));
        order.getProperties().put("traceId", "trace-1");
        order.activate();
        order.assignTo("vehicle-1");
        orderRegistry.createOrder(order);

        AtomicReference<String> failed = new AtomicReference<>();
        OrderLifecycleApi lifecycleApi = (orderId, vehicleId, success, reason) -> {
            if (!success) {
                failed.set(reason);
            }
        };

        OrderDispatchCommandListener listener = new OrderDispatchCommandListener(
                registry,
                orderRegistry,
                new DriverOrderFactory(routePlanner),
                lifecycleApi);

        listener.onOrderAssigned(new OrderAssignedEvent("order-1", "vehicle-1"));

        assertEquals("vehicle-1", gateway.lastVehicleId);
        assertNotNull(gateway.lastOrder);
        assertEquals("order-1", gateway.lastOrder.getOrderId());
        assertEquals("trace-1", gateway.lastOrder.getParameters().get("traceId"));
        assertEquals(2, gateway.lastOrder.getNodes().size());
        assertEquals(null, failed.get());
    }

    private static RoutingAlgorithm unreachableRouter() {
        return (points, paths, start, end) -> List.of();
    }

    private static class CapturingVehicleGateway implements VehicleGateway {
        private String lastVehicleId;
        private DriverOrder lastOrder;

        @Override
        public void initialize() {
        }

        @Override
        public void registerAdapter(String driverType, DriverAdapter adapter) {
        }

        @Override
        public void destroy() {
        }

        @Override
        public void registerVehicle(String vehicleId, DriverConfig config) {
        }

        @Override
        public void unregisterVehicle(String vehicleId) {
        }

        @Override
        public Set<String> getRegisteredVehicles() {
            return new HashSet<>();
        }

        @Override
        public Set<String> getOnlineVehicles() {
            return new HashSet<>();
        }

        @Override
        public void sendOrder(String vehicleId, DriverOrder order) {
            this.lastVehicleId = vehicleId;
            this.lastOrder = order;
        }

        @Override
        public void sendInstantAction(String vehicleId, InstantAction action) {
        }

        @Override
        public VehicleStatus getVehicleStatus(String vehicleId) {
            return null;
        }

        @Override
        public void addStatusListener(Consumer<VehicleStatus> listener) {
        }

        @Override
        public void removeStatusListener(Consumer<VehicleStatus> listener) {
        }
    }
}
