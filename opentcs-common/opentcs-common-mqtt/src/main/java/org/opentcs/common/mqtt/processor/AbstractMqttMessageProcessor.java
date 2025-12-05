package org.opentcs.common.mqtt.processor;

import org.opentcs.common.mqtt.model.MqttMessageContent;
import lombok.extern.slf4j.Slf4j;

/**
 * MQTT消息处理器抽象类
 * 提供默认实现，业务模块可以继承此类并实现 process 方法
 *
 * @author Lion Li
 */
@Slf4j
public abstract class AbstractMqttMessageProcessor implements MqttMessageProcessor {

    /**
     * 处理MQTT消息
     * 子类必须实现此方法来处理具体的业务逻辑
     *
     * @param mqttTopic MQTT主题
     * @param content   消息内容
     */
    @Override
    public abstract void process(String mqttTopic, MqttMessageContent content);

    /**
     * 获取处理器支持的MQTT主题模式
     * 子类可以重写此方法指定支持的主题模式
     * 默认返回 null，表示支持所有主题（仅根据消息内容路由）
     *
     * @return 主题模式
     */
    @Override
    public String getSupportedTopicPattern() {
        return null;
    }

    /**
     * 获取处理器支持的消息类型
     * 子类可以重写此方法指定支持的消息类型
     * 默认返回 null，表示支持所有消息类型（仅根据MQTT主题路由）
     *
     * @return 消息类型（topic 或 commandId）
     */
    @Override
    public String getSupportedMessageType() {
        return null;
    }
}
