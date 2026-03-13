package org.opentcs.kernel.domain.order;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 订单动作
 */
public class OrderAction {

    public enum ActionType {
        LOAD("装载"),
        UNLOAD("卸载"),
        NOP("无操作"),
        CHARGE("充电");

        private final String description;

        ActionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 动作状态
     */
    public enum ActionState {
        PENDING("待执行"),
        ACTIVE("执行中"),
        COMPLETED("已完成"),
        FAILED("失败");

        private final String description;

        ActionState(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final String actionId;
    private final ActionType actionType;
    private final String locationId;
    private final String locationName;
    private final Integer duration;
    private final Map<String, String> parameters;
    private ActionState state;

    public OrderAction(String actionId, ActionType actionType, String locationId) {
        this.actionId = Objects.requireNonNull(actionId, "actionId不能为空");
        this.actionType = Objects.requireNonNull(actionType, "actionType不能为空");
        this.locationId = locationId;
        this.locationName = null;
        this.duration = null;
        this.parameters = new HashMap<>();
        this.state = ActionState.PENDING;
    }

    public OrderAction(String actionId, ActionType actionType, String locationId,
                      String locationName, Integer duration) {
        this.actionId = Objects.requireNonNull(actionId, "actionId不能为空");
        this.actionType = Objects.requireNonNull(actionType, "actionType不能为空");
        this.locationId = locationId;
        this.locationName = locationName;
        this.duration = duration;
        this.parameters = new HashMap<>();
        this.state = ActionState.PENDING;
    }

    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    // Getters
    public String getActionId() {
        return actionId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public Integer getDuration() {
        return duration;
    }

    public ActionState getState() {
        return state;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "OrderAction{" +
                "actionId='" + actionId + '\'' +
                ", actionType=" + actionType +
                ", locationId='" + locationId + '\'' +
                '}';
    }

    /**
     * 动作构建器
     */
    public static class Builder {
        private final String actionId;
        private final ActionType actionType;
        private String locationId;
        private String locationName;
        private Integer duration;
        private final Map<String, String> parameters = new HashMap<>();

        public Builder(String actionId, ActionType actionType) {
            this.actionId = actionId;
            this.actionType = actionType;
        }

        public Builder locationId(String locationId) {
            this.locationId = locationId;
            return this;
        }

        public Builder locationName(String locationName) {
            this.locationName = locationName;
            return this;
        }

        public Builder duration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public Builder parameter(String key, String value) {
            this.parameters.put(key, value);
            return this;
        }

        public OrderAction build() {
            return new OrderAction(actionId, actionType, locationId, locationName, duration);
        }
    }
}
