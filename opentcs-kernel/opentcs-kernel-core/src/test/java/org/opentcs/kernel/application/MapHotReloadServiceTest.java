package org.opentcs.kernel.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.api.algorithm.Dispatcher;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.api.dto.OrderSpecDTO;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.kernel.api.map.MapSceneApi;
import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;
import org.opentcs.kernel.domain.routing.RoutingAlgorithm;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("dev")
class MapHotReloadServiceTest {

    private MapSceneApi mapSceneApi;
    private RoutePlannerImpl routePlanner;
    private MapRuntimeService mapRuntimeService;
    private Dispatcher dispatcher;
    private MapHotReloadService hotReloadService;
    private TransportOrderService transportOrderService;

    @BeforeEach
    void setUp() {
        mapSceneApi = mock(MapSceneApi.class);
        routePlanner = new RoutePlannerImpl(new PassThroughRoutingAlgorithm());
        mapRuntimeService = new MapRuntimeService(mapSceneApi, routePlanner);
        dispatcher = mock(Dispatcher.class);
        hotReloadService = new MapHotReloadService(mapRuntimeService, dispatcher);
        transportOrderService = new TransportOrderService(
                new TransportOrderRegistry(),
                mock(DispatcherService.class),
                routePlanner,
                mapRuntimeService,
                hotReloadService,
                mock(ApplicationEventPublisher.class)
        );

        when(mapSceneApi.getNavigationMapByMapId("map-1")).thenReturn(publishedMap("v1"));
        when(mapSceneApi.listPointsByMap(100L)).thenReturn(List.of(
                point("P1", 0, 0),
                point("P2", 10, 0)
        ));
        when(mapSceneApi.listPathsByMap(100L)).thenReturn(List.of(path("PATH-1", "P1", "P2")));
        when(mapSceneApi.listBlocksByMap(100L)).thenReturn(List.of());
    }

    @Test
    void hotReloadFreezesThenResumesAndDispatches() {
        MapRuntimeService.LoadedMap loaded = hotReloadService.hotReload("map-1");

        assertEquals("map-1", loaded.mapId());
        assertEquals("v1", loaded.version());
        assertTrue(hotReloadService.isAcceptingOrders());
        verify(dispatcher, times(1)).dispatch();
        assertEquals(2, routePlanner.getPointCount());
    }

    @Test
    void createOrderRejectedWhileFrozen() {
        mapRuntimeService.loadPublishedMap("map-1");
        hotReloadService.freezeAcceptingOrders();
        assertFalse(hotReloadService.isAcceptingOrders());

        OrderSpecDTO spec = new OrderSpecDTO();
        spec.setName("order-1");
        spec.setSourcePointId("P1");
        spec.setDestPointId("P2");

        assertThrows(IllegalStateException.class, () -> transportOrderService.createOrder(spec));

        hotReloadService.resumeAcceptingOrders();
        assertTrue(hotReloadService.isAcceptingOrders());
    }

    private NavigationMapDTO publishedMap(String version) {
        NavigationMapDTO map = new NavigationMapDTO();
        map.setId(100L);
        map.setMapId("map-1");
        map.setMapVersion(version);
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
