package org.opentcs.common.mqtt.processor;

import org.opentcs.common.mqtt.model.MqttMessageContent;

/**
 * MQTT消息处理器接口
 * 业务模块实现此接口来处理具体的消息类型
 *
 * @author Lion Li
 */
public interface MqttMessageProcessor {

    /**
     * 处理MQTT消息
     *
     * @param mqttTopic MQTT主题
     * @param content   消息内容
     */
    void process(String mqttTopic, MqttMessageContent content);

    /**
     * 获取处理器支持的MQTT主题模式
     * 支持通配符，如：/pji/cloud/robots/+/+/r2c/json
     * 如果返回 null 或空字符串，表示支持所有主题（仅根据消息内容路由）
     *
     * @return 主题模式
     */
    String getSupportedTopicPattern();

    /**
     * 获取处理器支持的消息类型
     * 从消息内容的 topic 或 commandId 字段匹配
     * 如果返回 null 或空字符串，表示支持所有消息类型（仅根据MQTT主题路由）
     *
     * @return 消息类型（topic 或 commandId）
     */
    String getSupportedMessageType();
}
