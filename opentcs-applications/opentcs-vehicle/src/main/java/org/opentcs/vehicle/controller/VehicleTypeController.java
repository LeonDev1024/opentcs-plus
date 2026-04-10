package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.vehicle.application.VehicleTypeApplicationService;
import org.opentcs.vehicle.persistence.entity.VehicleTypeEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车辆类型管理
 */
@RestController
@RequestMapping("/vehicle/type")
@RequiredArgsConstructor
public class VehicleTypeController extends BaseController {

    private final VehicleTypeApplicationService vehicleTypeApplicationService;

    @GetMapping("/list")
    public TableDataInfo<VehicleTypeEntity> listVehicleTypes(VehicleTypeEntity vehicleType, PageQuery pageQuery) {
        return vehicleTypeApplicationService.listVehicleTypes(vehicleType, pageQuery);
    }

    @GetMapping("/all")
    public R<List<VehicleTypeEntity>> getAllVehicleTypes() {
        return R.ok(vehicleTypeApplicationService.getAllVehicleTypes());
    }

    @GetMapping("/{id}")
    public R<VehicleTypeEntity> getVehicleTypeById(@PathVariable Long id) {
        return R.ok(vehicleTypeApplicationService.getById(id));
    }

    @PostMapping("/add")
    public R<Boolean> createVehicleType(@RequestBody VehicleTypeEntity vehicleType) {
        return R.ok(vehicleTypeApplicationService.create(vehicleType));
    }

    @PutMapping("/edit")
    public R<Boolean> updateVehicleType(@RequestBody VehicleTypeEntity vehicleType) {
        return R.ok(vehicleTypeApplicationService.update(vehicleType));
    }

    @DeleteMapping("/{id}")
    public R<Boolean> deleteVehicleType(@PathVariable Long id) {
        return R.ok(vehicleTypeApplicationService.delete(id));
    }

    @GetMapping("/by-brand/{brandId}")
    public R<List<VehicleTypeEntity>> getVehicleTypesByBrandId(@PathVariable Long brandId) {
        return R.ok(vehicleTypeApplicationService.getByBrandId(brandId));
    }
}
