package org.opentcs.vehicle.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.vehicle.domain.entity.Vehicle;
import org.opentcs.vehicle.mapper.VehicleMapper;
import org.opentcs.vehicle.service.VehicleService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.opentcs.driver.core.DriverManager;
import org.opentcs.driver.adapter.VDA5050Driver;
import org.opentcs.driver.protocol.InstantActionsMessage;

/**
 * 车辆 Service 实现类
 */
@Service
@CacheConfig(cacheNames = "vehicle")
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    private final DriverManager driverManager = DriverManager.getInstance();

    @Override
    public TableDataInfo<Vehicle> selectPageVehicle(Vehicle vehicle, PageQuery pageQuery) {
        Page<Vehicle> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<Vehicle> result = this.getBaseMapper().selectPageVehicle(page, vehicle);
        return TableDataInfo.build(result);
    }

    @Override
    @Cacheable(key = "#vehicleId")
    public Vehicle getVehicleStatus(Long vehicleId) {
        return this.getById(vehicleId);
    }

    @Override
    @Cacheable(key = "'all'")
    public List<Vehicle> getAllVehicleStatus() {
        return this.list();
    }

    @Override
    @CacheEvict(allEntries = true)
    public String controlVehicle(Long vehicleId, String command, Map<String, Object> params) {
        // 检查车辆是否存在
        Vehicle vehicle = this.getById(vehicleId);
        if (vehicle == null) {
            return "车辆不存在";
        }

        // 获取VDA5050驱动
        VDA5050Driver driver = (VDA5050Driver) driverManager.getDriver(vehicle.getName());
        if (driver == null) {
            return "车辆驱动未找到";
        }

        // 处理不同的控制命令
        switch (command) {
            case "start":
                // 启动车辆
                vehicle.setState("IDLE");
                this.updateById(vehicle);
                return "车辆启动成功";
            case "stop":
                // 停止车辆
                vehicle.setState("UNAVAILABLE");
                this.updateById(vehicle);
                return "车辆停止成功";
            case "charge":
                // 充电命令
                InstantActionsMessage chargeActions = new InstantActionsMessage();
                List<InstantActionsMessage.Action> chargeActionList = new ArrayList<>();
                InstantActionsMessage.Action chargeAction = new InstantActionsMessage.Action();
                chargeAction.setActionType("charge");
                chargeAction.setActionId("charge-" + System.currentTimeMillis());
                chargeAction.setBlockingType("HARD");
                chargeActionList.add(chargeAction);
                chargeActions.setActions(chargeActionList);
                driver.sendInstantActions(chargeActions);
                
                vehicle.setState("CHARGING");
                this.updateById(vehicle);
                return "车辆开始充电";
            case "move":
                // 移动命令
                String targetPosition = (String) params.get("targetPosition");
                if (targetPosition != null) {
                    InstantActionsMessage moveActions = new InstantActionsMessage();
                    List<InstantActionsMessage.Action> moveActionList = new ArrayList<>();
                    InstantActionsMessage.Action moveAction = new InstantActionsMessage.Action();
                    moveAction.setActionType("moveToPosition");
                    moveAction.setActionId("move-" + System.currentTimeMillis());
                    moveAction.setBlockingType("HARD");
                    
                    List<InstantActionsMessage.ActionParameter> parameters = new ArrayList<>();
                    InstantActionsMessage.ActionParameter positionParam = new InstantActionsMessage.ActionParameter();
                    positionParam.setKey("targetPosition");
                    positionParam.setValue(targetPosition);
                    parameters.add(positionParam);
                    moveAction.setActionParameters(parameters);
                    
                    moveActionList.add(moveAction);
                    moveActions.setActions(moveActionList);
                    driver.sendInstantActions(moveActions);
                    
                    vehicle.setNextPosition(targetPosition);
                    vehicle.setState("WORKING");
                    this.updateById(vehicle);
                    return "车辆开始移动到 " + targetPosition;
                } else {
                    return "目标位置不能为空";
                }
            case "cancelOrder":
                // 取消订单命令
                InstantActionsMessage cancelActions = new InstantActionsMessage();
                List<InstantActionsMessage.Action> cancelActionList = new ArrayList<>();
                InstantActionsMessage.Action cancelAction = new InstantActionsMessage.Action();
                cancelAction.setActionType("cancelOrder");
                cancelAction.setActionId("cancel-" + System.currentTimeMillis());
                cancelAction.setBlockingType("HARD");
                cancelActionList.add(cancelAction);
                cancelActions.setActions(cancelActionList);
                driver.sendInstantActions(cancelActions);
                
                vehicle.setState("IDLE");
                this.updateById(vehicle);
                return "订单已取消";
            default:
                return "未知命令";
        }
    }

    @Override
    @Cacheable(key = "'statistics'")
    public Map<String, Object> getVehicleStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总车辆数
        long totalVehicles = this.count();
        statistics.put("totalVehicles", totalVehicles);

        // 各状态车辆数
        long idleVehicles = this.count(new LambdaQueryWrapper<>(Vehicle.class).eq(Vehicle::getState, "IDLE"));
        long workingVehicles = this.count(new LambdaQueryWrapper<>(Vehicle.class).eq(Vehicle::getState, "WORKING"));
        long chargingVehicles = this.count(new LambdaQueryWrapper<>(Vehicle.class).eq(Vehicle::getState, "CHARGING"));
        long errorVehicles = this.count(new LambdaQueryWrapper<>(Vehicle.class).eq(Vehicle::getState, "ERROR"));
        long unavailableVehicles = this.count(new LambdaQueryWrapper<>(Vehicle.class).eq(Vehicle::getState, "UNAVAILABLE"));

        statistics.put("idleVehicles", idleVehicles);
        statistics.put("workingVehicles", workingVehicles);
        statistics.put("chargingVehicles", chargingVehicles);
        statistics.put("errorVehicles", errorVehicles);
        statistics.put("unavailableVehicles", unavailableVehicles);

        // 计算利用率
        double utilizationRate = totalVehicles > 0 ? (double) (workingVehicles + chargingVehicles) / totalVehicles * 100 : 0;
        statistics.put("utilizationRate", utilizationRate);

        return statistics;
    }

    @Override
    @Cacheable(key = "#vehicleId + '-' + #pageQuery.pageNum + '-' + #pageQuery.pageSize")
    public TableDataInfo<Map<String, Object>> getVehicleHistory(Long vehicleId, PageQuery pageQuery) {
        // 检查车辆是否存在
        Vehicle vehicle = this.getById(vehicleId);
        if (vehicle == null) {
            throw new RuntimeException("车辆不存在");
        }

        // 这里简化处理，实际应该从历史表中查询
        // 模拟历史数据
        List<Map<String, Object>> history = new ArrayList<>();
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

        Map<String, Object> record3 = new HashMap<>();
        record3.put("time", "2026-02-27 11:00:00");
        record3.put("action", "开始充电");
        record3.put("details", "在充电点充电");
        history.add(record3);

        // 构建分页结果
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
    public boolean save(Vehicle vehicle) {
        return super.save(vehicle);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateById(Vehicle vehicle) {
        return super.updateById(vehicle);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean registerVehicle(Vehicle vehicle) {
        // 检查车辆名称是否已存在
        long count = this.count(new LambdaQueryWrapper<>(Vehicle.class).eq(Vehicle::getName, vehicle.getName()));
        if (count > 0) {
            throw new RuntimeException("车辆名称已存在");
        }

        // 设置默认状态
        vehicle.setState("UNAVAILABLE");
        vehicle.setIntegrationLevel("TO_BE_UTILIZED");

        return this.save(vehicle);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean unregisterVehicle(Long vehicleId) {
        return this.removeById(vehicleId);
    }
}