package org.opentcs.driver.vda5050.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * VDA5050 消息转换器
 * 负责在内部DTO和VDA5050协议格式之间转换
 */
public class VDA5050MessageConverter {

    private static final Logger LOG = LoggerFactory.getLogger(VDA5050MessageConverter.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将DriverOrder转换为VDA5050 Order JSON
     */
    public String toVDA5050Order(DriverOrder order) {
        try {
            VDA5050OrderMessage message = new VDA5050OrderMessage();
            message.setOrderId(order.getOrderId());
            message.setOrderVersion(order.getOrderVersion());
            message.setNodes(convertNodes(order.getNodes()));
            message.setEdges(convertEdges(order.getEdges()));

            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            LOG.error("转换订单失败: {}", e.getMessage());
            throw new RuntimeException("消息转换失败", e);
        }
    }

    /**
     * 将VDA5050 Order JSON转换为DriverOrder
     */
    public DriverOrder fromVDA5050Order(String json) {
        try {
            VDA5050OrderMessage message = objectMapper.readValue(json, VDA5050OrderMessage.class);

            DriverOrder order = new DriverOrder();
            order.setOrderId(message.getOrderId());
            order.setOrderVersion(message.getOrderVersion());
            order.setNodes(convertFromNodes(message.getNodes()));
            order.setEdges(convertFromEdges(message.getEdges()));

            return order;
        } catch (JsonProcessingException e) {
            LOG.error("解析订单失败: {}", e.getMessage());
            throw new RuntimeException("消息解析失败", e);
        }
    }

    /**
     * 将InstantAction转换为VDA5050 InstantActions JSON
     */
    public String toVDA5050InstantAction(InstantAction action) {
        try {
            VDA5050InstantActionMessage message = new VDA5050InstantActionMessage();
            message.setInstantActionId(action.getActionId());
            message.setActionType(action.getActionType());
            message.setLocationId(action.getLocationId());
            message.setDuration(action.getDuration());
            message.setParameters(action.getParameters());

            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            LOG.error("转换即时动作失败: {}", e.getMessage());
            throw new RuntimeException("消息转换失败", e);
        }
    }

    /**
     * 将VDA5050 State JSON转换为VehicleStatus。
     * 同时兼容嵌套 {header, state} 与标准扁平 VDA5050 State。
     */
    public VehicleStatus fromVDA5050Status(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode state = root.has("state") && root.get("state").isObject()
                    ? root.get("state")
                    : root;
            JsonNode header = root.get("header");

            VehicleStatus status = new VehicleStatus();
            status.setVehicleId(firstText(header, "vehicleId", root, "serialNumber"));
            if (status.getVehicleId() == null) {
                status.setVehicleId(getTextValue(root, "vehicleId"));
            }
            status.setOrderId(getTextValue(state, "orderId"));
            status.setOrderUpdateId(firstInt(state, "orderUpdateId", "orderVersion"));
            status.setAgvState(resolveAgvState(state));
            status.setOperationMode(getTextValue(state, "operatingMode"));
            if (status.getOperationMode() == null) {
                status.setOperationMode(getTextValue(state, "operationMode"));
            }
            status.setLastNodeId(getTextValue(state, "lastNodeId"));
            status.setDriving(getBooleanValue(state, "driving"));
            status.setDistanceSinceLastNode(getDoubleValue(state, "distanceSinceLastNode"));

            // 位置信息：嵌套 position 或扁平 lastNode*
            JsonNode position = state.get("position");
            if (position == null) {
                position = state.get("agvPosition");
            }
            if (position != null) {
                status.setxPosition(getDoubleValue(position, "x"));
                status.setyPosition(getDoubleValue(position, "y"));
                status.setTheta(getDoubleValue(position, "theta"));
                status.setPositionId(firstText(position, "positionId", position, "mapId"));
            }
            if (status.getPositionId() == null) {
                status.setPositionId(status.getLastNodeId());
            }

            // 电池状态
            JsonNode battery = state.get("batteryState");
            if (battery != null) {
                status.setBatteryState(getDoubleValue(battery, "batteryCharge"));
                status.setCharging(getBooleanValue(battery, "charging"));
            }

            // 错误信息
            JsonNode errorsNode = state.get("errors");
            if (errorsNode != null && errorsNode.isArray()) {
                List<VehicleStatus.Error> errors = new ArrayList<>();
                for (JsonNode errorNode : errorsNode) {
                    VehicleStatus.Error error = new VehicleStatus.Error();
                    error.setErrorType(getTextValue(errorNode, "errorType"));
                    error.setDescription(getTextValue(errorNode, "errorDescription"));
                    if (error.getDescription() == null) {
                        error.setDescription(getTextValue(errorNode, "description"));
                    }
                    error.setErrorLevel(getTextValue(errorNode, "errorLevel"));
                    error.setErrorCode(getIntValue(errorNode, "errorCode"));
                    errors.add(error);
                }
                status.setErrors(errors);
            }

            // nodeStates
            JsonNode nodeStatesNode = state.get("nodeStates");
            if (nodeStatesNode != null && nodeStatesNode.isArray()) {
                List<VehicleStatus.NodeState> nodeStates = new ArrayList<>();
                for (JsonNode node : nodeStatesNode) {
                    VehicleStatus.NodeState ns = new VehicleStatus.NodeState();
                    ns.setNodeId(getTextValue(node, "nodeId"));
                    ns.setSequenceId(getIntValue(node, "sequenceId"));
                    ns.setReleased(getBooleanValue(node, "released"));
                    nodeStates.add(ns);
                }
                status.setNodeStates(nodeStates);
            }

            // actionStates
            JsonNode actionStatesNode = state.get("actionStates");
            if (actionStatesNode != null && actionStatesNode.isArray()) {
                List<VehicleStatus.ActionState> actionStates = new ArrayList<>();
                for (JsonNode node : actionStatesNode) {
                    VehicleStatus.ActionState as = new VehicleStatus.ActionState();
                    as.setActionId(getTextValue(node, "actionId"));
                    as.setActionType(getTextValue(node, "actionType"));
                    as.setActionStatus(getTextValue(node, "actionStatus"));
                    as.setResultDescription(getTextValue(node, "resultDescription"));
                    actionStates.add(as);
                }
                status.setActionStates(actionStates);
            }

            return status;
        } catch (JsonProcessingException e) {
            LOG.error("解析状态失败: {}", e.getMessage());
            throw new RuntimeException("消息解析失败", e);
        }
    }

