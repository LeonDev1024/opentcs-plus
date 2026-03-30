package org.opentcs.vehicle.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.VehicleCrudDTO;
import org.opentcs.vehicle.persistence.entity.VehicleEntity;
import org.opentcs.vehicle.persistence.mapper.VehicleMapper;
import org.opentcs.vehicle.persistence.service.VehicleDomainService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 车辆领域服务实现
 */
@Service
@CacheConfig(cacheNames = "vehicle")
public class VehicleDomainServiceImpl extends ServiceImpl<VehicleMapper, VehicleEntity> implements VehicleDomainService {

    @Override
    public TableDataInfo<VehicleEntity> selectPageVehicle(VehicleEntity vehicle, PageQuery pageQuery) {
        Page<VehicleEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<VehicleEntity> result = this.getBaseMapper().selectPageVehicle(page, vehicle);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<VehicleCrudDTO> selectPageVehicleDTO(VehicleEntity vehicle, PageQuery pageQuery) {
        Page<VehicleEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<VehicleEntity> result = this.getBaseMapper().selectPageVehicle(page, vehicle);

        List<VehicleCrudDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        TableDataInfo<VehicleCrudDTO> tableDataInfo = new TableDataInfo<>();
        tableDataInfo.setRows(dtoList);
        tableDataInfo.setTotal(result.getTotal());
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg("查询成功");
        return tableDataInfo;
    }

    @Override
    public VehicleCrudDTO getVehicleDTOById(Long id) {
        VehicleEntity entity = super.getById(id);
        return convertToDTO(entity);
    }

    @Override
    @Cacheable(key = "#vehicleId")
    public VehicleEntity getVehicleStatus(Long vehicleId) {
        return this.getById(vehicleId);
    }

    @Override
    @Cacheable(key = "'all'")
    public List<VehicleEntity> getAllVehicleStatus() {
        return this.list();
    }

    @Override
    public Map<String, Object> getVehicleStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        long totalVehicles = this.count();
        statistics.put("totalVehicles", totalVehicles);

        long idleVehicles = this.count(new LambdaQueryWrapper<>(VehicleEntity.class).eq(VehicleEntity::getState, "IDLE"));
        long workingVehicles = this.count(new LambdaQueryWrapper<>(VehicleEntity.class).eq(VehicleEntity::getState, "WORKING"));
        long chargingVehicles = this.count(new LambdaQueryWrapper<>(VehicleEntity.class).eq(VehicleEntity::getState, "CHARGING"));
        long errorVehicles = this.count(new LambdaQueryWrapper<>(VehicleEntity.class).eq(VehicleEntity::getState, "ERROR"));
        long unavailableVehicles = this.count(new LambdaQueryWrapper<>(VehicleEntity.class).eq(VehicleEntity::getState, "UNAVAILABLE"));

        statistics.put("idleVehicles", idleVehicles);
        statistics.put("workingVehicles", workingVehicles);
        statistics.put("chargingVehicles", chargingVehicles);
        statistics.put("errorVehicles", errorVehicles);
        statistics.put("unavailableVehicles", unavailableVehicles);

        double utilizationRate = totalVehicles > 0 ? (double) (workingVehicles + chargingVehicles) / totalVehicles * 100 : 0;
        statistics.put("utilizationRate", utilizationRate);

        return statistics;
    }

    @Override
    public TableDataInfo<Map<String, Object>> getVehicleHistory(Long vehicleId, PageQuery pageQuery) {
        List<Map<String, Object>> history = new java.util.ArrayList<>();
        Map<String, Object> record1 = new HashMap<>();
        record1.put("time", "2026-02-27 10:00:00");
        record1.put("action", "开始任务");
        record1.put("details", "从 A 点到 B 点");
        history.add(record1);

        Map<String, Object> record2 = new HashMap<>();
        record2.put("time", "2026-02-27 10:30:00");
        record2.put("action", "完成任务");
        record2.put("details", "到达 B 点");
        history.add(record2);

        int pageSize = pageQuery.getPageSize();
        int pageNum = pageQuery.getPageNum();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, history.size());
        List<Map<String, Object>> pageData = history.subList(start, end);

        TableDataInfo<Map<String, Object>> result = new TableDataInfo<>();
        result.setRows(pageData);
        result.setTotal(history.size());

        return result;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean save(VehicleEntity vehicle) {
        return super.save(vehicle);
    }

    @CacheEvict(allEntries = true)
    public boolean updateById(VehicleEntity vehicle) {
        return super.updateById(vehicle);
    }

    @CacheEvict(allEntries = true)
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @CacheEvict(allEntries = true)
    public boolean registerVehicle(VehicleEntity vehicle) {
        long count = this.count(new LambdaQueryWrapper<>(VehicleEntity.class).eq(VehicleEntity::getName, vehicle.getName()));
        if (count > 0) {
            throw new RuntimeException("车辆名称已存在");
        }

        vehicle.setState("UNAVAILABLE");
        vehicle.setIntegrationLevel("TO_BE_UTILIZED");

        return this.save(vehicle);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean unregisterVehicle(Long vehicleId) {
        return this.removeById(vehicleId);
    }

    @Override
    public VehicleEntity getByName(String name) {
        return this.getOne(
                new LambdaQueryWrapper<VehicleEntity>()
                        .eq(VehicleEntity::getName, name)
        );
    }

    private VehicleCrudDTO convertToDTO(VehicleEntity entity) {
        if (entity == null) {
            return null;
        }
        VehicleCrudDTO dto = new VehicleCrudDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setVinCode(entity.getVinCode());
        dto.setVehicleTypeId(entity.getVehicleTypeId());
        dto.setVehicleTypeName(entity.getVehicleTypeName());
        dto.setCurrentPosition(entity.getCurrentPosition());
        dto.setNextPosition(entity.getNextPosition());
        dto.setState(entity.getState());
        dto.setIntegrationLevel(entity.getIntegrationLevel());
        dto.setEnergyLevel(entity.getEnergyLevel());
        dto.setCurrentTransportOrder(entity.getCurrentTransportOrder());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}
