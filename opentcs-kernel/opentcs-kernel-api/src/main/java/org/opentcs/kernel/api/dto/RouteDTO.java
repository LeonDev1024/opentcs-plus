package org.opentcs.kernel.api.dto;

import java.util.List;

/**
 * 路由路径数据传输对象
 */
public class RouteDTO {

    private String routeId;

    private String sourcePointId;

    private String destPointId;

    private Double totalDistance;

    private List<PathDTO> paths;

    // Getters and Setters
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getSourcePointId() {
        return sourcePointId;
    }

    public void setSourcePointId(String sourcePointId) {
        this.sourcePointId = sourcePointId;
    }

    public String getDestPointId() {
        return destPointId;
    }

    public void setDestPointId(String destPointId) {
        this.destPointId = destPointId;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public List<PathDTO> getPaths() {
        return paths;
    }

    public void setPaths(List<PathDTO> paths) {
        this.paths = paths;
    }
}
