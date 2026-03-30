package org.opentcs.kernel.api.algorithm;

import java.util.List;
import java.util.Set;

/**
 * 路由策略接口。
 * <p>
 * 负责为车辆计算从一个点到另一个点的路线。
 * 使用 String ID 代替领域对象，实现接口层和领域层的解耦。
 * </p>
 */
public interface Router extends Lifecycle {

    /**
     * 更新路由拓扑。
     *
     * @param paths 路径集合（路径ID列表）
     */
    void updateRoutingTopology(Set<String> paths);

    /**
     * 检查订单的可路由性。
     *
     * @param orderId 运输订单ID
     * @return 可以处理该订单的车辆ID集合
     */
    Set<String> checkRoutability(String orderId);

    /**
     * 获取从一个点到另一个点的路线。
     *
     * @param vehicleId 车辆ID
     * @param sourcePointId 起点ID
     * @param destPointId 终点ID
     * @param resourcesToAvoid 要避免的资源（路径ID）
     * @param maxRouteCount 最大路线数量
     * @return 路线列表
     */
    List<Route> getRoutes(String vehicleId, String sourcePointId, String destPointId,
                           Set<String> resourcesToAvoid, int maxRouteCount);

    /**
     * 路线结果。
     */
    interface Route {
        /**
         * 获取路线上的路径ID序列。
         */
        List<String> getPathIds();

        /**
         * 获取路线成本。
         */
        double getCost();

        /**
         * 获取起点ID。
         */
        String getSourcePointId();

        /**
         * 获取终点ID。
         */
        String getDestinationPointId();
    }
}