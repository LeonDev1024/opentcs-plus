package org.opentcs.kernel.domain.routing;

import java.util.List;
import java.util.Map;

/**
 * 路径规划算法接口（领域层抽象）。
 * <p>
 * 具体算法实现（如 A*）位于 strategies 层，通过此接口注入，
 * 保持领域层对算法实现无感知。
 * </p>
 */
public interface RoutingAlgorithm {

    /**
     * 查找从起点到终点的最短路径。
     *
     * @param points 点位 Map（pointId → Point）
     * @param paths  路径 Map（pathId → Path）
     * @param start  起点
     * @param end    终点
     * @return 经过的点位列表（按顺序），无法到达时返回空列表
     */
    List<Point> findRoute(Map<String, Point> points, Map<String, Path> paths,
                          Point start, Point end);
}
