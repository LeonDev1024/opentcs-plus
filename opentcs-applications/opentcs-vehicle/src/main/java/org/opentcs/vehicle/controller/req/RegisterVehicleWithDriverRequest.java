package org.opentcs.vehicle.controller.req;

import lombok.Data;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.vehicle.application.bo.VehicleBO;

/**
 * 车辆注册请求（车辆基础信息 + 驱动配置）。
 */
@Data
public class RegisterVehicleWithDriverRequest {

    private VehicleBO vehicle;

    private DriverConfig driverConfig;
}
