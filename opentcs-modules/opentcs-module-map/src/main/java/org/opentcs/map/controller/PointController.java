package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.map.domain.entity.Point;
import org.opentcs.map.service.PointService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 点位管理
 * @author lyc
 */
@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController extends BaseController {

    private final PointService pointService;

    /**
     * 分页查询点位列表
     */
    @GetMapping("/list")
    public TableDataInfo<Point> listPoints(Point point, PageQuery pageQuery) {
        return pointService.selectPagePoint(point, pageQuery);
    }

    /**
     * 查询所有点位
     */
    @GetMapping("/")
    public R<List<Point>> getAllPoints() {
        return R.ok(pointService.list());
    }

    /**
     * 根据ID查询点位
     */
    @GetMapping("/{id}")
    public R<Point> getPointById(@PathVariable Long id) {
        return R.ok(pointService.getById(id));
    }

    /**
     * 创建点位
     */
    @PostMapping("/")
    public R<Boolean> createPoint(@RequestBody Point point) {
        return R.ok(pointService.save(point));
    }

    /**
     * 更新点位
     */
    @PutMapping("/")
    public R<Boolean> updatePoint(@RequestBody Point point) {
        return R.ok(pointService.updateById(point));
    }

    /**
     * 删除点位
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deletePoint(@PathVariable Long id) {
        return R.ok(pointService.removeById(id));
    }
}