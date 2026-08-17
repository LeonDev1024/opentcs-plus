package org.opentcs.monitor.websocket;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.core.utils.StringUtils;
import org.opentcs.common.websocket.interceptor.PlusWebSocketInterceptor;
import org.opentcs.common.websocket.utils.WebSocketHandshakeSupport;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 监控大屏握手：认证复用 common {@link PlusWebSocketInterceptor}，factoryId 从握手 query 读取。
 */
@Slf4j
public class MonitorWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    public static final String FACTORY_ID_ATTR = "monitorFactoryId";

    private final PlusWebSocketInterceptor authInterceptor = new PlusWebSocketInterceptor();

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!authInterceptor.beforeHandshake(request, response, wsHandler, attributes)) {
            return false;
        }
        String factoryId = WebSocketHandshakeSupport.firstValue(request, "factoryId");
        if (StringUtils.isNotBlank(factoryId)) {
            try {
                attributes.put(FACTORY_ID_ATTR, Long.parseLong(factoryId));
            } catch (NumberFormatException ignored) {
                log.warn("监控 WS factoryId 非法: {}", factoryId);
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        authInterceptor.afterHandshake(request, response, wsHandler, exception);
    }
}
