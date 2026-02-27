package org.opentcs.driver.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * VDA5050 消息类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VDA5050Message {
    private int headerId;
    private String timestamp;
    private String version;
    private String manufacturer;
    private String serialNumber;
    private Object payload;

    /**
     * 转换为JSON字符串
     * @return JSON字符串
     */
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert message to JSON", e);
        }
    }

    /**
     * 从JSON字符串解析消息
     * @param json JSON字符串
     * @return VDA5050Message实例
     */
    public static VDA5050Message fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, VDA5050Message.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON message", e);
        }
    }

    /**
     * 生成当前时间的ISO 8601格式字符串
     * @return ISO 8601格式的时间字符串
     */
    public static String generateTimestamp() {
        return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
