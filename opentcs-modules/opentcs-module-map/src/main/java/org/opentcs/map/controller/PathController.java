package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.Path;
import org.opentcs.map.service.PathService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路径控制器
 *
 * @author lyc
 */
@RestController
@RequestMapping("/path")
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    /**
     * 获取所有路径
     *
     * @return 路径列表
     */
    @GetMapping("/list")
    public List<Path> list() {
        return pathService.list();
    }

    /**
     * 根据ID获取路径
     *
     * @param id 路径ID
     * @return 路径信息
     */
    @GetMapping("/{id}")
    public Path get(@PathVariable Long id) {
        return pathService.getById(id);
    }

    /**
     * 创建路径
     *
     * @param path 路径信息
     * @return 是否成功
     */
    @PostMapping
    public boolean create(@RequestBody Path path) {
        return pathService.save(path);
    }

    /**
     * 更新路径
     *
     * @param path 路径信息
     * @return 是否成功
     */
    @PutMapping
    public boolean update(@RequestBody Path path) {
        return pathService.updateById(path);
    }

    /**
     * 删除路径
     *
     * @param id 路径ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return pathService.removeById(id);
    }
}