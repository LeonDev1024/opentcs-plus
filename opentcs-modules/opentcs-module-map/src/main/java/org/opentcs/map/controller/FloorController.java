package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.Floor;
import org.opentcs.map.service.FloorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 楼层控制器
 *
 * @author lyc
 */
@RestController
@RequestMapping("/floor")
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;

    /**
     * 获取所有楼层
     *
     * @return 楼层列表
     */
    @GetMapping("/list")
    public List<Floor> list() {
        return floorService.list();
    }

    /**
     * 根据ID获取楼层
     *
     * @param id 楼层ID
     * @return 楼层信息
     */
    @GetMapping("/{id}")
    public Floor get(@PathVariable Long id) {
        return floorService.getById(id);
    }

    /**
     * 创建楼层
     *
     * @param floor 楼层信息
     * @return 是否成功
     */
    @PostMapping
    public boolean create(@RequestBody Floor floor) {
        return floorService.save(floor);
    }

    /**
     * 更新楼层
     *
     * @param floor 楼层信息
     * @return 是否成功
     */
    @PutMapping
    public boolean update(@RequestBody Floor floor) {
        return floorService.updateById(floor);
    }

    /**
     * 删除楼层
     *
     * @param id 楼层ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return floorService.removeById(id);
    }
}