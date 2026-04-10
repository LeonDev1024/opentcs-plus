package org.opentcs.web.health;

import org.opentcs.kernel.api.VehicleTypeApi;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 调度内核健康检查。
 * <p>
 * 通过调用 {@link VehicleTypeApi}（kernel-api 端口接口）查询车辆类型，
 * 验证内核应用服务 → 持久化链路是否正常。
 * </p>
 *
 * <p>健康状态含义：
 * <ul>
 *   <li>UP — 内核服务响应正常，含已注册车辆类型数</li>
 *   <li>DOWN — 内核服务不可用（数据库连接异常、Bean 初始化失败等）</li>
 * </ul>
 * </p>
 */
@Component("dispatchKernel")
public class DispatchKernelHealthIndicator implements HealthIndicator {

    private final VehicleTypeApi vehicleTypeApi;

    public DispatchKernelHealthIndicator(VehicleTypeApi vehicleTypeApi) {
        this.vehicleTypeApi = vehicleTypeApi;
    }

    @Override
    public Health health() {
        try {
            int typeCount = vehicleTypeApi.findAll().size();
            return Health.up()
                    .withDetail("vehicle_type_count", typeCount)
                    .withDetail("kernel_api", "reachable")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", "调度内核服务不可用: " + e.getMessage())
                    .withDetail("kernel_api", "unreachable")
                    .build();
        }
    }
}
