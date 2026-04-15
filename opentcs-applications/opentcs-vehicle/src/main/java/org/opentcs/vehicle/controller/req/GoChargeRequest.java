package org.opentcs.vehicle.controller.req;

import lombok.Data;

@Data
public class GoChargeRequest {

    private String chargePolicy;
    private String stationId;
    private String interruptPolicy;
    private Integer minSocThreshold;
}
