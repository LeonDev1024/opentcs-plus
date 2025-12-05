package org.opentcs.common.mqtt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

/**
 * MQTT服务类
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
public class MqttService {

    private final MqttPahoMessageHandler mqttOutbound;

    /**
     * 发送MQTT消息
     *
     * @param topic   主题
     * @param payload 消息内容
     */
    public void sendMessage(String topic, String payload) {
        sendMessage(topic, payload, 1);
    }

    /**
     * 发送MQTT消息
     *
     * @param topic   主题
     * @param payload 消息内容
     * @param qos     QoS级别 (0, 1, 2)
     */
    public void sendMessage(String topic, String payload, int qos) {
        Message<String> message = MessageBuilder.withPayload(payload)
            .setHeader("mqtt_topic", topic)
            .setHeader("mqtt_qos", qos)
            .build();
        try {
            mqttOutbound.handleMessage(message);
            log.info("MQTT消息发送成功 - Topic: {}, Payload: {}", topic, payload);
        } catch (MessagingException e) {
            log.error("MQTT消息发送失败 - Topic: {}, Payload: {}", topic, payload, e);
            throw e;
        }
    }

    /**
     * 发送MQTT消息到默认主题
     *
     * @param payload 消息内容
     */
    public void sendMessage(String payload) {
        sendMessage(null, payload, 1);
    }

    /**
     * 发送MQTT消息到默认主题
     *
     * @param payload 消息内容
     * @param qos     QoS级别 (0, 1, 2)
     */
    public void sendMessage(String payload, int qos) {
        sendMessage(null, payload, qos);
    }
}