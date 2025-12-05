package org.opentcs.common.mqtt.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;

import java.lang.reflect.Field;
import java.util.List;

/**
 * MQTT连接初始化器
 * 在Spring上下文完全初始化后，启动MQTT适配器
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
public class MqttConnectionInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final List<MessageProducer> mqttInboundAdapters;
    private final MessageChannel mqttInputChannel;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 避免重复初始化（ContextRefreshedEvent 可能会触发多次）
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        if (mqttInboundAdapters == null || mqttInboundAdapters.isEmpty()) {
            log.warn("没有配置MQTT适配器，跳过启动");
            return;
        }

        log.info("正在启动 {} 个MQTT适配器...", mqttInboundAdapters.size());
        
        int startedCount = 0;
        int failedCount = 0;
        
        for (MessageProducer adapter : mqttInboundAdapters) {
            if (!(adapter instanceof MqttPahoMessageDrivenChannelAdapter)) {
                log.warn("跳过非 MqttPahoMessageDrivenChannelAdapter 类型的适配器: {}", adapter.getClass().getName());
                continue;
            }
            
            MqttPahoMessageDrivenChannelAdapter mqttAdapter = (MqttPahoMessageDrivenChannelAdapter) adapter;
            
            // 获取客户端ID和主题
            String clientId = "unknown";
            String topics = "unknown";
            try {
                Field clientIdField = mqttAdapter.getClass().getSuperclass().getDeclaredField("clientId");
                clientIdField.setAccessible(true);
                clientId = (String) clientIdField.get(mqttAdapter);
                topics = String.join(",", mqttAdapter.getTopic());
            } catch (Exception e) {
                log.debug("无法获取适配器信息", e);
            }
            
            try {
                // 检查适配器是否已经在运行
                if (mqttAdapter.isRunning()) {
                    log.info("MQTT适配器已在运行中 - ClientId: {}, Topic: {}", clientId, topics);
                    startedCount++;
                    continue;
                }
                
                log.debug("启动MQTT适配器 - ClientId: {}, Topic: {}", clientId, topics);
                mqttAdapter.start();
                
                // 等待连接建立（最多等待5秒）
                int maxWaitTime = 5000;
                int waitInterval = 100;
                int waitedTime = 0;
                boolean isRunning = false;
                
                while (waitedTime < maxWaitTime) {
                    Thread.sleep(waitInterval);
                    waitedTime += waitInterval;
                    isRunning = mqttAdapter.isRunning();
                    if (isRunning) {
                        break;
                    }
                }
                
                if (isRunning) {
                    startedCount++;
                    log.info("✓ MQTT适配器启动成功 - ClientId: {}, Topic: {}, 状态: 运行中 (等待时间: {}ms)", 
                        clientId, topics, waitedTime);
                } else {
                    failedCount++;
                    log.error("✗ MQTT适配器启动失败 - ClientId: {}, Topic: {}, 状态: 未运行 (等待 {}ms 后仍无法连接)", 
                        clientId, topics, maxWaitTime);
                }
            } catch (Exception e) {
                failedCount++;
                log.error("✗ 启动MQTT适配器时发生异常 - ClientId: {}, Topic: {}", clientId, topics, e);
            }
        }
        
        log.info("MQTT适配器启动完成 - 总数: {}, 成功: {}, 失败: {}", 
            mqttInboundAdapters.size(), startedCount, failedCount);
    }
}
