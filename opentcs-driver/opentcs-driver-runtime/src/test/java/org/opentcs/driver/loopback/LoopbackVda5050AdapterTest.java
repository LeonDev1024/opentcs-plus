package org.opentcs.driver.loopback;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.driver.api.dto.DriverConfig;
import org.opentcs.driver.api.dto.DriverOrder;
import org.opentcs.driver.api.dto.VehicleStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dev")
class LoopbackVda5050AdapterTest {

    @Test
    void shouldInterpolatePositionAtConfiguredSpeedAndReportHeading() throws Exception {
        LoopbackVda5050Adapter adapter = new LoopbackVda5050Adapter();
        adapter.initialize(new DriverConfig());
        DriverConfig vehicleConfig = new DriverConfig();
        vehicleConfig.setProperties(Map.of(
                "simulationSpeedMps", "1.0",
                "mapUnitsPerMeter", "100"));
        adapter.connect("sim-1", vehicleConfig);

        DriverOrder order = new DriverOrder();
        order.setOrderId("order-speed");
        DriverOrder.Node a = node("A", 0.0, 0.0);
        DriverOrder.Node b = node("B", 100.0, 0.0);
        order.setNodes(List.of(a, b));

        adapter.sendOrder("sim-1", order);
        VehicleStatus initial = waitStatus(adapter, "sim-1", 500);
        VehicleStatus moving = waitStatus(adapter, "sim-1", 700);

        assertNotNull(initial);
        assertEquals(0.0, initial.getxPosition());
        assertEquals(0.0, initial.getTheta());
        assertNotNull(moving);
        assertTrue(moving.getxPosition() > 0.0);
        assertTrue(moving.getxPosition() < 100.0);
        assertEquals(0.0, moving.getTheta());
        assertEquals("A", moving.getLastNodeId());
        adapter.destroy();
    }

    @Test
    void shouldReplayNodeAndIdleStatusesAfterSendOrder() throws Exception {
        LoopbackVda5050Adapter adapter = new LoopbackVda5050Adapter();
        adapter.initialize(new DriverConfig());
        adapter.connect("sim-1", null);

        DriverOrder order = new DriverOrder();
        order.setOrderId("order-loop");
        DriverOrder.Node a = new DriverOrder.Node();
        a.setNodeId("A");
        a.setX(0.0);
        a.setY(0.0);
        DriverOrder.Node b = new DriverOrder.Node();
        b.setNodeId("B");
        b.setX(10.0);
        b.setY(0.0);
        order.setNodes(List.of(a, b));

        adapter.sendOrder("sim-1", order);

        VehicleStatus first = waitStatus(adapter, "sim-1", 1000);
        assertNotNull(first);
        assertEquals("EXECUTING", first.getAgvState());
        assertEquals("order-loop", first.getOrderId());
        assertEquals(100.0, first.getBatteryState());
        assertEquals(false, first.getCharging());

        boolean sawB = false;
        boolean sawIdle = false;
        long deadline = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < deadline && !sawIdle) {
            VehicleStatus status = adapter.receiveStatus("sim-1");
            if (status == null) {
                TimeUnit.MILLISECONDS.sleep(20);
                continue;
            }
            if ("B".equals(status.getLastNodeId())) {
                sawB = true;
            }
            if ("IDLE".equalsIgnoreCase(status.getAgvState())) {
                sawIdle = true;
                assertEquals("B", status.getLastNodeId());
                assertEquals(100.0, status.getBatteryState());
            }
        }

        assertTrue(sawB);
        assertTrue(sawIdle);
        adapter.destroy();
    }

    private static DriverOrder.Node node(String id, double x, double y) {
        DriverOrder.Node node = new DriverOrder.Node();
        node.setNodeId(id);
        node.setX(x);
        node.setY(y);
        return node;
    }

    private static VehicleStatus waitStatus(LoopbackVda5050Adapter adapter,
                                            String vehicleId,
                                            long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            VehicleStatus status = adapter.receiveStatus(vehicleId);
            if (status != null) {
                return status;
            }
            TimeUnit.MILLISECONDS.sleep(10);
        }
        return null;
    }
}
