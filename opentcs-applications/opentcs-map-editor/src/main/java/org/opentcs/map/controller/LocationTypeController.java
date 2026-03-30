package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.LocationTypeDTO;
import org.opentcs.map.application.MapFacadeApplicationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 位置类型管理
 * @author lyc
 */
@Validated
@RestController
@RequestMapping("/map/locationType")
@RequiredArgsConstructor
public class LocationTypeController extends BaseController {

    private final MapFacadeApplicationService mapFacadeApplicationService;

    /**
     * 查询所有位置类型
     */
    @GetMapping("/list")
    public TableDataInfo<LocationTypeDTO> list(LocationTypeDTO locationType, PageQuery pageQuery) {
        return mapFacadeApplicationService.listLocationTypes(locationType, pageQuery);
    }

    /**
     * 根据ID查询位置类型
     */
    @GetMapping("/{id}")
    public R<LocationTypeDTO> getLocationTypeById(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.getLocationTypeById(id));
    }

    /**
     * 获取所有位置类型（不分页）
     */
    @GetMapping("/all")
    public R<List<LocationTypeDTO>> getAllLocationTypes() {
        return R.ok(mapFacadeApplicationService.listAllLocationTypes());
    }

    /**
     * 创建位置类型
     */
    @PostMapping("/create")
    public R<Boolean> createLocationType(@RequestBody LocationTypeDTO locationType) {
        return R.ok(mapFacadeApplicationService.createLocationType(locationType));
    }

    /**
     * 更新位置类型
     */
    @PutMapping("/update")
    public R<Boolean> updateLocationType(@RequestBody LocationTypeDTO locationType) {
        return R.ok(mapFacadeApplicationService.updateLocationType(locationType));
    }

    /**
     * 删除位置类型
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteLocationType(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.deleteLocationType(id));
    }
}