package org.opentcs.driver.protocol;

import lombok.Data;
import java.util.List;

/**
 * VDA5050 即时动作消息
 */
@Data
public class InstantActionsMessage {
    private int headerId;
    private String timestamp;
    private String version;
    private String manufacturer;
    private String serialNumber;
    private List<Action> actions;

    /**
     * 动作
     */
    @Data
    public static class Action {
        private String actionType;
        private String actionId;
        private String actionDescription;
        private String blockingType;
        private List<ActionParameter> actionParameters;
    }

    /**
     * 动作参数
     */
    @Data
    public static class ActionParameter {
        private String key;
        private String value;
    }
}