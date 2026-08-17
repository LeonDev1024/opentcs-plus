package org.opentcs.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 监控聚合模块配置。
 */
@Data
@ConfigurationProperties(prefix = "opentcs.monitor")
public class MonitorProperties {

    private Websocket websocket = new Websocket();

    @Data
    public static class Websocket {
        /** 独立于 RuoYi 通知通道 websocket.enabled */
        private boolean enabled = true;
        private String path = "/resource/ws/monitor";
        private String allowedOrigins = "*";
        /** 有订阅时的推送扫描间隔 */
        private long pushIntervalMs = 500;
        /** 数据无变化时的心跳间隔 */
        private long heartbeatIntervalMs = 5000;
    }
}
