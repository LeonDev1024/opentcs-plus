package org.opentcs.kernel.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.kernel.application.runtime.InMemoryRuntimeStateStore;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@Tag("dev")
class BlockOccupancyServiceTest {

    private ResourceLockService lockService;
    private BlockRegistry blockRegistry;
    private BlockOccupancyService occupancyService;

    @BeforeEach
    void setUp() {
        lockService = new ResourceLockService(new InMemoryRuntimeStateStore(), mock(ApplicationEventPublisher.class));
        blockRegistry = new BlockRegistry();
        BlockDTO block = new BlockDTO();
        block.setBlockId("B1");
        block.setName("Zone-A");
        block.setType("SINGLE_VEHICLE_ONLY");
        block.setMembers(List.of("P1", "P2"));
        blockRegistry.replaceAll(List.of(block));
        occupancyService = new BlockOccupancyService(lockService, blockRegistry);
    }

    @Test
    void shouldAllowFirstVehicleAndRejectSecondOnSameBlock() {
        assertTrue(occupancyService.onVehicleMoved("v1", "o1", "P1"));
        assertFalse(occupancyService.onVehicleMoved("v2", "o2", "P2"));
    }

    @Test
    void shouldReleaseBlockWhenVehicleLeaves() {
        assertTrue(occupancyService.onVehicleMoved("v1", "o1", "P1"));
        occupancyService.releaseAll("v1");
        assertTrue(occupancyService.onVehicleMoved("v2", "o2", "P2"));
    }

    @Test
    void shouldOccupyPointEvenWithoutBlock() {
        assertTrue(occupancyService.onVehicleMoved("v1", "o1", "PX"));
        assertFalse(occupancyService.onVehicleMoved("v2", "o2", "PX"));
        assertTrue(lockService.listHeldLocks().stream()
                .anyMatch(l -> l.getResourceType() == ResourceType.POINT && "PX".equals(l.getResourceId())));
    }
}
