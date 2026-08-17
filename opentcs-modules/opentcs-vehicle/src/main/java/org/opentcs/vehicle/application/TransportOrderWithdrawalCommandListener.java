package org.opentcs.vehicle.application;

import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.registry.DriverRegistry;
import org.opentcs.kernel.domain.event.OrderWithdrawalRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * 将内核订单撤回请求转换为车辆驱动即时动作。
 */
@Component
public class TransportOrderWithdrawalCommandListener {

    private static final Logger log = LoggerFactory.getLogger(TransportOrderWithdrawalCommandListener.class);

    private final DriverRegistry driverRegistry;

    public TransportOrderWithdrawalCommandListener(DriverRegistry driverRegistry) {
        this.driverRegistry = driverRegistry;
    }

    @EventListener
    public void onOrderWithdrawalRequested(OrderWithdrawalRequestedEvent event) {
        InstantAction action = new InstantAction(
                "withdraw-" + event.getOrderId() + "-" + UUID.randomUUID(),
                event.getActionType());
        action.setParameters(Map.of(
                "orderId", event.getOrderId(),
                "vehicleId", event.getVehicleId(),
                "immediateAbort", String.valueOf(event.isImmediateAbort())
        ));

        driverRegistry.sendInstantAction(event.getVehicleId(), action);
        log.info("订单撤回动作已下发车辆: orderId={}, vehicleId={}, actionType={}",
                event.getOrderId(), event.getVehicleId(), event.getActionType());
    }
}
