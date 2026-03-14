package org.opentcs.driver.api.dto;

import java.util.List;
import java.util.Map;

/**
 * 驱动层订单数据传输对象
 * 对应 VDA5050 的 Order 消息
 */
public class DriverOrder {

    /**
     * 订单唯一标识
     */
    private String orderId;

    /**
     * 订单版本号
     */
    private Integer orderVersion;

    /**
     * 节点列表（路径点序列）
     */
    private List<Node> nodes;

    /**
     * 边列表（路径段序列）
     */
    private List<Edge> edges;

    /**
     * 扩展参数
     */
    private Map<String, String> parameters;

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderVersion() {
        return orderVersion;
    }

    public void setOrderVersion(Integer orderVersion) {
        this.orderVersion = orderVersion;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * 节点（对应路径点）
     */
    public static class Node {
        private String nodeId;
        private String sequenceId;
        private Double x;
        private Double y;
        private Double theta;
        private List<Action> actions;

        // Getters and Setters
        public String getNodeId() { return nodeId; }
        public void setNodeId(String nodeId) { this.nodeId = nodeId; }
        public String getSequenceId() { return sequenceId; }
        public void setSequenceId(String sequenceId) { this.sequenceId = sequenceId; }
        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }
        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }
        public Double getTheta() { return theta; }
        public void setTheta(Double theta) { this.theta = theta; }
        public List<Action> getActions() { return actions; }
        public void setActions(List<Action> actions) { this.actions = actions; }
    }

    /**
     * 边（对应路径段）
     */
    public static class Edge {
        private String edgeId;
        private String sequenceId;
        private String startNodeId;
        private String endNodeId;
        private Double maxVelocity;
        private Double maxReverseVelocity;

        // Getters and Setters
        public String getEdgeId() { return edgeId; }
        public void setEdgeId(String edgeId) { this.edgeId = edgeId; }
        public String getSequenceId() { return sequenceId; }
        public void setSequenceId(String sequenceId) { this.sequenceId = sequenceId; }
        public String getStartNodeId() { return startNodeId; }
        public void setStartNodeId(String startNodeId) { this.startNodeId = startNodeId; }
        public String getEndNodeId() { return endNodeId; }
        public void setEndNodeId(String endNodeId) { this.endNodeId = endNodeId; }
        public Double getMaxVelocity() { return maxVelocity; }
        public void setMaxVelocity(Double maxVelocity) { this.maxVelocity = maxVelocity; }
        public Double getMaxReverseVelocity() { return maxReverseVelocity; }
        public void setMaxReverseVelocity(Double maxReverseVelocity) { this.maxReverseVelocity = maxReverseVelocity; }
    }

    /**
     * 动作
     */
    public static class Action {
        private String actionId;
        private String actionType;  // LOAD, UNLOAD, NOP, CHARGE
        private String locationId;
        private Integer duration;
        private Map<String, String> parameters;

        // Getters and Setters
        public String getActionId() { return actionId; }
        public void setActionId(String actionId) { this.actionId = actionId; }
        public String getActionType() { return actionType; }
        public void setActionType(String actionType) { this.actionType = actionType; }
        public String getLocationId() { return locationId; }
        public void setLocationId(String locationId) { this.locationId = locationId; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        public Map<String, String> getParameters() { return parameters; }
        public void setParameters(Map<String, String> parameters) { this.parameters = parameters; }
    }
}
