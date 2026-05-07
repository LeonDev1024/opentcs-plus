package org.opentcs.simulation.map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.api.dto.LocationDTO;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
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
            List<LocationDTO> locations = mapFacadeService.listLocationsByMap(mapId);
            if (locations == null || locations.isEmpty()) {
                log.warn("地图 {} 下没有点位数据", mapId);
                return Collections.emptyList();
            }
            return locations.stream()
                    .filter(l -> l.getXPosition() != null && l.getYPosition() != null)
                    .map(l -> new SimMapPoint(
                            l.getLocationId(),
                            l.getName(),
                            l.getXPosition().doubleValue() / 1000.0,
                            l.getYPosition().doubleValue() / 1000.0))
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
}
