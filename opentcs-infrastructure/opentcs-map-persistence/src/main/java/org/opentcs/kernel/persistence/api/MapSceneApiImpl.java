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
import org.opentcs.kernel.persistence.service.BlockRepository;
import org.opentcs.kernel.persistence.service.CrossLayerConnectionRepository;
import org.opentcs.kernel.persistence.service.DTOConverter;
import org.opentcs.kernel.persistence.service.FactoryModelRepository;
import org.opentcs.kernel.persistence.service.LocationRepository;
import org.opentcs.kernel.persistence.service.LocationTypeRepository;
import org.opentcs.kernel.persistence.service.NavigationMapRepository;
import org.opentcs.kernel.persistence.service.PathRepository;
import org.opentcs.kernel.persistence.service.PointRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapSceneApiImpl implements MapSceneApi {

    private final NavigationMapRepository navigationMapRepository;
    private final FactoryModelRepository factoryModelRepository;
    private final LocationTypeRepository locationTypeRepository;
    private final PointRepository pointRepository;
    private final PathRepository pathRepository;
    private final LocationRepository locationRepository;
    private final BlockRepository blockRepository;
    private final CrossLayerConnectionRepository crossLayerConnectionRepository;

    @Override
    public TableDataInfo<NavigationMapDTO> listNavigationMaps(NavigationMapDTO query, PageQuery pageQuery) {
        return navigationMapRepository.selectPageNavigationMap(toEntity(query), pageQuery);
    }

    @Override
    public List<NavigationMapDTO> listNavigationMapsByFactory(Long factoryId) {
        return navigationMapRepository.selectByFactoryModelId(factoryId);
    }

    @Override
    public NavigationMapDTO getNavigationMapById(Long id) {
        return DTOConverter.toNavigationMapDTO(navigationMapRepository.getNavigationMapDetail(id));
    }

    @Override
    public NavigationMapDTO getNavigationMapByFloor(Long factoryId, Integer floorNumber) {
        return navigationMapRepository.selectByFactoryModelIdAndFloor(factoryId, floorNumber);
    }

    @Override
    public NavigationMapDTO getNavigationMapByMapId(String mapId) {
        return navigationMapRepository.selectByMapId(mapId);
    }

    @Override
    public boolean createNavigationMap(NavigationMapDTO dto) {
        return navigationMapRepository.createNavigationMap(toEntity(dto));
    }

    @Override
    public boolean updateNavigationMap(NavigationMapDTO dto) {
        return navigationMapRepository.updateNavigationMap(toEntity(dto));
    }

    @Override
    public boolean deleteNavigationMap(Long id) {
        return navigationMapRepository.deleteNavigationMap(id);
    }

    @Override
    public TableDataInfo<FactoryModelDTO> listFactoryModels(FactoryModelDTO query, PageQuery pageQuery) {
        return factoryModelRepository.selectPageFactoryModelDTO(query, pageQuery);
    }

    @Override
    public FactoryModelDTO getFactoryModelById(Long id) {
        return factoryModelRepository.getFactoryModelDetailDTO(id);
    }

    @Override
    public boolean createFactoryModel(FactoryModelDTO dto) {
        return factoryModelRepository.createFactoryModelDTO(dto);
    }

    @Override
    public boolean updateFactoryModel(FactoryModelDTO dto) {
        return factoryModelRepository.updateFactoryModelDTO(dto);
    }

    @Override
    public boolean deleteFactoryModel(Long id) {
        return factoryModelRepository.deleteFactoryModel(id);
    }

    @Override
    public TableDataInfo<LocationTypeDTO> listLocationTypes(LocationTypeDTO query, PageQuery pageQuery) {
        return locationTypeRepository.selectPageDTO(query, pageQuery);
    }

    @Override
    public LocationTypeDTO getLocationTypeById(Long id) {
        return locationTypeRepository.getByIdDTO(id);
    }

    @Override
    public List<LocationTypeDTO> listAllLocationTypes() {
        return locationTypeRepository.listDTO();
    }

    @Override
    public boolean createLocationType(LocationTypeDTO dto) {
        return locationTypeRepository.saveDTO(dto);
    }

    @Override
    public boolean updateLocationType(LocationTypeDTO dto) {
        return locationTypeRepository.updateByIdDTO(dto);
    }

    @Override
    public boolean deleteLocationType(Long id) {
        return locationTypeRepository.removeById(id);
    }

    @Override
    public List<PointDTO> listPointsByMap(Long mapId) {
        return pointRepository.listByMapDTO(mapId);
    }

    @Override
    public boolean replacePointsByMap(Long mapId, List<PointDTO> points) {
        pointRepository.removeByMap(mapId);
        if (points == null || points.isEmpty()) {
            return true;
        }
        for (PointDTO point : points) {
            point.setId(null);
            point.setNavigationMapId(mapId);
            pointRepository.saveDTO(point);
        }
        return true;
    }

    @Override
    public TableDataInfo<PathDTO> listPaths(PathDTO query, PageQuery pageQuery) {
        return pathRepository.selectPageDTO(query, pageQuery);
    }

    @Override
    public List<PathDTO> listPathsByFactory(Long factoryId) {
        List<Long> mapIds = navigationMapRepository.selectByFactoryModelId(factoryId)
            .stream()
            .map(NavigationMapDTO::getId)
            .toList();
        if (mapIds.isEmpty()) {
            return List.of();
        }
        return pathRepository.listByMapIdsDTO(mapIds);
    }

    @Override
    public List<PathDTO> listPathsByMap(Long mapId) {
        return pathRepository.listByMapDTO(mapId);
    }

    @Override
    public PathDTO getPathById(Long id) {
        return pathRepository.getByIdDTO(id);
    }

    @Override
    public boolean replacePathsByMap(Long mapId, List<PathDTO> paths) {
        pathRepository.removeByMap(mapId);
        if (paths == null || paths.isEmpty()) {
            return true;
        }
        for (PathDTO path : paths) {
            path.setId(null);
            path.setNavigationMapId(mapId);
            pathRepository.saveDTO(path);
        }
        return true;
    }

    @Override
    public TableDataInfo<LocationDTO> listLocations(LocationDTO query, PageQuery pageQuery) {
        return locationRepository.selectPageDTO(query, pageQuery);
    }

    @Override
    public List<LocationDTO> listLocationsByFactory(Long factoryId) {
        List<Long> mapIds = navigationMapRepository.selectByFactoryModelId(factoryId)
            .stream()
            .map(NavigationMapDTO::getId)
            .toList();
        if (mapIds.isEmpty()) {
            return List.of();
        }
        return locationRepository.selectByMapIdsDTO(mapIds);
    }

    @Override
    public List<LocationDTO> listLocationsByMap(Long mapId) {
        return locationRepository.selectByNavigationMapIdDTO(mapId);
    }

    @Override
    public LocationDTO getLocationById(Long id) {
        return locationRepository.selectByIdDTO(id);
    }

    @Override
    public boolean replaceLocationsByMap(Long mapId, List<LocationDTO> locations) {
        locationRepository.removeByMap(mapId);
        if (locations == null || locations.isEmpty()) {
            return true;
        }
        for (LocationDTO location : locations) {
            location.setId(null);
            location.setNavigationMapId(mapId);
            locationRepository.saveDTO(location);
        }
        return true;
    }

    @Override
    public TableDataInfo<BlockDTO> listBlocks(BlockDTO query, PageQuery pageQuery) {
        return blockRepository.selectPageDTO(DTOConverter.toBlockEntity(query), pageQuery);
    }

    @Override
    public List<BlockDTO> listBlocksByFactory(Long factoryId) {
        return blockRepository.selectByFactoryModelIdDTO(factoryId);
    }

    @Override
    public List<BlockDTO> listBlocksByFactoryAndType(Long factoryId, String type) {
        return blockRepository.selectByFactoryModelIdAndTypeDTO(factoryId, type);
    }

    @Override
    public List<BlockDTO> listBlocksByMap(Long navigationMapId) {
        return DTOConverter.toBlockDTOList(blockRepository.selectByNavigationMapId(navigationMapId));
    }

    @Override
    public boolean replaceBlocksByMap(Long navigationMapId, List<BlockDTO> blocks) {
        blockRepository.remove(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<
                org.opentcs.kernel.persistence.entity.BlockEntity>()
                .eq(org.opentcs.kernel.persistence.entity.BlockEntity::getNavigationMapId, navigationMapId)
        );
        if (blocks == null || blocks.isEmpty()) {
            return true;
        }
        for (BlockDTO block : blocks) {
            org.opentcs.kernel.persistence.entity.BlockEntity entity = DTOConverter.toBlockEntity(block);
            entity.setId(null);
            entity.setNavigationMapId(navigationMapId);
            blockRepository.save(entity);
        }
        return true;
    }

    @Override
    public BlockDTO getBlockById(Long id) {
        return blockRepository.selectByIdDTO(id);
    }

    @Override
    public boolean createBlock(BlockDTO dto) {
        return blockRepository.create(DTOConverter.toBlockEntity(dto));
    }

    @Override
    public boolean updateBlock(BlockDTO dto) {
        return blockRepository.update(DTOConverter.toBlockEntity(dto));
    }

    @Override
    public boolean deleteBlock(Long id) {
        return blockRepository.delete(id);
    }

    @Override
    public TableDataInfo<CrossLayerConnectionDTO> listConnections(CrossLayerConnectionDTO query, PageQuery pageQuery) {
        TableDataInfo<CrossLayerConnectionEntity> entityPage =
            crossLayerConnectionRepository.selectPageConnection(DTOConverter.toCrossLayerConnectionEntity(query), pageQuery);
        return new TableDataInfo<>(
            entityPage.getRows().stream().map(DTOConverter::toCrossLayerConnectionDTO).toList(),
            entityPage.getTotal()
        );
    }

    @Override
    public List<CrossLayerConnectionDTO> listConnectionsByFactory(Long factoryId) {
        return DTOConverter.toCrossLayerConnectionDTOList(crossLayerConnectionRepository.selectByFactoryModelId(factoryId));
    }

    @Override
    public List<CrossLayerConnectionDTO> listAvailableConnections(Long factoryId) {
        return DTOConverter.toCrossLayerConnectionDTOList(crossLayerConnectionRepository.selectAvailableConnections(factoryId));
    }

    @Override
    public CrossLayerConnectionDTO getConnectionById(Long id) {
        return DTOConverter.toCrossLayerConnectionDTO(crossLayerConnectionRepository.selectById(id));
    }

    @Override
    public boolean createConnection(CrossLayerConnectionDTO dto) {
        return crossLayerConnectionRepository.createConnection(DTOConverter.toCrossLayerConnectionEntity(dto));
    }

    @Override
    public boolean updateConnection(CrossLayerConnectionDTO dto) {
        return crossLayerConnectionRepository.updateConnection(DTOConverter.toCrossLayerConnectionEntity(dto));
    }

    @Override
    public boolean deleteConnection(Long id) {
        return crossLayerConnectionRepository.deleteConnection(id);
    }

    @Override
    public boolean reserveElevator(String connectionId, Long vehicleId) {
        return crossLayerConnectionRepository.reserveElevator(connectionId, vehicleId);
    }

    @Override
    public boolean releaseElevator(String connectionId, Long vehicleId) {
        return crossLayerConnectionRepository.releaseElevator(connectionId, vehicleId);
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
        entity.setYamlUrl(dto.getYamlUrl());
        entity.setYamlOrigin(dto.getYamlOrigin());
        entity.setMapOrigin(dto.getMapOrigin());
        entity.setCreateTime(dto.getCreateTime());
        entity.setUpdateTime(dto.getUpdateTime());
        return entity;
    }
}
