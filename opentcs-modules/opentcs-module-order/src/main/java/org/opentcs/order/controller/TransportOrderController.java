package org.opentcs.order.controller;

import lombok.RequiredArgsConstructor;
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
public class TransportOrderController {

    private final TransportOrderService transportOrderService;

    /**
     * 查询所有运输订单
     */
    @GetMapping("/")
    public List<TransportOrder> getAllTransportOrders() {
        return transportOrderService.list();
    }

    /**
     * 根据ID查询运输订单
     */
    @GetMapping("/{id}")
    public TransportOrder getTransportOrderById(@PathVariable Long id) {
        return transportOrderService.getById(id);
    }

    /**
     * 创建运输订单
     */
    @PostMapping("/")
    public boolean createTransportOrder(@RequestBody TransportOrder transportOrder) {
        return transportOrderService.save(transportOrder);
    }

    /**
     * 更新运输订单
     */
    @PutMapping("/")
    public boolean updateTransportOrder(@RequestBody TransportOrder transportOrder) {
        return transportOrderService.updateById(transportOrder);
    }

    /**
     * 删除运输订单
     */
    @DeleteMapping("/{id}")
    public boolean deleteTransportOrder(@PathVariable Long id) {
        return transportOrderService.removeById(id);
    }
}