package org.opentcs.strategies.builtin.config;

import org.opentcs.kernel.api.algorithm.Router;
import org.opentcs.strategies.builtin.AStarRoutingAlgorithm;
import org.opentcs.strategies.builtin.BuiltinRouter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 内置策略自动装配。
 * <p>
 * 显式注册内置算法 Bean，不依赖 {@code @ComponentScan}，确保在任何启动模式下都能被
 * {@code AlgorithmLoaderAutoConfiguration} 正确收集。
 * </p>
 */
@AutoConfiguration
@EnableConfigurationProperties(StrategiesConfiguration.class)
public class BuiltinStrategiesAutoConfiguration {

    /**
     * 注册内置 A* 算法插件。
     * {@code @ConditionalOnMissingBean} 允许应用提供同名 Bean 进行覆盖。
     */
    @Bean
    @ConditionalOnMissingBean(AStarRoutingAlgorithm.class)
    public AStarRoutingAlgorithm aStarRoutingAlgorithm() {
        return new AStarRoutingAlgorithm();
    }

    /**
     * 注册内置路由器（{@link Router} 接口实现）。
     */
    @Bean
    @ConditionalOnMissingBean(Router.class)
    public Router builtinRouter(StrategiesConfiguration strategiesConfiguration) {
        BuiltinRouter router = new BuiltinRouter(strategiesConfiguration);
        router.initialize();
        return router;
    }
}
