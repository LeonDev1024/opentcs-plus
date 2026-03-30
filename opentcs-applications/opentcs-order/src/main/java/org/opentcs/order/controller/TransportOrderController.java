package org.opentcs.order.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.order.persistence.entity.TransportOrderEntity;
import org.opentcs.order.persistence.service.TransportOrderDomainService;
import org.opentcs.order.application.TransportOrderApplicationService;
import org.opentcs.kernel.api.dto.TransportOrderDTO;
import org.opentcs.kernel.api.dto.TransportOrderEntityDTO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 运输订单 Controller
 */
@RestController
@RequestMapping("/transport-order")
@RequiredArgsConstructor
public class TransportOrderController extends BaseController {

    private final TransportOrderDomainService transportOrderDomainService;
    private final TransportOrderApplicationService transportOrderApplicationService;

    /**
     * 分页查询运输订单列表
     */
    @GetMapping("/list")
    public TableDataInfo<TransportOrderEntityDTO> listTransportOrders(TransportOrderEntityDTO transportOrder, PageQuery pageQuery) {
        TableDataInfo<TransportOrderEntity> table = transportOrderDomainService.selectPageTransportOrder(toEntity(transportOrder), pageQuery);
        TableDataInfo<TransportOrderEntityDTO> result = new TableDataInfo<>();
        result.setTotal(table.getTotal());
        result.setCode(table.getCode());
        result.setMsg(table.getMsg());
        result.setRows(table.getRows() == null ? new ArrayList<>() : table.getRows().stream().map(this::toDTO).collect(Collectors.toList()));
        return result;
    }

    /**
     * 查询所有运输订单
     */
    @GetMapping("/")
    public R<List<TransportOrderEntityDTO>> getAllTransportOrders() {
        return R.ok(transportOrderDomainService.list().stream().map(this::toDTO).collect(Collectors.toList()));
    }

    /**
     * 根据ID查询运输订单
     */
    @GetMapping("/{id}")
    public R<TransportOrderEntityDTO> getTransportOrderById(@PathVariable Long id) {
        return R.ok(toDTO(transportOrderDomainService.getById(id)));
    }

    /**
     * 创建运输订单
     */
    @PostMapping("/")
    public R<Boolean> createTransportOrder(@RequestBody TransportOrderEntityDTO transportOrder) {
        return R.ok(transportOrderApplicationService.createTransportOrder(transportOrder));
    }

    /**
     * 批量创建运输订单
     */
    @PostMapping("/batch")
    public R<Boolean> batchCreateTransportOrder(@RequestBody List<TransportOrderEntityDTO> transportOrders) {
        return R.ok(transportOrderDomainService.batchCreateTransportOrder(
            transportOrders.stream().map(this::toEntity).collect(Collectors.toList())
        ));
    }

    /**
     * 更新运输订单
     */
    @PutMapping("/")
    public R<Boolean> updateTransportOrder(@RequestBody TransportOrderEntityDTO transportOrder) {
        return R.ok(transportOrderDomainService.updateById(toEntity(transportOrder)));
    }

    /**
     * 删除运输订单
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteTransportOrder(@PathVariable Long id) {
        return R.ok(transportOrderDomainService.removeById(id));
    }

    /**
     * 取消运输订单
     */
    @PostMapping("/cancel/{id}")
    public R<Boolean> cancelTransportOrder(@PathVariable Long id) {
        return R.ok(transportOrderApplicationService.cancelTransportOrder(id));
    }

    /**
     * 分配车辆
     */
    @PostMapping("/assign/{id}")
    public R<Boolean> assignVehicle(@PathVariable Long id, @RequestParam String vehicleId) {
        return R.ok(transportOrderApplicationService.assignVehicle(id, vehicleId));
    }

    /**
     * 获取订单运行时状态（从内核）
     */
    @GetMapping("/runtime/{orderNo}")
    public R<TransportOrderDTO> getOrderRuntimeStatus(@PathVariable String orderNo) {
        return R.ok(transportOrderApplicationService.getOrderRuntimeStatus(orderNo));
    }

    /**
     * 获取所有订单运行时状态（从内核）
     */
    @GetMapping("/runtime/")
    public R<List<TransportOrderDTO>> getAllOrderRuntimeStatus() {
        return R.ok(transportOrderApplicationService.getAllOrderRuntimeStatus());
    }

    /**
     * 获取等待中的订单
     */
    @GetMapping("/runtime/waiting")
    public R<List<TransportOrderDTO>> getWaitingOrders() {
        return R.ok(transportOrderApplicationService.getWaitingOrders());
    }

    /**
     * 获取已分配的订单
     */
    @GetMapping("/runtime/assigned")
    public R<List<TransportOrderDTO>> getAssignedOrders() {
        return R.ok(transportOrderApplicationService.getAssignedOrders());
    }

    /**
     * 获取订单执行状态
     */
    @GetMapping("/status/{id}")
    public R<Map<String, Object>> getOrderStatus(@PathVariable Long id) {
        return R.ok(transportOrderDomainService.getOrderStatus(id));
    }

    /**
     * 获取订单执行历史
     */
    @GetMapping("/history/{id}")
    public TableDataInfo<Map<String, Object>> getOrderHistory(@PathVariable Long id, PageQuery pageQuery) {
        return transportOrderDomainService.getOrderHistory(id, pageQuery);
    }

    /**
     * 优化订单调度
     */
    @PostMapping("/optimize")
    public R<Map<String, Object>> optimizeOrderDispatch(@RequestBody List<Long> orderIds) {
        return R.ok(transportOrderDomainService.optimizeOrderDispatch(orderIds));
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/statistics")
    public R<Map<String, Object>> getOrderStatistics() {
        return R.ok(transportOrderApplicationService.getOrderStatistics());
    }

    private TransportOrderEntity toEntity(TransportOrderEntityDTO dto) {
        TransportOrderEntity entity = new TransportOrderEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setOrderNo(dto.getOrderNo());
        entity.setState(dto.getState());
        entity.setIntendedVehicle(dto.getIntendedVehicle());
        entity.setProcessingVehicle(dto.getProcessingVehicle());
        entity.setDestinations(dto.getDestinations());
        entity.setCreationTime(dto.getCreationTime());
        entity.setFinishedTime(dto.getFinishedTime());
        entity.setDeadline(dto.getDeadline());
        entity.setProperties(dto.getProperties());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }

    private TransportOrderEntityDTO toDTO(TransportOrderEntity entity) {
        if (entity == null) {
            return null;
        }
        TransportOrderEntityDTO dto = new TransportOrderEntityDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setOrderNo(entity.getOrderNo());
        dto.setState(entity.getState());
        dto.setIntendedVehicle(entity.getIntendedVehicle());
        dto.setProcessingVehicle(entity.getProcessingVehicle());
        dto.setDestinations(entity.getDestinations());
        dto.setCreationTime(entity.getCreationTime());
        dto.setFinishedTime(entity.getFinishedTime());
        dto.setDeadline(entity.getDeadline());
        dto.setProperties(entity.getProperties());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}