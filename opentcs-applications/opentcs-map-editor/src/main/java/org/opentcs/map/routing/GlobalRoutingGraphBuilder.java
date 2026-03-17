package org.opentcs.map.routing;

import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.persistence.entity.*;
import org.opentcs.kernel.persistence.service.NavigationMapDomainService;
import org.opentcs.kernel.persistence.service.PointDomainService;
import org.opentcs.kernel.persistence.service.PathDomainService;
import org.opentcs.kernel.persistence.service.CrossLayerConnectionDomainService;

import java.util.List;

/**
 * 全局路由图构建器
 */
@Slf4j
public class GlobalRoutingGraphBuilder {

    private final NavigationMapDomainService navigationMapDomainService;
    private final PointDomainService pointDomainService;
    private final PathDomainService pathDomainService;
    private final CrossLayerConnectionDomainService crossLayerConnectionDomainService;

    public GlobalRoutingGraphBuilder(NavigationMapDomainService navigationMapDomainService,
                                     PointDomainService pointDomainService,
                                     PathDomainService pathDomainService,
                                     CrossLayerConnectionDomainService crossLayerConnectionDomainService) {
        this.navigationMapDomainService = navigationMapDomainService;
        this.pointDomainService = pointDomainService;
        this.pathDomainService = pathDomainService;
        this.crossLayerConnectionDomainService = crossLayerConnectionDomainService;
    }

    /**
     * 构建全局路由图
     * @param factoryModelId 工厂模型ID
     * @return 全局路由图
     */
    public GlobalRoutingGraph build(Long factoryModelId) {
        GlobalRoutingGraph globalGraph = new GlobalRoutingGraph();

        // 1. 获取工厂下所有导航地图
        List<NavigationMapEntity> maps = navigationMapDomainService.selectByFactoryModelId(factoryModelId);

        // 2. 为每个地图构建局部路由图
        for (NavigationMapEntity map : maps) {
            LocalRoutingGraph localGraph = buildLocalGraph(map.getId(), map.getMapId());
            globalGraph.addLocalGraph(map.getMapId(), localGraph);
        }

        // 3. 添加跨层连接
        List<CrossLayerConnectionEntity> connections = crossLayerConnectionDomainService.selectByFactoryModelId(factoryModelId);
        globalGraph.addCrossLayerConnections(connections);

        // 4. 添加点位和地图关联
        for (NavigationMapEntity map : maps) {
            List<PointEntity> points = pointDomainService.listByMap(map.getId());
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
        List<PointEntity> points = pointDomainService.listByMap(mapId);
        for (PointEntity point : points) {
            localGraph.addPoint(point);
        }

        // 获取该地图下的所有路径
        List<PathEntity> paths = pathDomainService.listByMap(mapId);
        for (PathEntity path : paths) {
            localGraph.addPath(path);
        }

        return localGraph;
    }
}
