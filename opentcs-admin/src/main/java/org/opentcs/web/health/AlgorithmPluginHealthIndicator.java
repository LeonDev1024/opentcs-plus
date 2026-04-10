package org.opentcs.web.health;

import org.opentcs.algorithm.loader.AlgorithmPluginRegistry;
import org.opentcs.algorithm.spi.AlgorithmDescriptor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 算法插件健康检查。
 * <p>
 * 检查是否有至少一个 {@link org.opentcs.algorithm.spi.RoutingAlgorithmPlugin} 已注册并可用。
 * 通过 Spring Boot Actuator 的 {@code /actuator/health} 端点暴露。
 * </p>
 *
 * <p>健康状态含义：
 * <ul>
 *   <li>UP — 至少一个路由算法插件已注册</li>
 *   <li>DOWN — 无任何算法插件（系统无法进行路径规划）</li>
 * </ul>
 * </p>
 */
@Component("algorithmPlugin")
public class AlgorithmPluginHealthIndicator implements HealthIndicator {

    private final AlgorithmPluginRegistry registry;

    public AlgorithmPluginHealthIndicator(AlgorithmPluginRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Health health() {
        List<AlgorithmDescriptor> descriptors = registry.listAllDescriptors();

        if (descriptors.isEmpty()) {
            return Health.down()
                    .withDetail("error", "无可用路径规划算法插件，系统无法进行调度路径规划")
                    .build();
        }

        String pluginSummary = descriptors.stream()
                .map(d -> String.format("%s v%s", d.getName(), d.getVersion()))
                .collect(Collectors.joining(", "));

        RoutingAlgorithmInfo active = getActiveAlgorithmInfo();

        return Health.up()
                .withDetail("registered_plugins", pluginSummary)
                .withDetail("plugin_count", descriptors.size())
                .withDetail("active_algorithm", active.name())
                .withDetail("active_version", active.version())
                .build();
    }

    private RoutingAlgorithmInfo getActiveAlgorithmInfo() {
        try {
            var activeAlgorithm = registry.getActiveRoutingAlgorithm();
            if (activeAlgorithm instanceof org.opentcs.algorithm.spi.AlgorithmPlugin plugin) {
                AlgorithmDescriptor desc = plugin.getDescriptor();
                return new RoutingAlgorithmInfo(desc.getName(), desc.getVersion());
            }
            return new RoutingAlgorithmInfo(activeAlgorithm.getClass().getSimpleName(), "unknown");
        } catch (Exception e) {
            return new RoutingAlgorithmInfo("unavailable", "unknown");
        }
    }

    private record RoutingAlgorithmInfo(String name, String version) {}
}
