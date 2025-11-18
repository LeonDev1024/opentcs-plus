package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.map.domain.entity.Location;
import org.opentcs.map.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 位置管理
 * @author lyc
 */
@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * 查询所有位置
     */
    @GetMapping("/")
    public R<List<Location>> getAllLocations() {
        return R.ok(locationService.list());
    }

    /**
     * 根据ID查询位置
     */
    @GetMapping("/{id}")
    public R<Location> getLocationById(@PathVariable Long id) {
        return R.ok(locationService.getById(id));
    }

    /**
     * 创建位置
     */
    @PostMapping("/")
    public R<Boolean> createLocation(@RequestBody Location location) {
        return R.ok(locationService.save(location));
    }

    /**
     * 更新位置
     */
    @PutMapping("/")
    public R<Boolean> updateLocation(@RequestBody Location location) {
        return R.ok(locationService.updateById(location));
    }

    /**
     * 删除位置
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteLocation(@PathVariable Long id) {
        return R.ok(locationService.removeById(id));
    }
}