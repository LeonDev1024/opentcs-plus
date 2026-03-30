package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.VehicleCrudDTO;
import org.opentcs.kernel.api.dto.VehicleEntityDTO;
import org.opentcs.vehicle.persistence.entity.VehicleEntity;
import org.opentcs.vehicle.persistence.service.VehicleDomainService;
import org.opentcs.vehicle.application.VehicleApplicationService;
import org.opentcs.kernel.api.dto.VehicleDTO;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.vehicle.controller.req.RegisterVehicleWithDriverRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    private final VehicleDomainService vehicleService;
    private final VehicleApplicationService vehicleApplicationService;

    /**
     * 分页查询车辆列表
     */
    @GetMapping("/list")
    public TableDataInfo<VehicleCrudDTO> listVehicles(VehicleEntityDTO vehicle, PageQuery pageQuery) {
        return vehicleService.selectPageVehicleDTO(toEntity(vehicle), pageQuery);
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
    public R<Boolean> createVehicle(@RequestBody VehicleEntityDTO vehicle) {
        return R.ok(vehicleService.save(toEntity(vehicle)));
    }

    /**
     * 更新车辆
     */
    @PutMapping("/update")
    public R<Boolean> updateVehicle(@RequestBody VehicleEntityDTO vehicle) {
        return R.ok(vehicleService.updateById(toEntity(vehicle)));
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
    public R<VehicleEntityDTO> getVehicleStatus(@PathVariable Long id) {
        return R.ok(toDTO(vehicleService.getVehicleStatus(id)));
    }

    /**
     * 获取所有车辆状态
     */
    @GetMapping("/status/all")
    public R<List<VehicleEntityDTO>> getAllVehicleStatus() {
        List<VehicleEntity> list = vehicleService.getAllVehicleStatus();
        List<VehicleEntityDTO> result = list == null
            ? new ArrayList<>()
            : list.stream().map(this::toDTO).collect(Collectors.toList());
        return R.ok(result);
    }

    /**
     * 远程控制车辆
     */
    @PostMapping("/control/{id}")
    public R<String> controlVehicle(@PathVariable Long id, @RequestParam String command, @RequestBody Map<String, Object> params) {
        return R.ok();
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
    public R<Boolean> registerVehicle(@RequestBody VehicleEntityDTO vehicle) {
        return R.ok(vehicleApplicationService.registerVehicle(vehicle, null));
    }

    /**
     * 车辆注册（含驱动配置）
     */
    @PostMapping("/registerWithDriver")
    public R<Boolean> registerVehicleWithDriver(@RequestBody RegisterVehicleWithDriverRequest request) {
        return R.ok(vehicleApplicationService.registerVehicle(request.getVehicle(), request.getDriverConfig()));
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

    private VehicleEntity toEntity(VehicleEntityDTO dto) {
        VehicleEntity entity = new VehicleEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setVinCode(dto.getVinCode());
        entity.setVehicleTypeId(dto.getVehicleTypeId());
        entity.setCurrentPosition(dto.getCurrentPosition());
        entity.setNextPosition(dto.getNextPosition());
        entity.setState(dto.getState());
        entity.setIntegrationLevel(dto.getIntegrationLevel());
        entity.setEnergyLevel(dto.getEnergyLevel());
        entity.setCurrentTransportOrder(dto.getCurrentTransportOrder());
        entity.setProperties(dto.getProperties());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }

    private VehicleEntityDTO toDTO(VehicleEntity entity) {
        if (entity == null) {
            return null;
        }
        VehicleEntityDTO dto = new VehicleEntityDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setVinCode(entity.getVinCode());
        dto.setVehicleTypeId(entity.getVehicleTypeId());
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