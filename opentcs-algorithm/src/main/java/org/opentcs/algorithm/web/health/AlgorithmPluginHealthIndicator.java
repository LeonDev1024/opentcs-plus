package org.opentcs.algorithm.web.health;

import org.opentcs.algorithm.loader.AlgorithmPluginRegistry;
import org.opentcs.algorithm.spi.AlgorithmDescriptor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 算法插件健康检查。
 */
@Component("algorithmPlugin")
@ConditionalOnBean(AlgorithmPluginRegistry.class)
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
            AlgorithmDescriptor desc = registry.getActiveDescriptor();
            return new RoutingAlgorithmInfo(desc.getName(), desc.getVersion());
        } catch (Exception e) {
            return new RoutingAlgorithmInfo("unavailable", "unknown");
        }
    }

    private record RoutingAlgorithmInfo(String name, String version) {}
}
