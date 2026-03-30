package org.opentcs.driver.config;

import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.api.VehicleGateway;
import org.opentcs.driver.gateway.VehicleGatewayImpl;
import org.opentcs.driver.registry.DriverRegistry;
import org.opentcs.driver.vda5050.VDA5050Adapter;
import org.opentcs.driver.api.dto.DriverConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 驱动配置：单一 {@link VehicleGateway}、单一 {@link DriverAdapter} 实例，注入 {@link DriverRegistry}。
 */
@Configuration
public class DriverConfiguration {

    @Bean(name = "vda5050Adapter")
    @ConditionalOnMissingBean(name = "vda5050Adapter")
    public DriverAdapter vda5050Adapter() {
        VDA5050Adapter adapter = new VDA5050Adapter();
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
    public DriverRegistry driverRegistry(VehicleGateway vehicleGateway, DriverAdapter vda5050Adapter) {
        DriverRegistry registry = new DriverRegistry(vehicleGateway);
        registry.registerAdapter("VDA5050", vda5050Adapter);
        return registry;
    }
}
