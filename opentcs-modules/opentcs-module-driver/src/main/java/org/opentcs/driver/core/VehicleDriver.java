package org.opentcs.driver.core;

import org.opentcs.driver.protocol.VDA5050Message;

/**
 * 车辆驱动接口
 */
public interface VehicleDriver {
    /**
     * 获取车辆ID
     * @return 车辆ID
     */
    String getVehicleId();

    /**
     * 启动驱动
     */
    void start();

    /**
     * 停止驱动
     */
    void stop();

    /**
     * 发送消息
     * @param message VDA5050消息
     */
    void sendMessage(VDA5050Message message);

    /**
     * 接收消息
     * @return VDA5050消息
     */
    VDA5050Message receiveMessage();

    /**
     * 连接状态
     * @return 是否连接
     */
    boolean isConnected();

    /**
     * 连接车辆
     */
    void connect();

    /**
     * 断开连接
     */
    void disconnect();
}
