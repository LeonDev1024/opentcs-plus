package org.opentcs.kernel.config;

import org.opentcs.kernel.application.VehicleRegistry;
import org.opentcs.kernel.application.TransportOrderRegistry;
import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.application.DispatcherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 内核核心配置
 * 初始化内核服务为Spring Bean
 */
@Configuration
public class KernelCoreConfiguration {

    /**
     * 车辆注册表
     */
    @Bean
    public VehicleRegistry vehicleRegistry() {
        return new VehicleRegistry();
    }

    /**
     * 运输订单注册表
     */
    @Bean
    public TransportOrderRegistry transportOrderRegistry() {
        return new TransportOrderRegistry();
    }

    /**
     * 路由规划器
     */
    @Bean
    public RoutePlannerImpl routePlanner() {
        return new RoutePlannerImpl();
    }

    /**
     * 调度服务
     */
    @Bean
    public DispatcherService dispatcherService(VehicleRegistry vehicleRegistry,
                                              TransportOrderRegistry transportOrderRegistry,
                                              RoutePlannerImpl routePlanner) {
        return new DispatcherService(vehicleRegistry, transportOrderRegistry, routePlanner);
    }
}
