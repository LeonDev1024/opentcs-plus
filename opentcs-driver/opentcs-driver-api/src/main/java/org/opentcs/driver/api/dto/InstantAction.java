package org.opentcs.driver.api.dto;

import java.util.Map;

/**
 * 即时动作
 * 用于向车辆发送立即执行的动作
 */
public class InstantAction {

    private String actionId;
    private String actionType;  // LOAD, UNLOAD, NOP, CHARGE, PAUSE, RESUME
    private String locationId;  // 动作位置
    private Integer duration;   // 持续时间（毫秒）
    private Map<String, String> parameters;

    public InstantAction() {
    }

    public InstantAction(String actionId, String actionType) {
        this.actionId = actionId;
        this.actionType = actionType;
    }

    public InstantAction(String actionId, String actionType, String locationId) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.locationId = locationId;
    }

    // Getters and Setters
    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
