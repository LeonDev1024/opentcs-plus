package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.map.application.MapFacadeApplicationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导航地图管理
 */
@Validated
@RestController
@RequestMapping("/factory/map")
@RequiredArgsConstructor
public class NavigationMapController extends BaseController {

    private final MapFacadeApplicationService mapFacadeApplicationService;

    /**
     * 查询导航地图列表
     */
    @GetMapping("/list")
    public TableDataInfo<NavigationMapDTO> list(NavigationMapDTO navigationMap, PageQuery pageQuery) {
        return mapFacadeApplicationService.listNavigationMaps(navigationMap, pageQuery);
    }

    /**
     * 根据工厂ID查询所有导航地图
     */
    @GetMapping("/list/{factoryId}")
    public R<List<NavigationMapDTO>> listByFactory(@PathVariable Long factoryId) {
        return R.ok(mapFacadeApplicationService.listNavigationMapsByFactory(factoryId));
    }

    /**
     * 根据ID查询导航地图详情
     */
    @GetMapping("/{id}")
    public R<NavigationMapDTO> getById(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.getNavigationMapById(id));
    }

    /**
     * 根据工厂ID和楼层号查询导航地图
     */
    @GetMapping("/floor/{factoryId}/{floorNumber}")
    public R<NavigationMapDTO> getByFloor(@PathVariable Long factoryId, @PathVariable Integer floorNumber) {
        return R.ok(mapFacadeApplicationService.getNavigationMapByFloor(factoryId, floorNumber));
    }

    /**
     * 创建导航地图
     */
    @PostMapping("/create")
    public R<Boolean> create(@RequestBody NavigationMapDTO navigationMap) {
        return R.ok(mapFacadeApplicationService.createNavigationMap(navigationMap));
    }

    /**
     * 更新导航地图
     */
    @PutMapping("/update")
    public R<Boolean> update(@RequestBody NavigationMapDTO navigationMap) {
        return R.ok(mapFacadeApplicationService.updateNavigationMap(navigationMap));
    }

    /**
     * 删除导航地图
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.deleteNavigationMap(id));
    }
}
