package org.opentcs.driver.api;

import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;

import java.util.Set;

/**
 * 驱动适配器接口
 * 所有 AGV 驱动实现必须实现此接口
 */
public interface DriverAdapter {

    /**
     * 获取适配器类型
     * 如: VDA5050, MIRACLE, LMARK, CUSTOM
     */
    String getType();

    /**
     * 获取适配器版本
     */
    String getVersion();

    /**
     * 初始化适配器
     * @param config 驱动配置
     */
    void initialize(DriverConfig config);

    /**
     * 销毁适配器
     */
    void destroy();

    /**
     * 连接到 AGV
     * @param vehicleId 车辆ID
     * @param connectionConfig 连接配置（MQTT/TCP）
     */
    void connect(String vehicleId, Object connectionConfig);

    /**
     * 断开连接
     * @param vehicleId 车辆ID
     */
    void disconnect(String vehicleId);

    /**
     * 检查车辆是否已连接
     * @param vehicleId 车辆ID
     * @return 是否已连接
     */
    boolean isConnected(String vehicleId);

    /**
     * 发送订单到车辆
     * @param vehicleId 车辆ID
     * @param order 订单信息
     */
    void sendOrder(String vehicleId, DriverOrder order);

    /**
     * 发送即时动作
     * @param vehicleId 车辆ID
     * @param action 即时动作
     */
    void sendInstantAction(String vehicleId, InstantAction action);

    /**
     * 订阅车辆状态
     * @param vehicleId 车辆ID
     * @return 车辆状态
     */
    VehicleStatus receiveStatus(String vehicleId);

    /**
     * 获取所有已连接车辆
     * @return 车辆ID列表
     */
    Set<String> getConnectedVehicles();
}
