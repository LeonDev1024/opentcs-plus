package org.opentcs.simulation.order;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.UUID;

/**
 * 订单生成器
 */
@Slf4j
@Data
public class OrderGenerator {
    
    private final Random random = new Random();
    private double orderCreationRate = 0.1; // 订单创建率（订单/秒）
    private int orderMaxDistance = 20; // 订单最大距离（m）
    private int orderMinDistance = 5; // 订单最小距离（m）
    private int orderTimeout = 300; // 订单超时时间（秒）
    
    /**
     * 生成新订单
     * @return 新订单
     */
    public SimulatedOrder generateOrder() {
        // 生成随机起点和终点（50x50 的小地图，便于快速验证）
        double startX = random.nextDouble() * 50;
        double startY = random.nextDouble() * 50;
        
        // 生成随机距离
        double distance = orderMinDistance + random.nextDouble() * (orderMaxDistance - orderMinDistance);
        // 生成随机方向
        double angle = random.nextDouble() * Math.PI * 2;
        
        // 计算终点坐标
        double endX = startX + Math.cos(angle) * distance;
        double endY = startY + Math.sin(angle) * distance;
        
        // 生成订单ID
        String orderId = "order-" + UUID.randomUUID().toString();
        
        // 创建订单
        SimulatedOrder order = new SimulatedOrder();
        order.setOrderId(orderId);
        order.setStartX(startX);
        order.setStartY(startY);
        order.setEndX(endX);
        order.setEndY(endY);
        order.setDistance(distance);
        order.setTimeout(orderTimeout);
        
        return order;
    }
}