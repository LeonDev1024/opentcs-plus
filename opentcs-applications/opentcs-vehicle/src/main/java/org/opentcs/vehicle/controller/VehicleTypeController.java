package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.vehicle.persistence.entity.VehicleTypeEntity;
import org.opentcs.vehicle.persistence.service.VehicleTypeDomainService;
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

    private final VehicleTypeDomainService vehicleTypeService;

    /**
     * 分页查询车辆类型列表
     */
    @GetMapping("/list")
    public TableDataInfo<VehicleTypeEntity> listVehicleTypes(VehicleTypeEntity vehicleType, PageQuery pageQuery) {
        return vehicleTypeService.selectPageVehicleType(vehicleType, pageQuery);
    }

    /**
     * 查询所有车辆类型（不分页）
     */
    @GetMapping("/all")
    public R<List<VehicleTypeEntity>> getAllVehicleTypes() {
        return R.ok(vehicleTypeService.list());
    }

    /**
     * 根据ID查询车辆类型
     */
    @GetMapping("/{id}")
    public R<VehicleTypeEntity> getVehicleTypeById(@PathVariable Long id) {
        return R.ok(vehicleTypeService.getById(id));
    }

    /**
     * 创建车辆类型
     */
    @PostMapping("/add")
    public R<Boolean> createVehicleType(@RequestBody VehicleTypeEntity vehicleType) {
        return R.ok(vehicleTypeService.save(vehicleType));
    }

    /**
     * 更新车辆类型
     */
    @PutMapping("/edit")
    public R<Boolean> updateVehicleType(@RequestBody VehicleTypeEntity vehicleType) {
        return R.ok(vehicleTypeService.updateById(vehicleType));
    }

    /**
     * 删除车辆类型
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteVehicleType(@PathVariable Long id) {
        return R.ok(vehicleTypeService.removeById(id));
    }

    /**
     * 根据品牌ID获取类型列表
     */
    @GetMapping("/by-brand/{brandId}")
    public R<List<VehicleTypeEntity>> getVehicleTypesByBrandId(@PathVariable Long brandId) {
        VehicleTypeEntity query = new VehicleTypeEntity();
        query.setBrandId(brandId);
        return R.ok(vehicleTypeService.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<VehicleTypeEntity>()
                .eq(VehicleTypeEntity::getBrandId, brandId)
                .eq(VehicleTypeEntity::getDelFlag, "0")));
    }
}