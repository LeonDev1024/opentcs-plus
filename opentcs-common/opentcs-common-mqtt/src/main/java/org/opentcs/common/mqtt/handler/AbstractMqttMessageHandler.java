package org.opentcs.common.mqtt.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * MQTT消息处理器抽象类
 * 业务模块可以继承此类并实现 processMessage 方法来处理具体的业务逻辑
 *
 * @author Lion Li
 */
@Slf4j
public abstract class AbstractMqttMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
            byte[] payload = (byte[]) message.getPayload();
            String content = new String(payload);

            log.debug("收到MQTT消息 - Topic: {}, Payload: {}", topic, content);
            
            // 调用子类实现的具体处理逻辑
            processMessage(topic, content);
        } catch (Exception e) {
            log.error("处理MQTT消息时发生异常", e);
            throw new MessagingException(message, "处理MQTT消息失败", e);
        }
    }

    /**
     * 处理MQTT消息
     * 子类必须实现此方法来处理具体的业务逻辑
     *
     * @param topic   主题
     * @param content 消息内容
     */
    protected abstract void processMessage(String topic, String content);
}