package org.opentcs.kernel.application;

import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.event.OrderCreatedEvent;
import org.opentcs.kernel.domain.event.VehicleStateChangedEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 调度服务 - 负责将运输订单分配给车辆
 */
public class DispatcherService {

    private final VehicleRegistry vehicleRegistry;
    private final TransportOrderRegistry orderRegistry;
    private final RoutePlannerImpl routePlanner;

    // 事件监听器
    private final List<EventListener> eventListeners = new ArrayList<>();

    public DispatcherService(VehicleRegistry vehicleRegistry,
                            TransportOrderRegistry orderRegistry,
                            RoutePlannerImpl routePlanner) {
        this.vehicleRegistry = vehicleRegistry;
        this.orderRegistry = orderRegistry;
        this.routePlanner = routePlanner;
    }

    /**
     * 创建运输订单并尝试分配
     */
    public TransportOrder createAndDispatchOrder(String orderId, String sourcePointId,
                                                  String destPointId, String orderName) {
        // 查找路径
        List<Path> route = routePlanner.findPath(sourcePointId, destPointId);
        if (route.isEmpty()) {
            throw new IllegalStateException("无法找到从 " + sourcePointId + " 到 " + destPointId + " 的路径");
        }

        // 创建订单
        TransportOrder order = new TransportOrder(orderId, orderName, sourcePointId, destPointId, route);
        orderRegistry.createOrder(order);

        // 发布订单创建事件
        publishEvent(new OrderCreatedEvent(orderId, orderName, null, route.size()));

        // 尝试分配订单（会激活订单并分配给车辆）
        dispatchOrder(order);

        return order;
    }

    /**
     * 调度订单 - 为订单分配可用车辆
     */
    public boolean dispatchOrder(TransportOrder order) {
        if (order.getState() != OrderState.RAW) {
            return false;
        }

        // 激活订单
        try {
            order.activate();
        } catch (IllegalStateException e) {
            // 订单可能已经是激活状态
            if (order.getState() != OrderState.ACTIVE) {
                return false;
            }
        }

        // 查找可用车辆
        List<Vehicle> availableVehicles = findAvailableVehiclesForOrder(order);

        if (availableVehicles.isEmpty()) {
            return false;
        }

        // 选择最佳车辆（选择离订单起点最近的车辆）
        Vehicle selectedVehicle = selectBestVehicle(availableVehicles, order.getSourcePointId());

        if (selectedVehicle == null) {
            return false;
        }

        // 分配订单给车辆
        assignOrderToVehicle(order, selectedVehicle);

        return true;
    }

    /**
     * 查找适合订单的可用车辆
     */
    private List<Vehicle> findAvailableVehiclesForOrder(TransportOrder order) {
        String orderSourcePointId = order.getSourcePointId();

        return vehicleRegistry.getAvailableVehicles().stream()
                .filter(vehicle -> canVehicleReachOrder(vehicle, orderSourcePointId))
                .collect(Collectors.toList());
    }

    /**
     * 检查车辆是否能到达订单起点
     */
    private boolean canVehicleReachOrder(Vehicle vehicle, String orderSourcePointId) {
        String vehicleCurrentPointId = vehicle.getPosition().getPointId();

        // 如果车辆已经在订单起点，直接返回
        if (vehicleCurrentPointId.equals(orderSourcePointId)) {
            return true;
        }

        // 检查是否有路径可达
        List<Point> route = routePlanner.findRoute(vehicleCurrentPointId, orderSourcePointId);
        return !route.isEmpty();
    }

    /**
     * 选择最佳车辆（离订单起点最近的）
     */
    private Vehicle selectBestVehicle(List<Vehicle> vehicles, String destPointId) {
        Vehicle bestVehicle = null;
        double minDistance = Double.MAX_VALUE;

        Point destPoint = routePlanner.getPoint(destPointId);
        if (destPoint == null) {
            return vehicles.isEmpty() ? null : vehicles.get(0);
        }

        for (Vehicle vehicle : vehicles) {
            Point vehiclePoint = routePlanner.getPoint(vehicle.getPosition().getPointId());
            if (vehiclePoint == null) {
                continue;
            }

            double distance = calculateDistance(vehiclePoint, destPoint);
            if (distance < minDistance) {
                minDistance = distance;
                bestVehicle = vehicle;
            }
        }

        return bestVehicle;
    }

