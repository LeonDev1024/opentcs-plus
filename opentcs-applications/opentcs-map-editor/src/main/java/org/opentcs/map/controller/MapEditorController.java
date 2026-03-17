package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.map.domain.bo.MapEditorBO;
import org.opentcs.map.domain.vo.LoadModelVO;
import org.opentcs.map.application.IMapEditorService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


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
    @PostMapping("/load")
    public R<MapEditorBO> load(@RequestBody LoadModelVO loadModelVO) {
        MapEditorBO result = mapEditorService.load(loadModelVO);
        if (result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "地图模型不存在");
        }
        return R.ok(result);
    }


    @PostMapping("/save")
    public R<Boolean> save(@RequestBody MapEditorBO mapEditorBO) {
        return R.ok(mapEditorService.save(mapEditorBO));
    }
}
