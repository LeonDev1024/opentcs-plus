package org.opentcs.map.routing;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.persistence.entity.*;
import org.opentcs.map.service.*;

import java.util.List;

/**
 * 全局路由图构建器
 */
@Slf4j
public class GlobalRoutingGraphBuilder {

    private final NavigationMapService navigationMapService;
    private final PointService pointService;
    private final PathService pathService;
    private final CrossLayerConnectionService crossLayerConnectionService;

    public GlobalRoutingGraphBuilder(NavigationMapService navigationMapService,
                                     PointService pointService,
                                     PathService pathService,
                                     CrossLayerConnectionService crossLayerConnectionService) {
        this.navigationMapService = navigationMapService;
        this.pointService = pointService;
        this.pathService = pathService;
        this.crossLayerConnectionService = crossLayerConnectionService;
    }

    /**
     * 构建全局路由图
     * @param factoryModelId 工厂模型ID
     * @return 全局路由图
     */
    public GlobalRoutingGraph build(Long factoryModelId) {
        GlobalRoutingGraph globalGraph = new GlobalRoutingGraph();

        // 1. 获取工厂下所有导航地图
        List<NavigationMapEntity> maps = navigationMapService.selectByFactoryModelId(factoryModelId);

        // 2. 为每个地图构建局部路由图
        for (NavigationMapEntity map : maps) {
            LocalRoutingGraph localGraph = buildLocalGraph(map.getId(), map.getMapId());
            globalGraph.addLocalGraph(map.getMapId(), localGraph);
        }

        // 3. 添加跨层连接
        List<CrossLayerConnectionEntity> connections = crossLayerConnectionService.selectByFactoryModelId(factoryModelId);
        globalGraph.addCrossLayerConnections(connections);

        // 4. 添加点位和地图关联
        for (NavigationMapEntity map : maps) {
            List<PointEntity> points = pointService.listByMap(map.getId());
            for (PointEntity point : points) {
                globalGraph.addPoint(point);
                globalGraph.setPointMap(point.getPointId(), map.getMapId());
            }
        }

        log.info("构建全局路由图完成: 地图数={}, 跨层连接数={}",
                maps.size(), connections.size());

        return globalGraph;
    }

    /**
     * 构建单地图局部路由图
     * @param mapId 导航地图ID
     * @param mapIdStr 地图标识
     * @return 局部路由图
     */
    private LocalRoutingGraph buildLocalGraph(Long mapId, String mapIdStr) {
        LocalRoutingGraph localGraph = new LocalRoutingGraph(mapIdStr);

        // 获取该地图下的所有点位
        List<PointEntity> points = pointService.listByMap(mapId);
        for (PointEntity point : points) {
            localGraph.addPoint(point);
        }

        // 获取该地图下的所有路径
        List<PathEntity> paths = pathService.listByMap(mapId);
        for (PathEntity path : paths) {
            localGraph.addPath(path);
        }

        return localGraph;
    }
}
