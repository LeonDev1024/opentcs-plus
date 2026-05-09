package org.opentcs.kernel.application;

import org.opentcs.kernel.api.algorithm.Dispatcher;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.event.OrderCreatedEvent;
import org.opentcs.kernel.domain.event.OrderStateChangedEvent;
import org.opentcs.kernel.domain.event.OrderWithdrawalRequestedEvent;
import org.opentcs.kernel.domain.event.VehicleStateChangedEvent;
import org.opentcs.kernel.application.runtime.RuntimeStateStore;
import org.opentcs.kernel.application.dispatch.DispatchStrategy;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 调度服务——将运输订单分配给最优可用车辆，同时实现 {@link Dispatcher} 端口接口。
 * <p>
 * 依赖：
 * <ul>
 *   <li>{@link VehicleRegistry} — 车辆运行时状态</li>
 *   <li>{@link TransportOrderRegistry} — 订单内存库</li>
 *   <li>{@link RoutePlannerImpl} — 路径规划</li>
 *   <li>{@link ApplicationEventPublisher} — Spring 事件发布</li>
 * </ul>
 * </p>
 */
public class DispatcherService implements Dispatcher {

    private static final Logger log = LoggerFactory.getLogger(DispatcherService.class);

    private final VehicleRegistry vehicleRegistry;
    private final TransportOrderRegistry orderRegistry;
    private final RoutePlannerImpl routePlanner;
    private final ApplicationEventPublisher eventPublisher;
    private final RuntimeStateStore runtimeStateStore;
    private final DispatchStrategy dispatchStrategy;

    private volatile boolean initialized = false;

    public DispatcherService(VehicleRegistry vehicleRegistry,
                             TransportOrderRegistry orderRegistry,
                             RoutePlannerImpl routePlanner,
                             ApplicationEventPublisher eventPublisher,
                             RuntimeStateStore runtimeStateStore,
                             DispatchStrategy dispatchStrategy) {
        this.vehicleRegistry = vehicleRegistry;
        this.orderRegistry = orderRegistry;
        this.routePlanner = routePlanner;
        this.eventPublisher = eventPublisher;
        this.runtimeStateStore = runtimeStateStore;
        this.dispatchStrategy = dispatchStrategy;
    }

    // ===== Lifecycle =====

    @PostConstruct
    @Override
    public void initialize() {
        initialized = true;
        log.info("DispatcherService 已初始化");
    }

