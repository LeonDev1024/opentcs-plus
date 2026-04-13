package org.opentcs.algorithm.loader.config;

import org.opentcs.algorithm.loader.AlgorithmPluginRegistry;
import org.opentcs.algorithm.spi.RoutingAlgorithmPlugin;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;
import org.opentcs.strategies.builtin.config.BuiltinStrategiesAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * 算法加载器自动配置。
 * <p>
 * 收集所有 {@link RoutingAlgorithmPlugin} Bean → 构建 {@link AlgorithmPluginRegistry} →
 * 暴露选中的 {@link RoutingAlgorithm} Bean 供 kernel-core 的 {@code RoutePlannerImpl} 注入。
 * </p>
 *
 * <p>声明 {@code after = BuiltinStrategiesAutoConfiguration.class}，确保内置算法 Bean 先于
 * 本配置执行，避免 {@code List<RoutingAlgorithmPlugin>} 注入时收到空列表。</p>
 *
 * <p>若应用已手动注册 {@link RoutingAlgorithm} Bean，此自动配置不会覆盖它。</p>
 */
@AutoConfiguration(after = BuiltinStrategiesAutoConfiguration.class)
@EnableConfigurationProperties(AlgorithmLoaderConfiguration.class)
public class AlgorithmLoaderAutoConfiguration {

    /**
     * 注册 {@link AlgorithmPluginRegistry} 为 {@link RoutingAlgorithm} Bean。
     * <p>
     * Registry 本身实现了 {@link RoutingAlgorithm}（代理模式），
     * 所有调用均委托到当前激活插件，支持运行时热切换。
     * </p>
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(RoutingAlgorithm.class)
    public AlgorithmPluginRegistry algorithmPluginRegistry(
            List<RoutingAlgorithmPlugin> plugins,
            AlgorithmLoaderConfiguration config) {
        return new AlgorithmPluginRegistry(plugins, config.getProvider());
    }
}
