package org.opentcs.common.mqtt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opentcs.common.mqtt.core.MqttMessageRouter;
import org.opentcs.common.mqtt.core.MqttProcessorRegistry;
import org.opentcs.common.mqtt.service.MqttService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * MQTT 自动装配
 *
 * @author lyc
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(value = "mqtt.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MqttProperties.class)
public class MqttAutoConfiguration {

    /**
     * MQTT消息通道
     *
     * @return MessageChannel
     */
    @Bean("mqttInputChannel")
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * 创建MQTT客户端工厂
     *
     * @param mqttProperties MQTT配置属性
     * @return MqttPahoClientFactory
     */
    @Bean
    @ConditionalOnMissingBean(name = "mqttClientFactory")
    public MqttPahoClientFactory mqttClientFactory(MqttProperties mqttProperties) {
        if (mqttProperties.getServerUrls() == null || mqttProperties.getServerUrls().length == 0) {
            throw new IllegalStateException("必须配置 mqtt.serverUrls");
        }
        
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        
        // 设置服务器URI
        options.setServerURIs(mqttProperties.getServerUrls());
        
        // 设置用户名和密码
        if (mqttProperties.getUsername() != null && !mqttProperties.getUsername().isEmpty()) {
            options.setUserName(mqttProperties.getUsername());
        }
        if (mqttProperties.getPassword() != null && !mqttProperties.getPassword().isEmpty()) {
            options.setPassword(mqttProperties.getPassword().toCharArray());
        }
        
        // 设置连接选项
        options.setCleanSession(mqttProperties.isCleanSession());
        options.setAutomaticReconnect(mqttProperties.isAutomaticReconnect());
        options.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());
        options.setConnectionTimeout(mqttProperties.getTimeout());
        
        factory.setConnectionOptions(options);
        return factory;
    }

    /**
     * MQTT入站适配器列表（支持多个topic订阅）
     *
     * @param mqttProperties MQTT配置属性
     * @param clientFactory  MQTT客户端工厂
     * @param mqttInputChannel 消息通道
     * @return MessageProducer列表
     */
    @Bean
    @ConditionalOnMissingBean(name = "mqttInboundAdapters")
    public List<MessageProducer> mqttInboundAdapters(MqttProperties mqttProperties, 
                                                      MqttPahoClientFactory clientFactory,
                                                      MessageChannel mqttInputChannel) {
        if (mqttProperties.getClientId() == null || mqttProperties.getClientId().isEmpty()) {
            throw new IllegalStateException("必须配置 mqtt.clientId");
        }
        
        List<String> topics = mqttProperties.getTopics();
        if (topics == null || topics.isEmpty()) {
            throw new IllegalStateException("必须配置 mqtt.topics");
        }
        
        List<MessageProducer> adapters = new ArrayList<>();
        int index = 0;
        
        for (String topic : topics) {
            if (topic == null || topic.isEmpty()) {
                log.warn("跳过空的topic配置");
                continue;
            }
            
            // 为每个topic创建独立的适配器
            String adapterClientId = mqttProperties.getClientId() + "_inbound_" + index++;
            MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(adapterClientId, clientFactory, topic);
            
            adapter.setCompletionTimeout(mqttProperties.getTimeout() * 1000L);
            adapter.setConverter(new DefaultPahoMessageConverter());
            adapter.setQos(mqttProperties.getQos());
            adapter.setOutputChannel(mqttInputChannel);
            
            adapters.add(adapter);
            log.info("创建MQTT订阅适配器 - Topic: {}, ClientId: {}, QoS: {}", topic, adapterClientId, mqttProperties.getQos());
        }
        
        return adapters;
    }

    /**
     * MQTT连接初始化器
     *
     * @param mqttInboundAdapters MQTT入站适配器列表
     * @param mqttInputChannel 消息通道
     * @return MqttConnectionInitializer
     */
    @Bean
    public MqttConnectionInitializer mqttConnectionInitializer(List<MessageProducer> mqttInboundAdapters,
                                                               MessageChannel mqttInputChannel) {
        return new MqttConnectionInitializer(mqttInboundAdapters, mqttInputChannel);
    }

    /**
     * MQTT处理器注册表
     *
     * @return MqttProcessorRegistry
     */
    @Bean
    public MqttProcessorRegistry mqttProcessorRegistry() {
        return new MqttProcessorRegistry();
    }

    /**
     * MQTT消息路由器
     * 如果业务模块提供了自定义的 AbstractMqttMessageHandler 实现，则优先使用业务模块的实现
     * 否则使用路由器模式（支持多个处理器）
     *
     * @param processorRegistry 处理器注册表
     * @param objectMapper      JSON对象映射器
     * @return MessageHandler
     */
    @Bean
    @ConditionalOnMissingBean(name = "mqttMessageHandler")
    public MessageHandler mqttMessageHandler(MqttProcessorRegistry processorRegistry,
                                             ObjectMapper objectMapper) {
        return new MqttMessageRouter(processorRegistry, objectMapper);
    }

    /**
     * MQTT出站适配器（用于发送消息）
     *
     * @param mqttProperties MQTT配置属性
     * @param clientFactory  MQTT客户端工厂
     * @return MqttPahoMessageHandler
     */
    @Bean
    @ConditionalOnMissingBean(name = "mqttOutbound")
    public MqttPahoMessageHandler mqttOutbound(MqttProperties mqttProperties, 
                                               MqttPahoClientFactory clientFactory) {
        if (mqttProperties.getClientId() == null || mqttProperties.getClientId().isEmpty()) {
            throw new IllegalStateException("必须配置 mqtt.clientId");
        }
        
        MqttPahoMessageHandler handler =
            new MqttPahoMessageHandler(mqttProperties.getClientId() + "_outbound", clientFactory);
        
        handler.setAsync(mqttProperties.isAsync());
        // 如果配置了topics，使用第一个topic作为默认topic
        List<String> topics = mqttProperties.getTopics();
        if (topics != null && !topics.isEmpty()) {
            handler.setDefaultTopic(topics.get(0));
        }
        handler.setDefaultQos(mqttProperties.getQos());
        return handler;
    }

    /**
     * MQTT服务
     *
     * @param mqttOutbound MQTT出站适配器
     * @return MqttService
     */
    @Bean
    @ConditionalOnMissingBean(name = "mqttService")
    public MqttService mqttService(MqttPahoMessageHandler mqttOutbound) {
        return new MqttService(mqttOutbound);
    }
}
