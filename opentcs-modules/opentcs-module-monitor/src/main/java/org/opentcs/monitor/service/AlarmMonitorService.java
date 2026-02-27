package org.opentcs.monitor.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 告警监控服务
 */
public class AlarmMonitorService {
    private boolean running;
    private ScheduledExecutorService executorService;
    private List<Map<String, Object>> alarms;
    private int alarmIdCounter;

    public AlarmMonitorService() {
        this.alarms = new ArrayList<>();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.alarmIdCounter = 1;
    }

    /**
     * 启动监控服务
     */
    public void start() {
        running = true;
        // 每1秒检查一次告警状态
        executorService.scheduleAtFixedRate(this::checkAlarms, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 停止监控服务
     */
    public void stop() {
        running = false;
        executorService.shutdown();
    }

    /**
     * 检查告警状态
     */
    private void checkAlarms() {
        if (!running) {
            return;
        }

        // 模拟告警生成
        simulateAlarms();

        // 清理过期告警
        cleanupExpiredAlarms();
    }

    /**
     * 模拟告警生成
     */
    private void simulateAlarms() {
        // 随机生成告警
        if (Math.random() > 0.95) {
            Map<String, Object> alarm = new HashMap<>();
            alarm.put("id", alarmIdCounter++);
            alarm.put("timestamp", System.currentTimeMillis());
            alarm.put("level", getRandomAlarmLevel());
            alarm.put("type", getRandomAlarmType());
            alarm.put("source", getRandomAlarmSource());
            alarm.put("message", getRandomAlarmMessage());
            alarm.put("status", "ACTIVE");
            alarm.put("acknowledged", false);

            alarms.add(alarm);
        }
    }

    /**
     * 获取随机告警级别
     * @return 告警级别
     */
    private String getRandomAlarmLevel() {
        String[] levels = {"INFO", "WARNING", "ERROR", "CRITICAL"};
        return levels[(int) (Math.random() * levels.length)];
    }

    /**
     * 获取随机告警类型
     * @return 告警类型
     */
    private String getRandomAlarmType() {
        String[] types = {"SYSTEM", "VEHICLE", "ORDER", "NETWORK", "HARDWARE"};
        return types[(int) (Math.random() * types.length)];
    }

    /**
     * 获取随机告警源
     * @return 告警源
     */
    private String getRandomAlarmSource() {
        String[] sources = {"AGV-001", "AGV-002", "AGV-003", "System", "Database", "Network"};
        return sources[(int) (Math.random() * sources.length)];
    }

    /**
     * 获取随机告警消息
     * @return 告警消息
     */
    private String getRandomAlarmMessage() {
        String[] messages = {
            "Vehicle battery level low",
            "Network connection lost",
            "Order execution failed",
            "System load high",
            "Database connection error",
            "Vehicle position error",
            "Order timeout",
            "Vehicle communication error"
        };
        return messages[(int) (Math.random() * messages.length)];
    }

    /**
     * 清理过期告警
     */
    private void cleanupExpiredAlarms() {
        long currentTime = System.currentTimeMillis();
        List<Map<String, Object>> expiredAlarms = new ArrayList<>();

        for (Map<String, Object> alarm : alarms) {
            long timestamp = (long) alarm.get("timestamp");
            // 清理5分钟前的告警
            if (currentTime - timestamp > 5 * 60 * 1000) {
                expiredAlarms.add(alarm);
            }
        }

        alarms.removeAll(expiredAlarms);
    }

    /**
     * 添加告警
     * @param level 告警级别
     * @param type 告警类型
     * @param source 告警源
     * @param message 告警消息
     */
    public void addAlarm(String level, String type, String source, String message) {
        Map<String, Object> alarm = new HashMap<>();
        alarm.put("id", alarmIdCounter++);
        alarm.put("timestamp", System.currentTimeMillis());
        alarm.put("level", level);
        alarm.put("type", type);
        alarm.put("source", source);
        alarm.put("message", message);
        alarm.put("status", "ACTIVE");
        alarm.put("acknowledged", false);

        alarms.add(alarm);
    }

    /**
     * 确认告警
     * @param alarmId 告警ID
     */
    public void acknowledgeAlarm(int alarmId) {
        for (Map<String, Object> alarm : alarms) {
            if (alarm.get("id").equals(alarmId)) {
                alarm.put("acknowledged", true);
                alarm.put("status", "ACKNOWLEDGED");
                break;
            }
        }
    }

    /**
     * 清除告警
     * @param alarmId 告警ID
     */
    public void clearAlarm(int alarmId) {
        alarms.removeIf(alarm -> alarm.get("id").equals(alarmId));
    }

    /**
     * 获取所有告警
     * @return 告警列表
     */
    public List<Map<String, Object>> getAlarms() {
        return alarms;
    }

    /**
     * 获取活跃告警
     * @return 活跃告警列表
     */
    public List<Map<String, Object>> getActiveAlarms() {
        List<Map<String, Object>> activeAlarms = new ArrayList<>();
        for (Map<String, Object> alarm : alarms) {
            if ("ACTIVE".equals(alarm.get("status"))) {
                activeAlarms.add(alarm);
            }
        }
        return activeAlarms;
    }

    /**
     * 获取告警统计数据
     * @return 告警统计数据
     */
    public Map<String, Object> getAlarmStatistics() {
        int totalAlarms = alarms.size();
        int activeAlarms = 0;
        int acknowledgedAlarms = 0;
        int criticalAlarms = 0;
        int errorAlarms = 0;
        int warningAlarms = 0;
        int infoAlarms = 0;

        for (Map<String, Object> alarm : alarms) {
            if ("ACTIVE".equals(alarm.get("status"))) {
                activeAlarms++;
            } else if ("ACKNOWLEDGED".equals(alarm.get("status"))) {
                acknowledgedAlarms++;
            }

            String level = (String) alarm.get("level");
            switch (level) {
                case "CRITICAL":
                    criticalAlarms++;
                    break;
                case "ERROR":
                    errorAlarms++;
                    break;
                case "WARNING":
                    warningAlarms++;
                    break;
                case "INFO":
                    infoAlarms++;
                    break;
            }
        }

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAlarms", totalAlarms);
        statistics.put("activeAlarms", activeAlarms);
        statistics.put("acknowledgedAlarms", acknowledgedAlarms);
        statistics.put("criticalAlarms", criticalAlarms);
        statistics.put("errorAlarms", errorAlarms);
        statistics.put("warningAlarms", warningAlarms);
        statistics.put("infoAlarms", infoAlarms);

        return statistics;
    }
}