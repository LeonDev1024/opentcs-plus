package org.opentcs.kernel.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehicleState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dev")
class VehicleRegistryTest {

    @Test
    void shouldRejectOlderVehicleRuntimeStateBySequence() {
        VehicleRegistry registry = new VehicleRegistry();
        Vehicle vehicle = new Vehicle("vehicle-1");
        registry.registerVehicleDomain(vehicle);

        boolean firstAccepted = registry.reportVehicleRuntimeStateDomain(
                "vehicle-1", VehicleState.EXECUTING, "order-1", 10L);
        boolean staleAccepted = registry.reportVehicleRuntimeStateDomain(
                "vehicle-1", VehicleState.IDLE, null, 9L);

        assertTrue(firstAccepted);
        assertFalse(staleAccepted);
        assertEquals(VehicleState.EXECUTING, vehicle.getState());
        assertEquals("order-1", vehicle.getCurrentOrderId());
        assertEquals(10, vehicle.getLastStatusSequence());
    }

    @Test
    void shouldExposeRuntimeVersionInVehicleDto() {
        VehicleRegistry registry = new VehicleRegistry();
        Vehicle vehicle = new Vehicle("vehicle-1");
        registry.registerVehicleDomain(vehicle);

        registry.reportVehicleRuntimeStateDomain("vehicle-1", VehicleState.IDLE, null, 1L);
        var dto = registry.getVehicle("vehicle-1").orElseThrow();

        assertEquals(1, dto.getRuntimeVersion());
        assertEquals(1, dto.getLastStatusSequence());
    }
}
