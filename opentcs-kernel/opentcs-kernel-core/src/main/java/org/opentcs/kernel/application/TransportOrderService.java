package org.opentcs.kernel.application;

import org.opentcs.kernel.api.TransportOrderApi;
import org.opentcs.kernel.api.dto.*;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.domain.order.OrderStep;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.event.OrderCreatedEvent;
import org.opentcs.kernel.domain.event.OrderStateChangedEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 运输订单应用服务，实现 {@link TransportOrderApi} 端口接口。
 * <p>
 * 编排 {@link TransportOrderRegistry}（内存订单库）与 {@link DispatcherService}（调度引擎），
 * 将创建→激活→调度的完整生命周期暴露给上层应用模块。
 * </p>
 */
public class TransportOrderService implements TransportOrderApi {

    private final TransportOrderRegistry registry;
    private final DispatcherService dispatcher;
    private final RoutePlannerImpl routePlanner;
    private final MapRuntimeService mapRuntimeService;
    private final ApplicationEventPublisher eventPublisher;

    public TransportOrderService(TransportOrderRegistry registry,
                                 DispatcherService dispatcher,
                                 RoutePlannerImpl routePlanner,
                                 MapRuntimeService mapRuntimeService,
                                 ApplicationEventPublisher eventPublisher) {
        this.registry = registry;
        this.dispatcher = dispatcher;
        this.routePlanner = routePlanner;
        this.mapRuntimeService = mapRuntimeService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String createOrder(OrderSpecDTO spec) {
        TransportOrder order = buildOrder(spec, null);

        registry.createOrder(order);
        eventPublisher.publishEvent(new OrderCreatedEvent(
                order.getOrderId(),
                order.getName(),
                order.getIntendedVehicle(),
                order.getSteps().size()));
        return order.getOrderId();
    }

    @Override
    public void restoreOrder(String orderId,
                             OrderSpecDTO spec,
                             OrderStateDTO state,
                             String processingVehicle) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("恢复订单 ID 不能为空");
        }
        if (registry.orderExists(orderId)) {
            return;
        }

        TransportOrder order = buildOrder(spec, orderId);
        OrderState restoreState = state == null ? OrderState.RAW : OrderState.valueOf(state.name());
        if (restoreState != OrderState.RAW
                && restoreState != OrderState.ACTIVE
                && restoreState != OrderState.RECOVERING) {
            throw new IllegalArgumentException("仅支持恢复非终态订单: " + restoreState);
        }
        order.restoreRuntimeState(restoreState, processingVehicle);