    private String resolveAgvState(JsonNode state) {
        String waiting = getTextValue(state, "waitingForAcknowledgement");
        if ("true".equalsIgnoreCase(waiting)) {
            return "WAITING";
        }
        JsonNode errors = state.get("errors");
        if (errors != null && errors.isArray()) {
            for (JsonNode error : errors) {
                if ("FATAL".equalsIgnoreCase(getTextValue(error, "errorLevel"))
                        || "FAULT".equalsIgnoreCase(getTextValue(error, "errorLevel"))) {
                    return "ERROR";
                }
            }
        }
        Boolean driving = getBooleanValue(state, "driving");
        if (Boolean.TRUE.equals(driving)) {
            String paused = getTextValue(state, "paused");
            if ("true".equalsIgnoreCase(paused)) {
                return "PAUSED";
            }
            return "EXECUTING";
        }
        String explicit = getTextValue(state, "agvState");
        return explicit != null ? explicit : "IDLE";
    }

    private String firstText(JsonNode primary, String primaryField, JsonNode fallback, String fallbackField) {
        String value = getTextValue(primary, primaryField);
        if (value != null) {
            return value;
        }
        return getTextValue(fallback, fallbackField);
    }

    private Integer firstInt(JsonNode node, String primaryField, String fallbackField) {
        Integer value = getIntValue(node, primaryField);
        if (value != null) {
            return value;
        }
        return getIntValue(node, fallbackField);
    }

    // ==================== 内部辅助方法 ====================

    private List<VDA5050Node> convertNodes(List<DriverOrder.Node> nodes) {
        if (nodes == null) {
            return null;
        }
        List<VDA5050Node> result = new ArrayList<>();
        for (DriverOrder.Node node : nodes) {
            VDA5050Node vdaNode = new VDA5050Node();
            vdaNode.setNodeId(node.getNodeId());
            vdaNode.setSequenceId(node.getSequenceId());
            vdaNode.setX(node.getX());
            vdaNode.setY(node.getY());
            vdaNode.setTheta(node.getTheta());
            vdaNode.setActions(convertActions(node.getActions()));
            result.add(vdaNode);
        }
        return result;
    }

    private List<DriverOrder.Node> convertFromNodes(List<VDA5050Node> nodes) {
        if (nodes == null) {
            return null;
        }
        List<DriverOrder.Node> result = new ArrayList<>();
        for (VDA5050Node node : nodes) {
            DriverOrder.Node orderNode = new DriverOrder.Node();
            orderNode.setNodeId(node.getNodeId());
            orderNode.setSequenceId(node.getSequenceId());
            orderNode.setX(node.getX());
            orderNode.setY(node.getY());
            orderNode.setTheta(node.getTheta());
            orderNode.setActions(convertFromActions(node.getActions()));
            result.add(orderNode);
        }
        return result;
    }

    private List<VDA5050Edge> convertEdges(List<DriverOrder.Edge> edges) {
        if (edges == null) {
            return null;
        }
        List<VDA5050Edge> result = new ArrayList<>();
        for (DriverOrder.Edge edge : edges) {
            VDA5050Edge vdaEdge = new VDA5050Edge();
            vdaEdge.setEdgeId(edge.getEdgeId());
            vdaEdge.setSequenceId(edge.getSequenceId());
            vdaEdge.setStartNodeId(edge.getStartNodeId());
            vdaEdge.setEndNodeId(edge.getEndNodeId());
            vdaEdge.setMaxVelocity(edge.getMaxVelocity());
            vdaEdge.setMaxReverseVelocity(edge.getMaxReverseVelocity());
            result.add(vdaEdge);
        }
        return result;
    }

