package org.opentcs.algorithm.grpc.config;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.opentcs.algorithm.grpc.GrpcRoutingAlgorithmPlugin;
import org.opentcs.algorithm.grpc.proto.RoutingAlgorithmServiceGrpc;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * gRPC 算法桥接自动配置。
 * <p>
 * 仅当 {@code opentcs.algorithm.grpc.enabled=true} 时激活，
 * 注册 {@link GrpcRoutingAlgorithmPlugin} Bean 进入插件注册表，
 * 在 {@code AlgorithmLoaderAutoConfiguration} 收集插件之前完成注册。
 * </p>
 */
@AutoConfiguration(before = org.opentcs.algorithm.loader.config.AlgorithmLoaderAutoConfiguration.class)
@EnableConfigurationProperties(GrpcAlgorithmConfiguration.class)
@ConditionalOnProperty(prefix = "opentcs.algorithm.grpc", name = "enabled", havingValue = "true")
public class GrpcAlgorithmAutoConfiguration {

    @GrpcClient("${opentcs.algorithm.grpc.client-name:routing-algorithm}")
    private RoutingAlgorithmServiceGrpc.RoutingAlgorithmServiceBlockingStub routingAlgorithmStub;

    @Bean
    public GrpcRoutingAlgorithmPlugin grpcRoutingAlgorithmPlugin(GrpcAlgorithmConfiguration config) {
        return new GrpcRoutingAlgorithmPlugin(routingAlgorithmStub, config);
    }
}
