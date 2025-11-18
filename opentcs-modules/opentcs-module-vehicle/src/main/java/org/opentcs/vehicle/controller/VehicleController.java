package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.vehicle.domain.entity.Vehicle;
import org.opentcs.vehicle.service.VehicleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车辆 Controller
 */
@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * 查询所有车辆
     */
    @GetMapping("/")
    public List<Vehicle> getAllVehicles() {
        return vehicleService.list();
    }

    /**
     * 根据ID查询车辆
     */
    @GetMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable Long id) {
        return vehicleService.getById(id);
    }

    /**
     * 创建车辆
     */
    @PostMapping("/")
    public boolean createVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.save(vehicle);
    }

    /**
     * 更新车辆
     */
    @PutMapping("/")
    public boolean updateVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.updateById(vehicle);
    }

    /**
     * 删除车辆
     */
    @DeleteMapping("/{id}")
    public boolean deleteVehicle(@PathVariable Long id) {
        return vehicleService.removeById(id);
    }
}