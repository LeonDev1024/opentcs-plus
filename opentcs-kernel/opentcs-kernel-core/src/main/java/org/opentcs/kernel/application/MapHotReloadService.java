package org.opentcs.kernel.application;

import org.opentcs.kernel.api.algorithm.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 地图热加载协调：冻结接单 → 切运行图版本 → 恢复调度。
 */
public class MapHotReloadService {

    private static final Logger log = LoggerFactory.getLogger(MapHotReloadService.class);

    private final MapRuntimeService mapRuntimeService;
    private final Dispatcher dispatcher;
    private final AtomicBoolean acceptingOrders = new AtomicBoolean(true);

    public MapHotReloadService(MapRuntimeService mapRuntimeService, Dispatcher dispatcher) {
        this.mapRuntimeService = mapRuntimeService;
        this.dispatcher = dispatcher;
    }

    public boolean isAcceptingOrders() {
        return acceptingOrders.get();
    }

    /**
     * 冻结接单，加载已发布地图到运行态，再恢复接单并触发调度。
     */
    public MapRuntimeService.LoadedMap hotReload(String mapId) {
        if (mapId == null || mapId.isBlank()) {
            throw new IllegalArgumentException("mapId 不能为空");
        }
        boolean previous = acceptingOrders.getAndSet(false);
        try {
            log.info("地图热加载开始（已冻结接单）: mapId={}, previousAccepting={}", mapId, previous);
            MapRuntimeService.LoadedMap loaded = mapRuntimeService.loadPublishedMap(mapId);
            log.info("地图热加载完成: mapId={}, version={}, points={}, paths={}",
                    loaded.mapId(), loaded.version(), loaded.pointCount(), loaded.pathCount());
            return loaded;
        } finally {
            acceptingOrders.set(true);
            try {
                dispatcher.dispatch();
            } catch (Exception e) {
                log.warn("热加载后触发调度失败: {}", e.getMessage());
            }
            log.info("地图热加载结束（已恢复接单）: mapId={}", mapId);
        }
    }

    /** 测试/运维手动冻结 */
    public void freezeAcceptingOrders() {
        acceptingOrders.set(false);
    }

    /** 测试/运维手动恢复 */
    public void resumeAcceptingOrders() {
        acceptingOrders.set(true);
    }
}
