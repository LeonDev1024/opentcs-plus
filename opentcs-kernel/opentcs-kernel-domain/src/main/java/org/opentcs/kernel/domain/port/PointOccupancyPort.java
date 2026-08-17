package org.opentcs.kernel.domain.port;

/**
 * 点位占用推进端口。
 */
public interface PointOccupancyPort {

    /**
     * 车辆位置变更：更新 POINT 占用并推进 Horizon。
     *
     * @param vehicleId 车辆 ID
     * @param orderId   当前订单，可为 null
     * @param pointId   新点位
     * @return 是否成功更新占用
     */
    boolean onVehicleMoved(String vehicleId, String orderId, String pointId);
}
