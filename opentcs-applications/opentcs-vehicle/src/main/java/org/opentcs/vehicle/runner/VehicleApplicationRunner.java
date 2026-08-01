package org.opentcs.vehicle.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.registry.DriverRegistry;
import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.application.VehicleRegistry;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.vehicle.Vehicle;
import org.opentcs.kernel.domain.vehicle.VehiclePosition;
import org.opentcs.kernel.domain.vehicle.VehicleState;
import org.opentcs.vehicle.persistence.entity.VehicleEntity;
import org.opentcs.vehicle.persistence.service.VehicleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时将数据库中的所有车辆加载到内核内存注册表，并恢复驱动连接。
 *
 * <p>VehicleRegistry 和 VehicleGatewayImpl.vehicleConfigs 均为纯内存结构，
 * 服务重启后为空。本 Runner 在 Spring 上下文就绪后立即执行：
 * <ol>
 *   <li>将车辆状态注册到 VehicleRegistry（保证 isOnline 正常工作）</li>
 *   <li>从 properties 字段反序列化 DriverConfig，重新注册到 DriverRegistry（保证指令下发）</li>
 * </ol>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class VehicleApplicationRunner implements ApplicationRunner {

    private static final double DEFAULT_LOOPBACK_ENERGY_LEVEL = 100.0;

    private final VehicleRepository vehicleRepository;
    private final VehicleRegistry vehicleRegistry;
    private final RoutePlannerImpl routePlanner;
    private final DriverRegistry driverRegistry;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) {
        List<VehicleEntity> entities;
        try {
            entities = vehicleRepository.getAllVehicleStatus();
        } catch (Exception e) {
            log.warn("启动时加载车辆失败，跳过内核/驱动初始化: {}", e.getMessage());
            return;
        }
        int kernelCount = 0;
        int driverCount = 0;

        for (VehicleEntity entity : entities) {
            String name = entity.getName();
            if (name == null || name.isBlank()) continue;

            DriverConfig driverConfig = parseDriverConfig(entity);

            // 1. 注册到内核状态注册表
            Vehicle vehicle = new Vehicle(name);
            vehicle.setName(name);
            vehicle.updateState(parseState(entity.getState()));
            vehicle.updateEnergy(resolveInitialEnergyLevel(entity, driverConfig));
            restoreVehiclePosition(vehicle, entity.getCurrentPosition());
            vehicleRegistry.registerVehicleDomain(vehicle);
            kernelCount++;

            // 2. 恢复驱动连接（若 properties 中有持久化的 DriverConfig）
            if (driverConfig != null) {
                try {
                    driverRegistry.registerVehicle(name, driverConfig);
                    driverCount++;
                } catch (Exception e) {
                    log.warn("车辆 {} 驱动连接恢复失败，跳过: {}", name, e.getMessage());
                }
            }
        }

        log.info("启动初始化完成：内核注册 {} 台，驱动恢复 {} 台", kernelCount, driverCount);
    }

    private DriverConfig parseDriverConfig(VehicleEntity entity) {
        String props = entity.getProperties();
        if (props == null || props.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(props, DriverConfig.class);
        } catch (Exception e) {
            log.warn("车辆 {} 驱动配置恢复失败（properties 格式不符），跳过: {}",
                    entity.getName(), e.getMessage());
            return null;
        }
    }

    private double resolveInitialEnergyLevel(VehicleEntity entity, DriverConfig driverConfig) {
        if (driverConfig != null
                && "LOOPBACK".equalsIgnoreCase(driverConfig.getDriverType())
                && (entity.getEnergyLevel() == null || entity.getEnergyLevel().doubleValue() <= 0)) {
            return DEFAULT_LOOPBACK_ENERGY_LEVEL;
        }
        return entity.getEnergyLevel() == null ? 0.0 : entity.getEnergyLevel().doubleValue();
    }

    private void restoreVehiclePosition(Vehicle vehicle, String pointId) {
        if (pointId == null || pointId.isBlank()) {
            return;
        }
        Point point = routePlanner.getPoint(pointId.trim());
        if (point == null) {
            log.warn("车辆 {} 的当前位置 {} 不在运行地图中，跳过位置恢复",
                    vehicle.getVehicleId(), pointId);
            return;
        }
        vehicle.updatePosition(new VehiclePosition(
                point.getPointId(),
                null,
                point.getX(),
                point.getY(),
                point.getZ(),
                point.getOrientation()));
    }

    private VehicleState parseState(String state) {
        if (state == null) return VehicleState.UNKNOWN;
        return switch (state.toUpperCase()) {
            case "IDLE"                 -> VehicleState.IDLE;
            case "EXECUTING", "WORKING" -> VehicleState.EXECUTING;
            case "CHARGING"             -> VehicleState.CHARGING;
            case "ERROR"                -> VehicleState.ERROR;
            case "UNAVAILABLE"          -> VehicleState.UNAVAILABLE;
            case "OFFLINE"              -> VehicleState.OFFLINE;
            default                     -> VehicleState.UNKNOWN;
        };
    }
}
