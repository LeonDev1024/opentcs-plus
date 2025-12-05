package org.opentcs.common.mqtt.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opentcs.common.mqtt.model.MqttMessageContent;
import org.opentcs.common.mqtt.processor.MqttMessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.util.List;

/**
 * MQTT消息路由器
 * 根据MQTT主题和消息内容路由到不同的处理器
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
public class MqttMessageRouter implements MessageHandler {

    private final MqttProcessorRegistry processorRegistry;
    private final ObjectMapper objectMapper;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            // 1. 提取MQTT主题
            Object topicHeader = message.getHeaders().get("mqtt_receivedTopic");
            if (topicHeader == null) {
                log.warn("MQTT消息缺少主题头信息");
                return;
            }
            String mqttTopic = topicHeader.toString();
            
            // 2. 提取消息内容（处理 byte[] 和 String 两种情况）
            String content;
            Object payload = message.getPayload();
            if (payload instanceof byte[]) {
                content = new String((byte[]) payload);
            } else if (payload instanceof String) {
                content = (String) payload;
            } else {
                content = payload.toString();
            }

            log.info("收到MQTT消息 - Topic: {}, Payload: {}", mqttTopic, content);

            // 3. 解析消息内容（尝试解析为JSON，如果失败则使用原始内容）
            MqttMessageContent messageContent = parseMessageContent(content);
            String messageType = null;
            
            if (messageContent != null) {
                // 4. 提取消息类型（优先使用 topic，其次使用 commandId）
                messageType = messageContent.getMessageType();
                log.debug("解析消息成功 - MessageType: {}", messageType);
            } else {
                // 如果解析失败，创建一个包含原始内容的消息对象
                log.debug("消息不是标准JSON格式，使用原始内容处理");
                messageContent = new MqttMessageContent();
                java.util.HashMap<String, Object> data = new java.util.HashMap<>();
                data.put("raw", content);
                messageContent.setData(data);
            }

            // 5. 查找匹配的处理器（即使消息类型为空，也尝试根据主题匹配）
            List<MqttMessageProcessor> processors = processorRegistry.findProcessors(mqttTopic, messageType);

            if (processors.isEmpty()) {
                log.warn("未找到匹配的处理器 - MQTT Topic: {}, Message Type: {}", mqttTopic, messageType != null ? messageType : "null");
                log.debug("当前注册的处理器数量: {}", processorRegistry.getProcessorCount());
                return;
            }

            log.info("找到 {} 个匹配的处理器 - Topic: {}, MessageType: {}", processors.size(), mqttTopic, messageType);

            // 6. 执行所有匹配的处理器
            for (MqttMessageProcessor processor : processors) {
                try {
                    log.debug("执行处理器: {}", processor.getClass().getName());
                    processor.process(mqttTopic, messageContent);
                    log.info("处理器 {} 处理消息成功", processor.getClass().getName());
                } catch (Exception e) {
                    log.error("处理器 {} 处理消息时发生异常", processor.getClass().getName(), e);
                    // 继续处理下一个处理器，不中断
                }
            }

        } catch (Exception e) {
            log.error("路由MQTT消息时发生异常", e);
            throw new MessagingException(message, "路由MQTT消息失败", e);
        }
    }

    /**
     * 解析消息内容
     *
     * @param content JSON字符串
     * @return 消息内容对象
     */
    private MqttMessageContent parseMessageContent(String content) {
        try {
            return objectMapper.readValue(content, MqttMessageContent.class);
        } catch (Exception e) {
            log.error("解析MQTT消息内容失败: {}", content, e);
            return null;
        }
    }
}
