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
}