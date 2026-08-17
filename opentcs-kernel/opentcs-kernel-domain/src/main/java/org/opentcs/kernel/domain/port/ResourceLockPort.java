package org.opentcs.kernel.domain.port;

import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceType;

import java.util.Collection;

/**
 * 资源锁协作端口（modules 运维 / 审计监听使用）。
 */
public interface ResourceLockPort {

    Collection<ResourceLock> listHeldLocks();

    boolean forceRelease(ResourceType resourceType, String resourceId);

    /**
     * 启动恢复：将持久化的 HELD 锁写回运行态（不改变过期时间）。
     */
    boolean restoreHeldLock(ResourceLock lock);
}
