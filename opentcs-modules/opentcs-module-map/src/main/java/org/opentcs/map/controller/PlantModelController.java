package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.PlantModel;
import org.opentcs.map.service.PlantModelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地图模型 Controller
 */
@RestController
@RequestMapping("/plant-model")
@RequiredArgsConstructor
public class PlantModelController {

    private final PlantModelService plantModelService;

    /**
     * 查询所有地图模型
     */
    @GetMapping("/")
    public List<PlantModel> getAllPlantModels() {
        return plantModelService.list();
    }

    /**
     * 根据ID查询地图模型
     */
    @GetMapping("/{id}")
    public PlantModel getPlantModelById(@PathVariable Long id) {
        return plantModelService.getById(id);
    }

    /**
     * 创建地图模型
     */
    @PostMapping("/")
    public boolean createPlantModel(@RequestBody PlantModel plantModel) {
        return plantModelService.save(plantModel);
    }

    /**
     * 更新地图模型
     */
    @PutMapping("/")
    public boolean updatePlantModel(@RequestBody PlantModel plantModel) {
        return plantModelService.updateById(plantModel);
    }

    /**
     * 删除地图模型
     */
    @DeleteMapping("/{id}")
    public boolean deletePlantModel(@PathVariable Long id) {
        return plantModelService.removeById(id);
    }
}