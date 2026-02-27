package org.opentcs.monitor.service;

import org.opentcs.order.service.TransportOrderService;
import org.opentcs.order.domain.entity.TransportOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 订单监控服务
 */
public class OrderMonitorService {
    private boolean running;
    private ScheduledExecutorService executorService;
    private Map<String, Object> orderStatus;
    private TransportOrderService transportOrderService;

    public OrderMonitorService() {
        this.orderStatus = new HashMap<>();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        // 这里应该注入 transportOrderService，暂时使用 null
        this.transportOrderService = null;
    }

    /**
     * 启动监控服务
     */
    public void start() {
        running = true;
        // 每3秒采集一次订单状态
        executorService.scheduleAtFixedRate(this::collectOrderStatus, 0, 3, TimeUnit.SECONDS);
    }

    /**
     * 停止监控服务
     */
    public void stop() {
        running = false;
        executorService.shutdown();
    }

    /**
     * 采集订单状态
     */
    private void collectOrderStatus() {
        if (!running) {
            return;
        }

        if (transportOrderService != null) {
            // 从订单服务获取所有订单状态
            List<TransportOrder> orders = transportOrderService.getAllOrders();
            Map<String, Object> orderMap = new HashMap<>();

            for (TransportOrder order : orders) {
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("id", order.getId());
                orderInfo.put("orderId", order.getOrderId());
                orderInfo.put("state", order.getState());
                orderInfo.put("creationTime", order.getCreationTime());
                orderInfo.put("startTime", order.getStartTime());
                orderInfo.put("completionTime", order.getCompletionTime());
                orderInfo.put("priority", order.getPriority());
                orderInfo.put("destination", order.getDestination());
                orderInfo.put("vehicleId", order.getVehicleId());
                orderInfo.put("progress", order.getProgress());

                orderMap.put(order.getOrderId(), orderInfo);
            }

            orderStatus.put("orders", orderMap);
        } else {
            // 模拟订单数据
            simulateOrderData();
        }

        // 计算订单统计数据
        calculateOrderStatistics();
    }

    /**
     * 模拟订单数据
     */
    private void simulateOrderData() {
        Map<String, Object> orderMap = new HashMap<>();

        // 模拟订单1
        Map<String, Object> order1 = new HashMap<>();
        order1.put("id", 1);
        order1.put("orderId", "Order-001");
        order1.put("state", "IN_EXECUTION");
        order1.put("creationTime", "2024-01-01T10:00:00");
        order1.put("startTime", "2024-01-01T10:01:00");
        order1.put("completionTime", null);
        order1.put("priority", 1);
        order1.put("destination", "Point-B");
        order1.put("vehicleId", "AGV-001");
        order1.put("progress", 50);
        orderMap.put("Order-001", order1);

        // 模拟订单2
        Map<String, Object> order2 = new HashMap<>();
        order2.put("id", 2);
        order2.put("orderId", "Order-002");
        order2.put("state", "PENDING");
        order2.put("creationTime", "2024-01-01T10:05:00");
        order2.put("startTime", null);
        order2.put("completionTime", null);
        order2.put("priority", 2);
        order2.put("destination", "Point-D");
        order2.put("vehicleId", null);
        order2.put("progress", 0);
        orderMap.put("Order-002", order2);

        // 模拟订单3
        Map<String, Object> order3 = new HashMap<>();
        order3.put("id", 3);
        order3.put("orderId", "Order-003");
        order3.put("state", "FINISHED");
        order3.put("creationTime", "2024-01-01T09:30:00");
        order3.put("startTime", "2024-01-01T09:31:00");
        order3.put("completionTime", "2024-01-01T09:45:00");
        order3.put("priority", 1);
        order3.put("destination", "Point-C");
        order3.put("vehicleId", "AGV-002");
        order3.put("progress", 100);
        orderMap.put("Order-003", order3);

        orderStatus.put("orders", orderMap);
    }

    /**
     * 计算订单统计数据
     */
    private void calculateOrderStatistics() {
        Map<String, Object> orders = (Map<String, Object>) orderStatus.getOrDefault("orders", new HashMap<>());
        int totalOrders = orders.size();
        int pendingOrders = 0;
        int inExecutionOrders = 0;
        int finishedOrders = 0;
        int failedOrders = 0;
        int cancelledOrders = 0;

        for (Object orderObj : orders.values()) {
            Map<String, Object> order = (Map<String, Object>) orderObj;
            String state = (String) order.get("state");

            switch (state) {
                case "PENDING":
                    pendingOrders++;
                    break;
                case "IN_EXECUTION":
                    inExecutionOrders++;
                    break;
                case "FINISHED":
                    finishedOrders++;
                    break;
                case "FAILED":
                    failedOrders++;
                    break;
                case "CANCELLED":
                    cancelledOrders++;
                    break;
            }
        }

        double completionRate = totalOrders > 0 ? (double) finishedOrders / totalOrders * 100 : 0;

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalOrders", totalOrders);
        statistics.put("pendingOrders", pendingOrders);
        statistics.put("inExecutionOrders", inExecutionOrders);
        statistics.put("finishedOrders", finishedOrders);
        statistics.put("failedOrders", failedOrders);
        statistics.put("cancelledOrders", cancelledOrders);
        statistics.put("completionRate", completionRate);

        orderStatus.put("statistics", statistics);
    }

    /**
     * 获取订单状态
     * @return 订单状态
     */
    public Map<String, Object> getOrderStatus() {
        return orderStatus;
    }

    /**
     * 获取单个订单状态
     * @param orderId 订单ID
     * @return 订单状态
     */
    public Map<String, Object> getOrderStatus(String orderId) {
        Map<String, Object> orders = (Map<String, Object>) orderStatus.getOrDefault("orders", new HashMap<>());
        return (Map<String, Object>) orders.getOrDefault(orderId, new HashMap<>());
    }

    /**
     * 获取订单统计数据
     * @return 订单统计数据
     */
    public Map<String, Object> getOrderStatistics() {
        return (Map<String, Object>) orderStatus.getOrDefault("statistics", new HashMap<>());
    }
}