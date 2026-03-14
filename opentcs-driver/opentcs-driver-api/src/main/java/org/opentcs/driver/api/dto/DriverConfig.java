package org.opentcs.driver.api.dto;

import java.util.Map;

/**
 * 驱动配置
 */
public class DriverConfig {

    /**
     * 驱动类型：VDA5050, MIRACLE, LMARK, CUSTOM
     */
    private String driverType;

    /**
     * 连接类型：MQTT, TCP
     */
    private String connectionType;

    /**
     * MQTT 配置
     */
    private MqttConfig mqttConfig;

    /**
     * TCP 配置
     */
    private TcpConfig tcpConfig;

    /**
     * 驱动特定配置
     */
    private Map<String, String> properties;

    // Getters and Setters
    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    public void setMqttConfig(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    public TcpConfig getTcpConfig() {
        return tcpConfig;
    }

    public void setTcpConfig(TcpConfig tcpConfig) {
        this.tcpConfig = tcpConfig;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * MQTT 连接配置
     */
    public static class MqttConfig {
        private String brokerUrl;
        private String username;
        private String password;
        private String clientIdPrefix;
        private String publishTopic;
        private String subscribeTopic;
        private int qos = 1;

        // Getters and Setters
        public String getBrokerUrl() { return brokerUrl; }
        public void setBrokerUrl(String brokerUrl) { this.brokerUrl = brokerUrl; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getClientIdPrefix() { return clientIdPrefix; }
        public void setClientIdPrefix(String clientIdPrefix) { this.clientIdPrefix = clientIdPrefix; }
        public String getPublishTopic() { return publishTopic; }
        public void setPublishTopic(String publishTopic) { this.publishTopic = publishTopic; }
        public String getSubscribeTopic() { return subscribeTopic; }
        public void setSubscribeTopic(String subscribeTopic) { this.subscribeTopic = subscribeTopic; }
        public int getQos() { return qos; }
        public void setQos(int qos) { this.qos = qos; }
    }

    /**
     * TCP 连接配置
     */
    public static class TcpConfig {
        private String host;
        private int port;
        private int connectionTimeout = 5000;
        private int readTimeout = 30000;

        // Getters and Setters
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        public int getConnectionTimeout() { return connectionTimeout; }
        public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }
        public int getReadTimeout() { return readTimeout; }
        public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }
    }
}
