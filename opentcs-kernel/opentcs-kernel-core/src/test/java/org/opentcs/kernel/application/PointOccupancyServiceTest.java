package org.opentcs.kernel.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.application.runtime.InMemoryRuntimeStateStore;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@Tag("dev")
class PointOccupancyServiceTest {

    private ResourceLockService lockService;
    private PointOccupancyService occupancyService;

    @BeforeEach
    void setUp() {
        lockService = new ResourceLockService(new InMemoryRuntimeStateStore(), mock(ApplicationEventPublisher.class));
        occupancyService = new PointOccupancyService(lockService);
    }

    @Test
    void shouldAllowFirstVehicleAndRejectSecondOnSamePoint() {
        assertTrue(occupancyService.onVehicleMoved("v1", "o1", "P1"));
        assertFalse(occupancyService.onVehicleMoved("v2", "o2", "P1"));
        assertTrue(lockService.listHeldLocks().stream()
                .anyMatch(l -> l.getResourceType() == ResourceType.POINT && "P1".equals(l.getResourceId())));
    }

    @Test
    void shouldReleasePointWhenVehicleLeaves() {
        assertTrue(occupancyService.onVehicleMoved("v1", "o1", "P1"));
        occupancyService.releaseAll("v1");
        assertTrue(occupancyService.onVehicleMoved("v2", "o2", "P1"));
    }
}
