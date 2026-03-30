package org.opentcs.kernel.persistence.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.persistence.entity.CrossLayerConnectionEntity;
import org.opentcs.kernel.persistence.entity.ElevatorScheduleEntity;
import org.opentcs.kernel.persistence.mapper.CrossLayerConnectionMapper;
import org.opentcs.kernel.persistence.mapper.ElevatorScheduleMapper;
import org.opentcs.kernel.persistence.service.CrossLayerConnectionDomainService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 跨层连接领域服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "crossLayerConnection")
public class CrossLayerConnectionServiceImpl extends ServiceImpl<CrossLayerConnectionMapper, CrossLayerConnectionEntity>
        implements CrossLayerConnectionDomainService {

    private final ElevatorScheduleMapper elevatorScheduleMapper;

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean createConnection(CrossLayerConnectionEntity connection) {
        long count = this.count(new LambdaQueryWrapper<CrossLayerConnectionEntity>()
                .eq(CrossLayerConnectionEntity::getFactoryModelId, connection.getFactoryModelId())
                .eq(CrossLayerConnectionEntity::getConnectionId, connection.getConnectionId())
                .eq(CrossLayerConnectionEntity::getDelFlag, "0"));
        if (count > 0) {
            throw new RuntimeException("连接标识已存在");
        }

        if (connection.getConnectionId() == null || connection.getConnectionId().isEmpty()) {
            connection.setConnectionId(IdUtil.fastSimpleUUID());
        }

        if (connection.getAvailable() == null) {
            connection.setAvailable(true);
        }
        if (connection.getCurrentLoad() == null) {
            connection.setCurrentLoad(0);
        }
        if (connection.getCapacity() == null) {
            connection.setCapacity(1);
        }

        return this.save(connection);
    }

    @Override
    public TableDataInfo<CrossLayerConnectionEntity> selectPage(CrossLayerConnectionEntity connection, PageQuery pageQuery) {
        IPage<CrossLayerConnectionEntity> page = this.getBaseMapper().selectPageCrossLayerConnection(
                pageQuery.build(), connection);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<CrossLayerConnectionEntity> selectPageConnection(CrossLayerConnectionEntity connection, PageQuery pageQuery) {
        return selectPage(connection, pageQuery);
    }

    @Override
    public List<CrossLayerConnectionEntity> selectByFactoryModelId(Long factoryModelId) {
        return this.getBaseMapper().selectByFactoryModelId(factoryModelId);
    }

    @Override
    public List<CrossLayerConnectionEntity> selectByNavigationMapId(Long navigationMapId) {
        return this.list(new LambdaQueryWrapper<CrossLayerConnectionEntity>()
                .eq(CrossLayerConnectionEntity::getSourceNavigationMapId, navigationMapId)
                .or()
                .eq(CrossLayerConnectionEntity::getDestNavigationMapId, navigationMapId));
    }

    @Override
    public List<CrossLayerConnectionEntity> selectBySourceAndDest(Long sourceMapId, Long destMapId) {
        return this.getBaseMapper().selectBySourceAndDest(sourceMapId, destMapId);
    }

    @Override
    public List<CrossLayerConnectionEntity> selectAvailableConnections(Long factoryModelId) {
        return this.getBaseMapper().selectAvailableConnections(factoryModelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reserveElevator(String connectionId, Long vehicleId) {
        CrossLayerConnectionEntity connection = this.getOne(
                new LambdaQueryWrapper<CrossLayerConnectionEntity>()
                        .eq(CrossLayerConnectionEntity::getConnectionId, connectionId)
                        .eq(CrossLayerConnectionEntity::getDelFlag, "0"));

        if (connection == null) {
            throw new RuntimeException("跨层连接不存在");
        }

        if (!connection.getAvailable()) {
            throw new RuntimeException("电梯不可用");
        }

        if (connection.getCurrentLoad() >= connection.getCapacity()) {
            throw new RuntimeException("电梯已满");
        }

        connection.setCurrentLoad(connection.getCurrentLoad() + 1);
        this.updateById(connection);

        ElevatorScheduleEntity schedule = new ElevatorScheduleEntity();
        schedule.setConnectionId(connectionId);
        schedule.setVehicleId(vehicleId);
        schedule.setSourceFloor(connection.getSourceFloor());
        schedule.setDestFloor(connection.getDestFloor());
        schedule.setScheduleType("RESERVE");
        schedule.setStatus("PENDING");

        Date now = new Date();
        schedule.setPickupTime(now);
        if (connection.getTravelTime() != null) {
            schedule.setDeliveryTime(new Date(now.getTime() + connection.getTravelTime() * 1000L));
        }

        elevatorScheduleMapper.insert(schedule);

        log.info("电梯预留成功: connectionId={}, vehicleId={}", connectionId, vehicleId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseElevator(String connectionId, Long vehicleId) {
        CrossLayerConnectionEntity connection = this.getOne(
                new LambdaQueryWrapper<CrossLayerConnectionEntity>()
                        .eq(CrossLayerConnectionEntity::getConnectionId, connectionId)
                        .eq(CrossLayerConnectionEntity::getDelFlag, "0"));

        if (connection == null) {
            throw new RuntimeException("跨层连接不存在");
        }

        if (connection.getCurrentLoad() > 0) {
            connection.setCurrentLoad(connection.getCurrentLoad() - 1);
            this.updateById(connection);
        }

        List<ElevatorScheduleEntity> schedules = elevatorScheduleMapper.selectByVehicleId(vehicleId);
        for (ElevatorScheduleEntity schedule : schedules) {
            if ("PENDING".equals(schedule.getStatus()) || "RUNNING".equals(schedule.getStatus())) {
                schedule.setStatus("COMPLETED");
                schedule.setActualDeliveryTime(new Date());
                elevatorScheduleMapper.updateById(schedule);
            }
        }

        log.info("电梯释放成功: connectionId={}, vehicleId={}", connectionId, vehicleId);
        return true;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateConnection(CrossLayerConnectionEntity connection) {
        return this.updateById(connection);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean deleteConnection(Long id) {
        return this.removeById(id);
    }

    @Override
    public CrossLayerConnectionEntity selectById(Long id) {
        return this.getById(id);
    }
}
