package org.opentcs.algorithm.traffic;

/**
 * 冲突类
 */
public class Conflict {
    private String vehicle1Id;
    private String vehicle2Id;
    private String nodeId;

    public Conflict(String vehicle1Id, String vehicle2Id, String nodeId) {
        this.vehicle1Id = vehicle1Id;
        this.vehicle2Id = vehicle2Id;
        this.nodeId = nodeId;
    }

    // Getters and Setters
    public String getVehicle1Id() {
        return vehicle1Id;
    }

    public void setVehicle1Id(String vehicle1Id) {
        this.vehicle1Id = vehicle1Id;
    }

    public String getVehicle2Id() {
        return vehicle2Id;
    }

    public void setVehicle2Id(String vehicle2Id) {
        this.vehicle2Id = vehicle2Id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String toString() {
        return "Conflict{" +
                "vehicle1Id='" + vehicle1Id + '\'' +
                ", vehicle2Id='" + vehicle2Id + '\'' +
                ", nodeId='" + nodeId + '\'' +
                '}';
    }
}
