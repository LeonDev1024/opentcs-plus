package org.opentcs.map.application;

import lombok.RequiredArgsConstructor;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.kernel.api.dto.CrossLayerConnectionDTO;
import org.opentcs.kernel.api.dto.FactoryModelDTO;
import org.opentcs.kernel.api.dto.LocationDTO;
import org.opentcs.kernel.api.dto.LocationTypeDTO;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.map.MapSceneApi;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapFacadeApplicationService {

    private final MapSceneApi mapSceneApi;

    public TableDataInfo<NavigationMapDTO> listNavigationMaps(NavigationMapDTO query, PageQuery pageQuery) {
        return mapSceneApi.listNavigationMaps(query, pageQuery);
    }

    public List<NavigationMapDTO> listNavigationMapsByFactory(Long factoryId) {
        return mapSceneApi.listNavigationMapsByFactory(factoryId);
    }

    public NavigationMapDTO getNavigationMapById(Long id) {
        return mapSceneApi.getNavigationMapById(id);
    }

    public NavigationMapDTO getNavigationMapByFloor(Long factoryId, Integer floorNumber) {
        return mapSceneApi.getNavigationMapByFloor(factoryId, floorNumber);
    }

    public boolean createNavigationMap(NavigationMapDTO dto) {
        return mapSceneApi.createNavigationMap(dto);
    }

    public boolean updateNavigationMap(NavigationMapDTO dto) {
        return mapSceneApi.updateNavigationMap(dto);
    }

    public boolean deleteNavigationMap(Long id) {
        return mapSceneApi.deleteNavigationMap(id);
    }

    public TableDataInfo<FactoryModelDTO> listFactoryModels(FactoryModelDTO query, PageQuery pageQuery) {
        return mapSceneApi.listFactoryModels(query, pageQuery);
    }

    public FactoryModelDTO getFactoryModelById(Long id) {
        return mapSceneApi.getFactoryModelById(id);
    }

    public boolean createFactoryModel(FactoryModelDTO dto) {
        return mapSceneApi.createFactoryModel(dto);
    }

    public boolean updateFactoryModel(FactoryModelDTO dto) {
        return mapSceneApi.updateFactoryModel(dto);
    }

    public boolean deleteFactoryModel(Long id) {
        return mapSceneApi.deleteFactoryModel(id);
    }

    public TableDataInfo<LocationTypeDTO> listLocationTypes(LocationTypeDTO query, PageQuery pageQuery) {
        return mapSceneApi.listLocationTypes(query, pageQuery);
    }

    public LocationTypeDTO getLocationTypeById(Long id) {
        return mapSceneApi.getLocationTypeById(id);
    }

    public List<LocationTypeDTO> listAllLocationTypes() {
        return mapSceneApi.listAllLocationTypes();
    }

    public boolean createLocationType(LocationTypeDTO dto) {
        return mapSceneApi.createLocationType(dto);
    }

    public boolean updateLocationType(LocationTypeDTO dto) {
        return mapSceneApi.updateLocationType(dto);
    }

    public boolean deleteLocationType(Long id) {
        return mapSceneApi.deleteLocationType(id);
    }

    public TableDataInfo<PathDTO> listPaths(PathDTO query, PageQuery pageQuery) {
        return mapSceneApi.listPaths(query, pageQuery);
    }

    public List<PathDTO> listPathsByFactory(Long factoryId) {
        return mapSceneApi.listPathsByFactory(factoryId);
    }

    public List<PathDTO> listPathsByMap(Long mapId) {
        return mapSceneApi.listPathsByMap(mapId);
    }

    public PathDTO getPathById(Long id) {
        return mapSceneApi.getPathById(id);
    }

    public TableDataInfo<LocationDTO> listLocations(LocationDTO query, PageQuery pageQuery) {
        return mapSceneApi.listLocations(query, pageQuery);
    }

    public List<LocationDTO> listLocationsByFactory(Long factoryId) {
        return mapSceneApi.listLocationsByFactory(factoryId);
    }

    public List<LocationDTO> listLocationsByMap(Long mapId) {
        return mapSceneApi.listLocationsByMap(mapId);
    }

    public LocationDTO getLocationById(Long id) {
        return mapSceneApi.getLocationById(id);
    }

    public TableDataInfo<BlockDTO> listBlocks(BlockDTO query, PageQuery pageQuery) {
        return mapSceneApi.listBlocks(query, pageQuery);
    }

    public List<BlockDTO> listBlocksByFactory(Long factoryId) {
        return mapSceneApi.listBlocksByFactory(factoryId);
    }

    public List<BlockDTO> listBlocksByFactoryAndType(Long factoryId, String type) {
        return mapSceneApi.listBlocksByFactoryAndType(factoryId, type);
    }

    public BlockDTO getBlockById(Long id) {
        return mapSceneApi.getBlockById(id);
    }

    public boolean createBlock(BlockDTO dto) {
        return mapSceneApi.createBlock(dto);
    }

    public boolean updateBlock(BlockDTO dto) {
        return mapSceneApi.updateBlock(dto);
    }

    public boolean deleteBlock(Long id) {
        return mapSceneApi.deleteBlock(id);
    }

    public TableDataInfo<CrossLayerConnectionDTO> listConnections(CrossLayerConnectionDTO query, PageQuery pageQuery) {
        return mapSceneApi.listConnections(query, pageQuery);
    }

    public List<CrossLayerConnectionDTO> listConnectionsByFactory(Long factoryId) {
        return mapSceneApi.listConnectionsByFactory(factoryId);
    }

    public List<CrossLayerConnectionDTO> listAvailableConnections(Long factoryId) {
        return mapSceneApi.listAvailableConnections(factoryId);
    }

    public CrossLayerConnectionDTO getConnectionById(Long id) {
        return mapSceneApi.getConnectionById(id);
    }

    public boolean createConnection(CrossLayerConnectionDTO dto) {
        return mapSceneApi.createConnection(dto);
    }

    public boolean updateConnection(CrossLayerConnectionDTO dto) {
        return mapSceneApi.updateConnection(dto);
    }

    public boolean deleteConnection(Long id) {
        return mapSceneApi.deleteConnection(id);
    }

    public boolean reserveElevator(String connectionId, Long vehicleId) {
        return mapSceneApi.reserveElevator(connectionId, vehicleId);
    }

    public boolean releaseElevator(String connectionId, Long vehicleId) {
        return mapSceneApi.releaseElevator(connectionId, vehicleId);
    }

}
