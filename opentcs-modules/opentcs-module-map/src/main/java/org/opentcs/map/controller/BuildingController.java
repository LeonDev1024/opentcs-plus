package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.Building;
import org.opentcs.map.service.BuildingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 建筑物管理
 *
 * @author lyc
 */
@RestController
@RequestMapping("/building")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    /**
     * 获取所有建筑物
     *
     * @return 建筑物列表
     */
    @GetMapping("/list")
    public List<Building> list() {
        return buildingService.list();
    }

    /**
     * 根据ID获取建筑物
     *
     * @param id 建筑物ID
     * @return 建筑物信息
     */
    @GetMapping("/{id}")
    public Building get(@PathVariable Long id) {
        return buildingService.getById(id);
    }

    /**
     * 创建建筑物
     *
     * @param building 建筑物信息
     * @return 是否成功
     */
    @PostMapping
    public boolean create(@RequestBody Building building) {
        return buildingService.save(building);
    }

    /**
     * 更新建筑物
     *
     * @param building 建筑物信息
     * @return 是否成功
     */
    @PutMapping
    public boolean update(@RequestBody Building building) {
        return buildingService.updateById(building);
    }

    /**
     * 删除建筑物
     *
     * @param id 建筑物ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return buildingService.removeById(id);
    }
}