package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.map.application.MapFacadeApplicationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 导航点查询（只读；编辑在地图编辑器中完成）
 */
@Validated
@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController extends BaseController {

    private final MapFacadeApplicationService mapFacadeApplicationService;

    /**
     * 根据导航地图主键 ID 查询点位列表
     */
    @GetMapping("/listByMap/{mapId}")
    public R<List<PointDTO>> listByMap(@PathVariable Long mapId) {
        return R.ok(mapFacadeApplicationService.listPointsByMap(mapId));
    }
}
