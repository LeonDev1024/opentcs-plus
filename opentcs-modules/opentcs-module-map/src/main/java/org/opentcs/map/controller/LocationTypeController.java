package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
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
    public List<LocationType> getAllLocationTypes() {
        return locationTypeService.list();
    }

    /**
     * 根据ID查询位置类型
     */
    @GetMapping("/{id}")
    public LocationType getLocationTypeById(@PathVariable Long id) {
        return locationTypeService.getById(id);
    }

    /**
     * 创建位置类型
     */
    @PostMapping("/")
    public boolean createLocationType(@RequestBody LocationType locationType) {
        return locationTypeService.save(locationType);
    }

    /**
     * 更新位置类型
     */
    @PutMapping("/")
    public boolean updateLocationType(@RequestBody LocationType locationType) {
        return locationTypeService.updateById(locationType);
    }

    /**
     * 删除位置类型
     */
    @DeleteMapping("/{id}")
    public boolean deleteLocationType(@PathVariable Long id) {
        return locationTypeService.removeById(id);
    }
}