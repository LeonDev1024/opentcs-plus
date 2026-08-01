package org.opentcs.vehicle.application;

import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.registry.DriverRegistry;
import org.opentcs.kernel.api.OrderFailureReasons;
import org.opentcs.kernel.api.OrderLifecycleApi;
import org.opentcs.kernel.api.OrderTraceKeys;
import org.opentcs.kernel.application.TransportOrderRegistry;
import org.opentcs.kernel.domain.event.OrderAssignedEvent;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 将内核订单分配事件转换为车辆驱动层 order 下发。
 */
@Component
public class OrderDispatchCommandListener {

    private static final Logger log = LoggerFactory.getLogger(OrderDispatchCommandListener.class);

    private final DriverRegistry driverRegistry;
    private final TransportOrderRegistry orderRegistry;
    private final DriverOrderFactory driverOrderFactory;
    private final OrderLifecycleApi orderLifecycleApi;

    public OrderDispatchCommandListener(DriverRegistry driverRegistry,
                                        TransportOrderRegistry orderRegistry,
                                        DriverOrderFactory driverOrderFactory,
                                        OrderLifecycleApi orderLifecycleApi) {
        this.driverRegistry = driverRegistry;
        this.orderRegistry = orderRegistry;
        this.driverOrderFactory = driverOrderFactory;
        this.orderLifecycleApi = orderLifecycleApi;
    }

    @EventListener
    public void onOrderAssigned(OrderAssignedEvent event) {
        TransportOrder order = orderRegistry.getOrder(event.getOrderId());
        if (order == null) {
            log.warn("订单分配事件找不到运行态订单: orderId={}", event.getOrderId());
            return;
        }

        String traceId = order.getProperties().get(OrderTraceKeys.TRACE_ID);
        DriverOrder driverOrder = driverOrderFactory.fromTransportOrder(
                order, event.getVehicleId());
        try {
            driverRegistry.sendOrder(event.getVehicleId(), driverOrder);
            order.getProperties().put(OrderTraceKeys.DISPATCH_STATE, "SENT");
            log.info("订单已下发车辆: orderId={}, vehicleId={}, nodes={}, traceId={}",
                    event.getOrderId(),
                    event.getVehicleId(),
                    driverOrder.getNodes() != null ? driverOrder.getNodes().size() : 0,
                    traceId);
        } catch (Exception ex) {
            log.error("订单下发车辆失败: orderId={}, vehicleId={}, traceId={}, reason={}",
                    event.getOrderId(), event.getVehicleId(), traceId, ex.getMessage(), ex);
            orderLifecycleApi.onOrderExecutionResult(
                    event.getOrderId(),
                    event.getVehicleId(),
                    false,
                    OrderFailureReasons.ORDER_DISPATCH_FAILED);
        }
    }
}
