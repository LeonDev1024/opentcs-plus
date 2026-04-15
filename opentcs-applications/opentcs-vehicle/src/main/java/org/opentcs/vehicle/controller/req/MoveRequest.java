package org.opentcs.vehicle.controller.req;

import lombok.Data;

@Data
public class MoveRequest {

    private String moveType;
    private String targetNodeId;
    private String mapId;
    private Double x;
    private Double y;
    private Double theta;
    private Boolean confirmRisk;
}
