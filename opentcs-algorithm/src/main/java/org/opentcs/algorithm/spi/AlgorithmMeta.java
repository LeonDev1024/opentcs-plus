package org.opentcs.algorithm.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 算法插件元数据注解。
 * <p>
 * 标注在 {@link AlgorithmPlugin} 实现类上，声明插件的唯一名称、版本和描述。
 * {@code name} 用于配置项 {@code opentcs.algorithm.routing.provider} 的值匹配。
 * </p>
 *
 * <pre>{@code
 * @AlgorithmMeta(name = "astar", version = "1.0", description = "A* 路径规划算法")
 * public class AStarRoutingAlgorithm implements RoutingAlgorithmPlugin { ... }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AlgorithmMeta {

    /** 插件唯一名称，用于配置选择（小写英文，如 "astar", "dijkstra", "grpc-cpp"）。 */
    String name();

    /** 插件版本号（语义化版本，如 "1.0.0"）。 */
    String version() default "1.0.0";

    /** 算法描述。 */
    String description() default "";

    /** 算法作者（可选）。 */
    String author() default "";
}
