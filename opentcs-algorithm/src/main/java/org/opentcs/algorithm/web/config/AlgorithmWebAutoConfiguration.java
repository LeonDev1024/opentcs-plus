package org.opentcs.algorithm.web.config;

import org.opentcs.algorithm.loader.config.AlgorithmLoaderAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 算法模块 Web 层自动配置（管理接口、健康检查）。
 */
@AutoConfiguration(after = AlgorithmLoaderAutoConfiguration.class)
@ConditionalOnWebApplication
@ComponentScan(basePackages = "org.opentcs.algorithm.web")
public class AlgorithmWebAutoConfiguration {
}