    /**
     * 计算两点之间的距离
     */
    private double calculateDistance(Point p1, Point p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 将订单分配给车辆
     */
    private void assignOrderToVehicle(TransportOrder order, Vehicle vehicle) {
        String orderId = order.getOrderId();
        String vehicleId = vehicle.getVehicleId();

        // 更新订单状态
        order.assignTo(vehicleId);
        orderRegistry.assignOrder(orderId, vehicleId);

        // 更新车辆状态
        vehicle.assignOrder(orderId);
        vehicle.updateState(VehicleState.EXECUTING);
        vehicleRegistry.updateVehicleState(vehicleId, VehicleState.EXECUTING);

        // 发布车辆状态变更事件
        publishEvent(new VehicleStateChangedEvent(
                vehicleId,
                VehicleState.IDLE,
                VehicleState.EXECUTING,
                orderId
        ));
    }

    /**
     * 车辆完成订单
     */
    public void vehicleCompletedOrder(String vehicleId) {
        Vehicle vehicle = vehicleRegistry.getVehicle(vehicleId);
        if (vehicle == null) {
            return;
        }

        String orderId = vehicle.getCurrentOrderId();
        if (orderId == null) {
            return;
        }

        TransportOrder order = orderRegistry.getOrder(orderId);
        if (order != null) {
            order.complete();
            orderRegistry.completeOrder(orderId);
        }

        // 更新车辆状态
        VehicleState oldState = vehicle.getState();
        vehicle.completeOrder();
        vehicleRegistry.updateVehicleState(vehicleId, VehicleState.IDLE);

        // 发布车辆状态变更事件
        publishEvent(new VehicleStateChangedEvent(
                vehicleId,
                oldState,
                VehicleState.IDLE,
                null
        ));
    }

    /**
     * 车辆取消订单
     */
    public void vehicleCancelledOrder(String vehicleId) {
        Vehicle vehicle = vehicleRegistry.getVehicle(vehicleId);
        if (vehicle == null) {
            return;
        }

        String orderId = vehicle.getCurrentOrderId();
        if (orderId == null) {
            return;
        }

        TransportOrder order = orderRegistry.getOrder(orderId);
        if (order != null) {
            order.cancel();
            orderRegistry.cancelOrder(orderId);
        }

        // 更新车辆状态
        VehicleState oldState = vehicle.getState();
        vehicle.cancelOrder();
        vehicleRegistry.updateVehicleState(vehicleId, VehicleState.IDLE);

        // 发布车辆状态变更事件
        publishEvent(new VehicleStateChangedEvent(
                vehicleId,
                oldState,
                VehicleState.IDLE,
                null
        ));

        // 尝试将订单重新分配给其他车辆
        if (order != null && order.getState() == OrderState.RAW) {
            dispatchOrder(order);
        }
    }

    /**
     * 处理新的可用车辆
     */
    public void processAvailableVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRegistry.getVehicle(vehicleId);
        if (vehicle == null || !vehicle.canAcceptOrder()) {
            return;
        }

        // 查找等待中的订单
        List<TransportOrder> waitingOrders = orderRegistry.getWaitingOrders();

        for (TransportOrder order : waitingOrders) {
            if (canVehicleReachOrder(vehicle, order.getSourcePointId())) {
                if (dispatchOrder(order)) {
                    break;
                }
            }
        }
    }

    /**
     * 添加事件监听器
     */
    public void addEventListener(EventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * 移除事件监听器
     */
    public void removeEventListener(EventListener listener) {
        eventListeners.remove(listener);
    }

    /**
     * 发布事件
     */
    private void publishEvent(Object event) {
        for (EventListener listener : eventListeners) {
            listener.onEvent(event);
        }
    }

    /**
     * 事件监听器接口
     */
    public interface EventListener {
        void onEvent(Object event);
    }
}
