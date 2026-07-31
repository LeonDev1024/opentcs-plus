package org.opentcs.map.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.application.MapHotReloadService;
import org.opentcs.kernel.persistence.entity.NavigationMapEntity;
import org.opentcs.kernel.persistence.service.NavigationMapRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动时将最近一张已生效地图加载到运行时内存。
 * <p>
 * 极简策略：保存即生效；进程重启后自动恢复最新已生效地图，避免再走「发布/加载」两步。
 * </p>
 */
@Slf4j
@Component
@Order(50)
@RequiredArgsConstructor
public class MapRuntimeBootstrapRunner implements ApplicationRunner {

    private static final String STATUS_PUBLISHED = "1";

    private final NavigationMapRepository navigationMapRepository;
    private final MapHotReloadService mapHotReloadService;

    @Override
    public void run(ApplicationArguments args) {
        NavigationMapEntity latest = navigationMapRepository.getOne(
                new LambdaQueryWrapper<NavigationMapEntity>()
                        .eq(NavigationMapEntity::getStatus, STATUS_PUBLISHED)
                        .eq(NavigationMapEntity::getDelFlag, "0")
                        .orderByDesc(NavigationMapEntity::getUpdateTime)
                        .last("LIMIT 1"),
                false
        );
        if (latest == null || latest.getMapId() == null || latest.getMapId().isBlank()) {
            log.info("启动时无已生效地图，跳过运行时地图加载");
            return;
        }
        try {
            mapHotReloadService.hotReload(latest.getMapId());
            log.info("启动已加载运行时地图: mapId={}, version={}, name={}",
                    latest.getMapId(), latest.getMapVersion(), latest.getName());
        } catch (Exception e) {
            log.warn("启动加载运行时地图失败（mapId={}）: {}", latest.getMapId(), e.getMessage());
        }
    }
}
