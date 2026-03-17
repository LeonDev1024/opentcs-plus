package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.persistence.entity.FactoryModelEntity;
import org.opentcs.kernel.persistence.service.FactoryModelDomainService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 工厂模型管理
 */
@Validated
@RestController
@RequestMapping("/factory/model")
@RequiredArgsConstructor
public class FactoryModelController extends BaseController {

    private final FactoryModelDomainService factoryModelDomainService;

    /**
     * 查询所有工厂模型
     */
    @GetMapping("/list")
    public TableDataInfo<FactoryModelEntity> list(FactoryModelEntity factoryModel, PageQuery pageQuery) {
        return factoryModelDomainService.selectPageFactoryModel(factoryModel, pageQuery);
    }

    /**
     * 根据ID查询工厂模型详情
     */
    @GetMapping("/{id}")
    public R<FactoryModelEntity> getById(@PathVariable Long id) {
        return R.ok(factoryModelDomainService.getFactoryModelDetail(id));
    }

    /**
     * 创建工厂模型
     */
    @PostMapping("/create")
    public R<Boolean> create(@RequestBody FactoryModelEntity factoryModel) {
        return R.ok(factoryModelDomainService.createFactoryModel(factoryModel));
    }

    /**
     * 更新工厂模型
     */
    @PutMapping("/update")
    public R<Boolean> update(@RequestBody FactoryModelEntity factoryModel) {
        return R.ok(factoryModelDomainService.updateFactoryModel(factoryModel));
    }

    /**
     * 删除工厂模型
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(factoryModelDomainService.deleteFactoryModel(id));
    }
}
