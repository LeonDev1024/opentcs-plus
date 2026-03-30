package org.opentcs.kernel.api.algorithm;

/**
 * 边评估器接口。
 * <p>
 * 用于计算路径的权重/成本，支持多种评估策略。
 * </p>
 */
public interface EdgeEvaluator {

    /**
     * 计算边权重。
     *
     * @param pathId 路径ID
     * @param vehicleId 车辆ID（可为 null）
     * @param reverseTravel 是否反向行驶
     * @param pathLength 路径长度（用于计算）
     * @return 边权重，返回 Double.POSITIVE_INFINITY 表示不可通行
     */
    double computeWeight(String pathId, String vehicleId, boolean reverseTravel, double pathLength);

    /**
     * 获取评估器名称。
     *
     * @return 评估器名称
     */
    default String getName() {
        return getClass().getSimpleName();
    }
}