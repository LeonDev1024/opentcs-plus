package org.opentcs.kernel.application.dispatch;

import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.vehicle.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * 派车策略，用于从候选车辆中选择订单执行车辆。
 */
public interface DispatchStrategy {

    /**
     * 策略名称，用于运行时观测和配置识别。
     */
    String getName();

    /**
     * 从已满足基础可用性和可达性的候选车辆中选择执行车辆。
     */
    Optional<Vehicle> selectVehicle(TransportOrder order,
                                    List<Vehicle> candidates,
                                    RoutePlannerImpl routePlanner);
}
