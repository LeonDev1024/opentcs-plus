package org.opentcs.algorithm.loader.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 算法加载器配置属性。
 *
 * <pre>
 * # application.yml 示例
 * opentcs:
 *   algorithm:
 *     routing:
 *       provider: astar    # 可选值: astar | dijkstra | grpc-cpp | 自定义插件名
 * </pre>
 */
@ConfigurationProperties(prefix = "opentcs.algorithm.routing")
public class AlgorithmLoaderConfiguration {

    /**
     * 激活的路径规划算法插件名（对应 @AlgorithmMeta#name）。
     * 留空时自动选择第一个可用插件。
     */
    private String provider = "astar";

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}
