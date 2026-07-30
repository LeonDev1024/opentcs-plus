package org.opentcs.driver.vda5050.converter;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.driver.api.dto.VehicleStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dev")
class VDA5050MessageConverterTest {

    private final VDA5050MessageConverter converter = new VDA5050MessageConverter();

    @Test
    void shouldParseNestedStatus() {
        String json = """
                {
                  "header": {"vehicleId": "agv-1"},
                  "state": {
                    "orderId": "order-1",
                    "orderUpdateId": 3,
                    "agvState": "EXECUTING",
                    "lastNodeId": "B",
                    "driving": true,
                    "position": {"x": 1.0, "y": 2.0, "theta": 0.0, "positionId": "B"},
                    "nodeStates": [{"nodeId": "B", "sequenceId": 1, "released": true}]
                  }
                }
                """;

        VehicleStatus status = converter.fromVDA5050Status(json);
        assertEquals("agv-1", status.getVehicleId());
        assertEquals("order-1", status.getOrderId());
        assertEquals(3, status.getOrderUpdateId());
        assertEquals("EXECUTING", status.getAgvState());
        assertEquals("B", status.getLastNodeId());
        assertNotNull(status.getNodeStates());
        assertEquals("B", status.getNodeStates().get(0).getNodeId());
    }

    @Test
    void shouldParseFlatVda5050State() {
        String json = """
                {
                  "orderId": "order-2",
                  "orderUpdateId": 1,
                  "lastNodeId": "A",
                  "driving": false,
                  "serialNumber": "agv-2",
                  "operatingMode": "AUTOMATIC",
                  "agvPosition": {"x": 0.0, "y": 0.0, "theta": 0.0, "mapId": "map-1"},
                  "batteryState": {"batteryCharge": 88.5, "charging": false},
                  "errors": [],
                  "nodeStates": [],
                  "actionStates": []
                }
                """;

        VehicleStatus status = converter.fromVDA5050Status(json);
        assertEquals("agv-2", status.getVehicleId());
        assertEquals("order-2", status.getOrderId());
        assertEquals("IDLE", status.getAgvState());
        assertEquals("A", status.getLastNodeId());
        assertEquals(88.5, status.getBatteryState());
        assertTrue(status.getActionStates().isEmpty());
    }
}
