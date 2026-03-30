package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.map.application.MapFacadeApplicationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路径管理
 * 仅提供查询功能，编辑在地图编辑器中完成
 */
@Validated
@RestController
@RequestMapping("/path")
@RequiredArgsConstructor
public class PathController extends BaseController {

    private final MapFacadeApplicationService mapFacadeApplicationService;

    /**
     * 分页查询路径列表
     * 支持按工厂ID和导航地图ID筛选
     */
    @GetMapping("/list")
    public TableDataInfo<PathDTO> list(PathDTO path, PageQuery pageQuery) {
        return mapFacadeApplicationService.listPaths(path, pageQuery);
    }

    /**
     * 根据工厂ID查询路径列表
     * 先查导航地图，再查路径，避免Service层循环依赖
     */
    @GetMapping("/listByFactory/{factoryId}")
    public R<List<PathDTO>> listByFactory(@PathVariable Long factoryId) {
        return R.ok(mapFacadeApplicationService.listPathsByFactory(factoryId));
    }

    /**
     * 根据导航地图ID查询路径列表
     */
    @GetMapping("/listByMap/{mapId}")
    public R<List<PathDTO>> listByMap(@PathVariable Long mapId) {
        return R.ok(mapFacadeApplicationService.listPathsByMap(mapId));
    }

    /**
     * 根据ID查询路径详情
     */
    @GetMapping("/{id}")
    public R<PathDTO> getById(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.getPathById(id));
    }
}
