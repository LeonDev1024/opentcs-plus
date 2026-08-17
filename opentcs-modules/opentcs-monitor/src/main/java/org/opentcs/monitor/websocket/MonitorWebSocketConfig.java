package org.opentcs.monitor.websocket;

import lombok.RequiredArgsConstructor;
import org.opentcs.monitor.config.MonitorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 监控大屏独立 WebSocket，不依赖 websocket.enabled（通知通道）。
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "opentcs.monitor.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MonitorWebSocketConfig implements WebSocketConfigurer {

    private final MonitorWebSocketHandler monitorWebSocketHandler;
    private final MonitorProperties monitorProperties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        MonitorProperties.Websocket ws = monitorProperties.getWebsocket();
        var registration = registry.addHandler(monitorWebSocketHandler, ws.getPath())
                .addInterceptors(new MonitorWebSocketHandshakeInterceptor());
        String origins = ws.getAllowedOrigins();
        if (origins == null || origins.isBlank() || "*".equals(origins.trim())) {
            registration.setAllowedOriginPatterns("*");
        } else {
            registration.setAllowedOrigins(origins);
        }
    }
}
