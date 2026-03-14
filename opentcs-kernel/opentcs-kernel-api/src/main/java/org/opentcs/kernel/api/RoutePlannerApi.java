package org.opentcs.kernel.api;

import org.opentcs.kernel.api.dto.RouteDTO;

import java.util.List;
import java.util.Optional;

/**
 * 路径规划 API
 * 负责计算两点之间的最优路径
 */
public interface RoutePlannerApi {

    /**
     * 计算两点之间的最短路径
     * @param sourcePointId 起点ID
     * @param destPointId 终点ID
     * @return 路径结果
     */
    Optional<RouteDTO> findRoute(String sourcePointId, String destPointId);

    /**
     * 计算多点顺序路径（TSP问题）
     * @param pointIds 途经点ID列表
     * @return 排序后的路径列表
     */
    List<RouteDTO> findMultiPointRoute(List<String> pointIds);

    /**
     * 获取地图所有可通行路径
     * @return 所有路径
     */
    List<RouteDTO> getAllRoutes();

    /**
     * 检查路径是否可达
     * @param sourcePointId 起点ID
     * @param destPointId 终点ID
     * @return 是否可达
     */
    boolean isReachable(String sourcePointId, String destPointId);

    /**
     * 获取两点之间的实际距离
     * @param sourcePointId 起点ID
     * @param destPointId 终点ID
     * @return 距离（米）
     */
    double getDistance(String sourcePointId, String destPointId);
}
