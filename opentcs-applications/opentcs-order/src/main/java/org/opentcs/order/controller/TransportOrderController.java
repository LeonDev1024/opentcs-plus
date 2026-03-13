package org.opentcs.order.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.kernel.persistence.entity.TransportOrderEntity;
import org.opentcs.order.service.TransportOrderService;
import org.opentcs.order.application.TransportOrderApplicationService;
import org.opentcs.kernel.api.dto.TransportOrderDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 运输订单 Controller
 */
@RestController
@RequestMapping("/transport-order")
@RequiredArgsConstructor
public class TransportOrderController extends BaseController {

    private final TransportOrderService transportOrderService;
    private final TransportOrderApplicationService transportOrderApplicationService;

    /**
     * 分页查询运输订单列表
     */
    @GetMapping("/list")
    public TableDataInfo<TransportOrderEntity> listTransportOrders(TransportOrderEntity transportOrder, PageQuery pageQuery) {
        return transportOrderService.selectPageTransportOrder(transportOrder, pageQuery);
    }

    /**
     * 查询所有运输订单
     */
    @GetMapping("/")
    public R<List<TransportOrderEntity>> getAllTransportOrders() {
        return R.ok(transportOrderService.list());
    }

    /**
     * 根据ID查询运输订单
     */
    @GetMapping("/{id}")
    public R<TransportOrderEntity> getTransportOrderById(@PathVariable Long id) {
        return R.ok(transportOrderService.getById(id));
    }

    /**
     * 创建运输订单
     */
    @PostMapping("/")
    public R<Boolean> createTransportOrder(@RequestBody TransportOrderEntity transportOrder) {
        return R.ok(transportOrderApplicationService.createTransportOrder(transportOrder));
    }

    /**
     * 批量创建运输订单
     */
    @PostMapping("/batch")
    public R<Boolean> batchCreateTransportOrder(@RequestBody List<TransportOrderEntity> transportOrders) {
        return R.ok(transportOrderService.batchCreateTransportOrder(transportOrders));
    }

    /**
     * 更新运输订单
     */
    @PutMapping("/")
    public R<Boolean> updateTransportOrder(@RequestBody TransportOrderEntity transportOrder) {
        return R.ok(transportOrderService.updateById(transportOrder));
    }

    /**
     * 删除运输订单
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteTransportOrder(@PathVariable Long id) {
        return R.ok(transportOrderService.removeById(id));
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
        return R.ok(transportOrderService.getOrderStatus(id));
    }

    /**
     * 获取订单执行历史
     */
    @GetMapping("/history/{id}")
    public TableDataInfo<Map<String, Object>> getOrderHistory(@PathVariable Long id, PageQuery pageQuery) {
        return transportOrderService.getOrderHistory(id, pageQuery);
    }

    /**
     * 优化订单调度
     */
    @PostMapping("/optimize")
    public R<Map<String, Object>> optimizeOrderDispatch(@RequestBody List<Long> orderIds) {
        return R.ok(transportOrderService.optimizeOrderDispatch(orderIds));
    }

    /**
     * 获取订单统计数据
     */
    @GetMapping("/statistics")
    public R<Map<String, Object>> getOrderStatistics() {
        return R.ok(transportOrderApplicationService.getOrderStatistics());
    }
}