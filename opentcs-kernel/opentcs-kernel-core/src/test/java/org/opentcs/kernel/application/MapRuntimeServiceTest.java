package org.opentcs.kernel.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.kernel.api.map.MapSceneApi;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 地图运行时加载测试。
 */
@Tag("dev")
class MapRuntimeServiceTest {

    private MapSceneApi mapSceneApi;
    private RoutePlannerImpl routePlanner;
    private MapRuntimeService mapRuntimeService;

    @BeforeEach
    void setUp() {
        mapSceneApi = mock(MapSceneApi.class);
        routePlanner = new RoutePlannerImpl(new PassThroughRoutingAlgorithm());
        mapRuntimeService = new MapRuntimeService(mapSceneApi, routePlanner);
    }

    @Test
    void shouldLoadPublishedMapIntoRoutePlanner() {
        when(mapSceneApi.getNavigationMapByMapId("map-1")).thenReturn(publishedMap());
        when(mapSceneApi.listPointsByMap(100L)).thenReturn(List.of(
                point("P1", 0, 0),
                point("P2", 10, 0)
        ));
        when(mapSceneApi.listPathsByMap(100L)).thenReturn(List.of(path("PATH-1", "P1", "P2")));

        MapRuntimeService.LoadedMap loaded = mapRuntimeService.loadPublishedMap("map-1");

        assertEquals("map-1", loaded.mapId());
        assertEquals("v1", loaded.version());
        assertEquals(2, loaded.pointCount());
        assertEquals(1, loaded.pathCount());
        assertEquals("map-1", mapRuntimeService.getActiveMapId());
        assertEquals("v1", mapRuntimeService.getActiveMapVersion());
        assertEquals(2, routePlanner.getPointCount());
        assertEquals(1, routePlanner.getPathCount());
        assertEquals("P1", routePlanner.getPoint("P1").getPointId());
        assertEquals("PATH-1", routePlanner.getPath("PATH-1").getPathId());
    }

    @Test
    void shouldRejectUnpublishedMap() {
        NavigationMapDTO map = publishedMap();
        map.setStatus("0");
        when(mapSceneApi.getNavigationMapByMapId("map-1")).thenReturn(map);

        assertThrows(IllegalStateException.class,
                () -> mapRuntimeService.loadPublishedMap("map-1"));
    }

    @Test
    void shouldRejectPathWithMissingEndpoint() {
        when(mapSceneApi.getNavigationMapByMapId("map-1")).thenReturn(publishedMap());
        when(mapSceneApi.listPointsByMap(100L)).thenReturn(List.of(point("P1", 0, 0)));
        when(mapSceneApi.listPathsByMap(100L)).thenReturn(List.of(path("PATH-1", "P1", "P2")));

        assertThrows(IllegalStateException.class,
                () -> mapRuntimeService.loadPublishedMap("map-1"));
    }

    @Test
    void shouldRejectDisconnectedMap() {
        when(mapSceneApi.getNavigationMapByMapId("map-1")).thenReturn(publishedMap());
        when(mapSceneApi.listPointsByMap(100L)).thenReturn(List.of(
                point("P1", 0, 0),
                point("P2", 10, 0),
                point("P3", 100, 0)
        ));
        when(mapSceneApi.listPathsByMap(100L)).thenReturn(List.of(path("PATH-1", "P1", "P2")));

        assertThrows(IllegalStateException.class,
                () -> mapRuntimeService.loadPublishedMap("map-1"));
    }

    @Test
    void shouldRejectInvalidDirectionSemantic() {
        PathDTO path = path("PATH-1", "P1", "P2");
        path.setRoutingType("ONE_WAY");
        path.setProperties("{\"bidirectional\":\"true\"}");
        when(mapSceneApi.getNavigationMapByMapId("map-1")).thenReturn(publishedMap());
        when(mapSceneApi.listPointsByMap(100L)).thenReturn(List.of(
                point("P1", 0, 0),
                point("P2", 10, 0)
        ));
        when(mapSceneApi.listPathsByMap(100L)).thenReturn(List.of(path));

        assertThrows(IllegalStateException.class,
                () -> mapRuntimeService.loadPublishedMap("map-1"));
    }

    @Test
    void shouldKeepBlockedPathState() {
        PathDTO blockedPath = path("PATH-1", "P1", "P2");
        blockedPath.setIsBlocked(true);
        when(mapSceneApi.getNavigationMapByMapId("map-1")).thenReturn(publishedMap());
        when(mapSceneApi.listPointsByMap(100L)).thenReturn(List.of(
                point("P1", 0, 0),
                point("P2", 10, 0)
        ));
        when(mapSceneApi.listPathsByMap(100L)).thenReturn(List.of(blockedPath));

        mapRuntimeService.loadPublishedMap("map-1");

        assertTrue(routePlanner.getPath("PATH-1").isBlocked());
    }

    @Test
    void shouldLoadPathVelocityAndRoutingProperties() {
        PathDTO path = path("PATH-1", "P1", "P2");
        path.setMaxVelocity(BigDecimal.valueOf(2));
        path.setMaxReverseVelocity(BigDecimal.valueOf(1));
        path.setRoutingType("ONE_WAY");
        path.setProperties("{\"blockedByZone\":\"Z1\",\"bidirectional\":\"false\"}");
        when(mapSceneApi.getNavigationMapByMapId("map-1")).thenReturn(publishedMap());
        when(mapSceneApi.listPointsByMap(100L)).thenReturn(List.of(
                point("P1", 0, 0),
                point("P2", 10, 0)
        ));
        when(mapSceneApi.listPathsByMap(100L)).thenReturn(List.of(path));

        mapRuntimeService.loadPublishedMap("map-1");

        Path loaded = routePlanner.getPath("PATH-1");
        assertEquals(2.0, loaded.getMaxVelocity());
        assertEquals(1.0, loaded.getMaxReverseVelocity());
        assertEquals("ONE_WAY", loaded.getProperties().get("routingType"));
        assertEquals("Z1", loaded.getProperties().get("blockedByZone"));
        assertEquals("false", loaded.getProperties().get("bidirectional"));
    }

    private NavigationMapDTO publishedMap() {
        NavigationMapDTO map = new NavigationMapDTO();
        map.setId(100L);
        map.setMapId("map-1");
        map.setMapVersion("v1");
        map.setStatus("1");
        return map;
    }

    private PointDTO point(String pointId, double x, double y) {
        PointDTO point = new PointDTO();
        point.setPointId(pointId);
        point.setName(pointId);
        point.setXPosition(BigDecimal.valueOf(x));
        point.setYPosition(BigDecimal.valueOf(y));
        return point;
    }

    private PathDTO path(String pathId, String sourcePointId, String destPointId) {
        PathDTO path = new PathDTO();
        path.setPathId(pathId);
        path.setSourcePointId(sourcePointId);
        path.setDestPointId(destPointId);
        path.setLength(BigDecimal.TEN);
        return path;
    }

    private static class PassThroughRoutingAlgorithm implements RoutingAlgorithm {
        @Override
        public List<Point> findRoute(Map<String, Point> points,
                                     Map<String, Path> paths,
                                     Point start,
                                     Point end) {
            return List.of(start, end);
        }
    }
}
