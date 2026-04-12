package org.opentcs.order.application;

import org.opentcs.common.json.utils.JsonUtils;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.TransportOrderApi;
import org.opentcs.kernel.api.dto.OrderSpecDTO;
import org.opentcs.kernel.application.TransportOrderRegistry;
import org.opentcs.kernel.application.VehicleRegistry;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.order.OrderState;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.api.dto.TransportOrderDTO;
import org.opentcs.kernel.api.dto.OrderStateDTO;
import org.opentcs.order.application.bo.CreateOrderCommand;
import org.opentcs.order.application.bo.TransportOrderQueryBO;
import org.opentcs.order.persistence.service.TransportOrderRepository;
import org.opentcs.order.persistence.entity.TransportOrderEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 运输订单应用服务
 * 整合数据库持久化和内核调度服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransportOrderApplicationService {

    private final TransportOrderRepository orderService;
    private final TransportOrderRegistry orderRegistry;
    private final VehicleRegistry vehicleRegistry;
    private final TransportOrderApi transportOrderApi;

    /**
     * 创建运输订单
     * 1. 先执行内核操作（路径规划、状态管理），确保业务可行
     * 2. 内核成功后一次性写入 DB（@Transactional 负责回滚）
     */
    @Transactional
    public boolean createTransportOrder(CreateOrderCommand command) {
        // 1. 先执行内核操作（路径规划、状态管理），确保业务可行后再写 DB
        OrderSpecDTO orderSpec = new OrderSpecDTO();
        orderSpec.setName(command.getName());
        orderSpec.setSourcePointId(command.getSourcePoint());
        orderSpec.setDestPointId(command.getDestPoint());

        String createdOrderId;
        try {
            createdOrderId = transportOrderApi.createOrder(orderSpec);
            transportOrderApi.activateOrder(createdOrderId);
        } catch (Exception e) {
            log.error("内核创建订单失败: {}", e.getMessage());
            throw new RuntimeException("订单创建失败: " + e.getMessage());
        }

        // 2. 内核成功后，一次性写入 DB（@Transactional 负责回滚）
        TransportOrderEntity entity = new TransportOrderEntity();
        entity.setOrderNo(createdOrderId);
        entity.setName(command.getName());
        entity.setIntendedVehicle(command.getIntendedVehicle());
        entity.setDestinations(command.getSourcePoint() + "," + command.getDestPoint());
        entity.setState("ACTIVE");
        orderService.save(entity);

        log.info("运输订单创建成功: {} -> {}", command.getSourcePoint(), command.getDestPoint());
        return true;
    }

    /**
     * 取消运输订单
     */
    @Transactional
    public boolean cancelTransportOrder(Long orderId) {
        TransportOrderEntity entity = orderService.getById(orderId);
        if (entity == null) {
            throw new RuntimeException("订单不存在: " + orderId);
        }

        // 从内核取消
        TransportOrder kernelOrder = orderRegistry.getOrder(entity.getOrderNo());
        if (kernelOrder != null) {
            kernelOrder.cancel();
        }

        // 更新数据库
        entity.setState("CANCELLED");
        orderService.updateById(entity);

        log.info("订单已取消: {}", entity.getOrderNo());
        return true;
    }

    /**
     * 手动分配车辆
     */
    @Transactional
    public boolean assignVehicle(Long orderId, String vehicleId) {
        TransportOrderEntity entity = orderService.getById(orderId);
        if (entity == null) {
            throw new RuntimeException("订单不存在: " + orderId);
        }

        // 检查车辆是否可用
        Vehicle vehicle = vehicleRegistry.getVehicleDomain(vehicleId);
        if (vehicle == null || !vehicle.canAcceptOrder()) {
            throw new RuntimeException("车辆不可用: " + vehicleId);
        }

        // 分配订单
        TransportOrder kernelOrder = orderRegistry.getOrder(entity.getOrderNo());
        if (kernelOrder != null) {
            kernelOrder.assignTo(vehicleId);
        }

        // 更新数据库
        entity.setState("DISPATCHED");
        entity.setIntendedVehicle(vehicleId);
        entity.setProcessingVehicle(vehicleId);
        orderService.updateById(entity);

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
            String processingVehicle = kernelOrder.getProcessingVehicle();
            if (processingVehicle != null) {
                // 通知内核车辆完成订单
                transportOrderApi.onOrderCompleted(orderId, processingVehicle, true);
            } else {
                kernelOrder.complete();
            }

            TransportOrderEntity entity = orderService.getByOrderNo(orderId);
            if (entity != null) {
                entity.setState("FINISHED");
                orderService.updateById(entity);
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
            String processingVehicle = kernelOrder.getProcessingVehicle();
            if (processingVehicle != null) {
                // 通知内核车辆取消订单
                transportOrderApi.onOrderCompleted(orderId, processingVehicle, false);
            } else {
                kernelOrder.fail();
            }

            TransportOrderEntity entity = orderService.getByOrderNo(orderId);
            if (entity != null) {
                entity.setState("FAILED");
                entity.setProperties(JsonUtils.toJsonString(Map.of("remark", reason != null ? reason : "")));
                orderService.updateById(entity);
            }
            log.info("订单失败: {}, 原因: {}", orderId, reason);
        }
    }

    public void onOrderExecutionResult(String orderId, String vehicleId, boolean success, String reason) {
        TransportOrder kernelOrder = orderRegistry.getOrder(orderId);
        if (kernelOrder == null) {
            return;
        }

        TransportOrderEntity entity = orderService.getByOrderNo(orderId);
        if (success) {
            kernelOrder.complete();
            if (entity != null) {
                entity.setState("FINISHED");
                entity.setProcessingVehicle(vehicleId);
                orderService.updateById(entity);
            }
            log.info("订单执行成功: orderId={}, vehicleId={}", orderId, vehicleId);
        } else {
            kernelOrder.fail();
            if (entity != null) {
                entity.setState("FAILED");
                entity.setProcessingVehicle(vehicleId);
                entity.setProperties(JsonUtils.toJsonString(Map.of("remark", reason != null ? reason : "车辆执行失败")));
                orderService.updateById(entity);
            }
            log.info("订单执行失败: orderId={}, vehicleId={}, reason={}", orderId, vehicleId, reason);
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

    // ===== 查询/持久化方法 =====

    public TableDataInfo<TransportOrderQueryBO> listOrders(TransportOrderQueryBO query, PageQuery pageQuery) {
        TableDataInfo<TransportOrderEntity> entityPage = orderService.selectPageTransportOrder(toEntity(query), pageQuery);
        TableDataInfo<TransportOrderQueryBO> result = new TableDataInfo<>();
        result.setTotal(entityPage.getTotal());
        result.setCode(entityPage.getCode());
        result.setMsg(entityPage.getMsg());
        result.setRows(entityPage.getRows() == null ? List.of()
                : entityPage.getRows().stream().map(this::toQueryBO).collect(Collectors.toList()));
        return result;
    }

    public List<TransportOrderQueryBO> getAllOrders() {
        return orderService.list().stream().map(this::toQueryBO).collect(Collectors.toList());
    }

    public TransportOrderQueryBO getOrderById(Long id) {
        return toQueryBO(orderService.getById(id));
    }

    @Transactional
    public boolean updateOrder(TransportOrderQueryBO bo) {
        return orderService.updateById(toEntity(bo));
    }

    @Transactional
    public boolean batchCreate(List<TransportOrderQueryBO> orders) {
        List<TransportOrderEntity> entities = orders.stream().map(this::toEntity).collect(Collectors.toList());
        return orderService.batchCreateTransportOrder(entities);
    }

    // ===== 辅助方法 =====

    private TransportOrderQueryBO toQueryBO(TransportOrderEntity entity) {
        if (entity == null) {
            return null;
        }
        TransportOrderQueryBO bo = new TransportOrderQueryBO();
        bo.setId(entity.getId());
        bo.setName(entity.getName());
        bo.setOrderNo(entity.getOrderNo());
        bo.setState(entity.getState());
        bo.setIntendedVehicle(entity.getIntendedVehicle());
        bo.setProcessingVehicle(entity.getProcessingVehicle());
        bo.setVehicleVin(entity.getVehicleVin());
        bo.setDestinations(entity.getDestinations());
        bo.setCreationTime(entity.getCreationTime());
        bo.setFinishedTime(entity.getFinishedTime());
        bo.setDeadline(entity.getDeadline());
        bo.setProperties(entity.getProperties());
        return bo;
    }

    private TransportOrderEntity toEntity(TransportOrderQueryBO bo) {
        if (bo == null) {
            return null;
        }
        TransportOrderEntity entity = new TransportOrderEntity();
        entity.setId(bo.getId());
        entity.setName(bo.getName());
        entity.setOrderNo(bo.getOrderNo());
        entity.setState(bo.getState());
        entity.setIntendedVehicle(bo.getIntendedVehicle());
        entity.setProcessingVehicle(bo.getProcessingVehicle());
        entity.setDestinations(bo.getDestinations());
        entity.setCreationTime(bo.getCreationTime());
        entity.setFinishedTime(bo.getFinishedTime());
        entity.setDeadline(bo.getDeadline());
        entity.setProperties(bo.getProperties());
        return entity;
    }

    private TransportOrderDTO toDTO(TransportOrder order) {
        TransportOrderDTO dto = new TransportOrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setName(order.getName());
        dto.setOrderNo(order.getOrderNo());
        dto.setProcessingVehicle(order.getProcessingVehicle());
        dto.setIntendedVehicle(order.getIntendedVehicle());

        // 使用枚举
        dto.setState(OrderStateDTO.valueOf(order.getState().name()));

        return dto;
    }

}
