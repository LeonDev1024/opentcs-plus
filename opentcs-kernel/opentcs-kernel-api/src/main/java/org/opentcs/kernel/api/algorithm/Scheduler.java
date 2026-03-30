package org.opentcs.kernel.api.algorithm;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 资源调度策略接口。
 * <p>
 * 负责管理资源的声明、分配和释放，防止冲突和死锁。
 * </p>
 */
public interface Scheduler extends Lifecycle {

    /**
     * 声明资源需求。
     *
     * @param client 调度客户端
     * @param resourceSequence 资源序列
     */
    void claim(Client client, List<Set<String>> resourceSequence);

    /**
     * 异步分配资源。
     *
     * @param client 调度客户端
     * @param resources 要分配的资源
     */
    void allocate(Client client, Set<String> resources);

    /**
     * 检查是否可以分配资源。
     *
     * @param client 调度客户端
     * @param resources 要检查的资源
     * @return true 如果可以安全分配
     */
    boolean mayAllocate(Client client, Set<String> resources);

    /**
     * 同步分配资源。
     *
     * @param client 调度客户端
     * @param resources 要分配的资源
     */
    void allocateNow(Client client, Set<String> resources);

    /**
     * 释放资源。
     *
     * @param client 调度客户端
     * @param resources 要释放的资源
     */
    void free(Client client, Set<String> resources);

    /**
     * 获取当前分配状态。
     *
     * @return 资源ID到客户端ID的映射
     */
    Map<String, String> getAllocations();

    /**
     * 调度客户端接口。
     */
    interface Client {
        String getId();
        String getVehicleId();
        void onAllocation(Set<String> resources);
    }

    /**
     * 资源分配异常。
     */
    class ResourceAllocationException extends Exception {
        public ResourceAllocationException(String message) {
            super(message);
        }
    }
}