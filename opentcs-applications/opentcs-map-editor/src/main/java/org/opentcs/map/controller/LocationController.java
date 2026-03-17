package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.LocationDTO;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.persistence.entity.LocationEntity;
import org.opentcs.kernel.persistence.service.LocationDomainService;
import org.opentcs.kernel.persistence.service.NavigationMapDomainService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 位置/站点管理
 * 仅提供查询功能，编辑在地图编辑器中完成
 */
@Validated
@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController extends BaseController {

    private final LocationDomainService locationDomainService;
    private final NavigationMapDomainService navigationMapDomainService;

    /**
     * 分页查询位置列表
     * 支持按工厂ID和导航地图ID筛选
     */
    @GetMapping("/list")
    public TableDataInfo<LocationDTO> list(LocationEntity location, PageQuery pageQuery) {
        return locationDomainService.selectPageDTO(location, pageQuery);
    }

    /**
     * 根据工厂ID查询位置列表
     * 先查导航地图，再查位置，避免Service层循环依赖
     */
    @GetMapping("/listByFactory/{factoryId}")
    public R<List<LocationDTO>> listByFactory(@PathVariable Long factoryId) {
        // 先查询该工厂下的所有导航地图
        List<NavigationMapDTO> maps = navigationMapDomainService.selectByFactoryModelId(factoryId);
        if (maps == null || maps.isEmpty()) {
            return R.ok(List.of());
        }
        // 获取所有地图ID
        List<Long> mapIds = maps.stream()
                .map(NavigationMapDTO::getId)
                .collect(Collectors.toList());
        // 查询所有属于这些地图的位置
        return R.ok(locationDomainService.selectByMapIdsDTO(mapIds));
    }

    /**
     * 根据导航地图ID查询位置列表
     */
    @GetMapping("/listByMap/{mapId}")
    public R<List<LocationDTO>> listByMap(@PathVariable Long mapId) {
        return R.ok(locationDomainService.selectByNavigationMapIdDTO(mapId));
    }

    /**
     * 根据ID查询位置详情
     */
    @GetMapping("/{id}")
    public R<LocationDTO> getById(@PathVariable Long id) {
        return R.ok(locationDomainService.selectByIdDTO(id));
    }
}
