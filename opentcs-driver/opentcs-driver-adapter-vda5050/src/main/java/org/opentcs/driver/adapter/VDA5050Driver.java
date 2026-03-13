package org.opentcs.driver.adapter;

import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;
import org.opentcs.driver.protocol.InstantActionsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * VDA5050 驱动实现
 */
public class VDA5050Driver implements DriverAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(VDA5050Driver.class);

    private DriverConfig config;
    private final Set<String> connectedVehicles = ConcurrentHashMap.newKeySet();

    @Override
    public String getType() {
        return "VDA5050";
    }

    @Override
    public String getVersion() {
        return "1.1.0";
    }

    @Override
    public void initialize(DriverConfig config) {
        this.config = config;
        LOG.info("VDA5050 驱动初始化完成");
    }

    @Override
    public void destroy() {
        connectedVehicles.clear();
        LOG.info("VDA5050 驱动已销毁");
    }

    @Override
    public void connect(String vehicleId, Object connectionConfig) {
        LOG.info("连接车辆: {}", vehicleId);
        connectedVehicles.add(vehicleId);
    }

    @Override
    public void disconnect(String vehicleId) {
        LOG.info("断开车辆连接: {}", vehicleId);
        connectedVehicles.remove(vehicleId);
    }

    @Override
    public boolean isConnected(String vehicleId) {
        return connectedVehicles.contains(vehicleId);
    }

    @Override
    public void sendOrder(String vehicleId, DriverOrder order) {
        LOG.info("发送订单到车辆 {}: {}", vehicleId, order);
    }

    @Override
    public void sendInstantAction(String vehicleId, InstantAction action) {
        LOG.info("发送即时动作到车辆 {}: {}", vehicleId, action);
    }

    /**
     * 发送即时动作消息
     */
    public void sendInstantActions(InstantActionsMessage message) {
        LOG.info("发送即时动作消息: {}", message);
    }

    @Override
    public VehicleStatus receiveStatus(String vehicleId) {
        LOG.debug("接收车辆状态: {}", vehicleId);
        return null;
    }

    @Override
    public Set<String> getConnectedVehicles() {
        return connectedVehicles;
    }
}
