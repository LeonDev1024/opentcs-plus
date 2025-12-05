# MQTT配置示例

## 基础配置

### 单服务器配置（兼容旧配置）
```yaml
mqtt:
  username: admin
  password: admin
  serverUrls: 
    - tcp://localhost:1883
  clientId: pji_datacycle_client
  defaultTopic: pji/datacycle/default
  timeout: 30
  keepAliveInterval: 60
  cleanSession: true
  automaticReconnect: true
  qos: 1
  async: true
```

### 多服务器多订阅配置（推荐）
```yaml
mqtt:
  enabled: true
  servers:
    # 服务器1：机器人服务器
    - name: robot-server
      username: admin
      password: admin
      serverUrls:
        - tcp://134.80.20.23:1883
      clientIdPrefix: robot_client
      timeout: 30
      keepAliveInterval: 60
      cleanSession: true
      automaticReconnect: true
      qos: 1
      subscriptions:
        # 订阅1：机器人状态
        - name: robot-status
          topic: /pji/cloud/robots/+/+/r2c/json
          qos: 1
        # 订阅2：机器人命令响应
        - name: robot-command-response
          topic: /pji/cloud/robots/+/+/c2r/json
          qos: 1
        # 订阅3：所有机器人消息（使用通配符）
        - name: robot-all
          topic: /pji/cloud/robots/#
          qos: 0
    
    # 服务器2：设备服务器
    - name: device-server
      username: device_user
      password: device_pass
      serverUrls:
        - tcp://192.168.1.100:1883
        - tcp://192.168.1.101:1883  # 支持多个地址（高可用）
      clientIdPrefix: device_client
      timeout: 30
      keepAliveInterval: 60
      cleanSession: true
      automaticReconnect: true
      qos: 1
      subscriptions:
        - name: device-status
          topic: /devices/+/status
          qos: 1
        - name: device-data
          topic: /devices/+/data
          qos: 2
```

## 使用说明

### 1. 添加依赖
在需要使用MQTT的模块中添加以下依赖:
```xml
<dependency>
    <groupId>com.pji.cloud</groupId>
    <artifactId>pji-datacycle-common-mqtt</artifactId>
</dependency>
```

