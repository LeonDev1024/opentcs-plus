package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.persistence.entity.PathEntity;
import org.opentcs.kernel.persistence.service.NavigationMapDomainService;
import org.opentcs.kernel.persistence.service.PathDomainService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 路径管理
 * 仅提供查询功能，编辑在地图编辑器中完成
 */
@Validated
@RestController
@RequestMapping("/path")
@RequiredArgsConstructor
public class PathController extends BaseController {

    private final PathDomainService pathDomainService;
    private final NavigationMapDomainService navigationMapDomainService;

    /**
     * 分页查询路径列表
     * 支持按工厂ID和导航地图ID筛选
     */
    @GetMapping("/list")
    public TableDataInfo<PathDTO> list(PathEntity path, PageQuery pageQuery) {
        return pathDomainService.selectPageDTO(path, pageQuery);
    }

    /**
     * 根据工厂ID查询路径列表
     * 先查导航地图，再查路径，避免Service层循环依赖
     */
    @GetMapping("/listByFactory/{factoryId}")
    public R<List<PathDTO>> listByFactory(@PathVariable Long factoryId) {
        // 先查询该工厂下的所有导航地图
        List<NavigationMapDTO> maps = navigationMapDomainService.selectByFactoryModelId(factoryId);
        if (maps == null || maps.isEmpty()) {
            return R.ok(List.of());
        }
        // 获取所有地图ID
        List<Long> mapIds = maps.stream()
                .map(NavigationMapDTO::getId)
                .collect(Collectors.toList());
        // 查询所有属于这些地图的路径
        return R.ok(pathDomainService.listByMapIdsDTO(mapIds));
    }

    /**
     * 根据导航地图ID查询路径列表
     */
    @GetMapping("/listByMap/{mapId}")
    public R<List<PathDTO>> listByMap(@PathVariable Long mapId) {
        return R.ok(pathDomainService.listByMapDTO(mapId));
    }

    /**
     * 根据ID查询路径详情
     */
    @GetMapping("/{id}")
    public R<PathDTO> getById(@PathVariable Long id) {
        return R.ok(pathDomainService.getByIdDTO(id));
    }
}
