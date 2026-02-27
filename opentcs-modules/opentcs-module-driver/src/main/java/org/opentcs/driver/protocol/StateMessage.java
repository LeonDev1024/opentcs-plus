package org.opentcs.driver.protocol;

import lombok.Data;
import java.util.List;

/**
 * VDA5050 状态消息
 */
@Data
public class StateMessage {
    private int headerId;
    private String timestamp;
    private String version;
    private String manufacturer;
    private String serialNumber;
    private String orderId;
    private int orderUpdateId;
    private String lastNodeId;
    private int lastNodeSequenceId;
    private String batteryState;
    private double batteryVoltage;
    private double batteryCharge;
    private List<ActionState> actionStates;
    private List<NodeState> nodeStates;
    private List<EdgeState> edgeStates;
    private List<Error> errors;
    private String operatingMode;
    private String drivingMode;
    private List<String> safetyStates;
    private Position position;
    private List<Load> loads;
    private String mapId;
    private String lastError;

    /**
     * 动作状态
     */
    @Data
    public static class ActionState {
        private String actionId;
        private String status;
        private List<Parameter> parameters;
        private String errorReference;
    }

    /**
     * 节点状态
     */
    @Data
    public static class NodeState {
        private String nodeId;
        private int sequenceId;
        private String status;
    }

    /**
     * 边状态
     */
    @Data
    public static class EdgeState {
        private String edgeId;
        private int sequenceId;
        private String status;
    }

    /**
     * 错误
     */
    @Data
    public static class Error {
        private String errorType;
        private int errorNumber;
        private String description;
        private String errorLevel;
        private String errorReference;
    }

    /**
     * 位置
     */
    @Data
    public static class Position {
        private double x;
        private double y;
        private double theta;
        private double allowedDeviationXY;
        private double allowedDeviationTheta;
        private String mapId;
        private String mapDescription;
    }

    /**
     * 负载
     */
    @Data
    public static class Load {
        private String loadId;
        private double x;
        private double y;
        private double z;
        private double weight;
        private String description;
    }

    /**
     * 参数
     */
    @Data
    public static class Parameter {
        private String key;
        private String value;
    }
}
