package org.opentcs.monitor.websocket;

import org.springframework.web.socket.WebSocketSession;

/**
 * 一条监控大屏 WS 连接及其工厂订阅。
 */
public class MonitorWsSession {

    private final WebSocketSession session;
    private volatile Long factoryId;

    public MonitorWsSession(WebSocketSession session, Long factoryId) {
        this.session = session;
        this.factoryId = factoryId;
    }

    public String sessionId() {
        return session.getId();
    }

    public WebSocketSession session() {
        return session;
    }

    public Long factoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }
}
