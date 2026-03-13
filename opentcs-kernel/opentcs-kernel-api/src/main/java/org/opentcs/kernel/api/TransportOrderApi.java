package org.opentcs.kernel.api;

import org.opentcs.kernel.api.dto.TransportOrderDTO;
import org.opentcs.kernel.api.dto.OrderSpecDTO;

import java.util.List;
import java.util.Optional;

/**
 * 运输订单 API
 * 负责订单的创建、激活、取消、状态管理
 */
public interface TransportOrderApi {

    /**
     * 创建运输订单
     * @param orderSpec 订单规格
     * @return 订单ID
     */
    String createOrder(OrderSpecDTO orderSpec);

    /**
     * 激活订单
     * 激活后订单进入调度流程
     * @param orderId 订单ID
     */
    void activateOrder(String orderId);

    /**
     * 取消订单
     * @param orderId 订单ID
     */
    void cancelOrder(String orderId);

    /**
     * 获取订单
     * @param orderId 订单ID
     * @return 订单信息
     */
    Optional<TransportOrderDTO> getOrder(String orderId);

    /**
     * 获取所有活动订单
     * @return 活动订单列表
     */
    List<TransportOrderDTO> getActiveOrders();

    /**
     * 获取所有订单
     * @return 订单列表
     */
    List<TransportOrderDTO> getAllOrders();

    /**
     * 订单完成回调（由驱动层调用）
     * @param orderId 订单ID
     * @param vehicleId 车辆ID
     * @param success 是否成功
     */
    void onOrderCompleted(String orderId, String vehicleId, boolean success);

    /**
     * 订单步骤完成回调
     * @param orderId 订单ID
     * @param stepIndex 步骤索引
     */
    void onStepCompleted(String orderId, int stepIndex);
}
