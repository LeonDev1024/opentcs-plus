package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.map.domain.bo.PlantModelBO;
import org.opentcs.map.service.IMapEditorService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 地图编辑器管理
 * @author lyc
 */
@Validated
@RestController
@RequestMapping("/map/editor")
@RequiredArgsConstructor
public class MapEditorController {

    private final IMapEditorService mapEditorService;

    /**
     * 加载地图模型
     */
    @RequestMapping("/load")
    public R<PlantModelBO> load(String plantModelId) {
        return R.ok(mapEditorService.load(plantModelId));
    }
}
