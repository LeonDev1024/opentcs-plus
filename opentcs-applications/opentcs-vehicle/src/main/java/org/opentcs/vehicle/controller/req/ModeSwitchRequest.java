package org.opentcs.vehicle.controller.req;

import lombok.Data;

@Data
public class ModeSwitchRequest {

    private String targetMode;
    private String executePolicy;
    private String reason;
}
