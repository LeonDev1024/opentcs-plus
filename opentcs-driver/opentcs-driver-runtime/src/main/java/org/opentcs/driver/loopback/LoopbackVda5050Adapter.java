package org.opentcs.driver.loopback;

import org.opentcs.driver.api.DriverAdapter;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.InstantAction;
import org.opentcs.driver.api.dto.VehicleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本地闭环 Loopback 适配器：sendOrder 后自动回放节点到达与 IDLE，用于无真实车验收。
 */
public class LoopbackVda5050Adapter implements DriverAdapter {

    public static final String DRIVER_TYPE = "LOOPBACK";
    public static final String DRIVER_VERSION = "1.0.0";

    private static final Logger LOG = LoggerFactory.getLogger(LoopbackVda5050Adapter.class);
    private static final double DEFAULT_BATTERY_LEVEL = 100.0;
    private static final double DEFAULT_SPEED_METERS_PER_SECOND = 0.8;
    /** 当前地图坐标以厘米为模型单位：1 m = 100 map units。 */
    private static final double DEFAULT_MAP_UNITS_PER_METER = 100.0;
    private static final long STATUS_INTERVAL_MS = 200L;
    private static final String PROPERTY_SIMULATION_SPEED_MPS = "simulationSpeedMps";
    private static final String PROPERTY_MAP_UNITS_PER_METER = "mapUnitsPerMeter";

