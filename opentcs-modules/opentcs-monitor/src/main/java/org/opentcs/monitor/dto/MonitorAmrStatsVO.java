package org.opentcs.monitor.dto;

import lombok.Data;

@Data
public class MonitorAmrStatsVO {

    private int totalVehicles;
    private int idleVehicles;
    private int executingVehicles;
    private int chargingVehicles;
    private int errorVehicles;
    private int offlineVehicles;
}
