package org.opentcs.monitor.websocket;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监控大屏 WebSocket 订阅会话。
 */
@Component
public class MonitorWebSocketSessionRegistry {

    private final ConcurrentHashMap<String, MonitorWsSession> sessions = new ConcurrentHashMap<>();

    public void register(MonitorWsSession session) {
        sessions.put(session.sessionId(), session);
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }

    public MonitorWsSession get(String sessionId) {
        return sessions.get(sessionId);
    }

    public boolean isEmpty() {
        return sessions.isEmpty();
    }

    public Collection<MonitorWsSession> all() {
        return sessions.values();
    }
}
