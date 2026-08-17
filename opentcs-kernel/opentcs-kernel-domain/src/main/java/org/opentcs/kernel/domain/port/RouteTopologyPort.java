package org.opentcs.kernel.domain.port;

import org.opentcs.kernel.domain.routing.Path;
import org.opentcs.kernel.domain.routing.Point;

import java.util.Collection;
import java.util.List;

/**
 * 运行时拓扑查询 / 注册端口（modules 依赖此接口，不依赖 {@code RoutePlannerImpl}）。
 * <p>
 * DTO 级规划仍走 {@code RoutePlannerApi}；本端口提供领域模型级拓扑协作。
 * </p>
 */
public interface RouteTopologyPort {

    void registerPoint(Point point);

    void registerPath(Path path);

    void unregisterPoint(String pointId);

    void unregisterPath(String pathId);

    Point getPoint(String pointId);

    Path getPath(String pathId);

    /** 当前运行时地图全部点位（只读视图）。 */
    Collection<Point> getAllPoints();

    void clear();

    List<Path> findPath(String sourcePointId, String destPointId);

    List<Path> findPath(String sourcePointId, String destPointId, String vehicleId);
}
