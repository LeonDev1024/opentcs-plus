package org.opentcs.monitor.application;

import lombok.RequiredArgsConstructor;
import org.opentcs.kernel.domain.port.TransportOrderRuntimePort;
import org.opentcs.kernel.domain.port.VehicleRuntimePort;
import org.opentcs.monitor.dto.MonitorSnapshotVO;
import org.opentcs.monitor.dto.MonitorVehicleVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 监控大屏聚合快照：车辆运行时 + KPI + 未确认告警数。
 */
@Service
@RequiredArgsConstructor
public class MonitorSnapshotApplicationService {

    private final VehicleRuntimePort vehicleRegistry;
    private final TransportOrderRuntimePort orderRegistry;
    private final OpsMonitorApplicationService opsMonitorApplicationService;
    private final AtomicLong seq = new AtomicLong();

    public MonitorSnapshotVO snapshot(Long factoryId) {
        return snapshot(factoryId, "snapshot");
    }

    public MonitorSnapshotVO snapshot(Long factoryId, String type) {
        List<MonitorVehicleVO> vehicles = vehicleRegistry.getAllVehicleDomains().stream()
                .map(MonitorSnapshotAssembler::toVehicleVO)
                .collect(Collectors.toList());
        MonitorSnapshotVO vo = new MonitorSnapshotVO();
        vo.setType(type);
        vo.setFactoryId(factoryId);
        vo.setGeneratedAt(System.currentTimeMillis());
        vo.setSeq(seq.incrementAndGet());
        vo.setVehicles(vehicles);
        vo.setAmrStats(MonitorSnapshotAssembler.toAmrStats(vehicles));
        vo.setTaskStats(MonitorSnapshotAssembler.toTaskStats(orderRegistry.getAllOrders()));
        vo.setAlarmCount(opsMonitorApplicationService.unackedAlarmCount());
        vo.setFingerprint(MonitorSnapshotAssembler.fingerprint(vo));
        return vo;
    }
}
