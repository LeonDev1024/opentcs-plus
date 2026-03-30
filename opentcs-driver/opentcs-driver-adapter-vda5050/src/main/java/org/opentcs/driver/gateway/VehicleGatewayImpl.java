package org.opentcs.driver.gateway;

import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.api.VehicleGateway;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 车辆网关实现
 * 统一管理所有驱动适配器
 */
public class VehicleGatewayImpl implements VehicleGateway {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleGatewayImpl.class);

    // 驱动适配器映射
    private final Map<String, DriverAdapter> adapters = new ConcurrentHashMap<>();

    // 车辆配置映射
    private final Map<String, DriverConfig> vehicleConfigs = new ConcurrentHashMap<>();

    // 车辆状态缓存
    private final Map<String, VehicleStatus> vehicleStatuses = new ConcurrentHashMap<>();

    // 状态监听器
    private final Set<Consumer<VehicleStatus>> statusListeners = ConcurrentHashMap.newKeySet();

    private boolean initialized = false;

    @Override
    public void initialize() {
        this.initialized = true;
        LOG.info("车辆网关初始化完成（适配器由 DriverRegistry 注册，不在此重复 new）");
    }

    @Override
    public void destroy() {
        // 断开所有车辆连接
        for (DriverAdapter adapter : adapters.values()) {
            adapter.destroy();
        }

        adapters.clear();
        vehicleConfigs.clear();
        vehicleStatuses.clear();
        statusListeners.clear();
        initialized = false;

        LOG.info("车辆网关已销毁");
    }

    @Override
    public void registerVehicle(String vehicleId, DriverConfig config) {
        checkInitialized();

        vehicleConfigs.put(vehicleId, config);

        String driverType = config.getDriverType();
        DriverAdapter adapter = adapters.get(driverType);

        if (adapter == null) {
            LOG.warn("未找到驱动类型 {} 的适配器", driverType);
            return;
        }

        try {
            // 连接车辆
            if (config.getConnectionType().equals("MQTT")) {
                adapter.connect(vehicleId, config.getMqttConfig());
            } else if (config.getConnectionType().equals("TCP")) {
                adapter.connect(vehicleId, config.getTcpConfig());
            }

            LOG.info("车辆 {} 注册成功，驱动类型: {}", vehicleId, driverType);
        } catch (Exception e) {
            LOG.error("车辆 {} 注册失败: {}", vehicleId, e.getMessage());
        }
    }

    @Override
    public void unregisterVehicle(String vehicleId) {
        checkInitialized();

        DriverConfig config = vehicleConfigs.remove(vehicleId);

        if (config != null) {
            String driverType = config.getDriverType();
            DriverAdapter adapter = adapters.get(driverType);

            if (adapter != null) {
                adapter.disconnect(vehicleId);
            }
        }

        vehicleStatuses.remove(vehicleId);

        LOG.info("车辆 {} 已注销", vehicleId);
    }

    @Override
    public Set<String> getRegisteredVehicles() {
        return vehicleConfigs.keySet();
    }

    @Override
    public Set<String> getOnlineVehicles() {
        return vehicleConfigs.entrySet().stream()
                .filter(entry -> {
                    String vehicleId = entry.getKey();
                    DriverConfig config = entry.getValue();
                    DriverAdapter adapter = adapters.get(config.getDriverType());
                    return adapter != null && adapter.isConnected(vehicleId);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public void sendOrder(String vehicleId, DriverOrder order) {
        checkInitialized();

        DriverConfig config = vehicleConfigs.get(vehicleId);
        if (config == null) {
            throw new IllegalArgumentException("车辆未注册: " + vehicleId);
        }

        DriverAdapter adapter = adapters.get(config.getDriverType());
        if (adapter == null) {
            throw new IllegalStateException("驱动适配器不存在: " + config.getDriverType());
        }

        adapter.sendOrder(vehicleId, order);

        LOG.debug("订单已发送到车辆 {}: {}", vehicleId, order.getOrderId());
    }

    @Override
    public void sendInstantAction(String vehicleId, InstantAction action) {
        checkInitialized();

        DriverConfig config = vehicleConfigs.get(vehicleId);
        if (config == null) {
            throw new IllegalArgumentException("车辆未注册: " + vehicleId);
        }

        DriverAdapter adapter = adapters.get(config.getDriverType());
        if (adapter == null) {
            throw new IllegalStateException("驱动适配器不存在: " + config.getDriverType());
        }

        adapter.sendInstantAction(vehicleId, action);

        LOG.debug("即时动作已发送到车辆 {}: {}", vehicleId, action.getActionId());
    }

    @Override
    public VehicleStatus getVehicleStatus(String vehicleId) {
        return vehicleStatuses.get(vehicleId);
    }

    @Override
    public void addStatusListener(Consumer<VehicleStatus> listener) {
        statusListeners.add(listener);
    }

    @Override
    public void removeStatusListener(Consumer<VehicleStatus> listener) {
        statusListeners.remove(listener);
    }

    /**
     * 更新车辆状态并通知监听器
     */
    public void updateVehicleStatus(VehicleStatus status) {
        vehicleStatuses.put(status.getVehicleId(), status);

        // 通知所有监听器
        for (Consumer<VehicleStatus> listener : statusListeners) {
            try {
                listener.accept(status);
            } catch (Exception e) {
                LOG.error("状态监听器执行失败: {}", e.getMessage());
            }
        }
    }

    @Override
    public void registerAdapter(String driverType, DriverAdapter adapter) {
        adapters.put(driverType, adapter);
        LOG.info("已注册驱动适配器: {}", driverType);
    }

    /**
     * 获取驱动适配器
     */
    public DriverAdapter getAdapter(String driverType) {
        return adapters.get(driverType);
    }

    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("车辆网关未初始化");
        }
    }
}
