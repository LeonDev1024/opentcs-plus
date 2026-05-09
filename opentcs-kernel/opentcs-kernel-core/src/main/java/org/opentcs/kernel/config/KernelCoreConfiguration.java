package org.opentcs.kernel.config;

import org.opentcs.kernel.api.OrderLifecycleApi;
import org.opentcs.kernel.api.RoutePlannerApi;
import org.opentcs.kernel.api.TransportOrderApi;
import org.opentcs.kernel.api.VehicleRegistryApi;
import org.opentcs.kernel.api.algorithm.Dispatcher;
import org.opentcs.kernel.api.map.MapSceneApi;
import org.opentcs.kernel.application.*;
import org.opentcs.kernel.application.dispatch.DispatchStrategy;
import org.opentcs.kernel.application.dispatch.RouteCostDispatchStrategy;
import org.opentcs.kernel.application.runtime.InMemoryRuntimeStateStore;
import org.opentcs.kernel.application.runtime.RedisRuntimeStateStore;
import org.opentcs.kernel.application.runtime.RuntimeStateStore;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 内核核心配置——将 kernel-core 应用服务注册为 Spring Bean 并暴露 kernel-api 端口接口。
 */
@Configuration
public class KernelCoreConfiguration {

    @Bean
    public VehicleRegistry vehicleRegistry() {
        return new VehicleRegistry();
    }

    @Bean
    public VehicleRegistryApi vehicleRegistryApi(VehicleRegistry vehicleRegistry) {
        return vehicleRegistry;
    }

    @Bean
    public TransportOrderRegistry transportOrderRegistry() {
        return new TransportOrderRegistry();
    }

    @Bean
    public RuntimeStateStore runtimeStateStore(
            @Value("${opentcs.runtime-state.store:memory}") String storeType) {
        if ("redis".equalsIgnoreCase(storeType)) {
            return new RedisRuntimeStateStore();
        }
        return new InMemoryRuntimeStateStore();
    }

    @Bean
    public ResourceLockService resourceLockService(RuntimeStateStore runtimeStateStore,
                                                   ApplicationEventPublisher eventPublisher) {
        return new ResourceLockService(runtimeStateStore, eventPublisher);
    }

    @Bean
    public ResourceLockRouteConstraintListener resourceLockRouteConstraintListener(
            RoutePlannerImpl routePlanner) {
        return new ResourceLockRouteConstraintListener(routePlanner);
    }

    @Bean
    public RoutePlannerImpl routePlanner(RoutingAlgorithm routingAlgorithm) {
        return new RoutePlannerImpl(routingAlgorithm);
    }

    @Bean
    public RoutePlannerApi routePlannerApi(RoutePlannerImpl routePlanner) {
        return routePlanner;
    }

    @Bean
    public MapRuntimeService mapRuntimeService(MapSceneApi mapSceneApi,
                                               RoutePlannerImpl routePlanner) {
        return new MapRuntimeService(mapSceneApi, routePlanner);
    }

    @Bean
    public DispatchStrategy dispatchStrategy(
            @Value("${opentcs.dispatch.strategy:route-cost}") String strategyName) {
        if ("route-cost".equals(strategyName)) {
            return new RouteCostDispatchStrategy();
        }
        throw new IllegalArgumentException("不支持的派车策略: " + strategyName);
    }

    @Bean
    public DispatcherService dispatcherService(VehicleRegistry vehicleRegistry,
                                               TransportOrderRegistry transportOrderRegistry,
                                               RoutePlannerImpl routePlanner,
                                               ApplicationEventPublisher eventPublisher,
                                               RuntimeStateStore runtimeStateStore,
                                               DispatchStrategy dispatchStrategy) {
        return new DispatcherService(vehicleRegistry, transportOrderRegistry,
                routePlanner, eventPublisher, runtimeStateStore, dispatchStrategy);
    }

    @Bean
    public Dispatcher dispatcher(DispatcherService dispatcherService) {
        return dispatcherService;
    }

    @Bean
    public TransportOrderService transportOrderService(TransportOrderRegistry registry,
                                                       DispatcherService dispatcher,
                                                       RoutePlannerImpl routePlanner,
                                                       MapRuntimeService mapRuntimeService,
                                                       ApplicationEventPublisher eventPublisher) {
        return new TransportOrderService(registry, dispatcher, routePlanner, mapRuntimeService,
                eventPublisher);
    }

    @Bean
    public TransportOrderApi transportOrderApi(TransportOrderService transportOrderService) {
        return transportOrderService;
    }

    @Bean
    public OrderLifecycleService orderLifecycleService(DispatcherService dispatcher) {
        return new OrderLifecycleService(dispatcher);
    }

    @Bean
    public OrderLifecycleApi orderLifecycleApi(OrderLifecycleService orderLifecycleService) {
        return orderLifecycleService;
    }
}
