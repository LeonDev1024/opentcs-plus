package org.opentcs.driver.core;

import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.registry.DriverRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 驱动管理器
 * 负责管理所有车辆驱动实例
 */
@Component
public class DriverManager {

    private static final Logger LOG = LoggerFactory.getLogger(DriverManager.class);

    private static DriverManager instance;

    private final DriverRegistry driverRegistry;
    private final Map<String, DriverAdapter> drivers = new ConcurrentHashMap<>();

    public DriverManager(DriverRegistry driverRegistry) {
        this.driverRegistry = driverRegistry;
        instance = this;
    }

    @PostConstruct
    public void init() {
        LOG.info("DriverManager 初始化完成");
    }

    /**
     * 获取驱动实例
     */
    public DriverAdapter getDriver(String vehicleId) {
        return drivers.computeIfAbsent(vehicleId, vid -> {
            LOG.info("为车辆 {} 创建驱动实例", vid);
            return driverRegistry.getAdapter("VDA5050");
        });
    }

    /**
     * 获取单例实例
     */
    public static DriverManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DriverManager 尚未初始化");
        }
        return instance;
    }

    /**
     * 移除驱动实例
     */
    public void removeDriver(String vehicleId) {
        DriverAdapter adapter = drivers.remove(vehicleId);
        if (adapter != null) {
            LOG.info("移除车辆 {} 的驱动实例", vehicleId);
        }
    }
}
