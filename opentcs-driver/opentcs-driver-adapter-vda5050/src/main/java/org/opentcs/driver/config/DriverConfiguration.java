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
 * 驱动配置
 * 初始化驱动适配器为Spring Bean
 */
@Configuration
public class DriverConfiguration {

    /**
     * VDA5050 适配器
     */
    @Bean
    @ConditionalOnMissingBean(name = "vda5050Adapter")
    public DriverAdapter vda5050Adapter() {
        VDA5050Adapter adapter = new VDA5050Adapter();
        adapter.initialize(new DriverConfig());
        return adapter;
    }

    /**
     * 车辆网关
     */
    @Bean
    @ConditionalOnMissingBean
    public VehicleGateway vehicleGateway() {
        VehicleGatewayImpl gateway = new VehicleGatewayImpl();
        gateway.initialize();
        return gateway;
    }

    /**
     * 驱动注册表
     */
    @Bean
    @ConditionalOnMissingBean
    public DriverRegistry driverRegistry(VehicleGateway vehicleGateway) {
        DriverRegistry registry = new DriverRegistry();

        // 注册VDA5050适配器
        DriverAdapter vda5050Adapter = vda5050Adapter();
        registry.registerAdapter("VDA5050", vda5050Adapter);

        return registry;
    }
}
