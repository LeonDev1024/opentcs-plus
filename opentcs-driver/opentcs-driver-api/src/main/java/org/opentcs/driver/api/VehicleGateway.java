package org.opentcs.driver.api;

import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;

import java.util.Set;
import java.util.function.Consumer;

/**
 * 车辆网关接口
 * 统一管理所有驱动适配器
 */
public interface VehicleGateway {

    /**
     * 初始化网关
     */
    void initialize();

    /**
     * 注册驱动适配器（由 {@link org.opentcs.driver.registry.DriverRegistry} 或装配代码调用）。
     */
    void registerAdapter(String driverType, DriverAdapter adapter);

    /**
     * 销毁网关
     */
    void destroy();

    /**
     * 注册车辆及其驱动配置
     * @param vehicleId 车辆ID
     * @param config 驱动配置
     */
    void registerVehicle(String vehicleId, DriverConfig config);

    /**
     * 注销车辆
     * @param vehicleId 车辆ID
     */
    void unregisterVehicle(String vehicleId);

    /**
     * 获取所有已注册车辆
     * @return 车辆ID集合
     */
    Set<String> getRegisteredVehicles();

    /**
     * 获取所有在线车辆
     * @return 在线车辆ID集合
     */
    Set<String> getOnlineVehicles();

    /**
     * 发送订单到指定车辆
     * @param vehicleId 车辆ID
     * @param order 订单
     */
    void sendOrder(String vehicleId, DriverOrder order);

    /**
     * 发送即时动作
     * @param vehicleId 车辆ID
     * @param action 即时动作
     */
    void sendInstantAction(String vehicleId, InstantAction action);

    /**
     * 获取车辆状态
     * @param vehicleId 车辆ID
     * @return 车辆状态
     */
    VehicleStatus getVehicleStatus(String vehicleId);

    /**
     * 添加车辆状态监听器
     * @param listener 监听器
     */
    void addStatusListener(Consumer<VehicleStatus> listener);

    /**
     * 移除车辆状态监听器
     * @param listener 监听器
     */
    void removeStatusListener(Consumer<VehicleStatus> listener);
}