    @Override
    public void terminate() {
        initialized = false;
        log.info("DispatcherService 已终止");
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public String getCurrentStrategyName() {
        return dispatchStrategy.getName();
    }

    // ===== Dispatcher 端口接口实现 =====

    /**
     * 触发全量调度：扫描所有等待中的订单，逐一尝试分配。
     */
    @Override
    public void dispatch() {
        List<TransportOrder> waiting = sortWaitingOrders(orderRegistry.getWaitingOrders());
        log.debug("触发全量调度，待处理订单数: {}", waiting.size());
        waiting.forEach(this::dispatchOrder);
    }

    /**
     * 周期兜底调度，避免订单创建/车辆状态事件丢失后等待队列长期滞留。
     */
    @Scheduled(fixedDelayString = "${opentcs.dispatch.interval-ms:1000}")
    public void scheduledDispatch() {
        if (!initialized) {
            return;
        }
        dispatch();
    }

    /**
     * 撤回指定订单。若订单正在被车辆执行，同时触发车辆侧取消。
     */
    @Override
    public void withdrawOrder(String orderId, boolean immediateAbort) {
        TransportOrder order = orderRegistry.getOrder(orderId);
        if (order == null) {
            log.warn("withdrawOrder: 订单 {} 不存在", orderId);
            return;
        }
        String vehicleId = order.getProcessingVehicle();
        if (vehicleId != null) {
            eventPublisher.publishEvent(new OrderWithdrawalRequestedEvent(
                    order.getOrderId(), vehicleId, immediateAbort));
            cancelVehicleOrder(vehicleId, immediateAbort ? "WITHDRAW_ABORTED" : "WITHDRAWN");
        } else {
            OrderState oldState = order.getState();
            order.cancel();
            publishOrderStateChanged(order, oldState, "WITHDRAWN");
        }
    }

    /**
     * 撤回指定车辆当前执行的订单。
     */
    @Override
    public void withdrawOrderByVehicle(String vehicleId, boolean immediateAbort) {
        Vehicle vehicle = vehicleRegistry.getVehicleDomain(vehicleId);
        if (vehicle != null && vehicle.getCurrentOrderId() != null) {
            eventPublisher.publishEvent(new OrderWithdrawalRequestedEvent(
                    vehicle.getCurrentOrderId(), vehicleId, immediateAbort));
        }
        cancelVehicleOrder(vehicleId, immediateAbort ? "WITHDRAW_ABORTED" : "WITHDRAWN");
    }

    /**
     * 重新路由指定车辆（当前为占位实现，待驱动层支持后补全）。
     */
    @Override
    public void reroute(String vehicleId, ReroutingType reroutingType) {
        log.warn("reroute 尚未实现: vehicleId={}, type={}", vehicleId, reroutingType);
    }

    /**
     * 重新路由所有车辆（占位实现）。
     */
    @Override
    public void rerouteAll(ReroutingType reroutingType) {
        log.warn("rerouteAll 尚未实现: type={}", reroutingType);
    }

    /**
     * 立即强制分配指定订单，无可用车辆时抛出异常。
     */
    @Override
    public void assignNow(String orderId) throws TransportOrderAssignmentException {
        TransportOrder order = orderRegistry.getOrder(orderId);
        if (order == null) {
            throw new TransportOrderAssignmentException("订单不存在: " + orderId);
        }
        boolean assigned = dispatchOrder(order);
        if (!assigned) {
            throw new TransportOrderAssignmentException(
                    "订单 " + orderId + " 分配失败，当前无可用车辆");
        }
    }

    // ===== 应用服务内部方法 =====

    /**
     * 创建订单并立即尝试调度。
     */
    public TransportOrder createAndDispatchOrder(String orderId, String sourcePointId,
                                                 String destPointId, String orderName) {
        var route = routePlanner.findPath(sourcePointId, destPointId);
        if (route.isEmpty()) {
            throw new IllegalStateException(
                    "无法找到从 %s 到 %s 的路径".formatted(sourcePointId, destPointId));
        }

        var order = new TransportOrder(orderId, orderName, sourcePointId, destPointId, route);
        orderRegistry.createOrder(order);

        eventPublisher.publishEvent(
                new OrderCreatedEvent(orderId, orderName, null, route.size()));

        dispatchOrder(order);
        return order;
    }

    /**
     * 为订单分配可用车辆。
     *
     * @return {@code true} 表示成功分配
     */
    public boolean dispatchOrder(TransportOrder order) {
        if (!runtimeStateStore.tryAcquireOrderDispatchLock(order.getOrderId())) {
            log.debug("订单 {} 正在调度中，跳过重复调度", order.getOrderId());
            return false;
        }
        try {
            return doDispatchOrder(order);
        } finally {
            runtimeStateStore.releaseOrderDispatchLock(order.getOrderId());
        }
    }

    private boolean doDispatchOrder(TransportOrder order) {
        if (order.getState() != OrderState.RAW && order.getState() != OrderState.ACTIVE) {
            return false;
        }

        if (order.getState() == OrderState.RAW) {
            try {
                order.activate();
            } catch (IllegalStateException e) {
                log.warn("订单 {} 激活失败: {}", order.getOrderId(), e.getMessage());
                return false;
            }
        }

        List<Vehicle> candidates = vehicleRegistry.getAvailableVehicleDomains().stream()
                .filter(v -> canReach(v, order.getSourcePointId()))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            log.debug("订单 {} 暂无可用车辆", order.getOrderId());
            return false;
        }

        Vehicle selected = dispatchStrategy.selectVehicle(order, candidates, routePlanner)
                .orElse(null);
        if (selected == null) return false;

        assignOrderToVehicle(order, selected);
        return true;
    }

