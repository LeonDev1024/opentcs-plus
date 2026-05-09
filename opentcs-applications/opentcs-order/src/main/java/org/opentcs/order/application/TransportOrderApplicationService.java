package org.opentcs.order.application;

import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.common.json.utils.JsonUtils;
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

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
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
        OrderSpecDTO orderSpec = toOrderSpec(command);

        String createdOrderId;
        try {
            createdOrderId = transportOrderApi.createOrder(orderSpec);
        } catch (Exception e) {
            log.error("内核创建订单失败: {}", e.getMessage());
            throw new RuntimeException("订单创建失败: " + e.getMessage());
        }

        // 2. 内核成功后先写入 RAW 记录，再激活内核订单。
        // 后续 ACTIVE/分配/完成等状态由领域事件监听器回写，逐步避免应用层双写状态。
        TransportOrderEntity entity = new TransportOrderEntity();
        entity.setOrderNo(createdOrderId);
        entity.setName(command.getName());
        entity.setIntendedVehicle(command.getIntendedVehicle());
        entity.setDestinations(command.getSourcePoint() + "," + command.getDestPoint());
        entity.setState("RAW");
        copyRuntimeProperties(createdOrderId, entity);
        orderService.createTransportOrder(entity);

        try {
            transportOrderApi.activateOrder(createdOrderId);
        } catch (Exception e) {
            log.error("内核激活订单失败: {}", e.getMessage());
            throw new RuntimeException("订单激活失败: " + e.getMessage());
        }

        log.info("运输订单创建成功: {} -> {}", command.getSourcePoint(), command.getDestPoint());
        return true;
    }

    /**
     * 保存草稿运输订单，不进入内核调度。
     */
    @Transactional
    public boolean createDraftTransportOrder(TransportOrderQueryBO bo) {
        TransportOrderEntity entity = toDraftEntity(bo);
        if (entity == null) {
            throw new IllegalArgumentException("草稿订单不能为空");
        }
        return orderService.createTransportOrder(entity);
    }

    /**
     * 提交草稿订单进入内核调度。
     */
    @Transactional
    public boolean submitDraftTransportOrder(Long orderId) {
        TransportOrderEntity entity = orderService.getById(orderId);
        if (entity == null) {
            throw new RuntimeException("订单不存在: " + orderId);
        }
        if (!OrderState.RAW.name().equals(entity.getState())) {
            throw new IllegalStateException("只有 RAW 草稿订单可以提交调度");
        }
        if (entity.getOrderNo() != null && !entity.getOrderNo().isBlank()) {
            throw new IllegalStateException("草稿订单已绑定内核订单号，不能重复提交: " + entity.getOrderNo());
        }

        OrderSpecDTO orderSpec = toOrderSpec(entity);
        String createdOrderId;
        try {
            createdOrderId = transportOrderApi.createOrder(orderSpec);
        } catch (Exception e) {
            log.error("草稿订单提交内核失败: orderId={}, error={}", orderId, e.getMessage());
            throw new RuntimeException("订单提交失败: " + e.getMessage());
        }

        entity.setOrderNo(createdOrderId);
        entity.setState(OrderState.RAW.name());
        copyRuntimeProperties(createdOrderId, entity);
        orderService.updateById(entity);

        try {
            transportOrderApi.activateOrder(createdOrderId);
        } catch (Exception e) {
            log.error("草稿订单激活失败: orderId={}, kernelOrderId={}, error={}",
                    orderId, createdOrderId, e.getMessage());
            throw new RuntimeException("订单激活失败: " + e.getMessage());
        }

        log.info("草稿订单已提交调度: orderId={}, kernelOrderId={}", orderId, createdOrderId);
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

        transportOrderApi.cancelOrder(entity.getOrderNo());

        log.info("订单已取消: {}", entity.getOrderNo());
        return true;
    }

    /**
     * 删除草稿运输订单。
     */
    @Transactional
    public boolean deleteDraftTransportOrder(Long orderId) {
        TransportOrderEntity entity = orderService.getById(orderId);
        if (entity == null) {
            throw new RuntimeException("订单不存在: " + orderId);
        }
        if (!OrderState.RAW.name().equals(entity.getState())) {
            throw new IllegalStateException("运行态订单不能删除，请使用取消接口进入内核状态机");
        }

        return orderService.removeById(orderId);
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

        transportOrderApi.assignOrderToVehicle(entity.getOrderNo(), vehicleId);

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
                transportOrderApi.completeOrder(orderId);
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
                transportOrderApi.failOrder(orderId, reason);
            }
            log.info("订单失败: {}, 原因: {}", orderId, reason);
        }
    }

    public void onOrderExecutionResult(String orderId, String vehicleId, boolean success, String reason) {
        TransportOrder kernelOrder = orderRegistry.getOrder(orderId);
        if (kernelOrder == null) {
            return;
        }

        if (success) {
            transportOrderApi.onOrderCompleted(orderId, vehicleId, true);
            log.info("订单执行成功: orderId={}, vehicleId={}", orderId, vehicleId);
        } else {
            transportOrderApi.failOrder(orderId, reason != null ? reason : "车辆执行失败");
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
        long recoveringOrders = allOrders.stream()
                .filter(o -> o.getState() == OrderState.RECOVERING)
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
        stats.put("recoveringOrders", recoveringOrders);
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
        if (bo == null || bo.getId() == null) {
            throw new IllegalArgumentException("订单 ID 不能为空");
        }
        TransportOrderEntity existing = orderService.getById(bo.getId());
        if (existing == null) {
            throw new RuntimeException("订单不存在: " + bo.getId());
        }
        if (!OrderState.RAW.name().equals(existing.getState())) {
            throw new IllegalStateException("运行态订单不能通过通用更新接口修改，请使用内核订单命令");
        }

        TransportOrderEntity entity = toEntity(bo);
        entity.setOrderNo(existing.getOrderNo());
        entity.setState(existing.getState());
        entity.setProcessingVehicle(existing.getProcessingVehicle());
        entity.setFinishedTime(existing.getFinishedTime());
        return orderService.updateById(entity);
    }

    @Transactional
    public boolean batchCreate(List<TransportOrderQueryBO> orders) {
        if (orders == null || orders.isEmpty()) {
            return true;
        }
        List<TransportOrderEntity> entities = orders.stream()
                .map(this::toDraftEntity)
                .collect(Collectors.toList());
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

    private TransportOrderEntity toDraftEntity(TransportOrderQueryBO bo) {
        TransportOrderEntity entity = toEntity(bo);
        if (entity == null) {
            return null;
        }
        entity.setId(null);
        entity.setOrderNo(null);
        entity.setState(OrderState.RAW.name());
        entity.setProcessingVehicle(null);
        entity.setFinishedTime(null);
        return entity;
    }

    private OrderSpecDTO toOrderSpec(CreateOrderCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("订单创建命令不能为空");
        }
        validatePoint(command.getSourcePoint(), "起始点");
        validatePoint(command.getDestPoint(), "目标点");

        OrderSpecDTO orderSpec = new OrderSpecDTO();
        orderSpec.setName(command.getName());
        orderSpec.setSourcePointId(command.getSourcePoint());
        orderSpec.setDestPointId(command.getDestPoint());
        orderSpec.setIntendedVehicle(command.getIntendedVehicle());
        return orderSpec;
    }

    private OrderSpecDTO toOrderSpec(TransportOrderEntity entity) {
        String[] points = parseDraftDestinations(entity.getDestinations());
        OrderSpecDTO orderSpec = new OrderSpecDTO();
        orderSpec.setName(entity.getName());
        orderSpec.setSourcePointId(points[0]);
        orderSpec.setDestPointId(points[1]);
        orderSpec.setIntendedVehicle(entity.getIntendedVehicle());
        return orderSpec;
    }

    private String[] parseDraftDestinations(String destinations) {
        if (destinations == null || destinations.isBlank()) {
            throw new IllegalArgumentException("草稿订单 destinations 不能为空，格式应为 sourcePoint,destPoint");
        }
        String[] points = destinations.split(",");
        if (points.length != 2) {
            throw new IllegalArgumentException("草稿订单 destinations 格式应为 sourcePoint,destPoint");
        }
        validatePoint(points[0], "起始点");
        validatePoint(points[1], "目标点");
        return new String[] {points[0].trim(), points[1].trim()};
    }

    private void validatePoint(String pointId, String fieldName) {
        if (pointId == null || pointId.isBlank()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
    }

    private void copyRuntimeProperties(String orderId, TransportOrderEntity entity) {
        var order = transportOrderApi.getOrder(orderId);
        if (order != null) {
            order.map(TransportOrderDTO::getProperties)
                    .ifPresent(properties -> mergeProperties(entity, properties));
        }
    }

    private void mergeProperties(TransportOrderEntity entity, Map<String, String> properties) {
        if (properties == null || properties.isEmpty()) {
            return;
        }
        Map<String, String> merged = parseProperties(entity.getProperties());
        merged.putAll(properties);
        entity.setProperties(JsonUtils.toJsonString(merged));
    }

    private Map<String, String> parseProperties(String properties) {
        if (properties == null || properties.isBlank()) {
            return new LinkedHashMap<>();
        }
        Map<String, String> parsed = JsonUtils.parseObject(
                properties, new TypeReference<Map<String, String>>() {
                });
        return parsed == null ? new LinkedHashMap<>() : new LinkedHashMap<>(parsed);
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
