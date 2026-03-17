package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.VehicleCrudDTO;
import org.opentcs.kernel.persistence.entity.VehicleEntity;
import org.opentcs.vehicle.service.VehicleService;
import org.opentcs.vehicle.application.VehicleApplicationService;
import org.opentcs.kernel.api.dto.VehicleDTO;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.InstantAction;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 车辆管理
 * @author liuyichun
 */
@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController extends BaseController {

    private final VehicleService vehicleService;
    private final VehicleApplicationService vehicleApplicationService;

    /**
     * 分页查询车辆列表
     */
    @GetMapping("/list")
    public TableDataInfo<VehicleCrudDTO> listVehicles(VehicleEntity vehicle, PageQuery pageQuery) {
        return vehicleService.selectPageVehicleDTO(vehicle, pageQuery);
    }

    /**
     * 查询所有车辆
     */
    @GetMapping("/getAll")
    public R<List<VehicleCrudDTO>> getAllVehicles() {
        return R.ok(vehicleService.list().stream()
                .map(entity -> vehicleService.getVehicleDTOById(entity.getId()))
                .collect(Collectors.toList()));
    }

    /**
     * 根据ID查询车辆
     */
    @GetMapping("/{id}")
    public R<VehicleCrudDTO> getVehicleById(@PathVariable Long id) {
        return R.ok(vehicleService.getVehicleDTOById(id));
    }

    /**
     * 创建车辆
     */
    @PostMapping("/create")
    public R<Boolean> createVehicle(@RequestBody VehicleEntity vehicle) {
        return R.ok(vehicleService.save(vehicle));
    }

    /**
     * 更新车辆
     */
    @PutMapping("/update")
    public R<Boolean> updateVehicle(@RequestBody VehicleEntity vehicle) {
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
     * 获取车辆实时状态（从内核）
     */
    @GetMapping("/runtime/status/{name}")
    public R<VehicleDTO> getVehicleRuntimeStatus(@PathVariable String name) {
        return R.ok(vehicleApplicationService.getVehicleRuntimeStatus(name));
    }

    /**
     * 获取所有车辆实时状态（从内核）
     */
    @GetMapping("/runtime/status/all")
    public R<List<VehicleDTO>> getAllVehicleRuntimeStatus() {
        return R.ok(vehicleApplicationService.getAllVehicleRuntimeStatus());
    }

    /**
     * 获取可用车辆
     */
    @GetMapping("/runtime/available")
    public R<List<VehicleDTO>> getAvailableVehicles() {
        return R.ok(vehicleApplicationService.getAvailableVehicles());
    }

    /**
     * 获取车辆实时状态（数据库）
     */
    @GetMapping("/status/{id}")
    public R<VehicleEntity> getVehicleStatus(@PathVariable Long id) {
        return R.ok(vehicleService.getVehicleStatus(id));
    }

    /**
     * 获取所有车辆状态
     */
    @GetMapping("/status/all")
    public R<List<VehicleEntity>> getAllVehicleStatus() {
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
     * 发送即时动作到车辆
     */
    @PostMapping("/instantAction/{name}")
    public R<Boolean> sendInstantAction(@PathVariable String name, @RequestBody InstantAction action) {
        vehicleApplicationService.sendInstantAction(name, action);
        return R.ok(true);
    }

    /**
     * 获取车辆统计数据
     */
    @GetMapping("/statistics")
    public R<Map<String, Object>> getVehicleStatistics() {
        return R.ok(vehicleApplicationService.getVehicleStatistics());
    }

    /**
     * 获取车辆运行历史
     */
    @GetMapping("/history/{id}")
    public TableDataInfo<Map<String, Object>> getVehicleHistory(@PathVariable Long id, PageQuery pageQuery) {
        return vehicleService.getVehicleHistory(id, pageQuery);
    }

    /**
     * 车辆注册（到内核）
     */
    @PostMapping("/register")
    public R<Boolean> registerVehicle(@RequestBody VehicleEntity vehicle) {
        return R.ok(vehicleService.registerVehicle(vehicle));
    }

    /**
     * 车辆注册（含驱动配置）
     */
    @PostMapping("/registerWithDriver")
    public R<Boolean> registerVehicleWithDriver(@RequestBody VehicleEntity vehicle, @RequestBody DriverConfig driverConfig) {
        return R.ok(vehicleApplicationService.registerVehicle(vehicle, driverConfig));
    }

    /**
     * 激活车辆
     */
    @PostMapping("/activate/{id}")
    public R<Boolean> activateVehicle(@PathVariable Long id) {
        return R.ok(vehicleApplicationService.activateVehicle(id));
    }

    /**
     * 停用车辆
     */
    @PostMapping("/deactivate/{id}")
    public R<Boolean> deactivateVehicle(@PathVariable Long id) {
        return R.ok(vehicleApplicationService.deactivateVehicle(id));
    }

    /**
     * 车辆注销
     */
    @PostMapping("/unregister/{id}")
    public R<Boolean> unregisterVehicle(@PathVariable Long id) {
        return R.ok(vehicleApplicationService.unregisterVehicle(id));
    }
}