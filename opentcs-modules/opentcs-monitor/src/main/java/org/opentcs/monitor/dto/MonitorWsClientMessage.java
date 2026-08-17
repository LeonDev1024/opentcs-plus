package org.opentcs.monitor.dto;

import lombok.Data;

@Data
public class MonitorWsClientMessage {

    private String type;
    private Long factoryId;
}
