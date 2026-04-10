package org.opentcs.algorithm.spi;

import org.opentcs.kernel.domain.routing.RoutingAlgorithm;

/**
 * 可插拔路径规划算法插件接口。
 * <p>
 * 同时继承：
 * <ul>
 *   <li>{@link RoutingAlgorithm} — 核心算法契约（领域层）</li>
 *   <li>{@link AlgorithmPlugin} — 插件标识与元数据</li>
 * </ul>
 * </p>
 *
 * <p>实现此接口的 Bean 会自动被 {@code AlgorithmPluginRegistry} 发现。
 * 通过配置项 {@code opentcs.algorithm.routing.provider} 指定激活的算法名称（对应 {@link AlgorithmMeta#name()}）。
 * </p>
 *
 * <pre>{@code
 * @Component
 * @AlgorithmMeta(name = "astar", version = "1.0", description = "A* 路径规划")
 * public class AStarRoutingAlgorithm implements RoutingAlgorithmPlugin {
 *     @Override
 *     public List<Point> findRoute(...) { ... }
 * }
 * }</pre>
 */
public interface RoutingAlgorithmPlugin extends RoutingAlgorithm, AlgorithmPlugin {
}
