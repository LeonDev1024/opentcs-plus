package org.opentcs.kernel.application.traffic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.application.ResourceLockService;
import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.application.runtime.InMemoryRuntimeStateStore;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@Tag("dev")
class TopologyConflictDetectorTest {

    private ResourceLockService lockService;
    private TopologyConflictDetector detector;

    @BeforeEach
    void setUp() {
        lockService = new ResourceLockService(new InMemoryRuntimeStateStore(), mock(ApplicationEventPublisher.class));
        RoutePlannerImpl planner = mock(RoutePlannerImpl.class);
        detector = new TopologyConflictDetector(lockService, planner);
    }

    @Test
    void shouldDetectStationOccupied() {
        lockService.tryAcquire(ResourceType.POINT, "DEST", "v-other", "o1", Duration.ofMinutes(1));
        Vehicle vehicle = new Vehicle("v1");
        vehicle.setName("v1");
        vehicle.updatePosition(new VehiclePosition("A", null, 0, 0, 0, 0));
        TransportOrder order = new TransportOrder("ord-1", "ord-1", "A", "DEST",
                List.of(new org.opentcs.kernel.domain.routing.Path("p", "A", "DEST", 10)));

        assertTrue(detector.findAssignConflict(vehicle, order).isPresent());
    }
}
