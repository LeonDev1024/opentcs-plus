package org.opentcs.kernel.application;

import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.order.OrderState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 运输订单注册表（内存实现）
 */
public class TransportOrderRegistry {

    private final Map<String, TransportOrder> orders = new ConcurrentHashMap<>();

    /**
     * 创建运输订单
     */
    public TransportOrder createOrder(TransportOrder order) {
        orders.put(order.getOrderId(), order);
        return order;
    }

    /**
     * 取消订单
     */
    public void cancelOrder(String orderId) {
        TransportOrder order = orders.get(orderId);
        if (order != null) {
            order.cancel();
        }
    }

    /**
     * 完成订单
     */
    public void completeOrder(String orderId) {
        TransportOrder order = orders.get(orderId);
        if (order != null) {
            order.complete();
        }
    }

    /**
     * 获取订单
     */
    public TransportOrder getOrder(String orderId) {
        return orders.get(orderId);
    }

    /**
     * 获取所有订单
     */
    public List<TransportOrder> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    /**
     * 获取等待中的订单
     */
    public List<TransportOrder> getWaitingOrders() {
        return orders.values().stream()
                .filter(o -> o.getState() == OrderState.RAW)
                .collect(Collectors.toList());
    }

    /**
     * 获取已分配的订单
     */
    public List<TransportOrder> getAssignedOrders() {
        return orders.values().stream()
                .filter(o -> o.getState() == OrderState.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * 检查订单是否存在
     */
    public boolean orderExists(String orderId) {
        return orders.containsKey(orderId);
    }

    /**
     * 分配订单给车辆
     */
    public void assignOrder(String orderId, String vehicleId) {
        TransportOrder order = orders.get(orderId);
        if (order != null) {
            order.assignTo(vehicleId);
        }
    }

    /**
     * 释放订单（车辆完成或取消）
     */
    public void releaseOrder(String orderId) {
        TransportOrder order = orders.get(orderId);
        if (order != null) {
            order.release();
        }
    }

    /**
     * 获取订单数量
     */
    public int size() {
        return orders.size();
    }

    /**
     * 清空所有订单
     */
    public void clear() {
        orders.clear();
    }
}
