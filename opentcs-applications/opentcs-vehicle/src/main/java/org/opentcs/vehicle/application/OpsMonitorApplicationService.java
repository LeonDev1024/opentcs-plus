package org.opentcs.vehicle.application;

import lombok.RequiredArgsConstructor;
import org.opentcs.kernel.domain.event.ResourceLockChangedEvent;
import org.opentcs.kernel.domain.resource.ResourceLockStatus;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.opentcs.kernel.application.ResourceLockService;
import org.opentcs.kernel.application.VehicleRegistry;
import org.opentcs.kernel.domain.resource.ResourceLock;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 监控侧：资源锁列表 + 最小告警聚合。
 */
@Service
@RequiredArgsConstructor
public class OpsMonitorApplicationService {

    private final ResourceLockService resourceLockService;
    private final VehicleRegistry vehicleRegistry;
    private final List<Map<String, Object>> recentAlarms = new CopyOnWriteArrayList<>();

    public List<Map<String, Object>> listLocks() {
        return resourceLockService.listHeldLocks().stream()
                .map(this::toLockMap)
                .collect(Collectors.toList());
    }

    public boolean forceReleaseLock(String resourceType, String resourceId) {
        ResourceType type = ResourceType.valueOf(resourceType);
        return resourceLockService.forceRelease(type, resourceId);
    }

    public List<Map<String, Object>> listAlarms() {
        List<Map<String, Object>> alarms = new ArrayList<>();

        for (Vehicle vehicle : vehicleRegistry.getAllVehicleDomains()) {
            if (vehicle.getState() == VehicleState.ERROR || vehicle.getState() == VehicleState.OFFLINE) {
                Map<String, Object> alarm = new HashMap<>();
                alarm.put("alarmId", "VEH-" + vehicle.getVehicleId() + "-" + vehicle.getState().name());
                alarm.put("severity", vehicle.getState() == VehicleState.ERROR ? "ERROR" : "WARNING");
                alarm.put("category", "VEHICLE");
                alarm.put("title", "车辆状态异常");
                alarm.put("message", vehicle.getName() + " 当前状态 " + vehicle.getState().name());
                alarm.put("vehicleName", vehicle.getName());
                alarm.put("acked", false);
                alarm.put("createdAt", Instant.now().toString());
                alarms.add(alarm);
            }
        }

        alarms.addAll(recentAlarms);
        alarms.sort(Comparator.comparing(a -> String.valueOf(a.get("createdAt")), Comparator.reverseOrder()));
        return alarms.stream().limit(200).collect(Collectors.toList());
    }

    public boolean ackAlarm(String alarmId) {
        for (Map<String, Object> alarm : recentAlarms) {
            if (alarmId.equals(alarm.get("alarmId"))) {
                alarm.put("acked", true);
                return true;
            }
        }
        return false;
    }

    @EventListener
    public void onResourceLockChanged(ResourceLockChangedEvent event) {
        if (event.getStatus() != ResourceLockStatus.EXPIRED && !"EXPIRED".equals(event.getReason())) {
            return;
        }
        Map<String, Object> alarm = new HashMap<>();
        alarm.put("alarmId", "LOCK-" + event.getLockId());
        alarm.put("severity", "WARNING");
        alarm.put("category", "RESOURCE_LOCK");
        alarm.put("title", "资源锁超时释放");
        alarm.put("message", event.getResourceType() + ":" + event.getResourceId()
                + " 被车辆 " + event.getVehicleId() + " 持有超时");
        alarm.put("vehicleName", event.getVehicleId());
        alarm.put("resourceId", event.getResourceId());
        alarm.put("resourceType", event.getResourceType() == null ? null : event.getResourceType().name());
        alarm.put("acked", false);
        alarm.put("createdAt", Instant.now().toString());
        recentAlarms.add(0, alarm);
        if (recentAlarms.size() > 200) {
            recentAlarms.subList(200, recentAlarms.size()).clear();
        }
    }

    private Map<String, Object> toLockMap(ResourceLock lock) {
        Map<String, Object> map = new HashMap<>();
        map.put("lockId", lock.getLockId());
        map.put("resourceId", lock.getResourceId());
        map.put("resourceType", lock.getResourceType() == null ? null : lock.getResourceType().name());
        map.put("vehicleId", lock.getVehicleId());
        map.put("orderId", lock.getOrderId());
        map.put("status", lock.getStatus() == null ? null : lock.getStatus().name());
        map.put("createdAt", lock.getCreatedAt() == null ? null : lock.getCreatedAt().toString());
        map.put("expiresAt", lock.getExpiresAt() == null ? null : lock.getExpiresAt().toString());
        return map;
    }
}
