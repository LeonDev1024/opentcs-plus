package org.opentcs.strategies.builtin;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dev")
class AStarRoutingAlgorithmTest {

    private final AStarRoutingAlgorithm algorithm = new AStarRoutingAlgorithm();

    @Test
    void shouldAvoidBlockedPaths() {
        Map<String, Point> points = points("A", "B", "C");
        Path blockedDirectPath = new Path("AB", "A", "B", 1);
        blockedDirectPath.block();
        Map<String, Path> paths = paths(
                blockedDirectPath,
                new Path("AC", "A", "C", 10),
                new Path("CB", "C", "B", 10)
        );

        List<Point> route = algorithm.findRoute(points, paths, points.get("A"), points.get("B"));

        assertEquals(List.of("A", "C", "B"), route.stream().map(Point::getPointId).toList());
    }

    @Test
    void shouldUseVelocityAwareTravelCost() {
        Map<String, Point> points = points("A", "B", "C");
        Map<String, Path> paths = paths(
                new Path("AB", "A", "B", 10, 1.0, 1.0),
                new Path("AC", "A", "C", 100, 100.0, 100.0),
                new Path("CB", "C", "B", 100, 100.0, 100.0)
        );

        List<Point> route = algorithm.findRoute(points, paths, points.get("A"), points.get("B"));

        assertEquals(List.of("A", "C", "B"), route.stream().map(Point::getPointId).toList());
    }

    @Test
    void shouldRespectOneWayPathDirection() {
        Map<String, Point> points = points("A", "B");
        Path oneWayPath = new Path("AB", "A", "B", 1);
        oneWayPath.getProperties().put("routingType", "ONE_WAY");

        List<Point> route = algorithm.findRoute(
                points,
                paths(oneWayPath),
                points.get("B"),
                points.get("A"));

        assertTrue(route.isEmpty());
    }

    @Test
    void shouldUseDynamicPathCostProperties() {
        Map<String, Point> points = points("A", "B", "C");
        Path congestedDirectPath = new Path("AB", "A", "B", 1);
        congestedDirectPath.getProperties().put("congestionCost", "100");
        Map<String, Path> paths = paths(
                congestedDirectPath,
                new Path("AC", "A", "C", 10),
                new Path("CB", "C", "B", 10)
        );

        List<Point> route = algorithm.findRoute(points, paths, points.get("A"), points.get("B"));

        assertEquals(List.of("A", "C", "B"), route.stream().map(Point::getPointId).toList());
    }

    @Test
    void shouldAvoidTemporaryBlockedPaths() {
        Map<String, Point> points = points("A", "B", "C");
        Path temporaryBlockedPath = new Path("AB", "A", "B", 1);
        temporaryBlockedPath.getProperties().put("temporaryBlocked", "true");
        Map<String, Path> paths = paths(
                temporaryBlockedPath,
                new Path("AC", "A", "C", 10),
                new Path("CB", "C", "B", 10)
        );

        List<Point> route = algorithm.findRoute(points, paths, points.get("A"), points.get("B"));

        assertEquals(List.of("A", "C", "B"), route.stream().map(Point::getPointId).toList());
    }

    private Map<String, Point> points(String... pointIds) {
        Map<String, Point> points = new LinkedHashMap<>();
        for (String pointId : pointIds) {
            points.put(pointId, new Point(pointId, pointId, 0, 0));
        }
        return points;
    }

    private Map<String, Path> paths(Path... paths) {
        Map<String, Path> result = new LinkedHashMap<>();
        for (Path path : paths) {
            result.put(path.getPathId(), path);
        }
        return result;
    }
}
