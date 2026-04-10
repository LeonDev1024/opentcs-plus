package org.opentcs.kernel.application;

import org.opentcs.kernel.api.OrderLifecycleApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 订单生命周期服务，实现 {@link OrderLifecycleApi} 端口接口。
 * <p>
 * 作为驱动层与调度内核之间的回调桥接，将车辆执行结果统一路由到 {@link DispatcherService}。
 * </p>
 */
public class OrderLifecycleService implements OrderLifecycleApi {

    private static final Logger log = LoggerFactory.getLogger(OrderLifecycleService.class);

    private final DispatcherService dispatcher;

    public OrderLifecycleService(DispatcherService dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void onOrderExecutionResult(String orderId, String vehicleId,
                                       boolean success, String reason) {
        if (success) {
            log.info("订单 {} 车辆 {} 执行完成", orderId, vehicleId);
            dispatcher.vehicleCompletedOrder(vehicleId);
        } else {
            log.warn("订单 {} 车辆 {} 执行失败，原因: {}", orderId, vehicleId, reason);
            dispatcher.vehicleCancelledOrder(vehicleId);
        }
    }
}
