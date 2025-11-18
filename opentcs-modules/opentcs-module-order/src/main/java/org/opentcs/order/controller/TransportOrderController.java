package org.opentcs.order.controller;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.core.domain.R;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.web.core.BaseController;
import org.opentcs.order.domain.entity.TransportOrder;
import org.opentcs.order.service.TransportOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 运输订单 Controller
 */
@RestController
@RequestMapping("/transport-order")
@RequiredArgsConstructor
public class TransportOrderController extends BaseController {

    private final TransportOrderService transportOrderService;

    /**
     * 分页查询运输订单列表
     */
    @GetMapping("/list")
    public TableDataInfo<TransportOrder> listTransportOrders(TransportOrder transportOrder, PageQuery pageQuery) {
        return transportOrderService.selectPageTransportOrder(transportOrder, pageQuery);
    }

    /**
     * 查询所有运输订单
     */
    @GetMapping("/")
    public R<List<TransportOrder>> getAllTransportOrders() {
        return R.ok(transportOrderService.list());
    }

    /**
     * 根据ID查询运输订单
     */
    @GetMapping("/{id}")
    public R<TransportOrder> getTransportOrderById(@PathVariable Long id) {
        return R.ok(transportOrderService.getById(id));
    }

    /**
     * 创建运输订单
     */
    @PostMapping("/")
    public R<Boolean> createTransportOrder(@RequestBody TransportOrder transportOrder) {
        return R.ok(transportOrderService.save(transportOrder));
    }

    /**
     * 更新运输订单
     */
    @PutMapping("/")
    public R<Boolean> updateTransportOrder(@RequestBody TransportOrder transportOrder) {
        return R.ok(transportOrderService.updateById(transportOrder));
    }

    /**
     * 删除运输订单
     */
    @DeleteMapping("/{id}")
    public R<Boolean> deleteTransportOrder(@PathVariable Long id) {
        return R.ok(transportOrderService.removeById(id));
    }
}