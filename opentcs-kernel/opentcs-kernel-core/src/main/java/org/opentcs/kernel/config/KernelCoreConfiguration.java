package org.opentcs.kernel.config;

import org.opentcs.kernel.api.OrderLifecycleApi;
import org.opentcs.kernel.api.RoutePlannerApi;
import org.opentcs.kernel.api.TransportOrderApi;
import org.opentcs.kernel.api.VehicleRegistryApi;
import org.opentcs.kernel.api.algorithm.Dispatcher;
import org.opentcs.kernel.application.*;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;
import org.springframework.context.ApplicationEventPublisher;
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
    public RoutePlannerImpl routePlanner(RoutingAlgorithm routingAlgorithm) {
        return new RoutePlannerImpl(routingAlgorithm);
    }

    @Bean
    public RoutePlannerApi routePlannerApi(RoutePlannerImpl routePlanner) {
        return routePlanner;
    }

    @Bean
    public DispatcherService dispatcherService(VehicleRegistry vehicleRegistry,
                                               TransportOrderRegistry transportOrderRegistry,
                                               RoutePlannerImpl routePlanner,
                                               ApplicationEventPublisher eventPublisher) {
        return new DispatcherService(vehicleRegistry, transportOrderRegistry,
                routePlanner, eventPublisher);
    }

    @Bean
    public Dispatcher dispatcher(DispatcherService dispatcherService) {
        return dispatcherService;
    }

    @Bean
    public TransportOrderService transportOrderService(TransportOrderRegistry registry,
                                                       DispatcherService dispatcher,
                                                       RoutePlannerImpl routePlanner) {
        return new TransportOrderService(registry, dispatcher, routePlanner);
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
