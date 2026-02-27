package org.opentcs.driver.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 驱动管理器
 */
public class DriverManager {
    private static final DriverManager INSTANCE = new DriverManager();
    private final Map<String, VehicleDriver> drivers = new ConcurrentHashMap<>();

    private DriverManager() {
    }

    public static DriverManager getInstance() {
        return INSTANCE;
    }

    /**
     * 注册驱动
     * @param driver 驱动实例
     */
    public void registerDriver(VehicleDriver driver) {
        drivers.put(driver.getVehicleId(), driver);
    }

    /**
     * 注销驱动
     * @param vehicleId 车辆ID
     */
    public void unregisterDriver(String vehicleId) {
        drivers.remove(vehicleId);
    }

    /**
     * 获取驱动
     * @param vehicleId 车辆ID
     * @return 驱动实例
     */
    public VehicleDriver getDriver(String vehicleId) {
        return drivers.get(vehicleId);
    }

    /**
     * 获取所有驱动
     * @return 驱动映射
     */
    public Map<String, VehicleDriver> getAllDrivers() {
        return drivers;
    }

    /**
     * 启动所有驱动
     */
    public void startAllDrivers() {
        for (VehicleDriver driver : drivers.values()) {
            driver.start();
        }
    }

    /**
     * 停止所有驱动
     */
    public void stopAllDrivers() {
        for (VehicleDriver driver : drivers.values()) {
            driver.stop();
        }
    }
}
