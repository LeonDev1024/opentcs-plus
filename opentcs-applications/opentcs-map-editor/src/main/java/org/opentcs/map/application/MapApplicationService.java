package org.opentcs.map.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.application.RoutePlannerImpl;
import org.opentcs.kernel.persistence.entity.PathEntity;
import org.opentcs.kernel.persistence.entity.PlantModelEntity;
import org.opentcs.kernel.persistence.entity.PointEntity;
import org.opentcs.kernel.persistence.service.PathDomainService;
import org.opentcs.kernel.persistence.service.PlantModelDomainService;
import org.opentcs.kernel.persistence.service.PointDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地图应用服务
 * 整合数据库持久化和内核路由服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MapApplicationService {

    private final PlantModelDomainService plantModelDomainService;
    private final PointDomainService pointDomainService;
    private final PathDomainService pathDomainService;
    private final RoutePlannerImpl routePlanner;

    /**
     * 加载地图到内核
     * 将数据库中的点位和路径加载到内核路由服务
     */
    @Transactional(readOnly = true)
    public void loadMapToKernel(Long plantModelId) {
        PlantModelEntity plantModel = plantModelDomainService.selectById(plantModelId);
        if (plantModel == null) {
            throw new RuntimeException("地图模型不存在: " + plantModelId);
        }

        // 清空现有路由数据
        routePlanner.clear();

        // 加载点位
        List<PointEntity> points = pointDomainService.selectAllPointByPlantModelId(plantModelId);
        for (PointEntity pointEntity : points) {
            org.opentcs.kernel.domain.routing.Point point =
                new org.opentcs.kernel.domain.routing.Point(
                    String.valueOf(pointEntity.getId()),
                    pointEntity.getName(),
                    pointEntity.getXPosition() != null ? pointEntity.getXPosition().doubleValue() : 0,
                    pointEntity.getYPosition() != null ? pointEntity.getYPosition().doubleValue() : 0,
                    pointEntity.getZPosition() != null ? pointEntity.getZPosition().doubleValue() : 0
            );
            routePlanner.registerPoint(point);
        }

        // 加载路径
        List<PathEntity> paths = pathDomainService.selectAllPathByPlantModelId(plantModelId);
        for (PathEntity pathEntity : paths) {
            org.opentcs.kernel.domain.routing.Path path =
                new org.opentcs.kernel.domain.routing.Path(
                    String.valueOf(pathEntity.getId()),
                    String.valueOf(pathEntity.getSourcePointId()),
                    String.valueOf(pathEntity.getDestPointId()),
                    pathEntity.getLength() != null ? pathEntity.getLength().doubleValue() : 0
            );
            routePlanner.registerPath(path);
        }

        log.info("地图已加载到内核: {} - {} 点, {} 路径",
                plantModel.getName(), points.size(), paths.size());
    }

    /**
     * 注册点到内核
     */
    public void registerPoint(PointEntity pointEntity) {
        org.opentcs.kernel.domain.routing.Point point =
            new org.opentcs.kernel.domain.routing.Point(
                String.valueOf(pointEntity.getId()),
                pointEntity.getName(),
                pointEntity.getXPosition() != null ? pointEntity.getXPosition().doubleValue() : 0,
                pointEntity.getYPosition() != null ? pointEntity.getYPosition().doubleValue() : 0,
                pointEntity.getZPosition() != null ? pointEntity.getZPosition().doubleValue() : 0
        );
        routePlanner.registerPoint(point);
        log.debug("点位已注册到内核: {}", pointEntity.getId());
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
    public void registerPath(PathEntity pathEntity) {
        org.opentcs.kernel.domain.routing.Path path =
            new org.opentcs.kernel.domain.routing.Path(
                String.valueOf(pathEntity.getId()),
                String.valueOf(pathEntity.getSourcePointId()),
                String.valueOf(pathEntity.getDestPointId()),
                pathEntity.getLength() != null ? pathEntity.getLength().doubleValue() : 0
        );
        routePlanner.registerPath(path);
        log.debug("路径已注册到内核: {}", pathEntity.getId());
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
    public List<org.opentcs.kernel.domain.routing.Point> findRoute(String sourcePointId, String destPointId) {
        return routePlanner.findRoute(sourcePointId, destPointId);
    }
}
