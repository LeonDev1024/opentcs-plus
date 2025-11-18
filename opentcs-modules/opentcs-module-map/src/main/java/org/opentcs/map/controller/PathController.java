package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.Path;
import org.opentcs.map.service.PathService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路径管理
 * @author lyc
 */
@RestController
@RequestMapping("/path")
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    /**
     * 查询所有路径
     */
    @GetMapping("/")
    public List<Path> getAllPaths() {
        return pathService.list();
    }

    /**
     * 根据ID查询路径
     */
    @GetMapping("/{id}")
    public Path getPathById(@PathVariable Long id) {
        return pathService.getById(id);
    }

    /**
     * 创建路径
     */
    @PostMapping("/")
    public boolean createPath(@RequestBody Path path) {
        return pathService.save(path);
    }

    /**
     * 更新路径
     */
    @PutMapping("/")
    public boolean updatePath(@RequestBody Path path) {
        return pathService.updateById(path);
    }

    /**
     * 删除路径
     */
    @DeleteMapping("/{id}")
    public boolean deletePath(@PathVariable Long id) {
        return pathService.removeById(id);
    }
}