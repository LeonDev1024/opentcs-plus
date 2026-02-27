package org.opentcs.algorithm.traffic;

import java.util.List;

/**
 * 车辆路径类
 */
public class VehiclePath {
    private String vehicleId;
    private List<NodeTime> nodeTimes;

    public VehiclePath(String vehicleId, List<NodeTime> nodeTimes) {
        this.vehicleId = vehicleId;
        this.nodeTimes = nodeTimes;
    }

    // Getters and Setters
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public List<NodeTime> getNodeTimes() {
        return nodeTimes;
    }

    public void setNodeTimes(List<NodeTime> nodeTimes) {
        this.nodeTimes = nodeTimes;
    }
}
