package org.opentcs.vehicle.application.bo;

import lombok.Data;

@Data
public class OpsActionResultBO {

    private String actionId;
    private Boolean accepted;
    private String status;
    private String reasonCode;
    private String reasonMessage;
    private String traceId;
    private String estimatedFinishTime;
}
