package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.Point;
import org.opentcs.map.service.PointService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 点位 Controller
 */
@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    /**
     * 查询所有点位
     */
    @GetMapping("/")
    public List<Point> getAllPoints() {
        return pointService.list();
    }

    /**
     * 根据ID查询点位
     */
    @GetMapping("/{id}")
    public Point getPointById(@PathVariable Long id) {
        return pointService.getById(id);
    }

    /**
     * 创建点位
     */
    @PostMapping("/")
    public boolean createPoint(@RequestBody Point point) {
        return pointService.save(point);
    }

    /**
     * 更新点位
     */
    @PutMapping("/")
    public boolean updatePoint(@RequestBody Point point) {
        return pointService.updateById(point);
    }

    /**
     * 删除点位
     */
    @DeleteMapping("/{id}")
    public boolean deletePoint(@PathVariable Long id) {
        return pointService.removeById(id);
    }
}