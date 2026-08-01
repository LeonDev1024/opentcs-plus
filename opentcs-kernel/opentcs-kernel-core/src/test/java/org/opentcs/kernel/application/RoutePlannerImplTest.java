package org.opentcs.kernel.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.domain.resource.ResourceType;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dev")
class RoutePlannerImplTest {

    @Test
    void shouldAllowVehicleToLeavePointLockedByItself() {
        RoutePlannerImpl planner = plannerWithSinglePath("P7", "P10");
        planner.setResourceLocked(ResourceType.POINT, "P7", "ASD1", true);

        assertFalse(planner.findPath("P7", "P10", "ASD1").isEmpty());
    }

    @Test
    void shouldBlockVehicleWhenStartPointIsLockedByAnotherVehicle() {
        RoutePlannerImpl planner = plannerWithSinglePath("P7", "P10");
        planner.setResourceLocked(ResourceType.POINT, "P7", "ASD1", true);

        assertTrue(planner.findPath("P7", "P10", "ASD2").isEmpty());
    }

    @Test
    void shouldKeepOwnerAgnosticPlanningConservative() {
        RoutePlannerImpl planner = plannerWithSinglePath("P7", "P10");
        planner.setResourceLocked(ResourceType.POINT, "P7", "ASD1", true);

        assertTrue(planner.findPath("P7", "P10").isEmpty());
    }

    private RoutePlannerImpl plannerWithSinglePath(String sourceId, String destId) {
        RoutingAlgorithm routingAlgorithm = (points, paths, start, end) ->
                paths.values().stream()
                        .anyMatch(path -> path.getSourcePointId().equals(start.getPointId())
                                && path.getDestPointId().equals(end.getPointId()))
                        ? List.of(start, end)
                        : Collections.emptyList();

        RoutePlannerImpl planner = new RoutePlannerImpl(routingAlgorithm);
        planner.registerPoint(new Point(sourceId, sourceId, 0, 0));
        planner.registerPoint(new Point(destId, destId, 1, 0));
        planner.registerPath(new Path(sourceId + "-" + destId, sourceId, destId, 1));
        return planner;
    }
}
