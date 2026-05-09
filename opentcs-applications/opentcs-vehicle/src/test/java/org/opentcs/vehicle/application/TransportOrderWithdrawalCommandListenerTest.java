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
import org.opentcs.kernel.domain.event.OrderWithdrawalRequestedEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("dev")
class TransportOrderWithdrawalCommandListenerTest {

    @Test
    void shouldSendCancelInstantActionToVehicleDriver() {
        CapturingVehicleGateway gateway = new CapturingVehicleGateway();
        TransportOrderWithdrawalCommandListener listener =
                new TransportOrderWithdrawalCommandListener(new DriverRegistry(gateway));

        listener.onOrderWithdrawalRequested(
                new OrderWithdrawalRequestedEvent("order-1", "vehicle-1", false));

        assertEquals("vehicle-1", gateway.lastVehicleId);
        assertEquals("CANCEL_ORDER", gateway.lastAction.getActionType());
        assertEquals("order-1", gateway.lastAction.getParameters().get("orderId"));
        assertEquals("false", gateway.lastAction.getParameters().get("immediateAbort"));
    }

    @Test
    void shouldSendAbortInstantActionForImmediateAbort() {
        CapturingVehicleGateway gateway = new CapturingVehicleGateway();
        TransportOrderWithdrawalCommandListener listener =
                new TransportOrderWithdrawalCommandListener(new DriverRegistry(gateway));

        listener.onOrderWithdrawalRequested(
                new OrderWithdrawalRequestedEvent("order-1", "vehicle-1", true));

        assertEquals("ABORT_ORDER", gateway.lastAction.getActionType());
        assertEquals("true", gateway.lastAction.getParameters().get("immediateAbort"));
    }

    private static class CapturingVehicleGateway implements VehicleGateway {
        private String lastVehicleId;
        private InstantAction lastAction;

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
        }

        @Override
        public void sendInstantAction(String vehicleId, InstantAction action) {
            this.lastVehicleId = vehicleId;
            this.lastAction = action;
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
