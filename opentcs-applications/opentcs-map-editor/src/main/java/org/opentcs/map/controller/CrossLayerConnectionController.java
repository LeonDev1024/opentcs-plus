package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.api.dto.CrossLayerConnectionDTO;
import org.opentcs.map.application.MapFacadeApplicationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 跨层连接管理
 */
@Validated
@RestController
@RequestMapping("/factory/connection")
@RequiredArgsConstructor
public class CrossLayerConnectionController extends BaseController {

    private final MapFacadeApplicationService mapFacadeApplicationService;

    /**
     * 查询跨层连接列表
     */
    @GetMapping("/list")
    public TableDataInfo<CrossLayerConnectionDTO> list(CrossLayerConnectionDTO connection, PageQuery pageQuery) {
        return mapFacadeApplicationService.listConnections(connection, pageQuery);
    }

    /**
     * 根据工厂ID查询所有跨层连接
     */
    @GetMapping("/list/{factoryId}")
    public R<List<CrossLayerConnectionDTO>> listByFactory(@PathVariable Long factoryId) {
        return R.ok(mapFacadeApplicationService.listConnectionsByFactory(factoryId));
    }

    /**
     * 查询可用的跨层连接
     */
    @GetMapping("/available/{factoryId}")
    public R<List<CrossLayerConnectionDTO>> availableConnections(@PathVariable Long factoryId) {
        return R.ok(mapFacadeApplicationService.listAvailableConnections(factoryId));
    }

    /**
     * 根据ID查询跨层连接详情
     */
    @GetMapping("/{id}")
    public R<CrossLayerConnectionDTO> getById(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.getConnectionById(id));
    }

    /**
     * 创建跨层连接
     */
    @PostMapping("/create")
    public R<Boolean> create(@RequestBody CrossLayerConnectionDTO connection) {
        return R.ok(mapFacadeApplicationService.createConnection(connection));
    }

    /**
     * 更新跨层连接
     */
    @PutMapping("/update")
    public R<Boolean> update(@RequestBody CrossLayerConnectionDTO connection) {
        return R.ok(mapFacadeApplicationService.updateConnection(connection));
    }

    /**
     * 删除跨层连接
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(mapFacadeApplicationService.deleteConnection(id));
    }

    /**
     * 预留电梯
     */
    @PostMapping("/{connectionId}/reserve")
    public R<Boolean> reserveElevator(@PathVariable String connectionId, @RequestParam Long vehicleId) {
        return R.ok(mapFacadeApplicationService.reserveElevator(connectionId, vehicleId));
    }

    /**
     * 释放电梯
     */
    @PostMapping("/{connectionId}/release")
    public R<Boolean> releaseElevator(@PathVariable String connectionId, @RequestParam Long vehicleId) {
        return R.ok(mapFacadeApplicationService.releaseElevator(connectionId, vehicleId));
    }
}
