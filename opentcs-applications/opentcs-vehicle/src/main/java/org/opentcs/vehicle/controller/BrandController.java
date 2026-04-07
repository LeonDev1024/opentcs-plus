package org.opentcs.vehicle.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.vehicle.persistence.entity.BrandEntity;
import org.opentcs.vehicle.persistence.service.BrandDomainService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌管理
 * @author lyc
 */
@RestController
@RequestMapping("/vehicle/brand")
@RequiredArgsConstructor
public class BrandController extends BaseController {

    private final BrandDomainService brandService;

    /**
     * 分页查询品牌列表
     */
    @GetMapping("/list")
    public TableDataInfo<BrandEntity> listBrands(BrandEntity brand, PageQuery pageQuery) {
        return brandService.selectPageBrand(brand, pageQuery);
    }

    /**
     * 查询所有启用的品牌（下拉选择用）
     */
    @GetMapping("/all")
    public R<List<BrandEntity>> getAllBrands() {
        return R.ok(brandService.selectBrandList());
    }

    /**
     * 根据ID查询品牌
     */
    @GetMapping("/{id}")
    public R<BrandEntity> getBrandById(@PathVariable Long id) {
        return R.ok(brandService.getById(id));
    }

    /**
     * 创建品牌
     */
    @PostMapping("/add")
    public R<Boolean> createBrand(@RequestBody BrandEntity brand) {
        return R.ok(brandService.save(brand));
    }

    /**
     * 更新品牌
     */
    @PutMapping("/edit")
    public R<Boolean> updateBrand(@RequestBody BrandEntity brand) {
        return R.ok(brandService.updateById(brand));
    }

    /**
     * 删除品牌
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteBrand(@PathVariable Long id) {
        return R.ok(brandService.removeById(id));
    }

    /**
     * 启用/禁用品牌
     */
    @PutMapping("/changeStatus")
    public R<Boolean> changeStatus(@RequestBody BrandEntity brand) {
        BrandEntity entity = new BrandEntity();
        entity.setId(brand.getId());
        entity.setEnabled(brand.getEnabled());
        return R.ok(brandService.updateById(entity));
    }
}
