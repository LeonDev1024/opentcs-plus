package org.opentcs.kernel.application;

import org.opentcs.kernel.api.TransportOrderApi;
import org.opentcs.kernel.api.dto.*;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.domain.order.OrderStep;
import org.opentcs.kernel.domain.order.TransportOrder;

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

    public TransportOrderService(TransportOrderRegistry registry,
                                 DispatcherService dispatcher,
                                 RoutePlannerImpl routePlanner) {
        this.registry = registry;
        this.dispatcher = dispatcher;
        this.routePlanner = routePlanner;
    }

    @Override
    public String createOrder(OrderSpecDTO spec) {
        String name = spec.getName() != null ? spec.getName() : "ORDER-" + System.currentTimeMillis();

        // 优先使用 sourcePointId/destPointId 进行路径规划
        String sourcePointId = spec.getSourcePointId();
        String destPointId = spec.getDestPointId();

        TransportOrder order;
        if (sourcePointId != null && destPointId != null) {
            // 使用路径规划创建订单
            List<org.opentcs.kernel.domain.routing.Path> route = routePlanner.findPath(sourcePointId, destPointId);
            if (route.isEmpty()) {
                throw new IllegalStateException(
                        "无法找到从 %s 到 %s 的路径".formatted(sourcePointId, destPointId));
            }
            order = new TransportOrder(
                    "ORDER-" + System.currentTimeMillis(),
                    name,
                    sourcePointId,
                    destPointId,
                    route);
        } else {
            // 使用步骤列表创建订单
            order = new TransportOrder(name);
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

        registry.createOrder(order);
        return order.getOrderId();
    }

    @Override
    public void activateOrder(String orderId) {
        TransportOrder order = registry.getOrder(orderId);
        if (order == null) throw new IllegalArgumentException("订单不存在: " + orderId);
        order.activate();
        dispatcher.dispatchOrder(order);
    }

    @Override
    public void cancelOrder(String orderId) {
        TransportOrder order = registry.getOrder(orderId);
        if (order == null) return;
        order.cancel();
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
            order.completeCurrentStep();
        }
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
