package org.opentcs.monitor.dto;

import lombok.Data;

@Data
public class MonitorTaskStatsVO {

    private int totalOrders;
    private int waitingOrders;
    private int activeOrders;
    private int finishedOrders;
    private int cancelledOrders;
    private int failedOrders;
}
