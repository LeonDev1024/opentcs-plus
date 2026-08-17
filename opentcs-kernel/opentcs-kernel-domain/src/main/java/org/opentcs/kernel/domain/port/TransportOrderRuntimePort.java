package org.opentcs.kernel.domain.port;

import org.opentcs.kernel.domain.order.TransportOrder;

import java.util.List;

/**
 * 运输订单运行时协作端口（modules 依赖此接口，不依赖 kernel.core 实现类）。
 */
public interface TransportOrderRuntimePort {

    TransportOrder createOrder(TransportOrder order);

    TransportOrder getOrder(String orderId);

    List<TransportOrder> getAllOrders();

    List<TransportOrder> getWaitingOrders();

    List<TransportOrder> getAssignedOrders();

    boolean orderExists(String orderId);
}
