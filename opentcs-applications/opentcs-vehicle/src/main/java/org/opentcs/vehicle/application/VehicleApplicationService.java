package org.opentcs.vehicle.application;

import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;
import org.opentcs.driver.registry.DriverRegistry;
import org.opentcs.kernel.api.OrderLifecycleApi;
import org.opentcs.kernel.api.dto.PositionDTO;
import org.opentcs.kernel.api.dto.TransportOrderDTO;
import org.opentcs.kernel.api.dto.VehicleDTO;
import org.opentcs.vehicle.application.bo.VehicleBO;
import org.opentcs.vehicle.application.bo.VehicleCrudBO;
import org.opentcs.vehicle.application.bo.OpsActionResultBO;
import org.opentcs.kernel.application.TransportOrderRegistry;
import org.opentcs.kernel.application.VehicleRegistry;
import org.opentcs.kernel.domain.order.TransportOrder;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.opentcs.vehicle.controller.req.GoChargeRequest;
import org.opentcs.vehicle.controller.req.MapSwitchRequest;
import org.opentcs.vehicle.controller.req.ModeSwitchRequest;
import org.opentcs.vehicle.controller.req.MoveRequest;
import org.opentcs.vehicle.persistence.entity.VehicleEntity;
import org.opentcs.vehicle.persistence.service.VehicleRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 车辆应用服务
 * 整合数据库持久化和内核运行时服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleApplicationService {

    private final VehicleRepository vehicleService;
    private final VehicleRegistry vehicleRegistry;
    private final TransportOrderRegistry orderRegistry;
    private final ObjectMapper objectMapper;
    private final OrderLifecycleApi orderLifecycleApi;
    private final DriverRegistry driverRegistry;
    private final List<Map<String, Object>> opsActionRecords = new CopyOnWriteArrayList<>();

    /**
     * 启动时向驱动层注册状态监听器，自动检测订单完成事件。
     */
    @PostConstruct
    public void initStatusListener() {
        driverRegistry.addStatusListener(this::handleDriverStatus);
        log.info("已注册驱动状态监听器");
    }

    /**
     * 驱动层推送的状态变更处理：
     * 1. 更新内核运行时状态（位置、电量、AGV 状态）
     * 2. 检测订单完成：车辆从 EXECUTING/WAITING 转为 IDLE 时，通知 OrderLifecycleApi
     */
    private void handleDriverStatus(VehicleStatus status) {
        String vehicleId = status.getVehicleId();
        Vehicle kernelVehicle = vehicleRegistry.getVehicleDomain(vehicleId);
        if (kernelVehicle == null) {
            log.warn("未注册车辆的状态推送，已忽略: {}", vehicleId);
            return;
        }

        VehicleState previousState = kernelVehicle.getState();
        String activeOrderId = vehicleRegistry.getVehicleCurrentOrder(vehicleId);

        // 更新内核状态（位置、电量、AGV 状态）
        onVehicleStatusChanged(status);

        // 检测订单完成：车辆曾处于执行/等待状态，且 AGV 回报 IDLE
        boolean wasExecuting = previousState == VehicleState.EXECUTING
                || previousState == VehicleState.WAITING;
        boolean isNowIdle = "IDLE".equalsIgnoreCase(status.getAgvState());

        if (wasExecuting && isNowIdle && activeOrderId != null) {
            boolean hasFault = status.getErrors() != null && status.getErrors().stream()
                    .anyMatch(e -> "FAULT".equalsIgnoreCase(e.getErrorLevel()));
            orderLifecycleApi.onOrderExecutionResult(
                    activeOrderId, vehicleId, !hasFault,
                    hasFault ? "AGV 报告 FAULT 错误，订单执行失败" : null);
            log.info("订单执行结果已上报: orderId={}, vehicleId={}, success={}", activeOrderId, vehicleId, !hasFault);
        }
    }

    /**
     * 注册车辆到系统
     * 1. 保存到数据库
     * 2. 注册到内核
     * 3. 配置驱动连接
     */
    @Transactional
    public boolean registerVehicle(VehicleBO command, DriverConfig driverConfig) {
        VehicleEntity entity = toEntity(command);
        // 1. 保存到数据库
        VehicleEntity dbVehicle = vehicleService.getByName(entity.getName());
        if (dbVehicle != null) {
            throw new RuntimeException("车辆名称已存在: " + entity.getName());
        }

        entity.setState("UNAVAILABLE");
        entity.setIntegrationLevel("TO_BE_UTILIZED");
        // 持久化驱动配置（用于服务重启后恢复连接）
        if (driverConfig != null) {
            try {
                entity.setProperties(objectMapper.writeValueAsString(driverConfig));
            } catch (Exception e) {
                log.warn("驱动配置序列化失败，将跳过持久化: {}", e.getMessage());
            }
        }
        vehicleService.save(entity);

        // 2. 注册到内核
        Vehicle kernelVehicle = new Vehicle(entity.getName());
        kernelVehicle.setName(entity.getName());
        kernelVehicle.updateState(VehicleState.UNAVAILABLE);
        vehicleRegistry.registerVehicleDomain(kernelVehicle);

        // 3. 配置驱动连接
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
        vehicleRegistry.unregisterVehicleDomain(entity.getName());

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
        vehicleRegistry.updateVehicleStateDomain(entity.getName(), VehicleState.IDLE);

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
        Vehicle kernelVehicle = vehicleRegistry.getVehicleDomain(entity.getName());
        if (kernelVehicle != null && kernelVehicle.getCurrentOrderId() != null) {
            throw new RuntimeException("车辆有未完成的订单，无法停用");
        }

        // 更新数据库状态
        entity.setState("UNAVAILABLE");
        entity.setIntegrationLevel("TO_BE_UTILIZED");
        vehicleService.updateById(entity);

        // 更新内核状态
        vehicleRegistry.updateVehicleStateDomain(entity.getName(), VehicleState.UNAVAILABLE);

        log.info("车辆停用成功: {}", entity.getName());
        return true;
    }

    /**
     * 获取车辆实时状态（从内核）
     */
    public VehicleDTO getVehicleRuntimeStatus(String vehicleId) {
        Vehicle kernelVehicle = vehicleRegistry.getVehicleDomain(vehicleId);
        if (kernelVehicle == null) {
            return null;
        }

        return toDTO(kernelVehicle);
    }

    /**
     * 获取所有车辆实时状态
     */
    public List<VehicleDTO> getAllVehicleRuntimeStatus() {
        return vehicleRegistry.getAllVehicleDomains().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取可用车辆
     */
    public List<VehicleDTO> getAvailableVehicles() {
        return vehicleRegistry.getAvailableVehicleDomains().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 发送即时动作到车辆
     */
    public void sendInstantAction(String vehicleId, InstantAction action) {
        // 检查车辆是否在线
        if (!vehicleRegistry.isOnline(vehicleId)) {  // domain helper
            throw new RuntimeException("车辆离线: " + vehicleId);
        }

        // 通过驱动发送动作
        driverRegistry.sendInstantAction(vehicleId, action);

        log.info("即时动作已发送到车辆 {}: {}", vehicleId, action.getActionType());
    }

    public OpsActionResultBO switchMode(String vehicleName, ModeSwitchRequest request) {
        String targetMode = request.getTargetMode() == null ? "AUTOMATIC" : request.getTargetMode().toUpperCase();
        String actionType = "AUTOMATIC".equals(targetMode) ? "RESUME" : "PAUSE";

        Map<String, String> params = new HashMap<>();
        params.put("targetMode", targetMode);
        params.put("executePolicy", nvl(request.getExecutePolicy(), "REJECT_IF_BUSY"));
        params.put("reason", nvl(request.getReason(), ""));
        return executeOpsAction(vehicleName, "MODE_SWITCH", actionType, params);
    }

    public OpsActionResultBO switchMap(String vehicleName, MapSwitchRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("targetMapId", nvl(request.getTargetMapId(), ""));
        params.put("targetMapVersion", nvl(request.getTargetMapVersion(), ""));
        params.put("initPosition", nvl(request.getInitPosition(), ""));
        params.put("fallbackMapId", nvl(request.getFallbackMapId(), ""));
        return executeOpsAction(vehicleName, "MAP_SWITCH", "enableMap", params);
    }

    public OpsActionResultBO goCharge(String vehicleName, GoChargeRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("chargePolicy", nvl(request.getChargePolicy(), "NEAREST"));
        params.put("stationId", nvl(request.getStationId(), ""));
        params.put("interruptPolicy", nvl(request.getInterruptPolicy(), "WAIT_CURRENT_TASK"));
        if (request.getMinSocThreshold() != null) {
            params.put("minSocThreshold", String.valueOf(request.getMinSocThreshold()));
        }
        return executeOpsAction(vehicleName, "GO_CHARGE", "CHARGE", params);
    }

    public OpsActionResultBO moveVehicle(String vehicleName, MoveRequest request) {
        String moveType = nvl(request.getMoveType(), "MOVE_TO_NODE");
        Map<String, String> params = new HashMap<>();
        params.put("moveType", moveType);
        params.put("targetNodeId", nvl(request.getTargetNodeId(), ""));
        params.put("mapId", nvl(request.getMapId(), ""));
        params.put("confirmRisk", String.valueOf(Boolean.TRUE.equals(request.getConfirmRisk())));
        if (request.getX() != null) {
            params.put("x", String.valueOf(request.getX()));
        }
        if (request.getY() != null) {
            params.put("y", String.valueOf(request.getY()));
        }
        if (request.getTheta() != null) {
            params.put("theta", String.valueOf(request.getTheta()));
        }

        String actionType = "INIT_POSITION".equalsIgnoreCase(moveType) ? "initPosition" : "MOVE";
        return executeOpsAction(vehicleName, "MOVE", actionType, params);
    }

    public Map<String, Object> precheck(String vehicleName, String actionType) {
        Map<String, Object> result = new HashMap<>();
        boolean online = vehicleRegistry.isOnline(vehicleName);
        Vehicle vehicle = vehicleRegistry.getVehicleDomain(vehicleName);

        result.put("vehicleName", vehicleName);
        result.put("actionType", actionType);
        result.put("online", online);
        result.put("state", vehicle == null ? "UNKNOWN" : vehicle.getState().name());
        result.put("allow", online);
        result.put("reasonCode", online ? null : "OPS_AMR_001");
        result.put("reasonMessage", online ? null : "车辆离线，禁止执行运维动作");
        return result;
    }

    public List<Map<String, Object>> listOpsActionRecords(String vehicleName) {
        if (vehicleName == null || vehicleName.isBlank()) {
            return new ArrayList<>(opsActionRecords);
        }
        return opsActionRecords.stream()
                .filter(record -> vehicleName.equals(record.get("vehicleName")))
                .collect(Collectors.toList());
    }

    private OpsActionResultBO executeOpsAction(String vehicleName,
                                               String actionCategory,
                                               String actionType,
                                               Map<String, String> parameters) {
        if (!vehicleRegistry.isOnline(vehicleName)) {
            throw new RuntimeException("车辆离线: " + vehicleName);
        }

        String actionId = "OPS-" + System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString().replace("-", "");

        InstantAction action = new InstantAction(actionId, actionType);
        action.setParameters(parameters);
        driverRegistry.sendInstantAction(vehicleName, action);

        Map<String, Object> record = new HashMap<>();
        record.put("actionId", actionId);
        record.put("traceId", traceId);
        record.put("vehicleName", vehicleName);
        record.put("actionCategory", actionCategory);
        record.put("actionType", actionType);
        record.put("requestPayload", parameters);
        record.put("executeStatus", "ACCEPTED");
        record.put("operatedAt", LocalDateTime.now().toString());
        opsActionRecords.add(0, record);

        OpsActionResultBO result = new OpsActionResultBO();
        result.setActionId(actionId);
        result.setAccepted(true);
        result.setStatus("PENDING");
        result.setTraceId(traceId);
        result.setEstimatedFinishTime(LocalDateTime.now().plusMinutes(1).toString());
        return result;
    }

    private String nvl(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    /**
     * 处理车辆状态变更（从驱动层回调）
     */
    public void onVehicleStatusChanged(VehicleStatus status) {
        String vehicleId = status.getVehicleId();

        // 从内核获取车辆
        Vehicle kernelVehicle = vehicleRegistry.getVehicleDomain(vehicleId);
        if (kernelVehicle == null) {
            log.warn("状态变更ignored - 车辆未注册: {}", vehicleId);
            return;
        }

        // 更新内核状态
        VehicleState newState = mapToVehicleState(status.getAgvState());
        vehicleRegistry.updateVehicleStateDomain(vehicleId, newState);

        // 更新位置信息
        VehiclePosition position = new VehiclePosition(
                status.getPositionId(),
                null,
                status.getxPosition() != null ? status.getxPosition() : 0,
                status.getyPosition() != null ? status.getyPosition() : 0,
                0,
                status.getTheta() != null ? status.getTheta() : 0
        );
        vehicleRegistry.updateVehiclePositionDomain(vehicleId, position);

        // 更新能量
        if (status.getBatteryState() != null) {
            vehicleRegistry.updateVehicleEnergyDomain(vehicleId, status.getBatteryState());
        }

        log.debug("车辆状态已更新: {} -> {}", vehicleId, newState);
    }

    /**
     * 车辆完成订单——通过 OrderLifecycleApi 统一回调，由 OrderLifecycleService 负责调度状态更新。
     */
    public void onVehicleOrderCompleted(String vehicleId) {
        String orderId = vehicleRegistry.getVehicleCurrentOrder(vehicleId);
        if (orderId != null) {
            orderLifecycleApi.onOrderExecutionResult(orderId, vehicleId, true, null);
        }
        log.info("车辆订单完成: vehicleId={}, orderId={}", vehicleId, orderId);
    }

    /**
     * 车辆订单取消——通过 OrderLifecycleApi 统一回调。
     */
    public void onVehicleOrderCancelled(String vehicleId) {
        String orderId = vehicleRegistry.getVehicleCurrentOrder(vehicleId);
        if (orderId != null) {
            orderLifecycleApi.onOrderExecutionResult(orderId, vehicleId, false, "车辆侧取消订单");
        }
        log.info("车辆订单取消: vehicleId={}, orderId={}", vehicleId, orderId);
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
        List<Vehicle> allVehicles = vehicleRegistry.getAllVehicleDomains();
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

    // ===== CRUD 委托方法（供 Controller 调用，屏蔽 persistence 层） =====

    public TableDataInfo<VehicleCrudBO> listVehicles(VehicleBO query, PageQuery pageQuery) {
        TableDataInfo<VehicleEntity> entityPage = vehicleService.selectPageVehicle(toEntity(query), pageQuery);
        TableDataInfo<VehicleCrudBO> result = new TableDataInfo<>();
        result.setTotal(entityPage.getTotal());
        result.setCode(entityPage.getCode());
        result.setMsg(entityPage.getMsg());
        result.setRows(entityPage.getRows() == null ? List.of()
                : entityPage.getRows().stream().map(this::toCrudBO).collect(Collectors.toList()));
        return result;
    }

    public List<VehicleCrudBO> getAllVehicles() {
        return vehicleService.list().stream()
                .map(this::toCrudBO)
                .collect(Collectors.toList());
    }

    public VehicleCrudBO getVehicleCrudById(Long id) {
        return toCrudBO(vehicleService.getById(id));
    }

    @Transactional
    public boolean createVehicle(VehicleBO vehicle) {
        return vehicleService.save(toEntity(vehicle));
    }

    @Transactional
    public boolean updateVehicle(VehicleBO vehicle) {
        return vehicleService.updateById(toEntity(vehicle));
    }

    @Transactional
    public boolean deleteVehicle(Long id) {
        return vehicleService.removeById(id);
    }

    public VehicleBO getVehicleStatus(Long id) {
        VehicleEntity entity = vehicleService.getVehicleStatus(id);
        return toBO(entity);
    }

    public List<VehicleBO> getAllVehicleStatus() {
        List<VehicleEntity> list = vehicleService.getAllVehicleStatus();
        if (list == null) return new ArrayList<>();
        return list.stream().map(this::toBO).collect(Collectors.toList());
    }

    public TableDataInfo<Map<String, Object>> getVehicleHistory(Long id, PageQuery pageQuery) {
        return vehicleService.getVehicleHistory(id, pageQuery);
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

    private VehicleBO toBO(VehicleEntity entity) {
        if (entity == null) return null;
        VehicleBO bo = new VehicleBO();
        bo.setId(entity.getId());
        bo.setName(entity.getName());
        bo.setVinCode(entity.getVinCode());
        bo.setVehicleTypeId(entity.getVehicleTypeId());
        bo.setCurrentPosition(entity.getCurrentPosition());
        bo.setNextPosition(entity.getNextPosition());
        bo.setState(entity.getState());
        bo.setIntegrationLevel(entity.getIntegrationLevel());
        bo.setEnergyLevel(entity.getEnergyLevel());
        bo.setCurrentTransportOrder(entity.getCurrentTransportOrder());
        bo.setProperties(entity.getProperties());
        bo.setCreateTime(entity.getCreateTime());
        bo.setUpdateTime(entity.getUpdateTime());
        return bo;
    }

    private VehicleCrudBO toCrudBO(VehicleEntity entity) {
        if (entity == null) return null;
        VehicleCrudBO bo = new VehicleCrudBO();
        bo.setId(entity.getId());
        bo.setName(entity.getName());
        bo.setVinCode(entity.getVinCode());
        bo.setVehicleTypeId(entity.getVehicleTypeId());
        bo.setVehicleTypeName(entity.getVehicleTypeName());
        bo.setCurrentPosition(entity.getCurrentPosition());
        bo.setNextPosition(entity.getNextPosition());
        bo.setState(entity.getState());
        bo.setIntegrationLevel(entity.getIntegrationLevel());
        bo.setEnergyLevel(entity.getEnergyLevel());
        bo.setCurrentTransportOrder(entity.getCurrentTransportOrder());
        bo.setProperties(entity.getProperties());
        bo.setCreateTime(entity.getCreateTime());
        bo.setUpdateTime(entity.getUpdateTime());
        return bo;
    }

    private VehicleEntity toEntity(VehicleBO bo) {
        VehicleEntity entity = new VehicleEntity();
        entity.setId(bo.getId());
        entity.setName(bo.getName());
        entity.setVinCode(bo.getVinCode());
        entity.setVehicleTypeId(bo.getVehicleTypeId());
        entity.setCurrentPosition(bo.getCurrentPosition());
        entity.setNextPosition(bo.getNextPosition());
        entity.setState(bo.getState());
        entity.setIntegrationLevel(bo.getIntegrationLevel());
        entity.setEnergyLevel(bo.getEnergyLevel());
        entity.setCurrentTransportOrder(bo.getCurrentTransportOrder());
        entity.setProperties(bo.getProperties());
        entity.setCreateTime(bo.getCreateTime());
        entity.setUpdateTime(bo.getUpdateTime());
        return entity;
    }
}
