package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.FunctionArea;
import org.opentcs.map.service.FunctionAreaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能区域控制器
 *
 * @author lyc
 */
@RestController
@RequestMapping("/function-area")
@RequiredArgsConstructor
public class FunctionAreaController {

    private final FunctionAreaService functionAreaService;

    /**
     * 获取所有功能区域
     *
     * @return 功能区域列表
     */
    @GetMapping("/list")
    public List<FunctionArea> list() {
        return functionAreaService.list();
    }

    /**
     * 根据ID获取功能区域
     *
     * @param id 功能区域ID
     * @return 功能区域信息
     */
    @GetMapping("/{id}")
    public FunctionArea get(@PathVariable Long id) {
        return functionAreaService.getById(id);
    }

    /**
     * 创建功能区域
     *
     * @param functionArea 功能区域信息
     * @return 是否成功
     */
    @PostMapping
    public boolean create(@RequestBody FunctionArea functionArea) {
        return functionAreaService.save(functionArea);
    }

    /**
     * 更新功能区域
     *
     * @param functionArea 功能区域信息
     * @return 是否成功
     */
    @PutMapping
    public boolean update(@RequestBody FunctionArea functionArea) {
        return functionAreaService.updateById(functionArea);
    }

    /**
     * 删除功能区域
     *
     * @param id 功能区域ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return functionAreaService.removeById(id);
    }
}