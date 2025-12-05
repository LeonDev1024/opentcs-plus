package org.opentcs.vehicle.processor;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.mqtt.model.MqttMessageContent;
import org.opentcs.common.mqtt.processor.AbstractMqttMessageProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class RobotStatusProcessor extends AbstractMqttMessageProcessor {
    
    @Override
    public String getSupportedTopicPattern() {
        // 支持的主题模式：匹配 bridge/# 下的所有主题
        return "bridge/#";
    }

    @Override
    public void process(String mqttTopic, MqttMessageContent content) {
        log.info("RobotStatusProcessor 收到消息 - Topic: {}", mqttTopic);
        
        // 处理机器人状态消息
        if (content == null || content.getData() == null) {
            log.warn("消息内容为空或数据为空 - Topic: {}", mqttTopic);
            return;
        }
        
        Map<String, Object> data = content.getData();
        String status = data.get("status") != null ? data.get("status").toString() : null;
        String taskStatus = data.get("taskStatus") != null ? data.get("taskStatus").toString() : null;

        log.info("机器人状态更新 - Topic: {}, Status: {}, TaskStatus: {}", mqttTopic, status, taskStatus);
        
        // 打印完整消息内容用于调试
        log.debug("完整消息内容: {}", data);
    }

    @Override
    public String getSupportedMessageType() {
        // 支持的消息类型（从消息内容的 topic 或 commandId 字段匹配）
        // 如果消息中没有这些字段，则仅根据主题模式匹配
        return "robotStatus";
    }
}
