package org.opentcs.map.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.api.dto.FactoryModelDTO;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.kernel.api.dto.RouteDTO;
import org.opentcs.kernel.api.map.MapSceneApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 地图应用服务
 * 整合数据库持久化和内核路由服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MapApplicationService {

    private final MapSceneApi mapSceneApi;
    private final RoutePlannerImpl routePlanner;

    /**
     * 加载工厂地图到内核
     * 将数据库中的点位和路径加载到内核路由服务
     */
    @Transactional(readOnly = true)
    public void loadMapToKernel(Long factoryModelId) {
        FactoryModelDTO factoryModel = mapSceneApi.getFactoryModelById(factoryModelId);
        if (factoryModel == null) {
            throw new RuntimeException("工厂模型不存在: " + factoryModelId);
        }

        // 清空现有路由数据
        routePlanner.clear();

        // 加载点位 - TODO: 需要按工厂查询
        // List<PointEntity> points = pointDomainService.selectAllPointByFactoryModelId(factoryModelId);

        // 加载路径 - TODO: 需要按工厂查询
        // List<PathEntity> paths = pathDomainService.selectAllPathByFactoryModelId(factoryModelId);

        log.info("地图已加载到内核: {}", factoryModel.getName());
    }

    /**
     * 注册点到内核
     */
    public void registerPoint(PointDTO pointDTO) {
        org.opentcs.kernel.domain.routing.Point point =
            new org.opentcs.kernel.domain.routing.Point(
                String.valueOf(pointDTO.getId()),
                pointDTO.getName(),
                pointDTO.getXPosition() != null ? pointDTO.getXPosition().doubleValue() : 0,
                pointDTO.getYPosition() != null ? pointDTO.getYPosition().doubleValue() : 0,
                pointDTO.getZPosition() != null ? pointDTO.getZPosition().doubleValue() : 0
        );
        routePlanner.registerPoint(point);
        log.debug("点位已注册到内核: {}", pointDTO.getId());
    }

    /**
     * 注销点位
     */
    public void unregisterPoint(String pointId) {
        routePlanner.unregisterPoint(pointId);
        log.debug("点位已从内核注销: {}", pointId);
    }

    /**
     * 注册路径到内核
     */
    public void registerPath(PathDTO pathDTO) {
        org.opentcs.kernel.domain.routing.Path path =
            new org.opentcs.kernel.domain.routing.Path(
                String.valueOf(pathDTO.getId()),
                String.valueOf(pathDTO.getSourcePointId()),
                String.valueOf(pathDTO.getDestPointId()),
                pathDTO.getLength() != null ? pathDTO.getLength().doubleValue() : 0
        );
        routePlanner.registerPath(path);
        log.debug("路径已注册到内核: {}", pathDTO.getId());
    }

    /**
     * 注销路径
     */
    public void unregisterPath(String pathId) {
        routePlanner.unregisterPath(pathId);
        log.debug("路径已从内核注销: {}", pathId);
    }

    /**
     * 查找最短路径
     */
    public RouteDTO findRoute(String sourcePointId, String destPointId) {
        Optional<RouteDTO> route = routePlanner.findRoute(sourcePointId, destPointId);
        return route.orElse(null);
    }
}
