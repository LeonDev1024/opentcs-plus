package org.opentcs.kernel.api.algorithm;

/**
 * 调度策略接口。
 * <p>
 * 负责管理运输订单到车辆的分配。
 * 使用 String ID 代替领域对象，实现接口层和领域层的解耦。
 * </p>
 */
public interface Dispatcher extends Lifecycle {

    /**
     * 触发调度流程。
     */
    void dispatch();

    /**
     * 撤回订单。
     *
     * @param orderId 运输订单ID
     * @param immediateAbort 是否立即中止
     */
    void withdrawOrder(String orderId, boolean immediateAbort);

    /**
     * 撤回车辆的订单。
     *
     * @param vehicleId 车辆ID
     * @param immediateAbort 是否立即中止
     */
    void withdrawOrderByVehicle(String vehicleId, boolean immediateAbort);

    /**
     * 重新路由车辆。
     *
     * @param vehicleId 车辆ID
     * @param reroutingType 重新路由类型
     */
    void reroute(String vehicleId, ReroutingType reroutingType);

    /**
     * 重新路由所有车辆。
     *
     * @param reroutingType 重新路由类型
     */
    void rerouteAll(ReroutingType reroutingType);

    /**
     * 立即分配订单。
     *
     * @param orderId 运输订单ID
     * @throws TransportOrderAssignmentException 分配失败时抛出
     */
    void assignNow(String orderId) throws TransportOrderAssignmentException;

    /**
     * 重新路由类型。
     */
    enum ReroutingType {
        /** 正常重新路由 */
        NORMAL,
        /** 紧急重新路由 */
        EMERGENCY,
        /** 到目的地重新路由 */
        TO_DESTINATION
    }

    /**
     * 运输订单分配异常。
     */
    class TransportOrderAssignmentException extends Exception {
        public TransportOrderAssignmentException(String message) {
            super(message);
        }
        public TransportOrderAssignmentException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}