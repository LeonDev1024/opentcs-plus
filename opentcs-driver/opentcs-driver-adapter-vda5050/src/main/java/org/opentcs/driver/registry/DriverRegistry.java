package org.opentcs.driver.registry;

import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.api.VehicleGateway;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 驱动注册表：由 Spring 单例装配，注入统一的 {@link VehicleGateway} 与各 {@link DriverAdapter}。
 */
public class DriverRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DriverRegistry.class);

    private final Map<String, DriverAdapter> adapters = new ConcurrentHashMap<>();

    private final VehicleGateway vehicleGateway;

    public DriverRegistry(VehicleGateway vehicleGateway) {
        this.vehicleGateway = Objects.requireNonNull(vehicleGateway, "vehicleGateway");
    }

    /**
     * 注册驱动适配器（通常在 {@link org.opentcs.driver.config.DriverConfiguration} 中调用）。
     */
    public void registerAdapter(String driverType, DriverAdapter adapter) {
        adapters.put(driverType, adapter);
        vehicleGateway.registerAdapter(driverType, adapter);
        LOG.info("已注册驱动适配器: {} v{}", adapter.getType(), adapter.getVersion());
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

    public DriverAdapter getAdapter(String driverType) {
        return adapters.get(driverType);
    }

    public Set<String> getRegisteredDriverTypes() {
        return adapters.keySet();
    }

    public void registerVehicle(String vehicleId, DriverConfig config) {
        vehicleGateway.registerVehicle(vehicleId, config);
    }

    public void unregisterVehicle(String vehicleId) {
        vehicleGateway.unregisterVehicle(vehicleId);
    }

    public Set<String> getRegisteredVehicles() {
        return vehicleGateway.getRegisteredVehicles();
    }

    public Set<String> getOnlineVehicles() {
        return vehicleGateway.getOnlineVehicles();
    }

    public void sendOrder(String vehicleId, DriverOrder order) {
        vehicleGateway.sendOrder(vehicleId, order);
    }

    public void sendInstantAction(String vehicleId, InstantAction action) {
        vehicleGateway.sendInstantAction(vehicleId, action);
    }

    public VehicleStatus getVehicleStatus(String vehicleId) {
        return vehicleGateway.getVehicleStatus(vehicleId);
    }

    public void addStatusListener(Consumer<VehicleStatus> listener) {
        vehicleGateway.addStatusListener(listener);
    }

    public void removeStatusListener(Consumer<VehicleStatus> listener) {
        vehicleGateway.removeStatusListener(listener);
    }

    public VehicleGateway getVehicleGateway() {
        return vehicleGateway;
    }
}
