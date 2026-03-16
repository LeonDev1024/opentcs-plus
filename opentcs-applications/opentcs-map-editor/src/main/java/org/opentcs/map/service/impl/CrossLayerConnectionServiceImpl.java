package org.opentcs.map.service.impl;

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
import org.opentcs.map.mapper.CrossLayerConnectionMapper;
import org.opentcs.map.mapper.ElevatorScheduleMapper;
import org.opentcs.map.service.CrossLayerConnectionService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 跨层连接 Service 实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "crossLayerConnection")
public class CrossLayerConnectionServiceImpl extends ServiceImpl<CrossLayerConnectionMapper, CrossLayerConnectionEntity>
        implements CrossLayerConnectionService {

    private final ElevatorScheduleMapper elevatorScheduleMapper;

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean createConnection(CrossLayerConnectionEntity connection) {
        // 校验连接标识是否已存在
        long count = this.count(new LambdaQueryWrapper<CrossLayerConnectionEntity>()
                .eq(CrossLayerConnectionEntity::getFactoryModelId, connection.getFactoryModelId())
                .eq(CrossLayerConnectionEntity::getConnectionId, connection.getConnectionId())
                .eq(CrossLayerConnectionEntity::getDelFlag, "0"));
        if (count > 0) {
            throw new RuntimeException("连接标识已存在");
        }

        // 生成唯一标识符
        if (connection.getConnectionId() == null || connection.getConnectionId().isEmpty()) {
            connection.setConnectionId(IdUtil.fastSimpleUUID());
        }

        // 默认值
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
    public TableDataInfo<CrossLayerConnectionEntity> selectPageConnection(CrossLayerConnectionEntity connection,
                                                                         PageQuery pageQuery) {
        IPage<CrossLayerConnectionEntity> page = this.getBaseMapper().selectPageCrossLayerConnection(
                pageQuery.build(), connection);
        return TableDataInfo.build(page);
    }

    @Override
    public List<CrossLayerConnectionEntity> selectByFactoryModelId(Long factoryModelId) {
        return this.getBaseMapper().selectByFactoryModelId(factoryModelId);
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

        // 更新电梯负载
        connection.setCurrentLoad(connection.getCurrentLoad() + 1);
        this.updateById(connection);

        // 创建调度记录
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

        // 更新电梯负载
        if (connection.getCurrentLoad() > 0) {
            connection.setCurrentLoad(connection.getCurrentLoad() - 1);
            this.updateById(connection);
        }

        // 更新调度记录状态
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
}
