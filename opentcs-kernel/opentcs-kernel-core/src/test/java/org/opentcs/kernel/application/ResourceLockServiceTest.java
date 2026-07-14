package org.opentcs.kernel.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.application.runtime.InMemoryRuntimeStateStore;
import org.opentcs.kernel.domain.event.ResourceLockChangedEvent;
import org.opentcs.kernel.domain.resource.ResourceLockStatus;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Tag("dev")
class ResourceLockServiceTest {

    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final InMemoryRuntimeStateStore runtimeStateStore = new InMemoryRuntimeStateStore();
    private final ResourceLockService service = new ResourceLockService(
            runtimeStateStore,
            eventPublisher,
            Clock.fixed(Instant.parse("2026-05-08T10:00:00Z"), ZoneOffset.UTC));

    @Test
    void shouldAcquireResourceLockAndRejectConflict() {
        var acquired = service.tryAcquire(ResourceType.PATH, "path-1", "vehicle-1", "order-1",
                Duration.ofSeconds(30));
        var conflict = service.tryAcquire(ResourceType.PATH, "path-1", "vehicle-2", "order-2",
                Duration.ofSeconds(30));

        assertTrue(acquired.isPresent());
        assertTrue(conflict.isEmpty());
        assertEquals("vehicle-1", acquired.get().getVehicleId());
        verify(eventPublisher).publishEvent((Object) argThat(event ->
                event instanceof ResourceLockChangedEvent changed
                        && changed.getStatus() == ResourceLockStatus.HELD
                        && changed.getResourceType() == ResourceType.PATH
                        && "ACQUIRED".equals(changed.getReason())));
    }

    @Test
    void shouldRenewAndReleaseHeldLock() {
        var lock = service.tryAcquire(ResourceType.PATH, "path-1", "vehicle-1", "order-1",
                Duration.ofSeconds(30)).orElseThrow();

        assertTrue(service.renew(lock.getLockId(), ResourceType.PATH, "path-1", "vehicle-1", "order-1",
                Duration.ofMinutes(2)));
        assertTrue(service.release(lock.getLockId(), ResourceType.PATH, "path-1"));
        assertTrue(runtimeStateStore.getResourceLock(ResourceType.PATH, "path-1").isEmpty());
        assertFalse(service.release(lock.getLockId(), ResourceType.PATH, "path-1"));
    }

    @Test
    void shouldExpireOverdueLockBeforeNewAcquire() {
        ResourceLockService shortTtlService = new ResourceLockService(
                runtimeStateStore,
                eventPublisher,
                Clock.fixed(Instant.parse("2026-05-08T10:00:00Z"), ZoneOffset.UTC));
        shortTtlService.tryAcquire(ResourceType.CHARGER, "charger-1", "vehicle-1", "order-1",
                Duration.ofSeconds(1)).orElseThrow();

        ResourceLockService laterService = new ResourceLockService(
                runtimeStateStore,
                eventPublisher,
                Clock.fixed(Instant.parse("2026-05-08T10:00:02Z"), ZoneOffset.UTC));

        var acquired = laterService.tryAcquire(ResourceType.CHARGER, "charger-1", "vehicle-2", "order-2",
                Duration.ofSeconds(30));

        assertTrue(acquired.isPresent());
        assertEquals("vehicle-2", acquired.get().getVehicleId());
    }
}
