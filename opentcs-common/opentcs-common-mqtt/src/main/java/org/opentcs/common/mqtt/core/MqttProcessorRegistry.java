package org.opentcs.common.mqtt.core;

import org.opentcs.common.mqtt.processor.MqttMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MQTT处理器注册表
 * 管理所有注册的 MQTT 消息处理器
 *
 * @author Lion Li
 */
@Slf4j
@Component
public class MqttProcessorRegistry {

    /**
     * 按主题模式存储的处理器列表
     * Key: 主题模式，Value: 处理器列表
     */
    private final ConcurrentHashMap<String, List<MqttMessageProcessor>> topicProcessors = new ConcurrentHashMap<>();

    /**
     * 按消息类型存储的处理器列表
     * Key: 消息类型（topic 或 commandId），Value: 处理器列表
     */
    private final ConcurrentHashMap<String, List<MqttMessageProcessor>> messageTypeProcessors = new ConcurrentHashMap<>();

    /**
     * 通用处理器列表（不指定主题和消息类型）
     */
    private final List<MqttMessageProcessor> generalProcessors = new CopyOnWriteArrayList<>();

    /**
     * 初始化处理器注册表
     * 由 Spring 自动注入所有 MqttMessageProcessor 实现
     *
     * @param processors 所有注册的处理器
     */
    @Autowired(required = false)
    public void setProcessors(List<MqttMessageProcessor> processors) {
        if (processors != null && !processors.isEmpty()) {
            for (MqttMessageProcessor processor : processors) {
                registerProcessor(processor);
            }
            log.info("已注册 {} 个MQTT消息处理器", processors.size());
        } else {
            log.warn("未找到任何MQTT消息处理器");
        }
    }

    /**
     * 注册处理器
     *
     * @param processor 处理器
     */
    public void registerProcessor(MqttMessageProcessor processor) {
        String topicPattern = processor.getSupportedTopicPattern();
        String messageType = processor.getSupportedMessageType();

        // 如果既没有指定主题模式，也没有指定消息类型，则作为通用处理器
        if ((topicPattern == null || topicPattern.isEmpty()) &&
            (messageType == null || messageType.isEmpty())) {
            generalProcessors.add(processor);
            log.debug("注册通用MQTT处理器: {}", processor.getClass().getName());
            return;
        }

        // 注册主题模式处理器
        if (topicPattern != null && !topicPattern.isEmpty()) {
            topicProcessors.computeIfAbsent(topicPattern, k -> new CopyOnWriteArrayList<>()).add(processor);
            log.debug("注册主题模式处理器: {} -> {}", topicPattern, processor.getClass().getName());
        }

        // 注册消息类型处理器
        if (messageType != null && !messageType.isEmpty()) {
            messageTypeProcessors.computeIfAbsent(messageType, k -> new CopyOnWriteArrayList<>()).add(processor);
            log.debug("注册消息类型处理器: {} -> {}", messageType, processor.getClass().getName());
        }
    }

    /**
     * 根据主题和消息类型查找处理器
     *
     * @param mqttTopic   MQTT主题
     * @param messageType 消息类型（从消息内容中提取的 topic 或 commandId）
     * @return 匹配的处理器列表
     */
    public List<MqttMessageProcessor> findProcessors(String mqttTopic, String messageType) {
        List<MqttMessageProcessor> matchedProcessors = new ArrayList<>();

        // 1. 优先查找同时匹配主题和消息类型的处理器
        for (String topicPattern : topicProcessors.keySet()) {
            if (matchTopic(mqttTopic, topicPattern)) {
                List<MqttMessageProcessor> processors = topicProcessors.get(topicPattern);
                if (messageType != null && !messageType.isEmpty()) {
                    // 如果消息类型匹配，则只返回匹配的处理器
                    for (MqttMessageProcessor processor : processors) {
                        String supportedType = processor.getSupportedMessageType();
                        if (supportedType == null || supportedType.isEmpty() || supportedType.equals(messageType)) {
                            matchedProcessors.add(processor);
                        }
                    }
                } else {
                    // 如果消息类型为空，返回所有匹配主题的处理器
                    matchedProcessors.addAll(processors);
                }
            }
        }

        // 2. 如果主题不匹配，查找仅匹配消息类型的处理器
        if (matchedProcessors.isEmpty() && messageType != null && !messageType.isEmpty()) {
            List<MqttMessageProcessor> processors = messageTypeProcessors.get(messageType);
            if (processors != null) {
                matchedProcessors.addAll(processors);
            }
        }

        // 3. 如果都没有匹配，返回通用处理器
        if (matchedProcessors.isEmpty()) {
            matchedProcessors.addAll(generalProcessors);
        }

        return matchedProcessors;
    }

    /**
     * 匹配主题模式
     * 支持通配符 + 和 #
     *
     * @param topic  实际主题
     * @param pattern 主题模式
     * @return 是否匹配
     */
    private boolean matchTopic(String topic, String pattern) {
        if (topic == null || pattern == null) {
            return false;
        }
        if (topic.equals(pattern)) {
            return true;
        }
        // 简单的通配符匹配：将 + 替换为正则表达式的 [^/]+，将 # 替换为 .*
        String regex = pattern
            .replace("+", "[^/]+")
            .replace("#", ".*")
            .replace("/", "\\/");
        return topic.matches(regex);
    }
}
