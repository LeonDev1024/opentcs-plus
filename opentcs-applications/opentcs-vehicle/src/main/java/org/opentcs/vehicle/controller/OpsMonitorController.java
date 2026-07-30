package org.opentcs.vehicle.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.vehicle.application.OpsMonitorApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 运维监控：资源锁 / 告警。
 */
@RestController
@RequestMapping("/ops/monitor")
@RequiredArgsConstructor
public class OpsMonitorController {

    private final OpsMonitorApplicationService opsMonitorApplicationService;

    @SaCheckPermission("ops:monitor:lock")
    @GetMapping("/locks")
    public R<List<Map<String, Object>>> listLocks() {
        return R.ok(opsMonitorApplicationService.listLocks());
    }

    @SaCheckPermission("ops:monitor:lock")
    @PostMapping("/locks/release")
    public R<Boolean> forceRelease(@RequestParam String resourceType, @RequestParam String resourceId) {
        return R.ok(opsMonitorApplicationService.forceReleaseLock(resourceType, resourceId));
    }

    @SaCheckPermission("ops:monitor:lock")
    @GetMapping("/locks/audit")
    public R<List<Map<String, Object>>> listLockAudits(
            @RequestParam(required = false, defaultValue = "100") int limit) {
        return R.ok(opsMonitorApplicationService.listLockAudits(limit));
    }

    @SaCheckPermission("ops:monitor:alarm")
    @GetMapping("/alarms")
    public R<List<Map<String, Object>>> listAlarms() {
        return R.ok(opsMonitorApplicationService.listAlarms());
    }

    @SaCheckPermission("ops:monitor:alarm")
    @PostMapping("/alarms/{alarmId}/ack")
    public R<Boolean> ackAlarm(@PathVariable String alarmId) {
        return R.ok(opsMonitorApplicationService.ackAlarm(alarmId));
    }
}
