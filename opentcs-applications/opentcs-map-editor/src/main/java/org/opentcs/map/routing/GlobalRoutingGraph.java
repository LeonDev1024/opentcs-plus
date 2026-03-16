package org.opentcs.map.routing;

import lombok.Getter;
import lombok.Setter;
import org.opentcs.kernel.persistence.entity.CrossLayerConnectionEntity;
import org.opentcs.kernel.persistence.entity.PointEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 全局路由图 - 支持跨楼层路径规划
 */
@Getter
@Setter
public class GlobalRoutingGraph {

    /**
     * 局部路由图缓存：mapId -> LocalRoutingGraph
     */
    private Map<String, LocalRoutingGraph> localGraphs = new HashMap<>();

    /**
     * 跨层连接列表
     */
    private List<CrossLayerConnectionEntity> crossLayerConnections = new ArrayList<>();

    /**
     * 所有点位缓存：pointId -> PointEntity
     */
    private Map<String, PointEntity> points = new HashMap<>();

    /**
     * 点位所属地图缓存：pointId -> mapId
     */
    private Map<String, String> pointToMap = new HashMap<>();

    /**
     * 添加局部路由图
     */
    public void addLocalGraph(String mapId, LocalRoutingGraph localGraph) {
        localGraphs.put(mapId, localGraph);
    }

    /**
     * 获取局部路由图
     */
    public LocalRoutingGraph getLocalGraph(String mapId) {
        return localGraphs.get(mapId);
    }

    /**
     * 添加跨层连接
     */
    public void addCrossLayerConnections(List<CrossLayerConnectionEntity> connections) {
        this.crossLayerConnections.addAll(connections);
    }

    /**
     * 添加点位
     */
    public void addPoint(PointEntity point) {
        points.put(point.getPointId(), point);
    }

    /**
     * 设置点位所属地图
     */
    public void setPointMap(String pointId, String mapId) {
        pointToMap.put(pointId, mapId);
    }

    /**
     * 根据点位ID获取点位
     */
    public PointEntity getPoint(String pointId) {
        return points.get(pointId);
    }

    /**
     * 根据点位ID获取所属地图
     */
    public String getMapIdByPoint(String pointId) {
        return pointToMap.get(pointId);
    }

    /**
     * 获取两个地图之间的可用跨层连接
     */
    public List<CrossLayerConnectionEntity> getCrossLayerConnections(String sourceMapId, String destMapId) {
        return crossLayerConnections.stream()
                .filter(conn -> conn.getSourceNavigationMapId().toString().equals(sourceMapId)
                        && conn.getDestNavigationMapId().toString().equals(destMapId)
                        && conn.getAvailable())
                .collect(Collectors.toList());
    }

    /**
     * 检查是否需要跨层
     */
    public boolean needsCrossFloor(String sourcePointId, String destPointId) {
        String sourceMapId = getMapIdByPoint(sourcePointId);
        String destMapId = getMapIdByPoint(destPointId);
        return sourceMapId != null && destMapId != null && !sourceMapId.equals(destMapId);
    }

    /**
     * 获取所有可用的跨层连接
     */
    public List<CrossLayerConnectionEntity> getAllAvailableConnections() {
        return crossLayerConnections.stream()
                .filter(CrossLayerConnectionEntity::getAvailable)
                .collect(Collectors.toList());
    }
}
