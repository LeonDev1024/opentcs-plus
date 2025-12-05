package org.opentcs.common.mqtt.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

/**
 * MQTT消息监听器
 * 用户可以实现此接口来自定义消息处理逻辑
 *
 * @author Lion Li
 */
@MessageEndpoint
@Component
@RequiredArgsConstructor
public class MqttMessageListener {

    private final ApplicationContext applicationContext;

    /**
     * 处理MQTT消息
     *
     * @param message 接收到的消息
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        // 获取实际的消息处理器
        MessageHandler handler = getMessageHandler();
        if (handler != null) {
            handler.handleMessage(message);
        } else {
            // 如果没有找到处理器，记录警告
            String topic = message.getHeaders().containsKey("mqtt_receivedTopic") ?
                message.getHeaders().get("mqtt_receivedTopic").toString() : "unknown";
            org.slf4j.LoggerFactory.getLogger(MqttMessageListener.class)
                .warn("收到MQTT消息但没有找到处理器 - Topic: {}", topic);
        }
    }

    /**
     * 获取消息处理器
     * 优先使用注入的 mqttMessageHandler，子类可以重写此方法提供自定义的消息处理器
     *
     * @return MessageHandler
     */
    protected MessageHandler getMessageHandler() {
        try {
            return applicationContext.getBean("mqttMessageHandler", MessageHandler.class);
        } catch (Exception e) {
            return getCustomMessageHandler();
        }
    }

    /**
     * 获取自定义消息处理器
     * 子类可以重写此方法提供自定义的消息处理器
     *
     * @return MessageHandler
     */
    protected MessageHandler getCustomMessageHandler() {
        return null;
    }
}