package org.opentcs.kernel.application;

import org.opentcs.kernel.domain.event.ResourceLockChangedEvent;
import org.opentcs.kernel.domain.resource.ResourceLockStatus;
import org.springframework.context.event.EventListener;

/**
 * 将资源锁变化投影到路由约束。
 */
public class ResourceLockRouteConstraintListener {

    private final RoutePlannerImpl routePlanner;

    public ResourceLockRouteConstraintListener(RoutePlannerImpl routePlanner) {
        this.routePlanner = routePlanner;
    }

    @EventListener
    public void onResourceLockChanged(ResourceLockChangedEvent event) {
        routePlanner.setResourceLocked(event.getResourceType(), event.getResourceId(),
                event.getStatus() == ResourceLockStatus.HELD);
    }
}
