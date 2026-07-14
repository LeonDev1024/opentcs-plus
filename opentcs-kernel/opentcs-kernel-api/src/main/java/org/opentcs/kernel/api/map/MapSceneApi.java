package org.opentcs.kernel.api.map;

import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.CrossLayerConnectionDTO;
import org.opentcs.kernel.api.dto.FactoryModelDTO;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.dto.PointDTO;

import java.util.List;

/**
 * 地图场景读写端口（导航图/区域/跨层连接）。
 */
public interface MapSceneApi {

    TableDataInfo<NavigationMapDTO> listNavigationMaps(NavigationMapDTO query, PageQuery pageQuery);

    List<NavigationMapDTO> listNavigationMapsByFactory(Long factoryId);

    NavigationMapDTO getNavigationMapById(Long id);

    NavigationMapDTO getNavigationMapByFloor(Long factoryId, Integer floorNumber);

    NavigationMapDTO getNavigationMapByMapId(String mapId);

    boolean createNavigationMap(NavigationMapDTO dto);

    boolean updateNavigationMap(NavigationMapDTO dto);

    boolean deleteNavigationMap(Long id);

    TableDataInfo<FactoryModelDTO> listFactoryModels(FactoryModelDTO query, PageQuery pageQuery);

    FactoryModelDTO getFactoryModelById(Long id);

    boolean createFactoryModel(FactoryModelDTO dto);

    boolean updateFactoryModel(FactoryModelDTO dto);

    boolean deleteFactoryModel(Long id);

    List<PointDTO> listPointsByMap(Long mapId);

    boolean replacePointsByMap(Long mapId, List<PointDTO> points);

    TableDataInfo<PathDTO> listPaths(PathDTO query, PageQuery pageQuery);

    List<PathDTO> listPathsByFactory(Long factoryId);

    List<PathDTO> listPathsByMap(Long mapId);

    PathDTO getPathById(Long id);

    boolean replacePathsByMap(Long mapId, List<PathDTO> paths);

    TableDataInfo<CrossLayerConnectionDTO> listConnections(CrossLayerConnectionDTO query, PageQuery pageQuery);

    List<CrossLayerConnectionDTO> listConnectionsByFactory(Long factoryId);

    List<CrossLayerConnectionDTO> listAvailableConnections(Long factoryId);

    CrossLayerConnectionDTO getConnectionById(Long id);

    boolean createConnection(CrossLayerConnectionDTO dto);

    boolean updateConnection(CrossLayerConnectionDTO dto);

    boolean deleteConnection(Long id);

    boolean reserveElevator(String connectionId, Long vehicleId);

    boolean releaseElevator(String connectionId, Long vehicleId);
}
