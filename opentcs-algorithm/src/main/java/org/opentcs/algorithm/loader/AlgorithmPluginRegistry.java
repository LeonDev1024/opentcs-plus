package org.opentcs.algorithm.loader;

import org.opentcs.algorithm.spi.AlgorithmDescriptor;
import org.opentcs.algorithm.spi.RoutingAlgorithmPlugin;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 算法插件注册表。
 * <p>
 * 收集 Spring 容器中所有 {@link RoutingAlgorithmPlugin} Bean，
 * 按配置项 {@code opentcs.algorithm.routing.provider} 选择并暴露激活的算法。
 * </p>
 */
public class AlgorithmPluginRegistry {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmPluginRegistry.class);

    private final Map<String, RoutingAlgorithmPlugin> pluginsByName;
    private final String configuredProvider;

    public AlgorithmPluginRegistry(List<RoutingAlgorithmPlugin> plugins, String configuredProvider) {
        this.configuredProvider = configuredProvider;
        this.pluginsByName = plugins.stream()
                .collect(Collectors.toMap(
                        p -> p.getDescriptor().getName(),
                        p -> p,
                        (existing, duplicate) -> {
                            log.warn("发现重复算法插件名称 '{}', 保留先注册的: {}",
                                    existing.getDescriptor().getName(),
                                    existing.getClass().getName());
                            return existing;
                        }
                ));

        log.info("已加载 {} 个路径规划算法插件: {}", pluginsByName.size(),
                pluginsByName.keySet());
    }

    /**
     * 按配置返回激活的路径规划算法。
     * <p>
     * 选择优先级：
     * <ol>
     *   <li>配置项 {@code opentcs.algorithm.routing.provider} 指定的插件名</li>
     *   <li>若配置为空或找不到，取注册的第一个插件（启动告警）</li>
     *   <li>若无任何插件，抛出 {@link IllegalStateException}</li>
     * </ol>
     * </p>
     */
    public RoutingAlgorithm getActiveRoutingAlgorithm() {
        if (StringUtils.hasText(configuredProvider)) {
            RoutingAlgorithmPlugin plugin = pluginsByName.get(configuredProvider.toLowerCase());
            if (plugin != null) {
                AlgorithmDescriptor desc = plugin.getDescriptor();
                log.info("激活路径规划算法: {} v{} - {}", desc.getName(), desc.getVersion(), desc.getDescription());
                return plugin;
            }
            log.warn("配置的路径规划算法 '{}' 未找到，将回退到默认算法。可用: {}",
                    configuredProvider, pluginsByName.keySet());
        }

        // 回退到第一个注册的插件
        return pluginsByName.values().stream()
                .findFirst()
                .map(plugin -> {
                    AlgorithmDescriptor desc = plugin.getDescriptor();
                    log.warn("使用默认路径规划算法（未配置 opentcs.algorithm.routing.provider）: {} v{}",
                            desc.getName(), desc.getVersion());
                    return (RoutingAlgorithm) plugin;
                })
                .orElseThrow(() -> new IllegalStateException(
                        "未找到任何 RoutingAlgorithmPlugin Bean！请添加 opentcs-strategies-default 或自定义算法模块依赖。"));
    }

    /**
     * 返回所有已注册的算法描述符（用于健康检查、管理接口展示）。
     */
    public List<AlgorithmDescriptor> listAllDescriptors() {
        return pluginsByName.values().stream()
                .map(RoutingAlgorithmPlugin::getDescriptor)
                .collect(Collectors.toList());
    }

    /**
     * 按名称查找插件（用于动态切换场景）。
     */
    public Optional<RoutingAlgorithmPlugin> findByName(String name) {
        return Optional.ofNullable(pluginsByName.get(name.toLowerCase()));
    }

    public Map<String, RoutingAlgorithmPlugin> getPluginsByName() {
        return Collections.unmodifiableMap(pluginsByName);
    }
}
