package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.vehicle.domain.entity.VehicleType;
import org.opentcs.vehicle.service.VehicleTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车辆类型管理
 * @author lyc
 */
@RestController
@RequestMapping("/vehicle/type")
@RequiredArgsConstructor
public class VehicleTypeController extends BaseController {

    private final VehicleTypeService vehicleTypeService;

    /**
     * 分页查询车辆类型列表
     */
    @GetMapping("/list")
    public TableDataInfo<VehicleType> listVehicleTypes(VehicleType vehicleType, PageQuery pageQuery) {
        return vehicleTypeService.selectPageVehicleType(vehicleType, pageQuery);
    }

    /**
     * 查询所有车辆类型（不分页）
     */
    @GetMapping("/all")
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
    @PostMapping("/add")
    public R<Boolean> createVehicleType(@RequestBody VehicleType vehicleType) {
        return R.ok(vehicleTypeService.save(vehicleType));
    }

    /**
     * 更新车辆类型
     */
    @PutMapping("/edit")
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