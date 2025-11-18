package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.vehicle.domain.entity.VehicleType;
import org.opentcs.vehicle.service.VehicleTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车辆类型 Controller
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
    public List<VehicleType> getAllVehicleTypes() {
        return vehicleTypeService.list();
    }

    /**
     * 根据ID查询车辆类型
     */
    @GetMapping("/{id}")
    public VehicleType getVehicleTypeById(@PathVariable Long id) {
        return vehicleTypeService.getById(id);
    }

    /**
     * 创建车辆类型
     */
    @PostMapping("/")
    public boolean createVehicleType(@RequestBody VehicleType vehicleType) {
        return vehicleTypeService.save(vehicleType);
    }

    /**
     * 更新车辆类型
     */
    @PutMapping("/")
    public boolean updateVehicleType(@RequestBody VehicleType vehicleType) {
        return vehicleTypeService.updateById(vehicleType);
    }

    /**
     * 删除车辆类型
     */
    @DeleteMapping("/{id}")
    public boolean deleteVehicleType(@PathVariable Long id) {
        return vehicleTypeService.removeById(id);
    }
}