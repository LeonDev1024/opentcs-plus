package org.opentcs.vehicle.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 运维动作超时扫描。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpsActionWatchdog {

    private final VehicleApplicationService vehicleApplicationService;

    @Scheduled(fixedDelayString = "${opentcs.ops-action.timeout-scan-ms:15000}")
    public void scanTimeouts() {
        int n = vehicleApplicationService.markTimedOutOpsActions();
        if (n > 0) {
            log.warn("运维动作超时标记: count={}", n);
        }
    }
}
