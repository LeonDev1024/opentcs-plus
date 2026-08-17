package org.opentcs.kernel.domain.port;

/**
 * 地图热加载协作端口。
 */
public interface MapHotReloadPort {

    boolean isAcceptingOrders();

    /**
     * 冻结接单 → 加载已发布地图 → 恢复接单并触发调度。
     *
     * @return 加载摘要（mapId / version / 点数 / 边数）
     */
    MapRuntimePort.LoadedMapSummary hotReload(String mapId);
}
