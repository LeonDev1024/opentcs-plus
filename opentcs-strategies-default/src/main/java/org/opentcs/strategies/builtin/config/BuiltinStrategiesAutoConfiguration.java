package org.opentcs.strategies.builtin.config;

import org.opentcs.kernel.api.algorithm.Router;
import org.opentcs.strategies.builtin.BuiltinRouter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 内置策略装配：提供 {@link Router} 默认 Bean，可被业务模块覆盖。
 */
@AutoConfiguration
@EnableConfigurationProperties(StrategiesConfiguration.class)
public class BuiltinStrategiesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Router.class)
    public Router builtinRouter(StrategiesConfiguration strategiesConfiguration) {
        BuiltinRouter router = new BuiltinRouter(strategiesConfiguration);
        router.initialize();
        return router;
    }
}
