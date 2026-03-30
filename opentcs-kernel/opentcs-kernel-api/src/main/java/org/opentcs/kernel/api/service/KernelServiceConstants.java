package org.opentcs.kernel.api.service;

/**
 * 核心服务接口定义。
 * <p>
 * strategies 模块需要的服务接口，这些接口由 kernel-core 实现，
 * strategies 通过 Spring 注入使用。
 * </p>
 */
public interface KernelServiceConstants {

    /**
     * 内核执行器线程池 Bean 名称。
     */
    String KERNEL_EXECUTOR_BEAN = "kernelExecutor";

    /**
     * 调度器 Bean 名称。
     */
    String SCHEDULER_BEAN = "scheduler";

    /**
     * 路由器 Bean 名称。
     */
    String ROUTER_BEAN = "router";

    /**
     * 调度器 Bean 名称。
     */
    String DISPATCHER_BEAN = "dispatcher";
}