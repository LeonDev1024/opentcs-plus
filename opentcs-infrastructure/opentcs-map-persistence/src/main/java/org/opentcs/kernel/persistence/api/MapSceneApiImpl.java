package org.opentcs.kernel.persistence.api;

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
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.kernel.api.map.MapSceneApi;
import org.opentcs.kernel.persistence.entity.CrossLayerConnectionEntity;
import org.opentcs.kernel.persistence.entity.NavigationMapEntity;
import org.opentcs.kernel.persistence.service.BlockDomainService;
import org.opentcs.kernel.persistence.service.CrossLayerConnectionDomainService;
import org.opentcs.kernel.persistence.service.DTOConverter;
import org.opentcs.kernel.persistence.service.FactoryModelDomainService;
import org.opentcs.kernel.persistence.service.LocationDomainService;
import org.opentcs.kernel.persistence.service.LocationTypeDomainService;
import org.opentcs.kernel.persistence.service.NavigationMapDomainService;
import org.opentcs.kernel.persistence.service.PathDomainService;
import org.opentcs.kernel.persistence.service.PointDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapSceneApiImpl implements MapSceneApi {

    private final NavigationMapDomainService navigationMapDomainService;
    private final FactoryModelDomainService factoryModelDomainService;
    private final LocationTypeDomainService locationTypeDomainService;
    private final PointDomainService pointDomainService;
    private final PathDomainService pathDomainService;
    private final LocationDomainService locationDomainService;
    private final BlockDomainService blockDomainService;
    private final CrossLayerConnectionDomainService crossLayerConnectionDomainService;

    @Override
    public TableDataInfo<NavigationMapDTO> listNavigationMaps(NavigationMapDTO query, PageQuery pageQuery) {
        return navigationMapDomainService.selectPageNavigationMap(toEntity(query), pageQuery);
    }

    @Override
    public List<NavigationMapDTO> listNavigationMapsByFactory(Long factoryId) {
        return navigationMapDomainService.selectByFactoryModelId(factoryId);
    }

    @Override
    public NavigationMapDTO getNavigationMapById(Long id) {
        return DTOConverter.toNavigationMapDTO(navigationMapDomainService.getNavigationMapDetail(id));
    }

    @Override
    public NavigationMapDTO getNavigationMapByFloor(Long factoryId, Integer floorNumber) {
        return navigationMapDomainService.selectByFactoryModelIdAndFloor(factoryId, floorNumber);
    }

    @Override
    public NavigationMapDTO getNavigationMapByMapId(String mapId) {
        return navigationMapDomainService.selectByMapId(mapId);
    }

    @Override
    public boolean createNavigationMap(NavigationMapDTO dto) {
        return navigationMapDomainService.createNavigationMap(toEntity(dto));
    }

    @Override
    public boolean updateNavigationMap(NavigationMapDTO dto) {
        return navigationMapDomainService.updateNavigationMap(toEntity(dto));
    }

    @Override
    public boolean deleteNavigationMap(Long id) {
        return navigationMapDomainService.deleteNavigationMap(id);
    }

    @Override
    public TableDataInfo<FactoryModelDTO> listFactoryModels(FactoryModelDTO query, PageQuery pageQuery) {
        return factoryModelDomainService.selectPageFactoryModelDTO(query, pageQuery);
    }

    @Override
    public FactoryModelDTO getFactoryModelById(Long id) {
        return factoryModelDomainService.getFactoryModelDetailDTO(id);
    }

    @Override
    public boolean createFactoryModel(FactoryModelDTO dto) {
        return factoryModelDomainService.createFactoryModelDTO(dto);
    }

    @Override
    public boolean updateFactoryModel(FactoryModelDTO dto) {
        return factoryModelDomainService.updateFactoryModelDTO(dto);
    }

    @Override
    public boolean deleteFactoryModel(Long id) {
        return factoryModelDomainService.deleteFactoryModel(id);
    }

    @Override
    public TableDataInfo<LocationTypeDTO> listLocationTypes(LocationTypeDTO query, PageQuery pageQuery) {
        return locationTypeDomainService.selectPageDTO(query, pageQuery);
    }

    @Override
    public LocationTypeDTO getLocationTypeById(Long id) {
        return locationTypeDomainService.getByIdDTO(id);
    }

    @Override
    public List<LocationTypeDTO> listAllLocationTypes() {
        return locationTypeDomainService.listDTO();
    }

    @Override
    public boolean createLocationType(LocationTypeDTO dto) {
        return locationTypeDomainService.saveDTO(dto);
    }

    @Override
    public boolean updateLocationType(LocationTypeDTO dto) {
        return locationTypeDomainService.updateByIdDTO(dto);
    }

    @Override
    public boolean deleteLocationType(Long id) {
        return locationTypeDomainService.removeById(id);
    }

    @Override
    public List<PointDTO> listPointsByMap(Long mapId) {
        return pointDomainService.listByMapDTO(mapId);
    }

    @Override
    public boolean replacePointsByMap(Long mapId, List<PointDTO> points) {
        pointDomainService.removeByMap(mapId);
        if (points == null || points.isEmpty()) {
            return true;
        }
        for (PointDTO point : points) {
            point.setId(null);
            point.setNavigationMapId(mapId);
            pointDomainService.saveDTO(point);
        }
        return true;
    }

    @Override
    public TableDataInfo<PathDTO> listPaths(PathDTO query, PageQuery pageQuery) {
        return pathDomainService.selectPageDTO(query, pageQuery);
    }

    @Override
    public List<PathDTO> listPathsByFactory(Long factoryId) {
        List<Long> mapIds = navigationMapDomainService.selectByFactoryModelId(factoryId)
            .stream()
            .map(NavigationMapDTO::getId)
            .toList();
        if (mapIds.isEmpty()) {
            return List.of();
        }
        return pathDomainService.listByMapIdsDTO(mapIds);
    }

    @Override
    public List<PathDTO> listPathsByMap(Long mapId) {
        return pathDomainService.listByMapDTO(mapId);
    }

    @Override
    public PathDTO getPathById(Long id) {
        return pathDomainService.getByIdDTO(id);
    }

    @Override
    public boolean replacePathsByMap(Long mapId, List<PathDTO> paths) {
        pathDomainService.removeByMap(mapId);
        if (paths == null || paths.isEmpty()) {
            return true;
        }
        for (PathDTO path : paths) {
            path.setId(null);
            path.setNavigationMapId(mapId);
            pathDomainService.saveDTO(path);
        }
        return true;
    }

    @Override
    public TableDataInfo<LocationDTO> listLocations(LocationDTO query, PageQuery pageQuery) {
        return locationDomainService.selectPageDTO(query, pageQuery);
    }

    @Override
    public List<LocationDTO> listLocationsByFactory(Long factoryId) {
        List<Long> mapIds = navigationMapDomainService.selectByFactoryModelId(factoryId)
            .stream()
            .map(NavigationMapDTO::getId)
            .toList();
        if (mapIds.isEmpty()) {
            return List.of();
        }
        return locationDomainService.selectByMapIdsDTO(mapIds);
    }

    @Override
    public List<LocationDTO> listLocationsByMap(Long mapId) {
        return locationDomainService.selectByNavigationMapIdDTO(mapId);
    }

    @Override
    public LocationDTO getLocationById(Long id) {
        return locationDomainService.selectByIdDTO(id);
    }

    @Override
    public boolean replaceLocationsByMap(Long mapId, List<LocationDTO> locations) {
        locationDomainService.removeByMap(mapId);
        if (locations == null || locations.isEmpty()) {
            return true;
        }
        for (LocationDTO location : locations) {
            location.setId(null);
            location.setNavigationMapId(mapId);
            locationDomainService.saveDTO(location);
        }
        return true;
    }

    @Override
    public TableDataInfo<BlockDTO> listBlocks(BlockDTO query, PageQuery pageQuery) {
        return blockDomainService.selectPageDTO(DTOConverter.toBlockEntity(query), pageQuery);
    }

    @Override
    public List<BlockDTO> listBlocksByFactory(Long factoryId) {
        return blockDomainService.selectByFactoryModelIdDTO(factoryId);
    }

    @Override
    public List<BlockDTO> listBlocksByFactoryAndType(Long factoryId, String type) {
        return blockDomainService.selectByFactoryModelIdAndTypeDTO(factoryId, type);
    }

    @Override
    public BlockDTO getBlockById(Long id) {
        return blockDomainService.selectByIdDTO(id);
    }

    @Override
    public boolean createBlock(BlockDTO dto) {
        return blockDomainService.create(DTOConverter.toBlockEntity(dto));
    }

    @Override
    public boolean updateBlock(BlockDTO dto) {
        return blockDomainService.update(DTOConverter.toBlockEntity(dto));
    }

    @Override
    public boolean deleteBlock(Long id) {
        return blockDomainService.delete(id);
    }

    @Override
    public TableDataInfo<CrossLayerConnectionDTO> listConnections(CrossLayerConnectionDTO query, PageQuery pageQuery) {
        TableDataInfo<CrossLayerConnectionEntity> entityPage =
            crossLayerConnectionDomainService.selectPageConnection(DTOConverter.toCrossLayerConnectionEntity(query), pageQuery);
        return new TableDataInfo<>(
            entityPage.getRows().stream().map(DTOConverter::toCrossLayerConnectionDTO).toList(),
            entityPage.getTotal()
        );
    }

    @Override
    public List<CrossLayerConnectionDTO> listConnectionsByFactory(Long factoryId) {
        return DTOConverter.toCrossLayerConnectionDTOList(crossLayerConnectionDomainService.selectByFactoryModelId(factoryId));
    }

    @Override
    public List<CrossLayerConnectionDTO> listAvailableConnections(Long factoryId) {
        return DTOConverter.toCrossLayerConnectionDTOList(crossLayerConnectionDomainService.selectAvailableConnections(factoryId));
    }

    @Override
    public CrossLayerConnectionDTO getConnectionById(Long id) {
        return DTOConverter.toCrossLayerConnectionDTO(crossLayerConnectionDomainService.selectById(id));
    }

    @Override
    public boolean createConnection(CrossLayerConnectionDTO dto) {
        return crossLayerConnectionDomainService.createConnection(DTOConverter.toCrossLayerConnectionEntity(dto));
    }

    @Override
    public boolean updateConnection(CrossLayerConnectionDTO dto) {
        return crossLayerConnectionDomainService.updateConnection(DTOConverter.toCrossLayerConnectionEntity(dto));
    }

    @Override
    public boolean deleteConnection(Long id) {
        return crossLayerConnectionDomainService.deleteConnection(id);
    }

    @Override
    public boolean reserveElevator(String connectionId, Long vehicleId) {
        return crossLayerConnectionDomainService.reserveElevator(connectionId, vehicleId);
    }

    @Override
    public boolean releaseElevator(String connectionId, Long vehicleId) {
        return crossLayerConnectionDomainService.releaseElevator(connectionId, vehicleId);
    }

    private NavigationMapEntity toEntity(NavigationMapDTO dto) {
        NavigationMapEntity entity = new NavigationMapEntity();
        entity.setId(dto.getId());
        entity.setFactoryModelId(dto.getFactoryModelId());
        entity.setMapId(dto.getMapId());
        entity.setName(dto.getName());
        entity.setFloorNumber(dto.getFloorNumber());
        entity.setVehicleTypeId(dto.getVehicleTypeId());
        entity.setOriginX(dto.getOriginX());
        entity.setOriginY(dto.getOriginY());
        entity.setRotation(dto.getRotation());
        entity.setProperties(dto.getProperties());
        entity.setStatus(dto.getStatus());
        entity.setMapVersion(dto.getMapVersion());
        entity.setRasterUrl(dto.getRasterUrl());
        entity.setRasterVersion(dto.getRasterVersion());
        entity.setRasterWidth(dto.getRasterWidth());
        entity.setRasterHeight(dto.getRasterHeight());
        entity.setRasterResolution(dto.getRasterResolution());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }
}
