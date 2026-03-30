package org.opentcs.strategies.builtin;

import org.opentcs.kernel.api.algorithm.Router;
import org.opentcs.strategies.builtin.config.StrategiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 内置路由策略占位实现：不依赖外部 OpenTCS，拓扑为空时返回无可行路线。
 * <p>
 * 路由相关参数读取 {@code opentcs.strategies.routing.*}；完整图算法可后续接入。
 * </p>
 */
public class BuiltinRouter implements Router {

    private static final Logger LOG = LoggerFactory.getLogger(BuiltinRouter.class);

    private final StrategiesConfiguration strategiesConfiguration;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public BuiltinRouter(StrategiesConfiguration strategiesConfiguration) {
        this.strategiesConfiguration = strategiesConfiguration != null
            ? strategiesConfiguration
            : new StrategiesConfiguration();
    }

    @Override
    public void initialize() {
        StrategiesConfiguration.Routing r = strategiesConfiguration.getRouting();
        LOG.info(
            "BuiltinRouter 初始化: algorithm={}, cacheEnabled={}, cacheTtlSeconds={}",
            r.getAlgorithm(),
            r.isCacheEnabled(),
            r.getCacheTtlSeconds()
        );
        initialized.set(true);
    }

    @Override
    public void terminate() {
        initialized.set(false);
    }

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public void updateRoutingTopology(Set<String> paths) {
        // 占位：完整实现中同步图结构
    }

    @Override
    public Set<String> checkRoutability(String orderId) {
        return Collections.emptySet();
    }

    @Override
    public List<Route> getRoutes(String vehicleId, String sourcePointId, String destPointId,
                                 Set<String> resourcesToAvoid, int maxRouteCount) {
        return Collections.emptyList();
    }
}
