package org.opentcs.vehicle.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.opentcs.vehicle.persistence.entity.OpsActionEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OpsActionRepository extends IService<OpsActionEntity> {

    Optional<OpsActionEntity> findByActionId(String actionId);

    Optional<OpsActionEntity> findByVehicleAndRequestId(String vehicleName, String requestId);

    List<OpsActionEntity> listRecent(String vehicleName, int limit);

    List<OpsActionEntity> listPendingBefore(LocalDateTime deadline);
}
