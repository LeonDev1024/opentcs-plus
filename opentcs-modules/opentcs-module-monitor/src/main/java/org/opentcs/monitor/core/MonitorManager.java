package org.opentcs.monitor.core;

import org.opentcs.monitor.service.SystemMonitorService;
import org.opentcs.monitor.service.VehicleMonitorService;
import org.opentcs.monitor.service.OrderMonitorService;
import org.opentcs.monitor.service.AlarmMonitorService;

/**
 * 监控管理器
 */
public class MonitorManager {
    private static final MonitorManager INSTANCE = new MonitorManager();
    private SystemMonitorService systemMonitorService;
    private VehicleMonitorService vehicleMonitorService;
    private OrderMonitorService orderMonitorService;
    private AlarmMonitorService alarmMonitorService;

    private MonitorManager() {
    }

    public static MonitorManager getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化监控服务
     */
    public void init() {
        // 初始化各个监控服务
        systemMonitorService = new SystemMonitorService();
        vehicleMonitorService = new VehicleMonitorService();
        orderMonitorService = new OrderMonitorService();
        alarmMonitorService = new AlarmMonitorService();

        // 启动监控服务
        systemMonitorService.start();
        vehicleMonitorService.start();
        orderMonitorService.start();
        alarmMonitorService.start();
    }

    /**
     * 停止监控服务
     */
    public void stop() {
        if (systemMonitorService != null) {
            systemMonitorService.stop();
        }
        if (vehicleMonitorService != null) {
            vehicleMonitorService.stop();
        }
        if (orderMonitorService != null) {
            orderMonitorService.stop();
        }
        if (alarmMonitorService != null) {
            alarmMonitorService.stop();
        }
    }

    // Getters
    public SystemMonitorService getSystemMonitorService() {
        return systemMonitorService;
    }

    public VehicleMonitorService getVehicleMonitorService() {
        return vehicleMonitorService;
    }

    public OrderMonitorService getOrderMonitorService() {
        return orderMonitorService;
    }

    public AlarmMonitorService getAlarmMonitorService() {
        return alarmMonitorService;
    }
}
