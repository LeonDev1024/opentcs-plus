package org.opentcs.driver.config;

import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.api.VehicleGateway;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.gateway.VehicleGatewayImpl;
import org.opentcs.driver.loopback.LoopbackVda5050Adapter;
import org.opentcs.driver.registry.DriverRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 驱动运行时装配：网关、注册表、LOOPBACK 仿真。
 * <p>具体协议适配器（如 VDA5050）由各自 adapter 模块注册进 {@link DriverRegistry}。</p>
 */
@Configuration
public class DriverRuntimeConfiguration {

    @Bean(name = "loopbackVda5050Adapter")
    @ConditionalOnMissingBean(name = "loopbackVda5050Adapter")
    public DriverAdapter loopbackVda5050Adapter() {
        LoopbackVda5050Adapter adapter = new LoopbackVda5050Adapter();
        adapter.initialize(new DriverConfig());
        return adapter;
    }

    @Bean
    @ConditionalOnMissingBean
    public VehicleGateway vehicleGateway() {
        VehicleGatewayImpl gateway = new VehicleGatewayImpl();
        gateway.initialize();
        return gateway;
    }

    @Bean
    @ConditionalOnMissingBean
    public DriverRegistry driverRegistry(VehicleGateway vehicleGateway,
                                         @Qualifier("loopbackVda5050Adapter") DriverAdapter loopbackAdapter) {
        DriverRegistry registry = new DriverRegistry(vehicleGateway);
        registry.registerAdapter(LoopbackVda5050Adapter.DRIVER_TYPE, loopbackAdapter);
        return registry;
    }
}
