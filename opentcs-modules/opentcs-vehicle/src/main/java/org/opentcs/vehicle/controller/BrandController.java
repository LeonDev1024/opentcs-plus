package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.vehicle.application.BrandApplicationService;
import org.opentcs.vehicle.application.bo.BrandBO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌管理
 */
@RestController
@RequestMapping("/vehicle/brand")
@RequiredArgsConstructor
public class BrandController extends BaseController {

    private final BrandApplicationService brandApplicationService;

    @GetMapping("/list")
    public TableDataInfo<BrandBO> listBrands(BrandBO brand, PageQuery pageQuery) {
        return brandApplicationService.listBrands(brand, pageQuery);
    }

    @GetMapping("/all")
    public R<List<BrandBO>> getAllBrands() {
        return R.ok(brandApplicationService.getAllEnabledBrands());
    }

    @GetMapping("/{id}")
    public R<BrandBO> getBrandById(@PathVariable Long id) {
        return R.ok(brandApplicationService.getById(id));
    }

    @PostMapping("/add")
    public R<Boolean> createBrand(@RequestBody BrandBO brand) {
        return R.ok(brandApplicationService.create(brand));
    }

    @PutMapping("/edit")
    public R<Boolean> updateBrand(@RequestBody BrandBO brand) {
        return R.ok(brandApplicationService.update(brand));
    }

    @DeleteMapping("/{id}")
    public R<Boolean> deleteBrand(@PathVariable Long id) {
        return R.ok(brandApplicationService.delete(id));
    }

    @PutMapping("/changeStatus")
    public R<Boolean> changeStatus(@RequestBody BrandBO brand) {
        return R.ok(brandApplicationService.changeStatus(brand.getId(), brand.getEnabled()));
    }
}
