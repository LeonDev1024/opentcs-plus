package org.opentcs.vehicle.controller.req;

import lombok.Data;

/**
 * 设置车辆初始点请求。
 */
@Data
public class SetVehiclePositionRequest {

    /**
     * 运行时点位 ID（须已在已发布地图中）
     */
    private String pointId;
}
