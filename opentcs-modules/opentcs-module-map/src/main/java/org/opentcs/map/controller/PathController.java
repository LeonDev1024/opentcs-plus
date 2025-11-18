package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
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
public class PathController extends BaseController {

    private final PathService pathService;

    /**
     * 分页查询路径列表
     */
    @GetMapping("/list")
    public TableDataInfo<Path> listPaths(Path path, PageQuery pageQuery) {
        return pathService.selectPagePath(path, pageQuery);
    }

    /**
     * 查询所有路径
     */
    @GetMapping("/")
    public R<List<Path>> getAllPaths() {
        return R.ok(pathService.list());
    }

    /**
     * 根据ID查询路径
     */
    @GetMapping("/{id}")
    public R<Path> getPathById(@PathVariable Long id) {
        return R.ok(pathService.getById(id));
    }

    /**
     * 创建路径
     */
    @PostMapping("/")
    public R<Boolean> createPath(@RequestBody Path path) {
        return R.ok(pathService.save(path));
    }

    /**
     * 更新路径
     */
    @PutMapping("/")
    public R<Boolean> updatePath(@RequestBody Path path) {
        return R.ok(pathService.updateById(path));
    }

    /**
     * 删除路径
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deletePath(@PathVariable Long id) {
        return R.ok(pathService.removeById(id));
    }
}