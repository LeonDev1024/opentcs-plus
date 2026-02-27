package org.opentcs.driver.protocol;

import lombok.Data;
import java.util.List;

/**
 * VDA5050 订单消息
 */
@Data
public class OrderMessage {
    private int headerId;
    private String timestamp;
    private String version;
    private String manufacturer;
    private String serialNumber;
    private String orderId;
    private int orderUpdateId;
    private String zoneSetId;
    private List<Node> nodes;
    private List<Edge> edges;

    /**
     * 节点
     */
    @Data
    public static class Node {
        private String nodeId;
        private int sequenceId;
        private String nodeDescription;
        private boolean released;
        private NodePosition nodePosition;
        private List<Action> actions;
    }

    /**
     * 节点位置
     */
    @Data
    public static class NodePosition {
        private double x;
        private double y;
        private Double theta;
        private Double allowedDeviationXY;
        private Double allowedDeviationTheta;
        private String mapId;
        private String mapDescription;
    }

    /**
     * 边
     */
    @Data
    public static class Edge {
        private String edgeId;
        private int sequenceId;
        private String edgeDescription;
        private boolean released;
        private String startNodeId;
        private String endNodeId;
        private Double maxSpeed;
        private Double maxHeight;
        private Double minHeight;
        private Double orientation;
        private String orientationType;
        private String direction;
        private Boolean rotationAllowed;
        private Double maxRotationSpeed;
        private Trajectory trajectory;
        private Double length;
        private Corridor corridor;
        private List<Action> actions;
    }

    /**
     * 轨迹
     */
    @Data
    public static class Trajectory {
        private double degree;
        private List<Double> knotVector;
        private List<ControlPoint> controlPoints;
    }

    /**
     * 控制点
     */
    @Data
    public static class ControlPoint {
        private double x;
        private double y;
        private Double weight;
    }

    /**
     * 走廊
     */
    @Data
    public static class Corridor {
        private double leftBound;
        private double rightBound;
        private String corridorRefPoint;
    }

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
