package org.opentcs.driver.vda5050.client;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.opentcs.driver.api.dto.DriverConfig.MqttConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * VDA5050 MQTT 客户端
 * 负责与AGV的MQTT通信
 */
public class VDA5050MqttClient {

    private static final Logger LOG = LoggerFactory.getLogger(VDA5050MqttClient.class);

    private final String vehicleId;
    private final MqttConfig mqttConfig;

    private volatile boolean connected = false;
    private final BlockingQueue<String> statusQueue = new LinkedBlockingQueue<>(100);
    private MqttClient client;

    public VDA5050MqttClient(String vehicleId, MqttConfig mqttConfig) {
        this.vehicleId = vehicleId;
        this.mqttConfig = mqttConfig;
    }

    /**
     * 连接到MQTT Broker
     */
    public void connect() {
        if (connected) {
            return;
        }

        try {
            validateConfig();
            Path persistenceDir = Files.createTempDirectory("opentcs-vda5050-mqtt-");
            client = new MqttClient(mqttConfig.getBrokerUrl(), clientId(),
                    new MqttDefaultFilePersistence(persistenceDir.toString()));
            client.setCallback(new VehicleMqttCallback());
            client.connect(connectOptions());
            for (String topic : subscribeTopics()) {
                client.subscribe(topic, mqttConfig.getQos());
                LOG.info("车辆 {} 已订阅 MQTT 主题: {}", vehicleId, topic);
            }
            connected = true;
            LOG.info("车辆 {} 已连接到 MQTT Broker: {}", vehicleId, mqttConfig.getBrokerUrl());
        } catch (Exception e) {
            LOG.error("连接车辆 {} 失败: {}", vehicleId, e.getMessage());
            throw new RuntimeException("MQTT连接失败", e);
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (!connected) {
            return;
        }

        try {
            if (client != null) {
                if (client.isConnected()) {
                    client.disconnect();
                }
                client.close();
            }
            connected = false;
            LOG.info("车辆 {} 已断开连接", vehicleId);
        } catch (Exception e) {
            LOG.error("断开车辆 {} 连接失败: {}", vehicleId, e.getMessage());
        }
    }

    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return connected && client != null && client.isConnected();
    }

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param payload 消息内容
     */
    public void publish(String topic, String payload) {
        if (!connected) {
            throw new IllegalStateException("未连接到MQTT Broker");
        }

        try {
            String fullTopic = resolveTopic(mqttConfig.getPublishTopic(), topic);
            MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            message.setQos(mqttConfig.getQos());
            message.setRetained(false);
            client.publish(fullTopic, message);

            LOG.debug("车辆 {} 发布消息到主题 {}: {}", vehicleId, fullTopic, payload);
        } catch (Exception e) {
            LOG.error("车辆 {} 发布消息失败: {}", vehicleId, e.getMessage());
            throw new RuntimeException("消息发布失败", e);
        }
    }

    /**
     * 接收状态消息（供内部回调使用）
     *
     * @param statusJson 状态JSON
     */
    public void onStatusReceived(String statusJson) {
        try {
            statusQueue.offer(statusJson, 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.warn("状态队列已满，丢弃消息: {}", vehicleId);
        }
    }

    /**
     * 轮询状态消息
     *
     * @return 状态JSON，如果没有消息返回null
     */
    public String pollStatus() {
        return statusQueue.poll();
    }

    /**
     * 获取车辆ID
     */
    public String getVehicleId() {
        return vehicleId;
    }

    private void validateConfig() {
        if (mqttConfig == null) {
            throw new IllegalArgumentException("MQTT 配置不能为空");
        }
        if (mqttConfig.getBrokerUrl() == null || mqttConfig.getBrokerUrl().isBlank()) {
            throw new IllegalArgumentException("MQTT brokerUrl 不能为空");
        }
        if (mqttConfig.getPublishTopic() == null || mqttConfig.getPublishTopic().isBlank()) {
            throw new IllegalArgumentException("MQTT publishTopic 不能为空");
        }
        if (mqttConfig.getSubscribeTopic() == null || mqttConfig.getSubscribeTopic().isBlank()) {
            throw new IllegalArgumentException("MQTT subscribeTopic 不能为空");
        }
    }

    private String clientId() {
        String prefix = mqttConfig.getClientIdPrefix();
        if (prefix == null || prefix.isBlank()) {
            prefix = "opentcs-vda5050";
        }
        return prefix + "_" + vehicleId;
    }

    private MqttConnectOptions connectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(30);
        if (mqttConfig.getUsername() != null && !mqttConfig.getUsername().isBlank()) {
            options.setUserName(mqttConfig.getUsername());
        }
        if (mqttConfig.getPassword() != null && !mqttConfig.getPassword().isBlank()) {
            options.setPassword(mqttConfig.getPassword().toCharArray());
        }
        return options;
    }

    private List<String> subscribeTopics() {
        String template = mqttConfig.getSubscribeTopic();
        if (template.contains("{topic}")) {
            return List.of(
                    resolveTopic(template, "state"),
                    resolveTopic(template, "connection"),
                    resolveTopic(template, "factsheet"),
                    resolveTopic(template, "visualization")
            );
        }
        return List.of(resolveTopic(template, null));
    }

    private String resolveTopic(String template, String topic) {
        String resolved = template.replace("{vehicleId}", vehicleId);
        if (topic != null && !topic.isBlank()) {
            if (resolved.contains("{topic}")) {
                resolved = resolved.replace("{topic}", topic);
            } else {
                resolved = resolved.endsWith("/") ? resolved + topic : resolved + "/" + topic;
            }
        }
        return resolved;
    }

    private class VehicleMqttCallback implements MqttCallbackExtended {

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            connected = true;
            LOG.info("车辆 {} MQTT {}完成: {}", vehicleId, reconnect ? "重连" : "连接", serverURI);
        }

        @Override
        public void connectionLost(Throwable cause) {
            connected = false;
            LOG.warn("车辆 {} MQTT 连接丢失: {}", vehicleId,
                    cause == null ? "unknown" : cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            if (topic.endsWith("/state") || topic.endsWith("/connection")
                    || topic.endsWith("/factsheet") || topic.endsWith("/visualization")
                    || topic.equals(resolveTopic(mqttConfig.getSubscribeTopic(), null))) {
                onStatusReceived(payload);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            LOG.debug("车辆 {} MQTT 消息发送完成: {}", vehicleId, token.getMessageId());
        }
    }
}
