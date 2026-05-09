package org.opentcs.kernel.application.runtime;

import org.opentcs.kernel.api.dto.VehicleStateDTO;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * 车辆运行时快照。
 */
public record VehicleRuntimeSnapshot(
        String vehicleId,
        String currentOrderId,
        VehicleStateDTO state,
        List<String> activeOrderIds,
        boolean hasFault,
        Instant updatedAt
) implements Serializable {
}
