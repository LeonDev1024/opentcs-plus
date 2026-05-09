package org.opentcs.simulation.order;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.simulation.map.SimMapPoint;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 订单生成器：优先使用真实地图点位，无地图时退回随机坐标
 */
@Slf4j
@Data
public class OrderGenerator {

    private final Random random = new Random();
    /** 订单创建率（订单/秒），默认 2.0 → 每 tick 20% 概率，约 2 个/秒 */
    private double orderCreationRate = 2.0;
    /** 随机模式下的订单最大距离（m） */
    private int orderMaxDistance = 5;
    /** 随机模式下的订单最小距离（m） */
    private int orderMinDistance = 1;
    private int orderTimeout = 300;
    /** 随机坐标模式下的坐标范围（m），车辆和订单都在此范围内 */
    private double randomCoordRange = 15.0;

    /** 真实地图点位；不为空时从中随机选取起终点 */
    private List<SimMapPoint> mapPoints;

    public SimulatedOrder generateOrder() {
        if (mapPoints != null && mapPoints.size() >= 2) {
            return generateOrderFromMapPoints();
        }
        return generateRandomOrder();
    }

    private SimulatedOrder generateOrderFromMapPoints() {
        int startIdx = random.nextInt(mapPoints.size());
        int endIdx;
        do {
            endIdx = random.nextInt(mapPoints.size());
        } while (endIdx == startIdx);

        SimMapPoint start = mapPoints.get(startIdx);
        SimMapPoint end = mapPoints.get(endIdx);

        double distance = Math.hypot(end.getX() - start.getX(), end.getY() - start.getY());

        SimulatedOrder order = new SimulatedOrder();
        order.setOrderId("order-" + UUID.randomUUID());
        order.setStartX(start.getX());
        order.setStartY(start.getY());
        order.setEndX(end.getX());
        order.setEndY(end.getY());
        order.setDistance(Math.max(distance, 0.1));
        order.setTimeout(orderTimeout);
        return order;
    }

    private SimulatedOrder generateRandomOrder() {
        double startX = random.nextDouble() * randomCoordRange;
        double startY = random.nextDouble() * randomCoordRange;
        double distance = orderMinDistance + random.nextDouble() * (orderMaxDistance - orderMinDistance);
        double angle = random.nextDouble() * Math.PI * 2;
        double endX = startX + Math.cos(angle) * distance;
        double endY = startY + Math.sin(angle) * distance;

        SimulatedOrder order = new SimulatedOrder();
        order.setOrderId("order-" + UUID.randomUUID());
        order.setStartX(startX);
        order.setStartY(startY);
        order.setEndX(endX);
        order.setEndY(endY);
        order.setDistance(distance);
        order.setTimeout(orderTimeout);
        return order;
    }
}
