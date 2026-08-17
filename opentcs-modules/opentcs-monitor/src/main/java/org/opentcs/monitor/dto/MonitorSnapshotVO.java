package org.opentcs.monitor.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 监控大屏聚合快照。HTTP 首屏与 WS snapshot/delta 共用。
 */
@Data
public class MonitorSnapshotVO {

    /** snapshot / delta / heartbeat */
    private String type;
    private Long factoryId;
    private long generatedAt;
    private long seq;
    private String fingerprint;
    private List<MonitorVehicleVO> vehicles = new ArrayList<>();
    private MonitorAmrStatsVO amrStats;
    private MonitorTaskStatsVO taskStats;
    private int alarmCount;
}
