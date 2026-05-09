package org.opentcs.kernel.api;

import org.opentcs.kernel.api.dto.OrderStateDTO;
import org.opentcs.kernel.api.dto.TransportOrderDTO;
import org.opentcs.kernel.api.dto.OrderSpecDTO;
import org.opentcs.kernel.api.dto.VehicleStateDTO;

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
     * 恢复已存在的运行态订单。
     * <p>
     * 用于服务重启后从持久化快照重建 Kernel Runtime。该方法不会重新生成订单 ID，
     * 也不会主动触发车辆下发，避免与车辆侧正在执行的订单产生重复命令。
     * </p>
     *
     * @param orderId 内核订单 ID
     * @param orderSpec 订单规格
     * @param state 恢复后的订单状态
     * @param processingVehicle 正在处理该订单的车辆，可为空
     */
    void restoreOrder(String orderId, OrderSpecDTO orderSpec, OrderStateDTO state, String processingVehicle);

    /**
     * 根据车辆运行快照对账恢复中的订单。
     * <p>
     * 如果车辆仍在执行同一订单，则将订单从 RECOVERING 确认为 ACTIVE；
     * 如果车辆空闲、报错或正在执行其他订单，则把对应恢复中订单置为失败；
     * 如果车辆离线/未知，则保持 RECOVERING 等待后续上报或人工处理。
     * </p>
     *
     * @param vehicleId 车辆 ID
     * @param currentOrderId 车辆当前上报的订单 ID，可为空
     * @param vehicleState 车辆当前状态
     * @param activeOrderIds 车辆当前活跃订单 ID 列表，可为空
     * @param hasFault 车辆状态中是否存在 FAULT 级错误
     */
    void reconcileVehicleRuntimeState(String vehicleId,
                                      String currentOrderId,
                                      VehicleStateDTO vehicleState,
                                      List<String> activeOrderIds,
                                      boolean hasFault);

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
     * 手动分配订单到指定车辆。
     * @param orderId 订单ID
     * @param vehicleId 车辆ID
     */
    void assignOrderToVehicle(String orderId, String vehicleId);

    /**
     * 标记订单完成。
     * @param orderId 订单ID
     */
    void completeOrder(String orderId);

    /**
     * 标记订单失败。
     * @param orderId 订单ID
     * @param reason 失败原因
     */
    void failOrder(String orderId, String reason);

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