### 2. 发送MQTT消息
通过注入[MqttService](file:///Users/liuyichun/Documents/code/pji/pji-datacycle/pji-datacycle-common/pji-datacycle-common-mqtt/src/main/java/com/pji/cloud/datacycle/common/mqtt/service/MqttService.java#L16-L70)来发送消息:
```java
@Autowired
private MqttService mqttService;

// 发送消息到指定主题
mqttService.sendMessage("test/topic", "Hello MQTT!");

// 发送消息到默认主题
mqttService.sendMessage("Hello Default Topic!");
```

### 3. 接收MQTT消息
可以通过以下方式接收MQTT消息:

#### 方式一: 实现 MqttMessageProcessor（推荐，支持路由）
在业务模块中实现 `MqttMessageProcessor` 接口，支持根据 MQTT 主题和消息内容进行双重路由:

```java
import model.org.opentcs.common.mqtt.MqttMessageContent;
import processor.org.opentcs.common.mqtt.AbstractMqttMessageProcessor;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 机器人状态处理器示例
 * 处理主题: /pji/cloud/robots/+/+/r2c/json
 * 处理消息类型: robotStatus
 */
@Component
public class RobotStatusProcessor extends AbstractMqttMessageProcessor {
    
    @Override
    public void process(String mqttTopic, MqttMessageContent content) {
        // 处理机器人状态消息
        Map<String, Object> data = content.getData();
        String status = (String) data.get("status");
        String taskStatus = (String) data.get("taskStatus");
        
        System.out.println("机器人状态更新 - Status: " + status + ", TaskStatus: " + taskStatus);
        // 实现具体的业务逻辑
    }
    
    @Override
    public String getSupportedTopicPattern() {
        // 支持的主题模式，支持通配符 + 和 #
        return "/pji/cloud/robots/+/+/r2c/json";
    }
    
    @Override
    public String getSupportedMessageType() {
        // 支持的消息类型（从消息内容的 topic 或 commandId 字段匹配）
        return "robotStatus";
    }
}
```

**路由规则说明**:
- **按 MQTT 主题路由**: 通过 `getSupportedTopicPattern()` 指定支持的主题模式，支持通配符 `+`（单级）和 `#`（多级）
- **按消息内容路由**: 通过 `getSupportedMessageType()` 指定支持的消息类型，会从消息内容的 `topic` 或 `commandId` 字段匹配
- **双重匹配**: 同时指定主题模式和消息类型时，需要两者都匹配才会路由到该处理器
- **优先级**: 主题匹配 > 消息类型匹配 > 通用处理器

#### 方式二: 继承 AbstractMqttMessageHandler
在业务模块中继承 `AbstractMqttMessageHandler` 并实现 `processMessage` 方法:

```java
import handler.org.opentcs.common.mqtt.AbstractMqttMessageHandler;
import org.springframework.stereotype.Component;

@Component("mqttMessageHandler")
public class CustomMqttMessageHandler extends AbstractMqttMessageHandler {
    
    @Override
    protected void processMessage(String topic, String content) {
        // 实现具体的业务处理逻辑
        if (topic.endsWith("/temperature")) {
            // 处理温度数据
            System.out.println("处理温度数据: " + content);
        } else if (topic.endsWith("/humidity")) {
            // 处理湿度数据
            System.out.println("处理湿度数据: " + content);
        } else {
            // 处理其他类型的数据
            System.out.println("处理通用消息: " + content);
        }
    }
}
```

**注意**: Bean 名称必须为 `mqttMessageHandler`，这样会自动替换默认的处理器。

#### 方式三: 继承MqttMessageListener
```java
import listener.org.opentcs.common.mqtt.MqttMessageListener;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomMqttMessageListener extends MqttMessageListener {
    
    @Override
    protected MessageHandler getCustomMessageHandler() {
        return message -> {
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
            byte[] payload = (byte[]) message.getPayload();
            String content = new String(payload);
            
            // 处理接收到的消息
            System.out.println("Received message: " + content + " from topic: " + topic);
        };
    }
}
```

## 配置参数说明

### 单服务器配置参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| mqtt.username | 用户名 | 无 |
| mqtt.password | 密码 | 无 |
| mqtt.serverUrls | 服务器地址列表 | 无 |
| mqtt.clientId | 客户端ID | 无 |
| mqtt.defaultTopic | 默认主题 | default/topic |
| mqtt.timeout | 超时时间(秒) | 30 |
| mqtt.keepAliveInterval | 心跳间隔(秒) | 60 |
| mqtt.cleanSession | 清除会话 | true |
| mqtt.automaticReconnect | 自动重连 | true |
| mqtt.qos | QoS级别 | 1 |
| mqtt.async | 是否异步发送 | true |

### 多服务器配置参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| mqtt.enabled | 是否启用MQTT | true |
| mqtt.servers | 服务器配置列表 | 无 |

#### 服务器配置 (mqtt.servers[].*)

| 参数 | 说明 | 默认值 |
|------|------|--------|
| name | 服务器名称（用于标识） | 无 |
| username | 用户名 | 无 |
| password | 密码 | 无 |
| serverUrls | 服务器地址列表（支持多个，用于高可用） | 无 |
| clientIdPrefix | 客户端ID前缀 | 无 |
| timeout | 超时时间(秒) | 30 |
| keepAliveInterval | 心跳间隔(秒) | 60 |
| cleanSession | 清除会话 | true |
| automaticReconnect | 自动重连 | true |
| qos | 默认QoS级别 | 1 |
| subscriptions | 订阅配置列表 | 无 |

#### 订阅配置 (mqtt.servers[].subscriptions[].*)

| 参数 | 说明 | 默认值 |
|------|------|--------|
| name | 订阅名称（用于标识，可选） | 无 |
| topic | 订阅主题（支持通配符 + 和 #） | 无 |
| qos | QoS级别（如果不设置，使用服务器的默认QoS） | 使用服务器默认值 |

### 主题通配符说明

- `+` : 单级通配符，匹配一个主题级别
  - 例如：`/devices/+/status` 匹配 `/devices/device1/status`，但不匹配 `/devices/device1/sensor/status`
- `#` : 多级通配符，匹配零个或多个主题级别（必须放在最后）
  - 例如：`/devices/#` 匹配 `/devices/device1/status` 和 `/devices/device1/sensor/temperature`