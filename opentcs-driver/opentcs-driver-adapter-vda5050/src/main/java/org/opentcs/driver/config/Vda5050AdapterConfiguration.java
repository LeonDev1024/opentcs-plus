package org.opentcs.driver.config;

import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.registry.DriverRegistry;
import org.opentcs.driver.vda5050.VDA5050Adapter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * VDA5050 协议适配器装配：创建适配器并注册到 {@link DriverRegistry}。
 * <p>仅由 admin 引入本模块时生效；业务 modules 不应直接依赖本 jar。</p>
 */
@Configuration
@ConditionalOnBean(DriverRegistry.class)
public class Vda5050AdapterConfiguration {

    @Bean(name = "vda5050Adapter")
    @ConditionalOnMissingBean(name = "vda5050Adapter")
    public DriverAdapter vda5050Adapter() {
        VDA5050Adapter adapter = new VDA5050Adapter();
        adapter.initialize(new DriverConfig());
        return adapter;
    }

    @Bean
    public Vda5050AdapterRegistrar vda5050AdapterRegistrar(
            DriverRegistry driverRegistry,
            @Qualifier("vda5050Adapter") DriverAdapter vda5050Adapter) {
        return new Vda5050AdapterRegistrar(driverRegistry, vda5050Adapter);
    }

    /**
     * 将 VDA5050 适配器挂到运行时注册表。
     */
    public static final class Vda5050AdapterRegistrar {

        private final DriverRegistry driverRegistry;
        private final DriverAdapter vda5050Adapter;

        public Vda5050AdapterRegistrar(DriverRegistry driverRegistry, DriverAdapter vda5050Adapter) {
            this.driverRegistry = driverRegistry;
            this.vda5050Adapter = vda5050Adapter;
        }

        @PostConstruct
        public void register() {
            driverRegistry.registerAdapter(VDA5050Adapter.DRIVER_TYPE, vda5050Adapter);
        }
    }
}
