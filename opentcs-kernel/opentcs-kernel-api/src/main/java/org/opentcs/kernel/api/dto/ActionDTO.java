package org.opentcs.kernel.api.dto;

import java.util.Map;

/**
 * 订单动作数据传输对象
 */
public class ActionDTO {

    private String actionId;

    private String actionType;  // LOAD, UNLOAD, NOP, CHARGE

    private String locationId;

    private String locationName;

    private Integer duration;  // 预期持续时间（秒）

    private Map<String, String> parameters;

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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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
