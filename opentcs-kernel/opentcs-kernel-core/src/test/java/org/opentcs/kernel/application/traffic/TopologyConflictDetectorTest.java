package org.opentcs.kernel.application.traffic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.kernel.application.BlockRegistry;
import org.opentcs.kernel.application.ResourceLockService;
import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.application.runtime.InMemoryRuntimeStateStore;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.opentcs.kernel.domain.routing.Point;
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
        BlockRegistry registry = new BlockRegistry();
        BlockDTO block = new BlockDTO();
        block.setBlockId("B1");
        block.setType("SINGLE_VEHICLE_ONLY");
        block.setMembers(List.of("DEST"));
        registry.replaceAll(List.of(block));
        RoutePlannerImpl planner = mock(RoutePlannerImpl.class);
        detector = new TopologyConflictDetector(lockService, registry, planner);
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

    @Test
    void shouldDetectBlockOccupied() {
        lockService.tryAcquire(ResourceType.BLOCK, "B1", "v-other", "o1", Duration.ofMinutes(1));
        Vehicle vehicle = new Vehicle("v1");
        vehicle.setName("v1");
        vehicle.updatePosition(new VehiclePosition("A", null, 0, 0, 0, 0));
        TransportOrder order = new TransportOrder("ord-1", "ord-1", "A", "DEST",
                List.of(new org.opentcs.kernel.domain.routing.Path("p", "A", "DEST", 10)));

        assertTrue(detector.findAssignConflict(vehicle, order).isPresent());
    }

    @Test
    void shouldIgnoreSameDirectionOnlyBlockLock() {
        BlockRegistry registry = new BlockRegistry();
        BlockDTO block = new BlockDTO();
        block.setBlockId("B-SAME");
        block.setType("SAME_DIRECTION_ONLY");
        block.setMembers(List.of("DEST"));
        registry.replaceAll(List.of(block));
        detector = new TopologyConflictDetector(lockService, registry, mock(RoutePlannerImpl.class));
        lockService.tryAcquire(ResourceType.BLOCK, "B-SAME", "v-other", "o1", Duration.ofMinutes(1));

        Vehicle vehicle = new Vehicle("v1");
        vehicle.setName("v1");
        vehicle.updatePosition(new VehiclePosition("A", null, 0, 0, 0, 0));
        TransportOrder order = new TransportOrder("ord-1", "ord-1", "A", "DEST",
                List.of(new org.opentcs.kernel.domain.routing.Path("p", "A", "DEST", 10)));

        assertTrue(detector.findAssignConflict(vehicle, order).isEmpty());
    }
}
