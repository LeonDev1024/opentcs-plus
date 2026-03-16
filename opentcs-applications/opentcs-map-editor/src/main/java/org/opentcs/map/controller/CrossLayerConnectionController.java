package org.opentcs.map.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.persistence.entity.CrossLayerConnectionEntity;
import org.opentcs.map.service.CrossLayerConnectionService;
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

    private final CrossLayerConnectionService crossLayerConnectionService;

    /**
     * 查询跨层连接列表
     */
    @GetMapping("/list")
    public TableDataInfo<CrossLayerConnectionEntity> list(CrossLayerConnectionEntity connection, PageQuery pageQuery) {
        return crossLayerConnectionService.selectPageConnection(connection, pageQuery);
    }

    /**
     * 根据工厂ID查询所有跨层连接
     */
    @GetMapping("/list/{factoryId}")
    public R<List<CrossLayerConnectionEntity>> listByFactory(@PathVariable Long factoryId) {
        return R.ok(crossLayerConnectionService.selectByFactoryModelId(factoryId));
    }

    /**
     * 查询可用的跨层连接
     */
    @GetMapping("/available/{factoryId}")
    public R<List<CrossLayerConnectionEntity>> availableConnections(@PathVariable Long factoryId) {
        return R.ok(crossLayerConnectionService.selectAvailableConnections(factoryId));
    }

    /**
     * 根据ID查询跨层连接详情
     */
    @GetMapping("/{id}")
    public R<CrossLayerConnectionEntity> getById(@PathVariable Long id) {
        return R.ok(crossLayerConnectionService.getById(id));
    }

    /**
     * 创建跨层连接
     */
    @PostMapping("/create")
    public R<Boolean> create(@RequestBody CrossLayerConnectionEntity connection) {
        return R.ok(crossLayerConnectionService.createConnection(connection));
    }

    /**
     * 更新跨层连接
     */
    @PutMapping("/update")
    public R<Boolean> update(@RequestBody CrossLayerConnectionEntity connection) {
        return R.ok(crossLayerConnectionService.updateConnection(connection));
    }

    /**
     * 删除跨层连接
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        return R.ok(crossLayerConnectionService.deleteConnection(id));
    }

    /**
     * 预留电梯
     */
    @PostMapping("/{connectionId}/reserve")
    public R<Boolean> reserveElevator(@PathVariable String connectionId, @RequestParam Long vehicleId) {
        return R.ok(crossLayerConnectionService.reserveElevator(connectionId, vehicleId));
    }

    /**
     * 释放电梯
     */
    @PostMapping("/{connectionId}/release")
    public R<Boolean> releaseElevator(@PathVariable String connectionId, @RequestParam Long vehicleId) {
        return R.ok(crossLayerConnectionService.releaseElevator(connectionId, vehicleId));
    }
}