        registry.createOrder(order);
    }

    @Override
    public void reconcileVehicleRuntimeState(String vehicleId,
                                             String currentOrderId,
                                             VehicleStateDTO vehicleState,
                                             List<String> activeOrderIds,
                                             boolean hasFault) {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new IllegalArgumentException("vehicleId 不能为空");
        }
        VehicleStateDTO state = vehicleState == null ? VehicleStateDTO.UNKNOWN : vehicleState;

        if (currentOrderId != null && !currentOrderId.isBlank()) {
            TransportOrder reportedOrder = registry.getOrder(currentOrderId);
            if (reportedOrder != null && reportedOrder.getState() == OrderState.RECOVERING) {
                reconcileRecoveringOrder(
                        reportedOrder, vehicleId, currentOrderId, state, activeOrderIds, hasFault);
                return;
            }
        }

        registry.getAllOrders().stream()
                .filter(order -> order.getState() == OrderState.RECOVERING)
                .filter(order -> vehicleId.equals(order.getProcessingVehicle()))
                .forEach(order -> reconcileRecoveringOrder(
                        order, vehicleId, currentOrderId, state, activeOrderIds, hasFault));
    }

    private void reconcileRecoveringOrder(TransportOrder order,
                                          String vehicleId,
                                          String currentOrderId,
                                          VehicleStateDTO vehicleState,
                                          List<String> activeOrderIds,
                                          boolean hasFault) {
        if (vehicleState == VehicleStateDTO.OFFLINE || vehicleState == VehicleStateDTO.UNKNOWN) {
            return;
        }

        OrderState oldState = order.getState();
        if (hasFault || vehicleState == VehicleStateDTO.ERROR || vehicleState == VehicleStateDTO.UNAVAILABLE) {
            order.fail();
            publishStateChanged(order, oldState, "RECOVERY_VEHICLE_UNAVAILABLE");
            return;
        }

        if (currentOrderId == null || currentOrderId.isBlank()) {
            order.fail();
            publishStateChanged(order, oldState, "RECOVERY_VEHICLE_IDLE");
            return;
        }

        if (!order.getOrderId().equals(currentOrderId)) {
            order.fail();
            publishStateChanged(order, oldState,
                    "RECOVERY_ORDER_MISMATCH:" + currentOrderId);
            return;
        }

        if (isVehicleIdleAfterOrder(vehicleState)
                && (activeOrderIds == null || activeOrderIds.isEmpty())) {
            order.complete();
            publishStateChanged(order, oldState, "RECOVERY_COMPLETED_BY_VEHICLE_SNAPSHOT");
            return;
        }

        if (activeOrderIds != null
                && !activeOrderIds.isEmpty()
                && !activeOrderIds.contains(order.getOrderId())) {
            order.fail();
            publishStateChanged(order, oldState, "RECOVERY_ORDER_NOT_ACTIVE");
            return;
        }

        if (isVehicleActive(vehicleState)) {
            order.confirmRecoveredExecution(vehicleId);
            publishStateChanged(order, oldState, "RECOVERY_CONFIRMED");
            return;
        }

        order.fail();
        publishStateChanged(order, oldState, "RECOVERY_VEHICLE_NOT_EXECUTING");
    }

    private boolean isVehicleIdleAfterOrder(VehicleStateDTO vehicleState) {
        return vehicleState == VehicleStateDTO.IDLE || vehicleState == VehicleStateDTO.CHARGING;
    }

    private boolean isVehicleActive(VehicleStateDTO vehicleState) {
        return vehicleState == VehicleStateDTO.EXECUTING
                || vehicleState == VehicleStateDTO.PAUSED
                || vehicleState == VehicleStateDTO.WAITING;
    }

    private TransportOrder buildOrder(OrderSpecDTO spec, String restoredOrderId) {
        String name = spec.getName() != null ? spec.getName() : "ORDER-" + System.currentTimeMillis();

        // 优先使用 sourcePointId/destPointId 进行路径规划
        String sourcePointId = spec.getSourcePointId();
        String destPointId = spec.getDestPointId();
        String activeMapId = mapRuntimeService.getActiveMapId();
        String activeMapVersion = mapRuntimeService.getActiveMapVersion();
        if (activeMapId == null || activeMapVersion == null) {
            throw new IllegalStateException("当前没有已加载的运行地图，不能创建运输订单");
        }

        TransportOrder order;
        if (sourcePointId != null && destPointId != null) {
            // 使用路径规划创建订单
            List<org.opentcs.kernel.domain.routing.Path> route = routePlanner.findPath(sourcePointId, destPointId);
            if (route.isEmpty()) {
                throw new IllegalStateException(
                        "无法找到从 %s 到 %s 的路径".formatted(sourcePointId, destPointId));
            }
            order = new TransportOrder(
                    restoredOrderId != null ? restoredOrderId : "ORDER-" + System.currentTimeMillis(),
                    name,
                    sourcePointId,
                    destPointId,
                    route);
        } else {
            // 使用步骤列表创建订单
            order = restoredOrderId != null ? new TransportOrder(restoredOrderId, name) : new TransportOrder(name);
            if (spec.getSteps() != null) {
                for (int i = 0; i < spec.getSteps().size(); i++) {
                    OrderStepDTO s = spec.getSteps().get(i);
                    String src = (i == 0) ? null : spec.getSteps().get(i - 1).getDestinationPointId();
                    order.addStep(new OrderStep(src, s.getDestinationPointId(), null));
                }
            }
        }

        if (spec.getIntendedVehicle() != null) {
            order.setIntendedVehicle(spec.getIntendedVehicle());
        }
        if (spec.getDeadline() != null) {
            order.setDeadline(spec.getDeadline());
        }
        if (spec.getProperties() != null) {
            spec.getProperties().forEach((k, v) -> order.getProperties().put(k, v));
        }
        order.getProperties().put("mapId", activeMapId);
        order.getProperties().put("mapVersion", activeMapVersion);

        return order;
    }

    @Override
    public void activateOrder(String orderId) {
        TransportOrder order = registry.getOrder(orderId);
        if (order == null) throw new IllegalArgumentException("订单不存在: " + orderId);
        OrderState oldState = order.getState();
        order.activate();
        publishStateChanged(order, oldState, null);
        dispatcher.dispatchOrder(order);
    }

    @Override
    public void cancelOrder(String orderId) {
        dispatcher.withdrawOrder(orderId, false);
    }

    @Override
    public void assignOrderToVehicle(String orderId, String vehicleId) {
        TransportOrder order = registry.getOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在: " + orderId);
        }
        if (order.getState() == OrderState.RAW) {
            OrderState oldState = order.getState();
            order.activate();
            publishStateChanged(order, oldState, null);
        }

        OrderState oldState = order.getState();
        order.assignTo(vehicleId);
        publishStateChanged(order, oldState, null);
    }

    @Override
    public void completeOrder(String orderId) {
        TransportOrder order = registry.getOrder(orderId);
        if (order == null) {
            return;
        }
        OrderState oldState = order.getState();
        order.complete();
        publishStateChanged(order, oldState, null);
    }

    @Override
    public void failOrder(String orderId, String reason) {
        TransportOrder order = registry.getOrder(orderId);
        if (order == null) {
            return;
        }
        OrderState oldState = order.getState();
        order.fail();
        publishStateChanged(order, oldState, reason);
    }

    @Override
    public Optional<TransportOrderDTO> getOrder(String orderId) {
        return Optional.ofNullable(registry.getOrder(orderId)).map(this::toDTO);
    }

    @Override
    public List<TransportOrderDTO> getActiveOrders() {
        return registry.getAssignedOrders().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransportOrderDTO> getAllOrders() {
        return registry.getAllOrders().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void onOrderCompleted(String orderId, String vehicleId, boolean success) {
        if (success) {
            dispatcher.vehicleCompletedOrder(vehicleId);
        } else {
            dispatcher.vehicleCancelledOrder(vehicleId);
        }
    }

    @Override
    public void onStepCompleted(String orderId, int stepIndex) {
        TransportOrder order = registry.getOrder(orderId);
        if (order != null && order.isActive()) {
            OrderState oldState = order.getState();
            order.completeCurrentStep();
            publishStateChanged(order, oldState, null);
        }
    }

    private void publishStateChanged(TransportOrder order, OrderState oldState, String reason) {
        eventPublisher.publishEvent(new OrderStateChangedEvent(
                order.getOrderId(),
                oldState,
                order.getState(),
                order.getProcessingVehicle(),
                reason));
    }

    // ===== DTO 映射 =====

    private TransportOrderDTO toDTO(TransportOrder o) {
        TransportOrderDTO dto = new TransportOrderDTO();
        dto.setOrderId(o.getOrderId());
        dto.setName(o.getName());
        dto.setOrderNo(o.getOrderNo());
        dto.setState(toStateDTO(o.getState()));
        dto.setIntendedVehicle(o.getIntendedVehicle());
        dto.setProcessingVehicle(o.getProcessingVehicle());
        dto.setCreationTime(o.getCreationTime());
        dto.setFinishedTime(o.getFinishedTime() > 0 ? o.getFinishedTime() : null);
        dto.setDeadline(o.getDeadline());
        dto.setProperties(o.getProperties());
        dto.setSteps(o.getSteps().stream().map(this::toStepDTO).collect(Collectors.toList()));
        return dto;
    }

    private OrderStateDTO toStateDTO(OrderState s) {
        return OrderStateDTO.valueOf(s.name());
    }

    private OrderStepDTO toStepDTO(OrderStep s) {
        OrderStepDTO dto = new OrderStepDTO();
        dto.setDestinationPointId(s.getDestPointId());
        dto.setState(StepStateDTO.valueOf(s.getState().name()));
        return dto;
    }
}