    private final Map<String, ConcurrentLinkedQueue<VehicleStatus>> statusQueues = new ConcurrentHashMap<>();
    private final Map<String, Boolean> connected = new ConcurrentHashMap<>();
    private final Map<String, Double> simulationSpeeds = new ConcurrentHashMap<>();
    private final Map<String, Double> mapUnitsPerMeter = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> simulationVersions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "loopback-vda5050");
        t.setDaemon(true);
        return t;
    });
    private final AtomicInteger updateSeq = new AtomicInteger(0);

    private boolean initialized;

    @Override
    public String getType() {
        return DRIVER_TYPE;
    }

    @Override
    public String getVersion() {
        return DRIVER_VERSION;
    }

    @Override
    public void initialize(DriverConfig config) {
        this.initialized = true;
    }

    @Override
    public void destroy() {
        scheduler.shutdownNow();
        statusQueues.clear();
        connected.clear();
        initialized = false;
    }

    @Override
    public void connect(String vehicleId, Object connectionConfig) {
        checkInitialized();
        connected.put(vehicleId, true);
        statusQueues.putIfAbsent(vehicleId, new ConcurrentLinkedQueue<>());
        simulationVersions.putIfAbsent(vehicleId, new AtomicInteger());

        DriverConfig config = connectionConfig instanceof DriverConfig
                ? (DriverConfig) connectionConfig
                : null;
        Map<String, String> properties = config != null ? config.getProperties() : null;
        double speedMps = positiveDouble(
                properties, PROPERTY_SIMULATION_SPEED_MPS, DEFAULT_SPEED_METERS_PER_SECOND);
        double unitsPerMeter = positiveDouble(
                properties, PROPERTY_MAP_UNITS_PER_METER, DEFAULT_MAP_UNITS_PER_METER);
        simulationSpeeds.put(vehicleId, speedMps);
        mapUnitsPerMeter.put(vehicleId, unitsPerMeter);

        LOG.info("Loopback 车辆已连接: {}, speed={}m/s, mapUnitsPerMeter={}",
                vehicleId, speedMps, unitsPerMeter);
    }

    @Override
    public void disconnect(String vehicleId) {
        connected.remove(vehicleId);
        statusQueues.remove(vehicleId);
        simulationSpeeds.remove(vehicleId);
        mapUnitsPerMeter.remove(vehicleId);
        AtomicInteger version = simulationVersions.remove(vehicleId);
        if (version != null) {
            version.incrementAndGet();
        }
    }

    @Override
    public boolean isConnected(String vehicleId) {
        return Boolean.TRUE.equals(connected.get(vehicleId));
    }

    @Override
    public void sendOrder(String vehicleId, DriverOrder order) {
        checkInitialized();
        if (!isConnected(vehicleId)) {
            throw new IllegalStateException("车辆未连接: " + vehicleId);
        }

        List<DriverOrder.Node> nodes = order.getNodes() != null ? order.getNodes() : List.of();
        int simulationVersion = simulationVersions
                .computeIfAbsent(vehicleId, id -> new AtomicInteger())
                .incrementAndGet();
        double speedMps = simulationSpeeds.getOrDefault(
                vehicleId, DEFAULT_SPEED_METERS_PER_SECOND);
        double unitsPerMeter = mapUnitsPerMeter.getOrDefault(
                vehicleId, DEFAULT_MAP_UNITS_PER_METER);
        double speedUnitsPerSecond = speedMps * unitsPerMeter;

        if (nodes.isEmpty()) {
            enqueueIfCurrent(vehicleId, simulationVersion, buildStatus(
                    vehicleId, order.getOrderId(), "EXECUTING",
                    null, null, null, 0.0, true));
            schedule(vehicleId, simulationVersion, STATUS_INTERVAL_MS, () -> buildStatus(
                    vehicleId, order.getOrderId(), "IDLE",
                    null, null, null, 0.0, false));
            return;
        }

        DriverOrder.Node first = nodes.get(0);
        double initialHeading = nodes.size() > 1
                ? headingDegrees(first, nodes.get(1))
                : valueOrDefault(first.getTheta(), 0.0);
        enqueueIfCurrent(vehicleId, simulationVersion, buildStatus(
                vehicleId, order.getOrderId(), "EXECUTING",
                first.getNodeId(), first.getX(), first.getY(), initialHeading, true));

        long elapsedMs = 0L;
        double lastHeading = initialHeading;
        for (int i = 1; i < nodes.size(); i++) {
            DriverOrder.Node from = nodes.get(i - 1);
            DriverOrder.Node to = nodes.get(i);
            double distance = distance(from, to);
            long segmentDurationMs = Math.max(
                    STATUS_INTERVAL_MS,
                    Math.round(distance / speedUnitsPerSecond * 1000.0));
            int sampleCount = Math.max(
                    1, (int) Math.ceil((double) segmentDurationMs / STATUS_INTERVAL_MS));
            double heading = headingDegrees(from, to);
            lastHeading = heading;

            for (int sample = 1; sample <= sampleCount; sample++) {
                double progress = (double) sample / sampleCount;
                long at = elapsedMs + Math.round(segmentDurationMs * progress);
                boolean arrived = sample == sampleCount;
                String lastReachedNode = arrived ? to.getNodeId() : from.getNodeId();
                double x = interpolate(from.getX(), to.getX(), progress);
                double y = interpolate(from.getY(), to.getY(), progress);
                boolean driving = !arrived || i < nodes.size() - 1;
                schedule(vehicleId, simulationVersion, at, () -> buildStatus(
                        vehicleId, order.getOrderId(), "EXECUTING",
                        lastReachedNode, x, y, heading, driving));
            }
            elapsedMs += segmentDurationMs;
        }

        DriverOrder.Node last = nodes.get(nodes.size() - 1);
        double finalHeading = lastHeading;
        schedule(vehicleId, simulationVersion, elapsedMs + STATUS_INTERVAL_MS, () -> buildStatus(
                vehicleId, order.getOrderId(), "IDLE",
                last.getNodeId(), last.getX(), last.getY(), finalHeading, false));

        LOG.info(
                "Loopback 已接收订单并开始匀速回放: vehicleId={}, orderId={}, nodes={}, speed={}m/s, durationMs={}",
                vehicleId, order.getOrderId(), nodes.size(), speedMps,
                elapsedMs + STATUS_INTERVAL_MS);
    }

    @Override
    public void sendInstantAction(String vehicleId, InstantAction action) {
        checkInitialized();
        LOG.info("Loopback 即时动作: vehicleId={}, actionType={}", vehicleId, action.getActionType());
    }

    @Override
    public VehicleStatus receiveStatus(String vehicleId) {
        ConcurrentLinkedQueue<VehicleStatus> queue = statusQueues.get(vehicleId);
        return queue == null ? null : queue.poll();
    }

    @Override
    public Set<String> getConnectedVehicles() {
        return connected.keySet();
    }

    private void enqueue(String vehicleId, VehicleStatus status) {
        statusQueues.computeIfAbsent(vehicleId, id -> new ConcurrentLinkedQueue<>()).offer(status);
    }

    private void enqueueIfCurrent(String vehicleId, int simulationVersion, VehicleStatus status) {
        AtomicInteger currentVersion = simulationVersions.get(vehicleId);
        if (isConnected(vehicleId)
                && currentVersion != null
                && currentVersion.get() == simulationVersion) {
            enqueue(vehicleId, status);
        }
    }

    private void schedule(String vehicleId,
                          int simulationVersion,
                          long delayMs,
                          java.util.function.Supplier<VehicleStatus> statusSupplier) {
        scheduler.schedule(
                () -> enqueueIfCurrent(vehicleId, simulationVersion, statusSupplier.get()),
                delayMs,
                TimeUnit.MILLISECONDS);
    }

    private VehicleStatus buildStatus(String vehicleId,
                                      String orderId,
                                      String agvState,
                                      String nodeId,
                                      Double x,
                                      Double y,
                                      double theta,
                                      boolean driving) {
        VehicleStatus status = new VehicleStatus();
        status.setVehicleId(vehicleId);
        status.setOrderId(orderId);
        status.setOrderUpdateId(updateSeq.incrementAndGet());
        status.setAgvState(agvState);
        status.setOperationMode("AUTOMATIC");
        status.setLastNodeId(nodeId);
        status.setPositionId(nodeId);
        status.setxPosition(x);
        status.setyPosition(y);
        status.setTheta(theta);
        status.setDriving(driving);
        status.setBatteryState(DEFAULT_BATTERY_LEVEL);
        status.setCharging(false);
        if (orderId != null && !"IDLE".equalsIgnoreCase(agvState)) {
            status.setActiveOrderIds(List.of(orderId));
        } else {
            status.setActiveOrderIds(new ArrayList<>());
        }
        if (nodeId != null) {
            VehicleStatus.NodeState nodeState = new VehicleStatus.NodeState();
            nodeState.setNodeId(nodeId);
            nodeState.setReleased(true);
            status.setNodeStates(List.of(nodeState));
        }
        return status;
    }

    private double positiveDouble(Map<String, String> properties,
                                  String key,
                                  double defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        String value = properties.get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            double parsed = Double.parseDouble(value.trim());
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException e) {
            LOG.warn("Loopback 配置 {}={} 无效，使用默认值 {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    private double distance(DriverOrder.Node from, DriverOrder.Node to) {
        double dx = valueOrDefault(to.getX(), 0.0) - valueOrDefault(from.getX(), 0.0);
        double dy = valueOrDefault(to.getY(), 0.0) - valueOrDefault(from.getY(), 0.0);
        return Math.hypot(dx, dy);
    }

    private double headingDegrees(DriverOrder.Node from, DriverOrder.Node to) {
        double dx = valueOrDefault(to.getX(), 0.0) - valueOrDefault(from.getX(), 0.0);
        double dy = valueOrDefault(to.getY(), 0.0) - valueOrDefault(from.getY(), 0.0);
        if (Math.abs(dx) < 1e-9 && Math.abs(dy) < 1e-9) {
            return valueOrDefault(to.getTheta(), valueOrDefault(from.getTheta(), 0.0));
        }
        return Math.toDegrees(Math.atan2(dy, dx));
    }

    private double interpolate(Double from, Double to, double progress) {
        double start = valueOrDefault(from, 0.0);
        return start + (valueOrDefault(to, start) - start) * progress;
    }

    private double valueOrDefault(Double value, double defaultValue) {
        return value == null || !Double.isFinite(value) ? defaultValue : value;
    }

    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("适配器未初始化");
        }
    }
}
