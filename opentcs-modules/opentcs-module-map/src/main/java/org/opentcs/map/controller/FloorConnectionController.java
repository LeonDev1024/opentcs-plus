package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.map.domain.entity.FloorConnection;
import org.opentcs.map.service.FloorConnectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 跨楼层连接控制器
 *
 * @author lyc
 */
@RestController
@RequestMapping("/floor-connection")
@RequiredArgsConstructor
public class FloorConnectionController {

    private final FloorConnectionService floorConnectionService;

    /**
     * 获取所有跨楼层连接
     *
     * @return 跨楼层连接列表
     */
    @GetMapping("/list")
    public List<FloorConnection> list() {
        return floorConnectionService.list();
    }

    /**
     * 根据ID获取跨楼层连接
     *
     * @param id 跨楼层连接ID
     * @return 跨楼层连接信息
     */
    @GetMapping("/{id}")
    public FloorConnection get(@PathVariable Long id) {
        return floorConnectionService.getById(id);
    }

    /**
     * 创建跨楼层连接
     *
     * @param floorConnection 跨楼层连接信息
     * @return 是否成功
     */
    @PostMapping
    public boolean create(@RequestBody FloorConnection floorConnection) {
        return floorConnectionService.save(floorConnection);
    }

    /**
     * 更新跨楼层连接
     *
     * @param floorConnection 跨楼层连接信息
     * @return 是否成功
     */
    @PutMapping
    public boolean update(@RequestBody FloorConnection floorConnection) {
        return floorConnectionService.updateById(floorConnection);
    }

    /**
     * 删除跨楼层连接
     *
     * @param id 跨楼层连接ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return floorConnectionService.removeById(id);
    }
}