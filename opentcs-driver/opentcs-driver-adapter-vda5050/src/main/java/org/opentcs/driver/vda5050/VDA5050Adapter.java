package org.opentcs.driver.vda5050;

import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;
import org.opentcs.driver.vda5050.client.VDA5050MqttClient;
import org.opentcs.driver.vda5050.converter.VDA5050MessageConverter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * VDA5050 协议适配器实现
 */
public class VDA5050Adapter implements DriverAdapter {

    public static final String DRIVER_TYPE = "VDA5050";
    public static final String DRIVER_VERSION = "1.1.0";

    private final Map<String, VDA5050MqttClient> clients = new ConcurrentHashMap<>();
    private final VDA5050MessageConverter messageConverter = new VDA5050MessageConverter();

    private DriverConfig config;
    private boolean initialized = false;

    @Override
    public String getType() {
        return DRIVER_TYPE;
    }

    @Override
    public String getVersion() {
        return DRIVER_VERSION;
    }

    @Override
    public void initialize(DriverConfig config) {
        this.config = config;
        this.initialized = true;
    }

    @Override
    public void destroy() {
        clients.values().forEach(VDA5050MqttClient::disconnect);
        clients.clear();
        initialized = false;
    }

    @Override
    public void connect(String vehicleId, Object connectionConfig) {
        checkInitialized();

        if (clients.containsKey(vehicleId)) {
            return; // 已经连接
        }

        DriverConfig.MqttConfig mqttConfig = resolveMqttConfig(connectionConfig);
        VDA5050MqttClient client = new VDA5050MqttClient(vehicleId, mqttConfig);
        client.connect();
        clients.put(vehicleId, client);
    }

    @Override
    public void disconnect(String vehicleId) {
        VDA5050MqttClient client = clients.remove(vehicleId);
        if (client != null) {
            client.disconnect();
        }
    }

    @Override
    public boolean isConnected(String vehicleId) {
        VDA5050MqttClient client = clients.get(vehicleId);
        return client != null && client.isConnected();
    }

    @Override
    public void sendOrder(String vehicleId, DriverOrder order) {
        checkInitialized();
        VDA5050MqttClient client = getClient(vehicleId);

        // 将DriverOrder转换为VDA5050协议格式
        String vda5050Order = messageConverter.toVDA5050Order(order);
        client.publish("order", vda5050Order);
    }

    @Override
    public void sendInstantAction(String vehicleId, InstantAction action) {
        checkInitialized();
        VDA5050MqttClient client = getClient(vehicleId);

        // 将即时动作转换为VDA5050协议格式
        String vda5050Action = messageConverter.toVDA5050InstantAction(action);
        client.publish("instantActions", vda5050Action);
    }

    @Override
    public VehicleStatus receiveStatus(String vehicleId) {
        checkInitialized();
        VDA5050MqttClient client = getClient(vehicleId);

        // 从消息队列中获取最新的状态消息
        String statusJson = client.pollStatus();
        if (statusJson == null) {
            return null;
        }

        // 将VDA5050状态转换为VehicleStatus
        return messageConverter.fromVDA5050Status(statusJson);
    }

    @Override
    public Set<String> getConnectedVehicles() {
        return clients.keySet();
    }

    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("适配器未初始化");
        }
    }

    private VDA5050MqttClient getClient(String vehicleId) {
        VDA5050MqttClient client = clients.get(vehicleId);
        if (client == null) {
            throw new IllegalStateException("车辆未连接: " + vehicleId);
        }
        return client;
    }

    private DriverConfig.MqttConfig resolveMqttConfig(Object connectionConfig) {
        if (connectionConfig instanceof DriverConfig.MqttConfig) {
            return (DriverConfig.MqttConfig) connectionConfig;
        }

        // 使用默认配置
        if (config != null && config.getMqttConfig() != null) {
            return config.getMqttConfig();
        }

        throw new IllegalArgumentException("无效的连接配置");
    }
}
