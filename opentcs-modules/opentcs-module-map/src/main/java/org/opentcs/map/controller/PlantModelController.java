package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.PlantModel;
import org.opentcs.map.service.PlantModelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地图模型控制器
 *
 * @author lyc
 */
@RestController
@RequestMapping("/plant-model")
@RequiredArgsConstructor
public class PlantModelController {

    private final PlantModelService plantModelService;

    /**
     * 获取所有地图模型
     *
     * @return 地图模型列表
     */
    @GetMapping("/list")
    public List<PlantModel> list() {
        return plantModelService.list();
    }

    /**
     * 根据ID获取地图模型
     *
     * @param id 地图模型ID
     * @return 地图模型信息
     */
    @GetMapping("/{id}")
    public PlantModel get(@PathVariable Long id) {
        return plantModelService.getById(id);
    }

    /**
     * 创建地图模型
     *
     * @param plantModel 地图模型信息
     * @return 是否成功
     */
    @PostMapping
    public boolean create(@RequestBody PlantModel plantModel) {
        return plantModelService.save(plantModel);
    }

    /**
     * 更新地图模型
     *
     * @param plantModel 地图模型信息
     * @return 是否成功
     */
    @PutMapping
    public boolean update(@RequestBody PlantModel plantModel) {
        return plantModelService.updateById(plantModel);
    }

    /**
     * 删除地图模型
     *
     * @param id 地图模型ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return plantModelService.removeById(id);
    }
}