    private List<DriverOrder.Edge> convertFromEdges(List<VDA5050Edge> edges) {
        if (edges == null) {
            return null;
        }
        List<DriverOrder.Edge> result = new ArrayList<>();
        for (VDA5050Edge edge : edges) {
            DriverOrder.Edge orderEdge = new DriverOrder.Edge();
            orderEdge.setEdgeId(edge.getEdgeId());
            orderEdge.setSequenceId(edge.getSequenceId());
            orderEdge.setStartNodeId(edge.getStartNodeId());
            orderEdge.setEndNodeId(edge.getEndNodeId());
            orderEdge.setMaxVelocity(edge.getMaxVelocity());
            orderEdge.setMaxReverseVelocity(edge.getMaxReverseVelocity());
            result.add(orderEdge);
        }
        return result;
    }

    private List<VDA5050Action> convertActions(List<DriverOrder.Action> actions) {
        if (actions == null) {
            return null;
        }
        List<VDA5050Action> result = new ArrayList<>();
        for (DriverOrder.Action action : actions) {
            VDA5050Action vdaAction = new VDA5050Action();
            vdaAction.setActionId(action.getActionId());
            vdaAction.setActionType(action.getActionType());
            vdaAction.setLocationId(action.getLocationId());
            vdaAction.setDuration(action.getDuration());
            vdaAction.setParameters(action.getParameters());
            result.add(vdaAction);
        }
        return result;
    }

    private List<DriverOrder.Action> convertFromActions(List<VDA5050Action> actions) {
        if (actions == null) {
            return null;
        }
        List<DriverOrder.Action> result = new ArrayList<>();
        for (VDA5050Action action : actions) {
            DriverOrder.Action orderAction = new DriverOrder.Action();
            orderAction.setActionId(action.getActionId());
            orderAction.setActionType(action.getActionType());
            orderAction.setLocationId(action.getLocationId());
            orderAction.setDuration(action.getDuration());
            orderAction.setParameters(action.getParameters());
            result.add(orderAction);
        }
        return result;
    }

    private String getTextValue(JsonNode node, String field) {
        if (node == null || field == null) {
            return null;
        }
        JsonNode fieldNode = node.get(field);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }

    private Integer getIntValue(JsonNode node, String field) {
        if (node == null || field == null) {
            return null;
        }
        JsonNode fieldNode = node.get(field);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asInt() : null;
    }

    private Double getDoubleValue(JsonNode node, String field) {
        if (node == null || field == null) {
            return null;
        }
        JsonNode fieldNode = node.get(field);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asDouble() : null;
    }

    private Boolean getBooleanValue(JsonNode node, String field) {
        if (node == null || field == null) {
            return null;
        }
        JsonNode fieldNode = node.get(field);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asBoolean() : null;
    }

    // ==================== VDA5050 消息内部类 ====================

    /**
     * VDA5050 Order 消息
     */
    public static class VDA5050OrderMessage {
        private String orderId;
        private Integer orderVersion;
        private List<VDA5050Node> nodes;
        private List<VDA5050Edge> edges;

        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public Integer getOrderVersion() { return orderVersion; }
        public void setOrderVersion(Integer orderVersion) { this.orderVersion = orderVersion; }
        public List<VDA5050Node> getNodes() { return nodes; }
        public void setNodes(List<VDA5050Node> nodes) { this.nodes = nodes; }
        public List<VDA5050Edge> getEdges() { return edges; }
        public void setEdges(List<VDA5050Edge> edges) { this.edges = edges; }
    }

    /**
     * VDA5050 Node
     */
    public static class VDA5050Node {
        private String nodeId;
        private String sequenceId;
        private Double x;
        private Double y;
        private Double theta;
        private List<VDA5050Action> actions;

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
        public List<VDA5050Action> getActions() { return actions; }
        public void setActions(List<VDA5050Action> actions) { this.actions = actions; }
    }

    /**
     * VDA5050 Edge
     */
    public static class VDA5050Edge {
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
     * VDA5050 Action
     */
    public static class VDA5050Action {
        private String actionId;
        private String actionType;
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

    /**
     * VDA5050 InstantAction 消息
     */
    public static class VDA5050InstantActionMessage {
        private String instantActionId;
        private String actionType;
        private String locationId;
        private Integer duration;
        private Map<String, String> parameters;

        // Getters and Setters
        public String getInstantActionId() { return instantActionId; }
        public void setInstantActionId(String instantActionId) { this.instantActionId = instantActionId; }
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
