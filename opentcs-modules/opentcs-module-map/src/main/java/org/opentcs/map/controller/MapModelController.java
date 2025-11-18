package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.map.domain.entity.PlantModel;
import org.opentcs.map.service.PlantModelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地图模型管理
 * @author lyc
 */
@RestController
@RequestMapping("/map/model")
@RequiredArgsConstructor
public class MapModelController {

    private final PlantModelService plantModelService;

    /**
     * 查询所有地图模型
     */
    @GetMapping("/list")
    public R<List<PlantModel>> getAllPlantModels() {
        return R.ok(plantModelService.list());
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
    @PostMapping("/")
    public R<Boolean> createPlantModel(@RequestBody PlantModel plantModel) {
        return R.ok(plantModelService.save(plantModel));
    }

    /**
     * 更新地图模型
     */
    @PutMapping("/")
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
}