    /**
     * 车辆完成订单回调。
     */
    public void vehicleCompletedOrder(String vehicleId) {
        Vehicle vehicle = vehicleRegistry.getVehicleDomain(vehicleId);
        if (vehicle == null) return;

        String orderId = vehicle.getCurrentOrderId();
        if (orderId != null) {
            TransportOrder order = orderRegistry.getOrder(orderId);
            if (order != null) {
                OrderState oldState = order.getState();
                order.complete();
                publishOrderStateChanged(order, oldState, null);
            }
        }

        VehicleState old = vehicle.getState();
        vehicle.completeOrder();
        vehicleRegistry.updateVehicleStateDomain(vehicleId, VehicleState.IDLE);

        eventPublisher.publishEvent(
                new VehicleStateChangedEvent(vehicleId, old, VehicleState.IDLE, null));

        processWaitingOrders(vehicleId);
    }

    /**
     * 车辆取消/放弃订单回调。
     */
    public void vehicleCancelledOrder(String vehicleId) {
        cancelVehicleOrder(vehicleId, "VEHICLE_CANCELLED");
    }

    private void cancelVehicleOrder(String vehicleId, String reason) {
        Vehicle vehicle = vehicleRegistry.getVehicleDomain(vehicleId);
        if (vehicle == null) return;

        String orderId = vehicle.getCurrentOrderId();
        TransportOrder order = orderId != null ? orderRegistry.getOrder(orderId) : null;

        if (order != null) {
            OrderState oldState = order.getState();
            order.cancel();
            publishOrderStateChanged(order, oldState, reason);
        }

        VehicleState old = vehicle.getState();
        vehicle.cancelOrder();
        vehicleRegistry.updateVehicleStateDomain(vehicleId, VehicleState.IDLE);

        eventPublisher.publishEvent(
                new VehicleStateChangedEvent(vehicleId, old, VehicleState.IDLE, null));

        if (order != null && order.getState() == OrderState.RAW) {
            dispatchOrder(order);
        }
    }

    // ===== 内部方法 =====

    private boolean canReach(Vehicle vehicle, String targetPointId) {
        String current = vehicle.getPosition().getPointId();
        if (current == null) return false;
        if (current.equals(targetPointId)) return true;
        return !routePlanner.findRouteDomain(current, targetPointId).isEmpty();
    }

    private void assignOrderToVehicle(TransportOrder order, Vehicle vehicle) {
        order.assignTo(vehicle.getVehicleId());
        publishOrderStateChanged(order, OrderState.ACTIVE, null);

        VehicleState old = vehicle.getState();
        vehicle.assignOrder(order.getOrderId());
        vehicle.updateState(VehicleState.EXECUTING);
        vehicleRegistry.updateVehicleStateDomain(vehicle.getVehicleId(), VehicleState.EXECUTING);

        log.info("订单 {} 已分配给车辆 {}", order.getOrderId(), vehicle.getVehicleId());

        eventPublisher.publishEvent(new VehicleStateChangedEvent(
                vehicle.getVehicleId(), old, VehicleState.EXECUTING, order.getOrderId()));
    }

    private void processWaitingOrders(String vehicleId) {
        Vehicle vehicle = vehicleRegistry.getVehicleDomain(vehicleId);
        if (vehicle == null || !vehicle.canAcceptOrder()) return;

        sortWaitingOrders(orderRegistry.getWaitingOrders()).stream()
                .filter(o -> canReach(vehicle, o.getSourcePointId()))
                .findFirst()
                .ifPresent(this::dispatchOrder);
    }

    private List<TransportOrder> sortWaitingOrders(List<TransportOrder> orders) {
        return orders.stream()
                .sorted(Comparator
                        .comparingInt(this::orderPriority).reversed()
                        .thenComparingLong(this::orderDeadline)
                        .thenComparingLong(TransportOrder::getCreationTime))
                .collect(Collectors.toList());
    }

    private int orderPriority(TransportOrder order) {
        String priority = order.getProperties().get("priority");
        if (priority == null || priority.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(priority.trim());
        } catch (NumberFormatException e) {
            log.warn("订单 {} priority={} 不是有效整数，按 0 处理", order.getOrderId(), priority);
            return 0;
        }
    }

    private long orderDeadline(TransportOrder order) {
        return order.getDeadline() == null ? Long.MAX_VALUE : order.getDeadline();
    }

    private void publishOrderStateChanged(TransportOrder order, OrderState oldState, String reason) {
        eventPublisher.publishEvent(new OrderStateChangedEvent(
                order.getOrderId(),
                oldState,
                order.getState(),
                order.getProcessingVehicle(),
                reason));
    }
}
