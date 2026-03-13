package org.opentcs.kernel.api.dto;

import java.util.List;
import java.util.Map;

/**
 * 订单步骤数据传输对象
 */
public class OrderStepDTO {

    private Integer stepIndex;

    private String destinationPointId;

    private String destinationName;

    private List<ActionDTO> actions;

    private StepStateDTO state;

    private Map<String, String> properties;

    // Getters and Setters
    public Integer getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(Integer stepIndex) {
        this.stepIndex = stepIndex;
    }

    public String getDestinationPointId() {
        return destinationPointId;
    }

    public void setDestinationPointId(String destinationPointId) {
        this.destinationPointId = destinationPointId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public List<ActionDTO> getActions() {
        return actions;
    }

    public void setActions(List<ActionDTO> actions) {
        this.actions = actions;
    }

    public StepStateDTO getState() {
        return state;
    }

    public void setState(StepStateDTO state) {
        this.state = state;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
