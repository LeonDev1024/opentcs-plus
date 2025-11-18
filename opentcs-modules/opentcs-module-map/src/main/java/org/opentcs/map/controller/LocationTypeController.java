package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.map.domain.entity.LocationType;
import org.opentcs.map.service.LocationTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 位置类型管理
 * @author lyc
 */
@RestController
@RequestMapping("/location-type")
@RequiredArgsConstructor
public class LocationTypeController {

    private final LocationTypeService locationTypeService;

    /**
     * 查询所有位置类型
     */
    @GetMapping("/")
    public R<List<LocationType>> getAllLocationTypes() {
        return R.ok(locationTypeService.list());
    }

    /**
     * 根据ID查询位置类型
     */
    @GetMapping("/{id}")
    public R<LocationType> getLocationTypeById(@PathVariable Long id) {
        return R.ok(locationTypeService.getById(id));
    }

    /**
     * 创建位置类型
     */
    @PostMapping("/")
    public R<Boolean> createLocationType(@RequestBody LocationType locationType) {
        return R.ok(locationTypeService.save(locationType));
    }

    /**
     * 更新位置类型
     */
    @PutMapping("/")
    public R<Boolean> updateLocationType(@RequestBody LocationType locationType) {
        return R.ok(locationTypeService.updateById(locationType));
    }

    /**
     * 删除位置类型
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteLocationType(@PathVariable Long id) {
        return R.ok(locationTypeService.removeById(id));
    }
}