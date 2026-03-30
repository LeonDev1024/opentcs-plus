package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.FactoryModelDTO;
import org.opentcs.map.application.MapFacadeApplicationService;
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

    private final MapFacadeApplicationService mapFacadeApplicationService;

    /**
     * 查询所有工厂模型
     */
    @GetMapping("/list")
    public TableDataInfo<FactoryModelDTO> list(FactoryModelDTO factoryModel, PageQuery pageQuery) {
        return mapFacadeApplicationService.listFactoryModels(factoryModel, pageQuery);
    }

    /**
     * 根据ID查询工厂模型详情
     */
    @GetMapping("/{id}")
    public R<FactoryModelDTO> getById(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.getFactoryModelById(id));
    }

    /**
     * 创建工厂模型
     */
    @PostMapping("/create")
    public R<Boolean> create(@RequestBody FactoryModelDTO factoryModel) {
        return R.ok(mapFacadeApplicationService.createFactoryModel(factoryModel));
    }

    /**
     * 更新工厂模型
     */
    @PutMapping("/update")
    public R<Boolean> update(@RequestBody FactoryModelDTO factoryModel) {
        return R.ok(mapFacadeApplicationService.updateFactoryModel(factoryModel));
    }

    /**
     * 删除工厂模型
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.deleteFactoryModel(id));
    }
}
