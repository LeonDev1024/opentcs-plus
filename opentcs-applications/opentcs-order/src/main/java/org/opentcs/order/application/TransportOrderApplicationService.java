package org.opentcs.order.application;

import org.opentcs.kernel.application.TransportOrderRegistry;
import org.opentcs.kernel.application.VehicleRegistry;
import org.opentcs.kernel.application.DispatcherService;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.api.dto.TransportOrderDTO;
import org.opentcs.kernel.api.dto.OrderStateDTO;
import org.opentcs.order.service.TransportOrderService;
import org.opentcs.order.mapper.TransportOrderMapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 运输订单应用服务
 * 整合数据库持久化和内核调度服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransportOrderApplicationService {

    private final TransportOrderService orderService;
    private final TransportOrderRegistry orderRegistry;
    private final VehicleRegistry vehicleRegistry;
    private final DispatcherService dispatcherService;

    /**
     * 创建运输订单
     * 1. 保存到数据库
     * 2. 注册到内核
     * 3. 自动分配车辆
     */
    @Transactional
    public boolean createTransportOrder(org.opentcs.kernel.persistence.entity.TransportOrderEntity entity) {
        // 1. 生成订单ID
        String orderId = entity.getOrderNo() != null ? entity.getOrderNo() : UUID.randomUUID().toString();

        // 2. 保存到数据库
        entity.setOrderNo(orderId);
        entity.setState("RAW");
        orderService.getBaseMapper().insert(entity);

        // 3. 创建并分配订单（内核自动处理）
        try {
            // 从destinations字段解析起点和终点
            String sourcePoint = entity.getDestinations() != null && entity.getDestinations().contains(",")
                    ? entity.getDestinations().split(",")[0]
                    : "";
            String destPoint = entity.getDestinations() != null && entity.getDestinations().contains(",")
                    ? entity.getDestinations().split(",")[1]
                    : (entity.getDestinations() != null ? entity.getDestinations() : "");

            TransportOrder kernelOrder = dispatcherService.createAndDispatchOrder(
                    orderId,
                    sourcePoint,
                    destPoint,
                    entity.getName()
            );

            // 更新数据库状态
            entity.setState(kernelOrder.getState().name());
            orderService.getBaseMapper().updateById(entity);

            log.info("运输订单创建成功: {} -> {}", sourcePoint, destPoint);
            return true;
        } catch (Exception e) {
            log.error("订单创建失败: {}", e.getMessage());
            // 回滚数据库
            orderService.getBaseMapper().deleteById(entity.getId());
            throw new RuntimeException("订单创建失败: " + e.getMessage());
        }
    }

    /**
     * 取消运输订单
     */
    @Transactional
    public boolean cancelTransportOrder(Long orderId) {
        org.opentcs.kernel.persistence.entity.TransportOrderEntity entity = orderService.getBaseMapper().selectById(orderId);
        if (entity == null) {
            throw new RuntimeException("订单不存在: " + orderId);
        }

        // 从内核取消
        TransportOrder kernelOrder = orderRegistry.getOrder(entity.getOrderNo());
        if (kernelOrder != null) {
            kernelOrder.cancel();
            orderRegistry.cancelOrder(entity.getOrderNo());
        }

        // 更新数据库
        entity.setState("CANCELLED");
        orderService.getBaseMapper().updateById(entity);

        log.info("订单已取消: {}", entity.getOrderNo());
        return true;
    }

    /**
     * 手动分配车辆
     */
    @Transactional
    public boolean assignVehicle(Long orderId, String vehicleId) {
        org.opentcs.kernel.persistence.entity.TransportOrderEntity entity = orderService.getBaseMapper().selectById(orderId);
        if (entity == null) {
            throw new RuntimeException("订单不存在: " + orderId);
        }

        // 检查车辆是否可用
        Vehicle vehicle = vehicleRegistry.getVehicle(vehicleId);
        if (vehicle == null || !vehicle.canAcceptOrder()) {
            throw new RuntimeException("车辆不可用: " + vehicleId);
        }

        // 分配订单
        orderRegistry.assignOrder(entity.getOrderNo(), vehicleId);
        vehicleRegistry.assignOrderToVehicle(vehicleId, entity.getOrderNo());

        // 更新数据库
        entity.setState("DISPATCHED");
        entity.setIntendedVehicle(vehicleId);
        entity.setProcessingVehicle(vehicleId);
        orderService.getBaseMapper().updateById(entity);

        log.info("订单已分配给车辆 {}: {}", vehicleId, entity.getOrderNo());
        return true;
    }

    /**
     * 获取订单运行时状态
     */
    public TransportOrderDTO getOrderRuntimeStatus(String orderId) {
        TransportOrder kernelOrder = orderRegistry.getOrder(orderId);
        if (kernelOrder == null) {
            return null;
        }

        return toDTO(kernelOrder);
    }

    /**
     * 获取所有订单运行时状态
     */
    public List<TransportOrderDTO> getAllOrderRuntimeStatus() {
        return orderRegistry.getAllOrders().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取等待中的订单
     */
    public List<TransportOrderDTO> getWaitingOrders() {
        return orderRegistry.getWaitingOrders().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取已分配的订单
     */
    public List<TransportOrderDTO> getAssignedOrders() {
        return orderRegistry.getAssignedOrders().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 处理订单完成
     */
    public void onOrderCompleted(String orderId) {
        TransportOrder kernelOrder = orderRegistry.getOrder(orderId);
        if (kernelOrder != null) {
            kernelOrder.complete();
            orderRegistry.completeOrder(orderId);

            // 更新数据库
            List<org.opentcs.kernel.persistence.entity.TransportOrderEntity> entities = orderService.getBaseMapper()
                    .selectList(new LambdaQueryWrapper<org.opentcs.kernel.persistence.entity.TransportOrderEntity>()
                            .eq(org.opentcs.kernel.persistence.entity.TransportOrderEntity::getOrderNo, orderId));
            if (!entities.isEmpty()) {
                org.opentcs.kernel.persistence.entity.TransportOrderEntity entity = entities.get(0);
                entity.setState("FINISHED");
                orderService.getBaseMapper().updateById(entity);
            }

            log.info("订单已完成: {}", orderId);
        }
    }

    /**
     * 处理订单失败
     */
    public void onOrderFailed(String orderId, String reason) {
        TransportOrder kernelOrder = orderRegistry.getOrder(orderId);
        if (kernelOrder != null) {
            kernelOrder.fail();
            orderRegistry.releaseOrder(orderId);

            // 更新数据库
            List<org.opentcs.kernel.persistence.entity.TransportOrderEntity> entities = orderService.getBaseMapper()
                    .selectList(new LambdaQueryWrapper<org.opentcs.kernel.persistence.entity.TransportOrderEntity>()
                            .eq(org.opentcs.kernel.persistence.entity.TransportOrderEntity::getOrderNo, orderId));
            if (!entities.isEmpty()) {
                org.opentcs.kernel.persistence.entity.TransportOrderEntity entity = entities.get(0);
                entity.setState("FAILED");
                // 使用properties字段存储备注
                entity.setProperties("{\"remark\":\"" + reason + "\"}");
                orderService.getBaseMapper().updateById(entity);
            }

            log.info("订单失败: {}, 原因: {}", orderId, reason);
        }
    }

    /**
     * 获取订单统计
     */
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 内核统计
        List<TransportOrder> allOrders = orderRegistry.getAllOrders();
        long totalOrders = allOrders.size();
        long finishedOrders = allOrders.stream()
                .filter(o -> o.getState() == OrderState.FINISHED)
                .count();
        long activeOrders = allOrders.stream()
                .filter(o -> o.getState() == OrderState.ACTIVE)
                .count();
        long waitingOrders = allOrders.stream()
                .filter(o -> o.getState() == OrderState.RAW)
                .count();
        long failedOrders = allOrders.stream()
                .filter(o -> o.getState() == OrderState.FAILED)
                .count();
        long cancelledOrders = allOrders.stream()
                .filter(o -> o.getState() == OrderState.CANCELLED)
                .count();

        stats.put("totalOrders", totalOrders);
        stats.put("finishedOrders", finishedOrders);
        stats.put("activeOrders", activeOrders);
        stats.put("waitingOrders", waitingOrders);
        stats.put("failedOrders", failedOrders);
        stats.put("cancelledOrders", cancelledOrders);

        // 完成率
        double completionRate = totalOrders > 0 ? (double) finishedOrders / totalOrders * 100 : 0;
        stats.put("completionRate", completionRate);

        // 执行率
        double executionRate = totalOrders > 0 ? (double) activeOrders / totalOrders * 100 : 0;
        stats.put("executionRate", executionRate);

        return stats;
    }

    // ===== 辅助方法 =====

    private TransportOrderDTO toDTO(TransportOrder order) {
        TransportOrderDTO dto = new TransportOrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setName(order.getName());
        dto.setOrderNo(order.getOrderNo());
        dto.setProcessingVehicle(order.getProcessingVehicle());
        dto.setIntendedVehicle(order.getIntendedVehicle());

        // 使用枚举
        dto.setState(org.opentcs.kernel.api.dto.OrderStateDTO.valueOf(order.getState().name()));

        return dto;
    }
}
