package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.LocationDTO;
import org.opentcs.map.application.MapFacadeApplicationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 位置/站点管理
 * 仅提供查询功能，编辑在地图编辑器中完成
 */
@Validated
@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController extends BaseController {

    private final MapFacadeApplicationService mapFacadeApplicationService;

    /**
     * 分页查询位置列表
     * 支持按工厂ID和导航地图ID筛选
     */
    @GetMapping("/list")
    public TableDataInfo<LocationDTO> list(LocationDTO location, PageQuery pageQuery) {
        return mapFacadeApplicationService.listLocations(location, pageQuery);
    }

    /**
     * 根据工厂ID查询位置列表
     * 先查导航地图，再查位置，避免Service层循环依赖
     */
    @GetMapping("/listByFactory/{factoryId}")
    public R<List<LocationDTO>> listByFactory(@PathVariable Long factoryId) {
        return R.ok(mapFacadeApplicationService.listLocationsByFactory(factoryId));
    }

    /**
     * 根据导航地图ID查询位置列表
     */
    @GetMapping("/listByMap/{mapId}")
    public R<List<LocationDTO>> listByMap(@PathVariable Long mapId) {
        return R.ok(mapFacadeApplicationService.listLocationsByMap(mapId));
    }

    /**
     * 根据ID查询位置详情
     */
    @GetMapping("/{id}")
    public R<LocationDTO> getById(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.getLocationById(id));
    }
}
