package org.opentcs.algorithm.loader;

import org.opentcs.algorithm.spi.AlgorithmDescriptor;
import org.opentcs.algorithm.spi.RoutingAlgorithmPlugin;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
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
 * 算法插件注册表，同时实现 {@link RoutingAlgorithm} 代理模式，支持运行时热切换。
 * <p>
 * 作为 Spring 容器中唯一的 {@link RoutingAlgorithm} Bean 注册，
 * 所有路径规划调用均代理到当前激活的插件。
 * 切换算法时只需调用 {@link #switchProvider(String)}，无需重启服务。
 * </p>
 */
public class AlgorithmPluginRegistry implements RoutingAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmPluginRegistry.class);

    private final Map<String, RoutingAlgorithmPlugin> pluginsByName;
    private volatile RoutingAlgorithmPlugin activePlugin;

    public AlgorithmPluginRegistry(List<RoutingAlgorithmPlugin> plugins, String configuredProvider) {
        this.pluginsByName = plugins.stream()
                .collect(Collectors.toMap(
                        p -> p.getDescriptor().getName().toLowerCase(),
                        p -> p,
                        (existing, duplicate) -> {
                            log.warn("发现重复算法插件名称 '{}', 保留先注册的: {}",
                                    existing.getDescriptor().getName(),
                                    existing.getClass().getName());
                            return existing;
                        }
                ));

        log.info("已加载 {} 个路径规划算法插件: {}", pluginsByName.size(), pluginsByName.keySet());
        this.activePlugin = resolveInitialPlugin(configuredProvider);

        AlgorithmDescriptor desc = activePlugin.getDescriptor();
        log.info("初始路径规划算法: {} v{} - {}", desc.getName(), desc.getVersion(), desc.getDescription());
    }

    // ===== RoutingAlgorithm 代理实现 =====

    @Override
    public List<Point> findRoute(Map<String, Point> points, Map<String, Path> paths,
                                 Point start, Point end) {
        return activePlugin.findRoute(points, paths, start, end);
    }

    // ===== 热切换 API =====

    /**
     * 运行时切换路径规划算法。
     *
     * @param providerName 目标算法插件名（对应 {@code @AlgorithmMeta#name}，大小写不敏感）
     * @throws IllegalArgumentException 若插件名不存在
     */
    public synchronized void switchProvider(String providerName) {
        String key = providerName.toLowerCase();
        RoutingAlgorithmPlugin target = pluginsByName.get(key);
        if (target == null) {
            throw new IllegalArgumentException(
                    "算法插件 '" + providerName + "' 未注册。可用插件: " + pluginsByName.keySet());
        }
        RoutingAlgorithmPlugin previous = this.activePlugin;
        this.activePlugin = target;
        AlgorithmDescriptor desc = target.getDescriptor();
        log.info("路径规划算法已切换: {} → {} v{}", previous.getDescriptor().getName(),
                desc.getName(), desc.getVersion());
    }

    // ===== 查询 API =====

    /**
     * 返回当前激活的算法描述符。
     */
    public AlgorithmDescriptor getActiveDescriptor() {
        return activePlugin.getDescriptor();
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
     * 按名称查找插件。
     */
    public Optional<RoutingAlgorithmPlugin> findByName(String name) {
        return Optional.ofNullable(pluginsByName.get(name.toLowerCase()));
    }

    public Map<String, RoutingAlgorithmPlugin> getPluginsByName() {
        return Collections.unmodifiableMap(pluginsByName);
    }

    // ===== 内部方法 =====

    private RoutingAlgorithmPlugin resolveInitialPlugin(String configuredProvider) {
        if (StringUtils.hasText(configuredProvider)) {
            RoutingAlgorithmPlugin plugin = pluginsByName.get(configuredProvider.toLowerCase());
            if (plugin != null) {
                return plugin;
            }
            log.warn("配置的路径规划算法 '{}' 未找到，将回退到默认算法。可用: {}",
                    configuredProvider, pluginsByName.keySet());
        }

        return pluginsByName.values().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "未找到任何 RoutingAlgorithmPlugin Bean！请添加算法模块依赖。"));
    }
}
