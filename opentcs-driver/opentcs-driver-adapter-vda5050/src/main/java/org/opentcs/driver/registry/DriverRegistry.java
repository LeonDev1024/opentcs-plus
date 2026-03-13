package org.opentcs.driver.registry;

import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.api.VehicleGateway;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;
import org.opentcs.driver.gateway.VehicleGatewayImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 驱动注册表
 * 统一管理所有驱动适配器和车辆网关
 */
@Component
public class DriverRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DriverRegistry.class);

    // 驱动适配器映射
    private final Map<String, DriverAdapter> adapters = new ConcurrentHashMap<>();

    // 车辆网关
    private VehicleGateway vehicleGateway;

    @PostConstruct
    public void initialize() {
        this.vehicleGateway = new VehicleGatewayImpl();
        vehicleGateway.initialize();
        LOG.info("驱动注册表初始化完成");
    }

    @PreDestroy
    public void destroy() {
        if (vehicleGateway != null) {
            vehicleGateway.destroy();
        }
        for (DriverAdapter adapter : adapters.values()) {
            adapter.destroy();
        }
        adapters.clear();
        LOG.info("驱动注册表已销毁");
    }

    /**
     * 注册驱动适配器
     */
    public void registerAdapter(String driverType, DriverAdapter adapter) {
        adapters.put(driverType, adapter);
        LOG.info("已注册驱动适配器: {} v{}", adapter.getType(), adapter.getVersion());
    }

    /**
     * 获取驱动适配器
     */
    public DriverAdapter getAdapter(String driverType) {
        return adapters.get(driverType);
    }

    /**
     * 获取所有已注册的驱动类型
     */
    public Set<String> getRegisteredDriverTypes() {
        return adapters.keySet();
    }

    /**
     * 注册车辆
     */
    public void registerVehicle(String vehicleId, DriverConfig config) {
        vehicleGateway.registerVehicle(vehicleId, config);
    }

    /**
     * 注销车辆
     */
    public void unregisterVehicle(String vehicleId) {
        vehicleGateway.unregisterVehicle(vehicleId);
    }

    /**
     * 获取所有已注册车辆
     */
    public Set<String> getRegisteredVehicles() {
        return vehicleGateway.getRegisteredVehicles();
    }

    /**
     * 获取所有在线车辆
     */
    public Set<String> getOnlineVehicles() {
        return vehicleGateway.getOnlineVehicles();
    }

    /**
     * 发送订单到车辆
     */
    public void sendOrder(String vehicleId, DriverOrder order) {
        vehicleGateway.sendOrder(vehicleId, order);
    }

    /**
     * 发送即时动作
     */
    public void sendInstantAction(String vehicleId, InstantAction action) {
        vehicleGateway.sendInstantAction(vehicleId, action);
    }

    /**
     * 获取车辆状态
     */
    public VehicleStatus getVehicleStatus(String vehicleId) {
        return vehicleGateway.getVehicleStatus(vehicleId);
    }

    /**
     * 添加状态监听器
     */
    public void addStatusListener(Consumer<VehicleStatus> listener) {
        vehicleGateway.addStatusListener(listener);
    }

    /**
     * 移除状态监听器
     */
    public void removeStatusListener(Consumer<VehicleStatus> listener) {
        vehicleGateway.removeStatusListener(listener);
    }

    /**
     * 获取车辆网关
     */
    public VehicleGateway getVehicleGateway() {
        return vehicleGateway;
    }
}
