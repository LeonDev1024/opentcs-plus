package org.opentcs.algorithm.traffic;

/**
 * 节点时间类
 */
public class NodeTime {
    private String nodeId;
    private double startTime;
    private double endTime;

    public NodeTime(String nodeId, double startTime, double endTime) {
        this.nodeId = nodeId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }
}
