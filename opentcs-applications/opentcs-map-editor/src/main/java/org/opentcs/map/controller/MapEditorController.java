package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.map.domain.dto.MapEditorDTO;
import org.opentcs.map.domain.dto.MapEditorSaveDTO;
import org.opentcs.map.domain.vo.LoadModelVO;
import org.opentcs.map.application.IMapEditorService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public R<MapEditorDTO> load(@RequestBody LoadModelVO loadModelVO) {
        MapEditorDTO result = mapEditorService.load(loadModelVO);
        if (result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "地图模型不存在");
        }
        return R.ok(result);
    }


    @PostMapping("/save")
    public R<Boolean> save(@RequestBody MapEditorSaveDTO saveDTO) {
        return R.ok(mapEditorService.save(saveDTO));
    }

    /**
     * 发布地图
     * @param mapId 地图业务标识（mapId）
     * @return 是否发布成功
     */
    @PostMapping("/publish/{mapId}")
    public R<Boolean> publish(@PathVariable String mapId) {
        return R.ok(mapEditorService.publish(mapId));
    }
}
