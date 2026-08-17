package org.opentcs.vehicle.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.vehicle.persistence.entity.OpsActionEntity;
import org.opentcs.vehicle.persistence.mapper.OpsActionMapper;
import org.opentcs.vehicle.persistence.service.OpsActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OpsActionRepositoryImpl extends ServiceImpl<OpsActionMapper, OpsActionEntity>
        implements OpsActionRepository {

    @Override
    public Optional<OpsActionEntity> findByActionId(String actionId) {
        if (!StringUtils.hasText(actionId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<OpsActionEntity>()
                .eq(OpsActionEntity::getActionId, actionId)
                .last("LIMIT 1")));
    }

    @Override
    public Optional<OpsActionEntity> findByVehicleAndRequestId(String vehicleName, String requestId) {
        if (!StringUtils.hasText(vehicleName) || !StringUtils.hasText(requestId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<OpsActionEntity>()
                .eq(OpsActionEntity::getVehicleName, vehicleName)
                .eq(OpsActionEntity::getRequestId, requestId)
                .last("LIMIT 1")));
    }

    @Override
    public List<OpsActionEntity> listRecent(String vehicleName, int limit) {
        LambdaQueryWrapper<OpsActionEntity> wrapper = new LambdaQueryWrapper<OpsActionEntity>()
                .eq(StringUtils.hasText(vehicleName), OpsActionEntity::getVehicleName, vehicleName)
                .orderByDesc(OpsActionEntity::getOperatedAt)
                .last("LIMIT " + Math.max(1, Math.min(limit, 500)));
        return list(wrapper);
    }

    @Override
    public List<OpsActionEntity> listPendingBefore(LocalDateTime deadline) {
        return list(new LambdaQueryWrapper<OpsActionEntity>()
                .in(OpsActionEntity::getExecuteStatus, List.of("PENDING", "ACCEPTED", "RUNNING"))
                .lt(OpsActionEntity::getOperatedAt, deadline)
                .orderByAsc(OpsActionEntity::getOperatedAt)
                .last("LIMIT 200"));
    }
}
