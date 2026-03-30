package org.opentcs.vehicle.controller.req;

import lombok.Data;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.kernel.api.dto.VehicleEntityDTO;

/**
 * 车辆注册请求（车辆基础信息 + 驱动配置）。
 */
@Data
public class RegisterVehicleWithDriverRequest {

    private VehicleEntityDTO vehicle;

    private DriverConfig driverConfig;
}
