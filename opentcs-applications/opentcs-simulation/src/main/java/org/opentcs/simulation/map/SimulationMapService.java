package org.opentcs.simulation.map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.map.application.MapFacadeApplicationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 仿真地图服务：从地图编辑器模块加载真实点位数据供仿真引擎使用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationMapService {

    private final MapFacadeApplicationService mapFacadeService;

    /**
     * 加载指定地图下的所有点位（mm → m 单位转换）
     */
    public List<SimMapPoint> loadMapPoints(Long mapId) {
        try {
            List<PointDTO> points = mapFacadeService.listPointsByMap(mapId);
            if (points == null || points.isEmpty()) {
                log.warn("地图 {} 下没有点位数据", mapId);
                return Collections.emptyList();
            }
            return points.stream()
                    .filter(p -> p.getXPosition() != null && p.getYPosition() != null)
                    .map(p -> new SimMapPoint(
                            p.getPointId(),
                            p.getName(),
                            p.getXPosition().doubleValue() / 1000.0,
                            p.getYPosition().doubleValue() / 1000.0))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("加载地图点位失败 mapId={}: {}", mapId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 获取地图元数据（栅格图信息）
     */
    public NavigationMapDTO getMapInfo(Long mapId) {
        try {
            return mapFacadeService.getNavigationMapById(mapId);
        } catch (Exception e) {
            log.error("获取地图信息失败 mapId={}: {}", mapId, e.getMessage());
            return null;
        }
    }

    /**
     * 获取工厂下所有地图列表
     */
    public List<NavigationMapDTO> listMapsByFactory(Long factoryId) {
        try {
            return mapFacadeService.listNavigationMapsByFactory(factoryId);
        } catch (Exception e) {
            log.error("获取工厂地图列表失败 factoryId={}: {}", factoryId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 加载指定导航地图的拓扑图（点位 + 有向路径）
     */
    public SimMapGraph loadMapGraph(Long navMapId) {
        SimMapGraph graph = new SimMapGraph();
        try {
            // 加载点位
            List<PointDTO> points = mapFacadeService.listPointsByMap(navMapId);
            for (PointDTO point : points) {
                if (point.getXPosition() == null || point.getYPosition() == null) continue;
                SimMapPoint simPoint = new SimMapPoint(
                        point.getPointId(),
                        point.getName(),
                        point.getXPosition().doubleValue() / 1000.0,
                        point.getYPosition().doubleValue() / 1000.0);
                graph.addPoint(simPoint);
            }
            // 加载路径（有向边）
            List<PathDTO> paths = mapFacadeService.listPathsByMap(navMapId);
            for (PathDTO path : paths) {
                if (path.getSourcePointId() == null || path.getDestPointId() == null) continue;
                double length = path.getLength() != null
                        ? path.getLength().doubleValue() / 1000.0  // mm → m
                        : 1.0; // 默认 1m
                if (length <= 0) length = 1.0;
                graph.addEdge(new SimMapEdge(path.getSourcePointId(), path.getDestPointId(), length));
            }
            log.info("加载地图拓扑图 navMapId={}: {} 个点, {} 条边",
                    navMapId, graph.pointCount(), graph.edgeCount());
        } catch (Exception e) {
            log.error("加载地图拓扑图失败 navMapId={}: {}", navMapId, e.getMessage());
        }
        return graph;
    }

    /**
     * 合并工厂下所有导航地图的拓扑图
     */
    public SimMapGraph loadMapGraphForFactory(Long factoryModelId) {
        SimMapGraph merged = new SimMapGraph();
        List<NavigationMapDTO> navMaps = listMapsByFactory(factoryModelId);
        for (NavigationMapDTO navMap : navMaps) {
            SimMapGraph sub = loadMapGraph(navMap.getId());
            // merge into combined graph (re-add all points/edges)
            // We expose individual add methods so we need a merge helper
            mergeInto(merged, sub);
        }
        log.info("合并工厂拓扑图 factoryModelId={}: {} 个点, {} 条边",
                factoryModelId, merged.pointCount(), merged.edgeCount());
        return merged;
    }

    private void mergeInto(SimMapGraph target, SimMapGraph source) {
        source.getPoints().forEach(target::addPoint);
        source.getAllEdges().forEach(target::addEdge);
    }
}
