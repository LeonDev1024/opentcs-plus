package org.opentcs.algorithm.grpc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * gRPC 算法桥接配置属性。
 *
 * <pre>
 * opentcs:
 *   algorithm:
 *     grpc:
 *       enabled: true               # 是否启用 gRPC 桥接（默认 false，需显式开启）
 *       client-name: routing-algorithm  # grpc.client.xxx 配置中的名称
 *       connect-timeout-ms: 5000    # 连接超时（毫秒）
 *       call-timeout-ms: 10000      # 调用超时（毫秒）
 * </pre>
 */
@ConfigurationProperties(prefix = "opentcs.algorithm.grpc")
public class GrpcAlgorithmConfiguration {

    /** 是否启用 gRPC 算法桥接（需在 application.yml 显式设置为 true）。 */
    private boolean enabled = false;

    /** gRPC 客户端配置名称，对应 grpc.client.{clientName}.address。 */
    private String clientName = "routing-algorithm";

    /** 调用超时（毫秒），0 表示不限制。 */
    private long callTimeoutMs = 10000;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public long getCallTimeoutMs() { return callTimeoutMs; }
    public void setCallTimeoutMs(long callTimeoutMs) { this.callTimeoutMs = callTimeoutMs; }
}
