package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.vehicle.domain.entity.Vehicle;
import org.opentcs.vehicle.service.VehicleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 车辆管理
 * @author: lyc
 */
@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController extends BaseController {

    private final VehicleService vehicleService;

    /**
     * 分页查询车辆列表
     */
    @GetMapping("/list")
    public TableDataInfo<Vehicle> listVehicles(Vehicle vehicle, PageQuery pageQuery) {
        return vehicleService.selectPageVehicle(vehicle, pageQuery);
    }

    /**
     * 查询所有车辆
     */
    @GetMapping("/getAll")
    public R<List<Vehicle>> getAllVehicles() {
        return R.ok(vehicleService.list());
    }

    /**
     * 根据ID查询车辆
     */
    @GetMapping("/{id}")
    public R<Vehicle> getVehicleById(@PathVariable Long id) {
        return R.ok(vehicleService.getById(id));
    }

    /**
     * 创建车辆
     */
    @PostMapping("/create")
    public R<Boolean> createVehicle(@RequestBody Vehicle vehicle) {
        return R.ok(vehicleService.save(vehicle));
    }

    /**
     * 更新车辆
     */
    @PutMapping("/update")
    public R<Boolean> updateVehicle(@RequestBody Vehicle vehicle) {
        return R.ok(vehicleService.updateById(vehicle));
    }

    /**
     * 删除车辆
     */
    @DeleteMapping("/delete/{id}")
    public R<Boolean> deleteVehicle(@PathVariable Long id) {
        return R.ok(vehicleService.removeById(id));
    }

    /**
     * 获取车辆实时状态
     */
    @GetMapping("/status/{id}")
    public R<Vehicle> getVehicleStatus(@PathVariable Long id) {
        return R.ok(vehicleService.getVehicleStatus(id));
    }

    /**
     * 获取所有车辆状态
     */
    @GetMapping("/status/all")
    public R<List<Vehicle>> getAllVehicleStatus() {
        return R.ok(vehicleService.getAllVehicleStatus());
    }

    /**
     * 远程控制车辆
     */
    @PostMapping("/control/{id}")
    public R<String> controlVehicle(@PathVariable Long id, @RequestParam String command, @RequestBody Map<String, Object> params) {
        return R.ok(vehicleService.controlVehicle(id, command, params));
    }

    /**
     * 获取车辆统计数据
     */
    @GetMapping("/statistics")
    public R<Map<String, Object>> getVehicleStatistics() {
        return R.ok(vehicleService.getVehicleStatistics());
    }

    /**
     * 获取车辆运行历史
     */
    @GetMapping("/history/{id}")
    public TableDataInfo<Map<String, Object>> getVehicleHistory(@PathVariable Long id, PageQuery pageQuery) {
        return vehicleService.getVehicleHistory(id, pageQuery);
    }

    /**
     * 车辆注册
     */
    @PostMapping("/register")
    public R<Boolean> registerVehicle(@RequestBody Vehicle vehicle) {
        return R.ok(vehicleService.registerVehicle(vehicle));
    }

    /**
     * 车辆注销
     */
    @PostMapping("/unregister/{id}")
    public R<Boolean> unregisterVehicle(@PathVariable Long id) {
        return R.ok(vehicleService.unregisterVehicle(id));
    }
}