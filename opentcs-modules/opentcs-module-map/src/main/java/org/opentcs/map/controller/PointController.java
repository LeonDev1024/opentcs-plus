package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.Point;
import org.opentcs.map.service.PointService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导航点控制器
 *
 * @author lyc
 */
@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    /**
     * 获取所有导航点
     *
     * @return 导航点列表
     */
    @GetMapping("/list")
    public List<Point> list() {
        return pointService.list();
    }

    /**
     * 根据ID获取导航点
     *
     * @param id 导航点ID
     * @return 导航点信息
     */
    @GetMapping("/{id}")
    public Point get(@PathVariable Long id) {
        return pointService.getById(id);
    }

    /**
     * 创建导航点
     *
     * @param point 导航点信息
     * @return 是否成功
     */
    @PostMapping
    public boolean create(@RequestBody Point point) {
        return pointService.save(point);
    }

    /**
     * 更新导航点
     *
     * @param point 导航点信息
     * @return 是否成功
     */
    @PutMapping
    public boolean update(@RequestBody Point point) {
        return pointService.updateById(point);
    }

    /**
     * 删除导航点
     *
     * @param id 导航点ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return pointService.removeById(id);
    }
}