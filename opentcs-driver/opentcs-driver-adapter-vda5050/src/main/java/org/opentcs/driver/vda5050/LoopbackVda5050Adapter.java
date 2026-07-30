package org.opentcs.driver.vda5050;

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

    private final Map<String, ConcurrentLinkedQueue<VehicleStatus>> statusQueues = new ConcurrentHashMap<>();
    private final Map<String, Boolean> connected = new ConcurrentHashMap<>();
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
        LOG.info("Loopback 车辆已连接: {}", vehicleId);
    }

    @Override
    public void disconnect(String vehicleId) {
        connected.remove(vehicleId);
        statusQueues.remove(vehicleId);
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
        long delayMs = 50L;
        enqueue(vehicleId, buildStatus(vehicleId, order.getOrderId(), "EXECUTING",
                nodes.isEmpty() ? null : nodes.get(0).getNodeId(),
                nodes.isEmpty() ? null : nodes.get(0).getX(),
                nodes.isEmpty() ? null : nodes.get(0).getY(),
                true));

        for (int i = 0; i < nodes.size(); i++) {
            DriverOrder.Node node = nodes.get(i);
            long at = delayMs * (i + 1);
            scheduler.schedule(() -> enqueue(vehicleId, buildStatus(
                    vehicleId, order.getOrderId(), "EXECUTING",
                    node.getNodeId(), node.getX(), node.getY(), true)), at, TimeUnit.MILLISECONDS);
        }

        long finishAt = delayMs * (nodes.size() + 1);
        DriverOrder.Node last = nodes.isEmpty() ? null : nodes.get(nodes.size() - 1);
        scheduler.schedule(() -> enqueue(vehicleId, buildStatus(
                vehicleId, order.getOrderId(), "IDLE",
                last != null ? last.getNodeId() : null,
                last != null ? last.getX() : null,
                last != null ? last.getY() : null,
                false)), finishAt, TimeUnit.MILLISECONDS);

        LOG.info("Loopback 已接收订单并开始回放: vehicleId={}, orderId={}, nodes={}",
                vehicleId, order.getOrderId(), nodes.size());
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

    private VehicleStatus buildStatus(String vehicleId,
                                      String orderId,
                                      String agvState,
                                      String nodeId,
                                      Double x,
                                      Double y,
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
        status.setDriving(driving);
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

    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("适配器未初始化");
        }
    }
}
