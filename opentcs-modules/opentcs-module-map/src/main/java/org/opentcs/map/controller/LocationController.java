package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.Location;
import org.opentcs.map.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 位置 Controller
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
    public List<Location> getAllLocations() {
        return locationService.list();
    }

    /**
     * 根据ID查询位置
     */
    @GetMapping("/{id}")
    public Location getLocationById(@PathVariable Long id) {
        return locationService.getById(id);
    }

    /**
     * 创建位置
     */
    @PostMapping("/")
    public boolean createLocation(@RequestBody Location location) {
        return locationService.save(location);
    }

    /**
     * 更新位置
     */
    @PutMapping("/")
    public boolean updateLocation(@RequestBody Location location) {
        return locationService.updateById(location);
    }

    /**
     * 删除位置
     */
    @DeleteMapping("/{id}")
    public boolean deleteLocation(@PathVariable Long id) {
        return locationService.removeById(id);
    }
}