package org.opentcs.common.mqtt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * MQTT消息内容模型
 *
 * @author Lion Li
 */
@Data
public class MqttMessageContent {

    /**
     * 消息数据
     */
    private Map<String, Object> data;

    /**
     * 消息主题（从消息内容中提取）
     */
    private String topic;

    /**
     * 命令ID（从消息内容中提取）
     */
    @JsonProperty("commandId")
    private String commandId;

    /**
     * 时间戳
     */
    private Long time;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 获取消息类型标识
     * 优先使用 topic，如果 topic 为空则使用 commandId
     *
     * @return 消息类型标识
     */
    public String getMessageType() {
        if (topic != null && !topic.isEmpty()) {
            return topic;
        }
        return commandId;
    }
}
