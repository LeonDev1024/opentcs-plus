package org.opentcs.kernel.api;

/**
 * 订单生命周期失败原因码（写入事件 reason / properties.failureReasonCode）。
 */
public final class OrderFailureReasons {

    public static final String ORDER_DISPATCH_FAILED = "ORDER_DISPATCH_FAILED";
    public static final String ORDER_REJECTED = "ORDER_REJECTED";
    public static final String AGV_FAULT = "AGV_FAULT";
    public static final String STEP_FAILED = "STEP_FAILED";
    public static final String VEHICLE_CANCELLED = "VEHICLE_CANCELLED";
    public static final String ORDER_EXECUTION_FAILED = "ORDER_EXECUTION_FAILED";

    private OrderFailureReasons() {
    }
}
