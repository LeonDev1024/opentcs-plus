package org.opentcs.monitor.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.json.utils.JsonUtils;
import org.opentcs.common.websocket.utils.WebSocketUtils;
import org.opentcs.monitor.application.MonitorSnapshotApplicationService;
import org.opentcs.monitor.dto.MonitorSnapshotVO;
import org.opentcs.monitor.dto.MonitorWsClientMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 监控大屏实时通道：首包 snapshot，客户端可 subscribe 切换工厂。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonitorWebSocketHandler extends TextWebSocketHandler {

    private final MonitorWebSocketSessionRegistry registry;
    private final MonitorSnapshotApplicationService snapshotService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long factoryId = (Long) session.getAttributes().get(MonitorWebSocketHandshakeInterceptor.FACTORY_ID_ATTR);
        MonitorWsSession binding = new MonitorWsSession(session, factoryId);
        registry.register(binding);
        try {
            sendSnapshot(binding, "snapshot");
        } catch (Exception e) {
            log.warn("监控 WS 首包 snapshot 失败 sessionId={}: {}", session.getId(), e.getMessage());
        }
        log.info("监控 WS 已连接 sessionId={} factoryId={}", session.getId(), factoryId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if (payload != null && payload.contains("\"ping\"")) {
            sendRaw(session, "{\"type\":\"pong\"}");
            return;
        }
        MonitorWsClientMessage client;
        try {
            client = JsonUtils.parseObject(payload, MonitorWsClientMessage.class);
        } catch (RuntimeException e) {
            log.debug("监控 WS 无法解析客户端消息 sessionId={}: {}", session.getId(), e.getMessage());
            return;
        }
        if (client == null || client.getType() == null) {
            return;
        }
        if ("ping".equalsIgnoreCase(client.getType())) {
            sendRaw(session, "{\"type\":\"pong\"}");
            return;
        }
        if ("subscribe".equalsIgnoreCase(client.getType())) {
            MonitorWsSession binding = registry.get(session.getId());
            if (binding == null) {
                return;
            }
            binding.setFactoryId(client.getFactoryId());
            sendSnapshot(binding, "snapshot");
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) {
        // 浏览器心跳即可，无需回包
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        registry.remove(session.getId());
        log.info("监控 WS 已断开 sessionId={} status={}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("监控 WS 传输异常 sessionId={}: {}", session.getId(), exception.getMessage());
        registry.remove(session.getId());
    }

    public void sendSnapshot(MonitorWsSession binding, String type) {
        if (binding == null || !binding.session().isOpen()) {
            return;
        }
        MonitorSnapshotVO snapshot = snapshotService.snapshot(binding.factoryId(), type);
        sendPayload(binding.session(), snapshot);
    }

    public void sendPayload(WebSocketSession session, Object payload) {
        sendRaw(session, JsonUtils.toJsonString(payload));
    }

    private void sendRaw(WebSocketSession session, String json) {
        WebSocketUtils.sendMessage(session, json);
    }
}
