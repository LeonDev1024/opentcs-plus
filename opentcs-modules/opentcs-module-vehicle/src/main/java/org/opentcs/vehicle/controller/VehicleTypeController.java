package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.vehicle.domain.entity.VehicleType;
import org.opentcs.vehicle.service.VehicleTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车辆类型管理
 * @author lyc
 */
@RestController
@RequestMapping("/vehicle-type")
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    /**
     * 查询所有车辆类型
     */
    @GetMapping("/")
    public R<List<VehicleType>> getAllVehicleTypes() {
        return R.ok(vehicleTypeService.list());
    }

    /**
     * 根据ID查询车辆类型
     */
    @GetMapping("/{id}")
    public R<VehicleType> getVehicleTypeById(@PathVariable Long id) {
        return R.ok(vehicleTypeService.getById(id));
    }

    /**
     * 创建车辆类型
     */
    @PostMapping("/")
    public R<Boolean> createVehicleType(@RequestBody VehicleType vehicleType) {
        return R.ok(vehicleTypeService.save(vehicleType));
    }

    /**
     * 更新车辆类型
     */
    @PutMapping("/")
    public R<Boolean> updateVehicleType(@RequestBody VehicleType vehicleType) {
        return R.ok(vehicleTypeService.updateById(vehicleType));
    }

    /**
     * 删除车辆类型
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteVehicleType(@PathVariable Long id) {
        return R.ok(vehicleTypeService.removeById(id));
    }
}