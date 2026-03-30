package org.opentcs.vehicle.application;

import org.opentcs.kernel.api.OrderLifecycleApi;
import org.opentcs.kernel.application.DispatcherService;
import org.opentcs.kernel.application.VehicleRegistry;
import org.opentcs.kernel.application.TransportOrderRegistry;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.api.dto.VehicleEntityDTO;
import org.opentcs.kernel.api.dto.VehicleDTO;
import org.opentcs.kernel.api.dto.TransportOrderDTO;
import org.opentcs.kernel.api.dto.VehicleStateDTO;
import org.opentcs.kernel.api.dto.OrderStateDTO;
import org.opentcs.kernel.api.dto.PositionDTO;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.registry.DriverRegistry;
import org.opentcs.vehicle.persistence.service.VehicleDomainService;
import org.opentcs.driver.api.dto.VehicleStatus;
import org.opentcs.vehicle.persistence.entity.VehicleEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 车辆应用服务
 * 整合数据库持久化和内核运行时服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleApplicationService {

    private final VehicleDomainService vehicleService;
    private final VehicleRegistry vehicleRegistry;
    private final TransportOrderRegistry orderRegistry;
    private final DispatcherService dispatcherService;
    private final OrderLifecycleApi orderLifecycleApi;
    private final DriverRegistry driverRegistry;

    /**
     * 注册车辆到系统
     * 1. 保存到数据库
     * 2. 注册到内核
     * 3. 配置驱动连接
     */
    @Transactional
    public boolean registerVehicle(VehicleEntityDTO command, DriverConfig driverConfig) {
        VehicleEntity entity = toEntity(command);
        // 1. 保存到数据库
        VehicleEntity dbVehicle = vehicleService.getByName(entity.getName());
        if (dbVehicle != null) {
            throw new RuntimeException("车辆名称已存在: " + entity.getName());
        }

        entity.setState("UNAVAILABLE");
        entity.setIntegrationLevel("TO_BE_UTILIZED");
        vehicleService.save(entity);

        // 2. 注册到内核
        Vehicle kernelVehicle = new Vehicle(entity.getName());
        kernelVehicle.setName(entity.getName());
        kernelVehicle.updateState(VehicleState.UNAVAILABLE);
        vehicleRegistry.registerVehicle(kernelVehicle);

        // 3. 配置驱动连接（延迟连接）
        if (driverConfig != null) {
            driverRegistry.registerVehicle(entity.getName(), driverConfig);
        }

        log.info("车辆注册成功: {}", entity.getName());
        return true;
    }

    /**
     * 注销车辆
     */
    @Transactional
    public boolean unregisterVehicle(Long vehicleId) {
        VehicleEntity entity = vehicleService.getById(vehicleId);
        if (entity == null) {
            throw new RuntimeException("车辆不存在: " + vehicleId);
        }

        // 1. 从内核注销
        vehicleRegistry.unregisterVehicle(entity.getName());

        // 2. 从驱动注销
        driverRegistry.unregisterVehicle(entity.getName());

        // 3. 从数据库删除
        vehicleService.removeById(vehicleId);

        log.info("车辆注销成功: {}", entity.getName());
        return true;
    }

    /**
     * 激活车辆（使车辆上线）
     */
    public boolean activateVehicle(Long vehicleId) {
        VehicleEntity entity = vehicleService.getById(vehicleId);
        if (entity == null) {
            throw new RuntimeException("车辆不存在: " + vehicleId);
        }

        // 更新数据库状态
        entity.setState("IDLE");
        entity.setIntegrationLevel("LEVEL_3");
        vehicleService.updateById(entity);

        // 更新内核状态
        vehicleRegistry.updateVehicleState(entity.getName(), VehicleState.IDLE);

        log.info("车辆激活成功: {}", entity.getName());
        return true;
    }

    /**
     * 停用车辆（使车辆下线）
     */
    public boolean deactivateVehicle(Long vehicleId) {
        VehicleEntity entity = vehicleService.getById(vehicleId);
        if (entity == null) {
            throw new RuntimeException("车辆不存在: " + vehicleId);
        }

        // 检查是否有未完成的订单
        Vehicle kernelVehicle = vehicleRegistry.getVehicle(entity.getName());
        if (kernelVehicle != null && kernelVehicle.getCurrentOrderId() != null) {
            throw new RuntimeException("车辆有未完成的订单，无法停用");
        }

        // 更新数据库状态
        entity.setState("UNAVAILABLE");
        entity.setIntegrationLevel("TO_BE_UTILIZED");
        vehicleService.updateById(entity);

        // 更新内核状态
        vehicleRegistry.updateVehicleState(entity.getName(), VehicleState.UNAVAILABLE);

        log.info("车辆停用成功: {}", entity.getName());
        return true;
    }

    /**
     * 获取车辆实时状态（从内核）
     */
    public VehicleDTO getVehicleRuntimeStatus(String vehicleId) {
        Vehicle kernelVehicle = vehicleRegistry.getVehicle(vehicleId);
        if (kernelVehicle == null) {
            return null;
        }

        return toDTO(kernelVehicle);
    }

    /**
     * 获取所有车辆实时状态
     */
    public List<VehicleDTO> getAllVehicleRuntimeStatus() {
        return vehicleRegistry.getAllVehicles().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取可用车辆
     */
    public List<VehicleDTO> getAvailableVehicles() {
        return vehicleRegistry.getAvailableVehicles().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 发送即时动作到车辆
     */
    public void sendInstantAction(String vehicleId, InstantAction action) {
        // 检查车辆是否在线
        if (!vehicleRegistry.isOnline(vehicleId)) {
            throw new RuntimeException("车辆离线: " + vehicleId);
        }

        // 通过驱动发送动作
        driverRegistry.sendInstantAction(vehicleId, action);

        log.info("即时动作已发送到车辆 {}: {}", vehicleId, action.getActionType());
    }

    /**
     * 处理车辆状态变更（从驱动层回调）
     */
    public void onVehicleStatusChanged(VehicleStatus status) {
        String vehicleId = status.getVehicleId();

        // 从内核获取车辆
        Vehicle kernelVehicle = vehicleRegistry.getVehicle(vehicleId);
        if (kernelVehicle == null) {
            log.warn("状态变更ignored - 车辆未注册: {}", vehicleId);
            return;
        }

        // 更新内核状态
        VehicleState newState = mapToVehicleState(status.getAgvState());
        vehicleRegistry.updateVehicleState(vehicleId, newState);

        // 更新位置信息
        VehiclePosition position = new VehiclePosition(
                status.getPositionId(),
                null,
                status.getxPosition() != null ? status.getxPosition() : 0,
                status.getyPosition() != null ? status.getyPosition() : 0,
                0,
                status.getTheta() != null ? status.getTheta() : 0
        );
        vehicleRegistry.updateVehiclePosition(vehicleId, position);

        // 更新能量
        if (status.getBatteryState() != null) {
            vehicleRegistry.updateVehicleEnergy(vehicleId, status.getBatteryState());
        }

        log.debug("车辆状态已更新: {} -> {}", vehicleId, newState);
    }

    /**
     * 车辆完成订单
     */
    public void onVehicleOrderCompleted(String vehicleId) {
        String orderId = vehicleRegistry.getVehicleCurrentOrder(vehicleId);
        dispatcherService.vehicleCompletedOrder(vehicleId);
        if (orderId != null) {
            orderLifecycleApi.onOrderExecutionResult(orderId, vehicleId, true, null);
        }
        log.info("车辆订单完成: {}", vehicleId);
    }

    /**
     * 车辆订单取消
     */
    public void onVehicleOrderCancelled(String vehicleId) {
        String orderId = vehicleRegistry.getVehicleCurrentOrder(vehicleId);
        dispatcherService.vehicleCancelledOrder(vehicleId);
        if (orderId != null) {
            orderLifecycleApi.onOrderExecutionResult(orderId, vehicleId, false, "车辆侧取消订单");
        }
        log.info("车辆订单取消: {}", vehicleId);
    }

    /**
     * 获取车辆当前订单
     */
    public TransportOrderDTO getVehicleCurrentOrder(String vehicleId) {
        String orderId = vehicleRegistry.getVehicleCurrentOrder(vehicleId);
        if (orderId == null) {
            return null;
        }

        TransportOrder order = orderRegistry.getOrder(orderId);
        if (order == null) {
            return null;
        }

        return toOrderDTO(order);
    }

    /**
     * 获取车辆统计数据
     */
    public Map<String, Object> getVehicleStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 数据库统计
        long totalVehicles = vehicleService.count();
        stats.put("totalVehicles", totalVehicles);

        // 内核运行时统计
        List<Vehicle> allVehicles = vehicleRegistry.getAllVehicles();
        long idleCount = allVehicles.stream()
                .filter(v -> v.getState() == VehicleState.IDLE)
                .count();
        long executingCount = allVehicles.stream()
                .filter(v -> v.getState() == VehicleState.EXECUTING)
                .count();
        long chargingCount = allVehicles.stream()
                .filter(v -> v.getState() == VehicleState.CHARGING)
                .count();
        long errorCount = allVehicles.stream()
                .filter(v -> v.getState() == VehicleState.ERROR)
                .count();
        long offlineCount = allVehicles.stream()
                .filter(v -> v.getState() == VehicleState.OFFLINE)
                .count();

        stats.put("idleVehicles", idleCount);
        stats.put("executingVehicles", executingCount);
        stats.put("chargingVehicles", chargingCount);
        stats.put("errorVehicles", errorCount);
        stats.put("offlineVehicles", offlineCount);

        // 在线率
        double onlineRate = totalVehicles > 0 ? (double) (totalVehicles - offlineCount) / totalVehicles * 100 : 0;
        stats.put("onlineRate", onlineRate);

        // 利用率
        double utilizationRate = totalVehicles > 0 ? (double) executingCount / totalVehicles * 100 : 0;
        stats.put("utilizationRate", utilizationRate);

        return stats;
    }

    // ===== 辅助方法 =====

    private VehicleDTO toDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setVehicleId(vehicle.getVehicleId());
        dto.setName(vehicle.getName());
        dto.setVehicleType(vehicle.getVehicleType());
        dto.setEnergyLevel(vehicle.getEnergyLevel());
        dto.setCurrentOrderId(vehicle.getCurrentOrderId());

        // 位置信息
        if (vehicle.getPosition() != null) {
            PositionDTO positionDTO = new PositionDTO();
            positionDTO.setPointId(vehicle.getPosition().getPointId());
            positionDTO.setX(vehicle.getPosition().getX());
            positionDTO.setY(vehicle.getPosition().getY());
            positionDTO.setZ(vehicle.getPosition().getZ());
            dto.setPosition(positionDTO);
        }

        // 状态信息 - 使用枚举
        dto.setState(org.opentcs.kernel.api.dto.VehicleStateDTO.valueOf(vehicle.getState().name()));

        return dto;
    }

    private TransportOrderDTO toOrderDTO(TransportOrder order) {
        TransportOrderDTO dto = new TransportOrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setName(order.getName());
        dto.setProcessingVehicle(order.getProcessingVehicle());

        // 状态信息 - 使用枚举
        dto.setState(org.opentcs.kernel.api.dto.OrderStateDTO.valueOf(order.getState().name()));

        return dto;
    }

    private VehicleState mapToVehicleState(String agvState) {
        if (agvState == null) {
            return VehicleState.UNKNOWN;
        }

        switch (agvState.toUpperCase()) {
            case "IDLE":
                return VehicleState.IDLE;
            case "EXECUTING":
                return VehicleState.EXECUTING;
            case "PAUSED":
                return VehicleState.PAUSED;
            case "WAITING":
                return VehicleState.WAITING;
            case "ERROR":
                return VehicleState.ERROR;
            default:
                return VehicleState.UNKNOWN;
        }
    }

    private VehicleEntity toEntity(VehicleEntityDTO dto) {
        VehicleEntity entity = new VehicleEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setVinCode(dto.getVinCode());
        entity.setVehicleTypeId(dto.getVehicleTypeId());
        entity.setCurrentPosition(dto.getCurrentPosition());
        entity.setNextPosition(dto.getNextPosition());
        entity.setState(dto.getState());
        entity.setIntegrationLevel(dto.getIntegrationLevel());
        entity.setEnergyLevel(dto.getEnergyLevel());
        entity.setCurrentTransportOrder(dto.getCurrentTransportOrder());
        entity.setProperties(dto.getProperties());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }
}
