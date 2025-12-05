package org.opentcs.common.mqtt.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * MQTT消息处理器默认实现
 * 如果业务模块没有提供自定义的处理器，将使用此默认实现
 * 业务模块可以通过继承 AbstractMqttMessageHandler 并提供自己的实现来覆盖此默认行为
 *
 * @author Lion Li
 */
@Slf4j
public class DefaultMqttMessageHandler extends AbstractMqttMessageHandler {

    @Override
    protected void processMessage(String topic, String content) {
        log.info("收到MQTT消息 - Topic: {}, Payload: {}", topic, content);
        log.warn("使用默认消息处理器，建议在业务模块中实现自定义的 AbstractMqttMessageHandler");
    }
}
