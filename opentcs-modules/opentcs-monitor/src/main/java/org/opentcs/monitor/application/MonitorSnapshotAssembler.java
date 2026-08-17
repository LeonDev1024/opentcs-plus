package org.opentcs.monitor.application;

import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.opentcs.monitor.dto.MonitorAmrStatsVO;
import org.opentcs.monitor.dto.MonitorSnapshotVO;
import org.opentcs.monitor.dto.MonitorTaskStatsVO;
import org.opentcs.monitor.dto.MonitorVehicleVO;

import java.util.List;

/**
 * 运行时领域对象 → 监控快照 VO。
 */
public final class MonitorSnapshotAssembler {

    private MonitorSnapshotAssembler() {
    }

    public static MonitorVehicleVO toVehicleVO(Vehicle vehicle) {
        MonitorVehicleVO vo = new MonitorVehicleVO();
        vo.setVehicleId(vehicle.getVehicleId());
        vo.setName(vehicle.getName());
        vo.setTypeId(vehicle.getTypeId());
        vo.setState(vehicle.getState() == null ? VehicleState.UNKNOWN.name() : vehicle.getState().name());
        vo.setCurrentOrderId(vehicle.getCurrentOrderId());
        vo.setEnergyLevel(vehicle.getEnergyLevel());
        vo.setRuntimeVersion(vehicle.getRuntimeVersion());
        VehiclePosition position = vehicle.getPosition();
        if (position != null) {
            MonitorVehicleVO.Position pos = new MonitorVehicleVO.Position();
            pos.setPointId(position.getPointId());
            pos.setMapId(position.getMapId());
            pos.setX(position.getX());
            pos.setY(position.getY());
            pos.setOrientation(position.getOrientation());
            vo.setPosition(pos);
        }
        return vo;
    }

    public static MonitorAmrStatsVO toAmrStats(List<MonitorVehicleVO> vehicles) {
        MonitorAmrStatsVO stats = new MonitorAmrStatsVO();
        stats.setTotalVehicles(vehicles.size());
        for (MonitorVehicleVO vehicle : vehicles) {
            String state = vehicle.getState();
            if ("IDLE".equals(state)) {
                stats.setIdleVehicles(stats.getIdleVehicles() + 1);
            } else if (isExecuting(state)) {
                stats.setExecutingVehicles(stats.getExecutingVehicles() + 1);
            } else if ("CHARGING".equals(state)) {
                stats.setChargingVehicles(stats.getChargingVehicles() + 1);
            } else if ("ERROR".equals(state)) {
                stats.setErrorVehicles(stats.getErrorVehicles() + 1);
            } else if (isOffline(state)) {
                stats.setOfflineVehicles(stats.getOfflineVehicles() + 1);
            }
        }
        return stats;
    }

    public static MonitorTaskStatsVO toTaskStats(List<TransportOrder> orders) {
        MonitorTaskStatsVO stats = new MonitorTaskStatsVO();
        stats.setTotalOrders(orders.size());
        for (TransportOrder order : orders) {
            OrderState state = order.getState();
            if (state == OrderState.RAW) {
                stats.setWaitingOrders(stats.getWaitingOrders() + 1);
            } else if (state == OrderState.ACTIVE || state == OrderState.RECOVERING) {
                stats.setActiveOrders(stats.getActiveOrders() + 1);
            } else if (state == OrderState.FINISHED) {
                stats.setFinishedOrders(stats.getFinishedOrders() + 1);
            } else if (state == OrderState.CANCELLED) {
                stats.setCancelledOrders(stats.getCancelledOrders() + 1);
            } else if (state == OrderState.FAILED) {
                stats.setFailedOrders(stats.getFailedOrders() + 1);
            }
        }
        return stats;
    }

    public static String fingerprint(MonitorSnapshotVO snapshot) {
        long vehicleSig = 0;
        for (MonitorVehicleVO vehicle : snapshot.getVehicles()) {
            vehicleSig = 31 * vehicleSig
                    + (vehicle.getVehicleId() == null ? 0 : vehicle.getVehicleId().hashCode())
                    + (vehicle.getRuntimeVersion() == null ? 0 : vehicle.getRuntimeVersion());
        }
        MonitorAmrStatsVO amr = snapshot.getAmrStats();
        MonitorTaskStatsVO task = snapshot.getTaskStats();
        return vehicleSig + ":" + snapshot.getAlarmCount()
                + ":" + (amr == null ? 0 : amr.getExecutingVehicles())
                + ":" + (task == null ? 0 : task.getActiveOrders() + task.getWaitingOrders());
    }

    private static boolean isExecuting(String state) {
        return "EXECUTING".equals(state) || "WORKING".equals(state)
                || "WAITING".equals(state) || "PAUSED".equals(state);
    }

    private static boolean isOffline(String state) {
        return "OFFLINE".equals(state) || "UNKNOWN".equals(state) || "UNAVAILABLE".equals(state);
    }
}
