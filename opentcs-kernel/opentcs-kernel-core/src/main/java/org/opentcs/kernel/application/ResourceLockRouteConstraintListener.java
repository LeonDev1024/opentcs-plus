package org.opentcs.kernel.application;

import org.opentcs.kernel.domain.event.ResourceLockChangedEvent;
import org.opentcs.kernel.domain.resource.ResourceLockStatus;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.opentcs.kernel.domain.resource.RuntimeBlock;
import org.springframework.context.event.EventListener;

/**
 * 将资源锁变化投影到路由约束；BLOCK 锁会展开到成员 POINT/PATH。
 */
public class ResourceLockRouteConstraintListener {

    private final RoutePlannerImpl routePlanner;
    private final BlockRegistry blockRegistry;

    public ResourceLockRouteConstraintListener(RoutePlannerImpl routePlanner) {
        this(routePlanner, new BlockRegistry());
    }

    public ResourceLockRouteConstraintListener(RoutePlannerImpl routePlanner, BlockRegistry blockRegistry) {
        this.routePlanner = routePlanner;
        this.blockRegistry = blockRegistry;
    }

    @EventListener
    public void onResourceLockChanged(ResourceLockChangedEvent event) {
        boolean locked = event.getStatus() == ResourceLockStatus.HELD;
        routePlanner.setResourceLocked(event.getResourceType(), event.getResourceId(), locked);

        if (event.getResourceType() == ResourceType.BLOCK) {
            blockRegistry.get(event.getResourceId()).ifPresent(block -> projectBlockMembers(block, locked));
        }
    }

    private void projectBlockMembers(RuntimeBlock block, boolean locked) {
        for (String member : block.getMembers()) {
            // 成员可能是点或路径名称/ID，两侧都投影，规划器按命中的 key 过滤
            routePlanner.setResourceLocked(ResourceType.POINT, member, locked);
            routePlanner.setResourceLocked(ResourceType.PATH, member, locked);
        }
    }
}
