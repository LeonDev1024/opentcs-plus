package org.opentcs.driver.api.dto;

import java.util.List;
import java.util.Map;

/**
 * 车辆状态
 * 对应 VDA5050 的 State 消息
 */
public class VehicleStatus {

    private String vehicleId;

    private String orderId;

    private Integer orderUpdateId;

    private String agvState;  // IDLE, EXECUTING, PAUSED, WAITING, ERROR

    private String operationMode;  // AUTOMATIC, SEMIAUTOMATIC, MANUAL, SERVICE

    private Double xPosition;

    private Double yPosition;

    private Double theta;

    private String positionId;

    private Double velocityX;

    private Double velocityY;

    private Double angularVelocity;

    private Double batteryState;

    private Boolean charging;

    private Double distanceSinceLastNode;

    private String lastNodeId;

    private Boolean driving;

    private List<NodeState> nodeStates;

    private List<ActionState> actionStates;

    private List<Error> errors;

    private List<String> activeOrderIds;

    private Map<String, String> additionalFields;

    // Getters and Setters
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Integer getOrderUpdateId() { return orderUpdateId; }
    public void setOrderUpdateId(Integer orderUpdateId) { this.orderUpdateId = orderUpdateId; }
    public String getAgvState() { return agvState; }
    public void setAgvState(String agvState) { this.agvState = agvState; }
    public String getOperationMode() { return operationMode; }
    public void setOperationMode(String operationMode) { this.operationMode = operationMode; }
    public Double getxPosition() { return xPosition; }
    public void setxPosition(Double xPosition) { this.xPosition = xPosition; }
    public Double getyPosition() { return yPosition; }
    public void setyPosition(Double yPosition) { this.yPosition = yPosition; }
    public Double getTheta() { return theta; }
    public void setTheta(Double theta) { this.theta = theta; }
    public String getPositionId() { return positionId; }
    public void setPositionId(String positionId) { this.positionId = positionId; }
    public Double getVelocityX() { return velocityX; }
    public void setVelocityX(Double velocityX) { this.velocityX = velocityX; }
    public Double getVelocityY() { return velocityY; }
    public void setVelocityY(Double velocityY) { this.velocityY = velocityY; }
    public Double getAngularVelocity() { return angularVelocity; }
    public void setAngularVelocity(Double angularVelocity) { this.angularVelocity = angularVelocity; }
    public Double getBatteryState() { return batteryState; }
    public void setBatteryState(Double batteryState) { this.batteryState = batteryState; }
    public Boolean getCharging() { return charging; }
    public void setCharging(Boolean charging) { this.charging = charging; }
    public Double getDistanceSinceLastNode() { return distanceSinceLastNode; }
    public void setDistanceSinceLastNode(Double distanceSinceLastNode) { this.distanceSinceLastNode = distanceSinceLastNode; }
    public String getLastNodeId() { return lastNodeId; }
    public void setLastNodeId(String lastNodeId) { this.lastNodeId = lastNodeId; }
    public Boolean getDriving() { return driving; }
    public void setDriving(Boolean driving) { this.driving = driving; }
    public List<NodeState> getNodeStates() { return nodeStates; }
    public void setNodeStates(List<NodeState> nodeStates) { this.nodeStates = nodeStates; }
    public List<ActionState> getActionStates() { return actionStates; }
    public void setActionStates(List<ActionState> actionStates) { this.actionStates = actionStates; }
    public List<Error> getErrors() { return errors; }
    public void setErrors(List<Error> errors) { this.errors = errors; }
    public List<String> getActiveOrderIds() { return activeOrderIds; }
    public void setActiveOrderIds(List<String> activeOrderIds) { this.activeOrderIds = activeOrderIds; }
    public Map<String, String> getAdditionalFields() { return additionalFields; }
    public void setAdditionalFields(Map<String, String> additionalFields) { this.additionalFields = additionalFields; }

    /**
     * 节点状态（对应 VDA5050 nodeStates）
     */
    public static class NodeState {
        private String nodeId;
        private Integer sequenceId;
        private Boolean released;
        private Boolean nodeDescription;

        public String getNodeId() { return nodeId; }
        public void setNodeId(String nodeId) { this.nodeId = nodeId; }
        public Integer getSequenceId() { return sequenceId; }
        public void setSequenceId(Integer sequenceId) { this.sequenceId = sequenceId; }
        public Boolean getReleased() { return released; }
        public void setReleased(Boolean released) { this.released = released; }
        public Boolean getNodeDescription() { return nodeDescription; }
        public void setNodeDescription(Boolean nodeDescription) { this.nodeDescription = nodeDescription; }
    }

    /**
     * 动作状态（对应 VDA5050 actionStates）
     */
    public static class ActionState {
        private String actionId;
        private String actionType;
        private String actionStatus;
        private String resultDescription;

        public String getActionId() { return actionId; }
        public void setActionId(String actionId) { this.actionId = actionId; }
        public String getActionType() { return actionType; }
        public void setActionType(String actionType) { this.actionType = actionType; }
        public String getActionStatus() { return actionStatus; }
        public void setActionStatus(String actionStatus) { this.actionStatus = actionStatus; }
        public String getResultDescription() { return resultDescription; }
        public void setResultDescription(String resultDescription) { this.resultDescription = resultDescription; }
    }

    /**
     * 错误信息
     */
    public static class Error {
        private String errorType;
        private String description;
        private String errorLevel;  // WARNING, FAULT
        private Integer errorCode;

        // Getters and Setters
        public String getErrorType() { return errorType; }
        public void setErrorType(String errorType) { this.errorType = errorType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getErrorLevel() { return errorLevel; }
        public void setErrorLevel(String errorLevel) { this.errorLevel = errorLevel; }
        public Integer getErrorCode() { return errorCode; }
        public void setErrorCode(Integer errorCode) { this.errorCode = errorCode; }
    }
}
