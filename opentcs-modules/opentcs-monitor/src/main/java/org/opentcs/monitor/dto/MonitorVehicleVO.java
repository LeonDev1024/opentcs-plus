package org.opentcs.monitor.dto;

import lombok.Data;

/**
 * 监控车辆运行时（大屏 / WS 共用）。
 */
@Data
public class MonitorVehicleVO {

    private String vehicleId;
    private String name;
    private String typeId;
    private String state;
    private Position position;
    private String currentOrderId;
    private Double energyLevel;
    private Long factoryId;
    private Long runtimeVersion;

    @Data
    public static class Position {
        private String pointId;
        private String mapId;
        private double x;
        private double y;
        private double orientation;
    }
}
