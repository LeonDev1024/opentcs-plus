package org.opentcs.kernel.api;

/**
 * 订单生命周期回调接口。
 * <p>
 * 用于驱动/车辆执行结果统一回流到订单应用服务，避免各模块各自更新订单状态。
 * </p>
 */
public interface OrderLifecycleApi {

    /**
     * 处理订单执行结果。
     *
     * @param orderId 订单号
     * @param vehicleId 车辆标识
     * @param success 是否执行成功
     * @param reason 失败原因（成功时可为空）
     */
    void onOrderExecutionResult(String orderId, String vehicleId, boolean success, String reason);
}
