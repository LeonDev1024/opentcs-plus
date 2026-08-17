package org.opentcs.monitor.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.monitor.application.MonitorSnapshotApplicationService;
import org.opentcs.monitor.config.MonitorProperties;
import org.opentcs.monitor.dto.MonitorSnapshotVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 有大屏订阅时扫描运行时，变化则推 delta，否则按间隔心跳。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "opentcs.monitor.websocket", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MonitorRealtimePushService {

    private final MonitorWebSocketSessionRegistry registry;
    private final MonitorSnapshotApplicationService snapshotService;
    private final MonitorWebSocketHandler webSocketHandler;
    private final MonitorProperties monitorProperties;
    private final Map<Long, String> lastFingerprintByFactory = new ConcurrentHashMap<>();
    private volatile long lastHeartbeatAt;

    @Scheduled(fixedDelayString = "${opentcs.monitor.websocket.push-interval-ms:500}")
    public void push() {
        if (registry.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        long heartbeatMs = monitorProperties.getWebsocket().getHeartbeatIntervalMs();
        Map<Long, MonitorSnapshotVO> built = new HashMap<>();
        boolean heartbeatDue = now - lastHeartbeatAt >= heartbeatMs;
        boolean sentHeartbeat = false;
        for (MonitorWsSession binding : registry.all()) {
            Long factoryKey = binding.factoryId() == null ? 0L : binding.factoryId();
            MonitorSnapshotVO snapshot = built.computeIfAbsent(factoryKey,
                    key -> snapshotService.snapshot(binding.factoryId(), "delta"));
            String previous = lastFingerprintByFactory.get(factoryKey);
            if (snapshot.getFingerprint().equals(previous)) {
                if (heartbeatDue) {
                    webSocketHandler.sendPayload(binding.session(), heartbeatOf(snapshot));
                    sentHeartbeat = true;
                }
                continue;
            }
            lastFingerprintByFactory.put(factoryKey, snapshot.getFingerprint());
            webSocketHandler.sendPayload(binding.session(), snapshot);
        }
        if (sentHeartbeat) {
            lastHeartbeatAt = now;
        }
    }

    private static Map<String, Object> heartbeatOf(MonitorSnapshotVO snapshot) {
        Map<String, Object> heartbeat = new HashMap<>();
        heartbeat.put("type", "heartbeat");
        heartbeat.put("factoryId", snapshot.getFactoryId());
        heartbeat.put("generatedAt", snapshot.getGeneratedAt());
        heartbeat.put("seq", snapshot.getSeq());
        heartbeat.put("fingerprint", snapshot.getFingerprint());
        heartbeat.put("alarmCount", snapshot.getAlarmCount());
        return heartbeat;
    }
}
