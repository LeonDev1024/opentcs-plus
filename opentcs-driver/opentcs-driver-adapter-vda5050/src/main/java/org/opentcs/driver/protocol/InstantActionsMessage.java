package org.opentcs.driver.protocol;

import lombok.Data;

import java.util.List;

/**
 * VDA5050 即时动作消息
 */
@Data
public class InstantActionsMessage {

    private List<Action> actions;

    @Data
    public static class Action {
        private String actionId;
        private String actionType;
        private String blockingType;
        private List<ActionParameter> actionParameters;
    }

    @Data
    public static class ActionParameter {
        private String key;
        private String value;
    }
}
