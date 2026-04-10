package org.opentcs.algorithm.spi;

/**
 * 算法插件标记接口。
 * <p>
 * 所有可插拔算法必须实现此接口，并搭配 {@link AlgorithmMeta} 注解声明元数据。
 * 该接口本身不定义方法——具体算法能力通过子接口（如 {@link RoutingAlgorithmPlugin}）扩展。
 * </p>
 *
 * <p>插件注册方式（Spring Bean 或 Java SPI）：
 * <ul>
 *   <li>Spring Bean：添加 {@code @Component} 或在 AutoConfiguration 中 {@code @Bean} 注册</li>
 *   <li>Java SPI：在 {@code META-INF/services/org.opentcs.algorithm.spi.AlgorithmPlugin} 文件中声明</li>
 * </ul>
 * </p>
 */
public interface AlgorithmPlugin {

    /**
     * 返回插件元信息。由框架从 {@link AlgorithmMeta} 注解中读取并缓存，无需手动实现。
     * 实现类应在类上标注 {@link AlgorithmMeta}。
     */
    default AlgorithmDescriptor getDescriptor() {
        AlgorithmMeta meta = this.getClass().getAnnotation(AlgorithmMeta.class);
        if (meta == null) {
            return AlgorithmDescriptor.unknown(this.getClass().getSimpleName());
        }
        return new AlgorithmDescriptor(meta.name(), meta.version(), meta.description(), meta.author());
    }
}
