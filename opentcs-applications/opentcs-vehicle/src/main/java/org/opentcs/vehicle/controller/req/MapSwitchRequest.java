package org.opentcs.vehicle.controller.req;

import lombok.Data;

@Data
public class MapSwitchRequest {

    private String targetMapId;
    private String targetMapVersion;
    private String initPosition;
    private String fallbackMapId;
}
