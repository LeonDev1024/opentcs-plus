package org.opentcs.common.mqtt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * MQTT配置属性
 *
 * @author Lion Li
 */
@Data
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    /**
     * 是否启用MQTT
     */
    private boolean enabled = true;

    /**
     * MQTT服务器配置
     */
    private String username;
    private String password;
    private String[] serverUrls;
    private String clientId;
    
    /**
     * 订阅的主题列表（支持多个topic订阅）
     */
    private List<String> topics = new ArrayList<>();
    
    private int timeout = 30;
    private int keepAliveInterval = 60;
    private boolean cleanSession = true;
    private boolean automaticReconnect = true;
    private int qos = 1;
    private boolean async = true;
}
