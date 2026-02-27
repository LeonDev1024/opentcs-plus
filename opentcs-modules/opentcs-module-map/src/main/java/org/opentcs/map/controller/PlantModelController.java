package org.opentcs.map.controller;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.map.domain.entity.PlantModel;
import org.opentcs.map.service.PlantModelService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 地图模型管理
 * @author lyc
 */
@Validated
@RestController
@RequestMapping("/map/model")
@RequiredArgsConstructor
public class PlantModelController extends BaseController {

    private final PlantModelService plantModelService;

    /**
     * 查询所有地图模型
     */
    @GetMapping("/list")
    public TableDataInfo<PlantModel> list(PlantModel plantModel, PageQuery pageQuery) {
        return plantModelService.selectPagePlantModel(plantModel, pageQuery);
    }

    /**
     * 根据ID查询地图模型
     */
    @GetMapping("/{id}")
    public R<PlantModel> getPlantModelById(@PathVariable Long id) {
        return R.ok(plantModelService.getById(id));
    }

    /**
     * 创建地图模型
     */
    @PostMapping("/create")
    public R<Boolean> createPlantModel(@RequestBody PlantModel plantModel) {
        return R.ok(plantModelService.createPlantModel(plantModel));
    }

    /**
     * 更新地图模型
     */
    @PutMapping("/update")
    public R<Boolean> updatePlantModel(@RequestBody PlantModel plantModel) {
        return R.ok(plantModelService.updateById(plantModel));
    }

    /**
     * 删除地图模型
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deletePlantModel(@PathVariable Long id) {
        return R.ok(plantModelService.removeById(id));
    }

    /**
     * 导入地图
     */
    @PostMapping("/import")
    public R<Boolean> importMap(@RequestParam("file") MultipartFile file) {
        return R.ok(plantModelService.importMap(file));
    }

    /**
     * 导出地图
     */
    @GetMapping("/export/{id}")
    public void exportMap(@PathVariable Long id, HttpServletResponse response) {
        plantModelService.exportMap(id, response);
    }

    /**
     * 创建地图版本
     */
    @PostMapping("/version/create")
    public R<Long> createVersion(@RequestParam Long modelId, @RequestParam String versionName) {
        return R.ok(plantModelService.createVersion(modelId, versionName));
    }

    /**
     * 获取版本历史
     */
    @GetMapping("/version/history/{id}")
    public TableDataInfo<PlantModel> getVersionHistory(@PathVariable Long id, PageQuery pageQuery) {
        return plantModelService.getVersionHistory(id, pageQuery);
    }

    /**
     * 拓扑验证
     */
    @GetMapping("/validate/{id}")
    public R<String> validateTopology(@PathVariable Long id) {
        return R.ok(plantModelService.validateTopology(id));
    }

    /**
     * 复制地图
     */
    @PostMapping("/copy")
    public R<Long> copyMap(@RequestParam Long modelId, @RequestParam String newName) {
        return R.ok(plantModelService.copyMap(modelId, newName));
    }
}