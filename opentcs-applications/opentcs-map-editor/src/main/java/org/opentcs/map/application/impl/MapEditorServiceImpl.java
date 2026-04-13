package org.opentcs.map.application.impl;

import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.kernel.api.dto.FactoryModelDTO;
import org.opentcs.kernel.api.dto.LocationDTO;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.api.dto.PathDTO;
import org.opentcs.kernel.api.dto.PointDTO;
import org.opentcs.kernel.api.map.MapSceneApi;
import org.opentcs.kernel.api.map.MapSnapshotHistoryPort;
import org.opentcs.kernel.persistence.entity.LayerEntity;
import org.opentcs.kernel.persistence.entity.LayerGroupEntity;
import org.opentcs.kernel.persistence.service.LayerRepository;
import org.opentcs.kernel.persistence.service.LayerGroupRepository;
import org.opentcs.map.application.IMapEditorService;
import org.opentcs.map.domain.dto.MapEditorDTO;
import org.opentcs.map.domain.dto.MapEditorLayerDTO;
import org.opentcs.map.domain.dto.MapEditorLayerGroupDTO;
import org.opentcs.map.domain.dto.MapEditorMapInfoDTO;
import org.opentcs.map.domain.dto.MapEditorSaveDTO;
import org.opentcs.map.domain.vo.LoadModelVO;
import org.opentcs.map.utils.MapVersionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 地图编辑器应用服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MapEditorServiceImpl implements IMapEditorService {

    /**
     * 地图文件存储根目录
     */
    @Value("${opentcs.map.storage-path:./maps}")
    private String mapStoragePath;

    private final MapSceneApi mapSceneApi;
    private final MapSnapshotHistoryPort mapSnapshotHistoryPort;
    private final LayerGroupRepository layerGroupRepository;
    private final LayerRepository layerRepository;
    private final ObjectMapper objectMapper;

    /**
     * 地图状态：草稿
     */
    private static final String STATUS_DRAFT = "0";
    /**
     * 地图状态：已发布
     */
    private static final String STATUS_PUBLISHED = "1";
    private static final String MAP_ID_PATTERN = "^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$";

    @Override
    public MapEditorDTO load(LoadModelVO loadModelVO) {
        String mapId = loadModelVO.getMapId();
        validateMapId(mapId);

        // 获取导航地图基本信息（根据地图编号查询）
        NavigationMapDTO navMapDTO = mapSceneApi.getNavigationMapByMapId(mapId);
        if (navMapDTO == null) {
            return null;
        }
        Long navMapId = navMapDTO.getId();

        // 获取工厂模型信息
        FactoryModelDTO factoryModel = mapSceneApi.getFactoryModelById(navMapDTO.getFactoryModelId());
        if (factoryModel == null) {
            return null;
        }

        // 查询地图元素
        var points = mapSceneApi.listPointsByMap(navMapId);
        var paths = mapSceneApi.listPathsByMap(navMapId);
        var locations = mapSceneApi.listLocationsByMap(navMapId);
        var layerGroups = layerGroupRepository.selectByNavigationMapId(navMapId);
        var layers = layerRepository.selectByNavigationMapId(navMapId);

        // 尝试加载 JSON 快照
        String snapshotUrl = getSnapshotFilePath(navMapDTO.getFactoryModelId(), mapId, navMapDTO.getMapVersion());
        String jsonData = null;
        try {
            Path path = Paths.get(snapshotUrl);
            if (Files.exists(path)) {
                jsonData = Files.readString(path);
            }
        } catch (IOException e) {
            log.warn("加载 JSON 快照失败: {}", snapshotUrl, e);
        }

        // 构建 DTO
        MapEditorMapInfoDTO mapInfo = new MapEditorMapInfoDTO();
        mapInfo.setId(navMapDTO.getId());
        mapInfo.setName(navMapDTO.getName());
        mapInfo.setMapId(navMapDTO.getMapId());
        mapInfo.setFactoryModelId(navMapDTO.getFactoryModelId());
        mapInfo.setFactoryName(factoryModel.getName());
        mapInfo.setOriginX(navMapDTO.getOriginX());
        mapInfo.setOriginY(navMapDTO.getOriginY());
        mapInfo.setRotation(navMapDTO.getRotation());
        mapInfo.setMapVersion(navMapDTO.getMapVersion());
        mapInfo.setStatus(navMapDTO.getStatus());
        mapInfo.setData(jsonData);
        mapInfo.setCreateTime(navMapDTO.getCreateTime());
        mapInfo.setUpdateTime(navMapDTO.getUpdateTime());

        // 设置栅格底图相关字段
        mapInfo.setRasterUrl(navMapDTO.getRasterUrl());
        mapInfo.setRasterVersion(navMapDTO.getRasterVersion());
        mapInfo.setRasterWidth(navMapDTO.getRasterWidth());
        mapInfo.setRasterHeight(navMapDTO.getRasterHeight());
        mapInfo.setRasterResolution(navMapDTO.getRasterResolution());
        mapInfo.setYamlOrigin(navMapDTO.getYamlOrigin());
        mapInfo.setYamlUrl(navMapDTO.getYamlUrl());
        mapInfo.setMapOrigin(navMapDTO.getMapOrigin());

        var blocks = mapSceneApi.listBlocksByMap(navMapId);

        MapEditorDTO dto = new MapEditorDTO();
        dto.setMapInfo(mapInfo);
        dto.setPoints(points);
        dto.setPaths(paths);
        dto.setLocations(locations);
        dto.setLayerGroups(toLayerGroupDTOs(layerGroups));
        dto.setLayers(toLayerDTOs(layers));
        dto.setBlocks(blocks);

        log.info("加载导航地图完成: {}, 版本: {}, 状态: {}, 点位: {}, 路径: {}, 位置: {}, Block: {}",
                navMapDTO.getName(), navMapDTO.getMapVersion(), navMapDTO.getStatus(),
                points.size(), paths.size(), locations.size(), blocks.size());

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(MapEditorSaveDTO saveDTO) {
        MapEditorMapInfoDTO info = saveDTO.getMapInfo();
        if (info == null || info.getMapId() == null || info.getMapId().isBlank()) {
            throw new IllegalArgumentException("mapInfo.mapId 不能为空");
        }
        String mapId = info.getMapId();
        validateMapId(mapId);
        log.info("保存地图模型: {}, 版本: {}", mapId, info.getMapVersion());

        // 获取导航地图
        NavigationMapDTO navMapDTO = mapSceneApi.getNavigationMapByMapId(mapId);
        if (navMapDTO == null) {
            throw new RuntimeException("地图不存在: " + mapId);
        }
        Long navMapId = navMapDTO.getId();

        // 计算新版本号
        String currentVersion = navMapDTO.getMapVersion();
        if (info.getMapVersion() != null && !info.getMapVersion().isBlank() && currentVersion != null
            && !currentVersion.equals(info.getMapVersion())) {
            throw new RuntimeException("保存冲突：地图版本已变化，请刷新后重试");
        }
        String newVersion = MapVersionUtil.getNextVersion(currentVersion);
        log.info("版本升级: {} -> {}", currentVersion, newVersion);

        // 0. 保存图层组/图层，并回填元素 layerId
        Map<String, Long> layerIdMapping = persistLayers(navMapId, saveDTO.getLayerGroups(), saveDTO.getLayers());
        remapElementLayerIds(saveDTO, layerIdMapping);
        validateElementLayouts(saveDTO);

        // 1. 更新语义表（point / path / location）
        // 先删除旧数据，再插入新数据
        if (saveDTO.getPoints() != null) {
            mapSceneApi.replacePointsByMap(navMapId, saveDTO.getPoints());
        }

        if (saveDTO.getPaths() != null) {
            mapSceneApi.replacePathsByMap(navMapId, saveDTO.getPaths());
        }

        if (saveDTO.getLocations() != null) {
            mapSceneApi.replaceLocationsByMap(navMapId, saveDTO.getLocations());
        }

        if (saveDTO.getBlocks() != null) {
            // 补填 navigationMapId 和 factoryModelId
            for (BlockDTO block : saveDTO.getBlocks()) {
                block.setNavigationMapId(navMapId);
                if (block.getFactoryModelId() == null) {
                    block.setFactoryModelId(navMapDTO.getFactoryModelId());
                }
            }
            mapSceneApi.replaceBlocksByMap(navMapId, saveDTO.getBlocks());
        }

        // 2. 生成并保存 JSON 快照（只保存 data 字段，不保存点路径位置）
        String snapshotPayload = buildSnapshotPayload(info.getData(), saveDTO);
        String snapshotUrl = saveJsonSnapshot(snapshotPayload, navMapDTO.getFactoryModelId(), mapId, newVersion);

        // 3. 记录历史版本
        mapSnapshotHistoryPort.recordSnapshot(navMapId, newVersion, snapshotUrl, info.getName());

        // 4. 更新 navigation_map 的版本号和状态
        NavigationMapDTO navMapUpdate = new NavigationMapDTO();
        navMapUpdate.setId(navMapId);
        navMapUpdate.setMapVersion(newVersion);
        navMapUpdate.setStatus(STATUS_DRAFT); // 保存后仍为草稿状态
        mapSceneApi.updateNavigationMap(navMapUpdate);

        log.info("保存地图完成: {} -> v{}", mapId, newVersion);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean publish(String mapId) {
        validateMapId(mapId);
        log.info("发布地图: {}", mapId);

        // 查询地图
        NavigationMapDTO navMap = mapSceneApi.getNavigationMapByMapId(mapId);
        if (navMap == null) {
            throw new RuntimeException("地图不存在: " + mapId);
        }

        // 已经是发布状态
        if (STATUS_PUBLISHED.equals(navMap.getStatus())) {
            log.info("地图已经是发布状态: {}", mapId);
            return true;
        }

        // 发布前硬校验（基础 5 条）
        Long navMapId = navMap.getId();
        var points = mapSceneApi.listPointsByMap(navMapId);
        var paths = mapSceneApi.listPathsByMap(navMapId);
        var locations = mapSceneApi.listLocationsByMap(navMapId);
        var layers = layerRepository.selectByNavigationMapId(navMapId);
        if (points.isEmpty()) {
            throw new RuntimeException("发布失败：地图缺少点位数据");
        }
        if (paths.isEmpty()) {
            throw new RuntimeException("发布失败：地图缺少路径数据");
        }
        if (locations.isEmpty()) {
            throw new RuntimeException("发布失败：地图缺少位置数据");
        }
        Set<String> pointIds = new HashSet<>();
        for (var point : points) {
            if (point.getPointId() != null && !point.getPointId().isBlank()) {
                pointIds.add(point.getPointId());
            }
        }
        for (var path : paths) {
            if (path.getSourcePointId() == null || !pointIds.contains(path.getSourcePointId())) {
                throw new RuntimeException("发布失败：存在路径起点不存在，pathId=" + path.getPathId());
            }
            if (path.getDestPointId() == null || !pointIds.contains(path.getDestPointId())) {
                throw new RuntimeException("发布失败：存在路径终点不存在，pathId=" + path.getPathId());
            }
        }
        Set<Long> layerIds = new HashSet<>();
        for (var layer : layers) {
            layerIds.add(layer.getId());
        }
        for (var point : points) {
            if (point.getLayerId() != null && !layerIds.contains(point.getLayerId())) {
                throw new RuntimeException("发布失败：点位存在无效 layerId，pointId=" + point.getPointId());
            }
        }
        for (var path : paths) {
            if (path.getLayerId() != null && !layerIds.contains(path.getLayerId())) {
                throw new RuntimeException("发布失败：路径存在无效 layerId，pathId=" + path.getPathId());
            }
        }
        for (var location : locations) {
            if (location.getLayerId() != null && !layerIds.contains(location.getLayerId())) {
                throw new RuntimeException("发布失败：位置存在无效 layerId，locationId=" + location.getLocationId());
            }
        }

        // 更新为发布状态
        navMap.setStatus(STATUS_PUBLISHED);
        mapSceneApi.updateNavigationMap(navMap);

        log.info("发布地图完成: {} -> v{}", navMap.getMapId(), navMap.getMapVersion());
        return true;
    }

    @Override
    public MapEditorDTO importMap(MultipartFile file) {
        // TODO: 实现地图导入逻辑
        log.info("导入地图: {}", file.getOriginalFilename());
        throw new UnsupportedOperationException("地图导入功能待实现");
    }

    @Override
    public void exportMap(Long id, HttpServletResponse response) {
        // TODO: 实现地图导出逻辑
        log.info("导出地图: {}", id);
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=map.json");
            response.getWriter().write("{}");
        } catch (IOException e) {
            throw new RuntimeException("导出地图失败", e);
        }
    }

    @Override
    public Boolean uploadEditorData(Long id, MultipartFile file) {
        // TODO: 实现编辑器数据上传
        log.info("上传编辑器数据: {}, file: {}", id, file.getOriginalFilename());
        FactoryModelDTO factoryModel = mapSceneApi.getFactoryModelById(id);
        if (factoryModel == null) {
            throw new RuntimeException("地图模型不存在");
        }
        // Store the file content or path as needed
        return true;
    }

    /**
     * 保存 JSON 快照文件
     */
    private String saveJsonSnapshot(String jsonData, Long factoryModelId, String mapId, String version) {
        try {
            // 创建目录
            Path dirPath = Paths.get(mapStoragePath, factoryModelId.toString(), mapId);
            Files.createDirectories(dirPath);

            // 生成文件名
            String fileName = "v" + version + ".json";
            Path filePath = dirPath.resolve(fileName);

            // 写入文件
            if (jsonData == null || jsonData.isBlank()) {
                throw new IllegalArgumentException("快照内容不能为空");
            }
            Files.writeString(filePath, jsonData);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("保存 JSON 快照失败", e);
        }
    }

    /**
     * 获取快照文件路径
     */
    private String getSnapshotFilePath(Long factoryModelId, String mapId, String version) {
        return Paths.get(mapStoragePath, factoryModelId.toString(), mapId, "v" + version + ".json").toString();
    }

    private String buildSnapshotPayload(String incomingData, MapEditorSaveDTO saveDTO) {
        if (incomingData != null && !incomingData.isBlank()) {
            return incomingData;
        }
        String layerGroupsCount = saveDTO.getLayerGroups() == null ? "0" : String.valueOf(saveDTO.getLayerGroups().size());
        String layersCount = saveDTO.getLayers() == null ? "0" : String.valueOf(saveDTO.getLayers().size());
        return "{\"visualLayout\":{\"layerGroupsCount\":" + layerGroupsCount + ",\"layersCount\":" + layersCount + "}}";
    }

    private List<MapEditorLayerGroupDTO> toLayerGroupDTOs(List<LayerGroupEntity> entities) {
        List<MapEditorLayerGroupDTO> list = new ArrayList<>();
        for (LayerGroupEntity entity : entities) {
            MapEditorLayerGroupDTO dto = new MapEditorLayerGroupDTO();
            dto.setId(String.valueOf(entity.getId()));
            dto.setName(entity.getName());
            dto.setVisible(entity.getVisible());
            dto.setOrdinal(entity.getOrdinal());
            dto.setProperties(entity.getProperties());
            list.add(dto);
        }
        return list;
    }

    private List<MapEditorLayerDTO> toLayerDTOs(List<LayerEntity> entities) {
        List<MapEditorLayerDTO> list = new ArrayList<>();
        for (LayerEntity entity : entities) {
            MapEditorLayerDTO dto = new MapEditorLayerDTO();
            dto.setId(String.valueOf(entity.getId()));
            dto.setLayerGroupId(entity.getLayerGroupId() == null ? null : String.valueOf(entity.getLayerGroupId()));
            dto.setName(entity.getName());
            dto.setVisible(entity.getVisible());
            dto.setOrdinal(entity.getOrdinal());
            dto.setProperties(entity.getProperties());
            list.add(dto);
        }
        return list;
    }

    private Map<String, Long> persistLayers(Long navMapId,
                                            List<MapEditorLayerGroupDTO> layerGroups,
                                            List<MapEditorLayerDTO> layers) {
        Map<String, Long> mapping = new HashMap<>();
        if (layerGroups == null && layers == null) {
            return mapping;
        }
        layerRepository.remove(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LayerEntity>()
            .eq(LayerEntity::getNavigationMapId, navMapId));
        layerGroupRepository.remove(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LayerGroupEntity>()
            .eq(LayerGroupEntity::getNavigationMapId, navMapId));

        if (layerGroups != null) {
            for (int i = 0; i < layerGroups.size(); i++) {
                MapEditorLayerGroupDTO dto = layerGroups.get(i);
                LayerGroupEntity entity = new LayerGroupEntity();
                entity.setNavigationMapId(navMapId);
                entity.setName(dto.getName());
                entity.setVisible(dto.getVisible() == null || dto.getVisible());
                entity.setOrdinal(dto.getOrdinal() != null ? dto.getOrdinal() : i + 1);
                entity.setProperties(dto.getProperties());
                layerGroupRepository.save(entity);
                if (dto.getId() != null) {
                    mapping.put(dto.getId(), entity.getId());
                }
                mapping.put(String.valueOf(entity.getId()), entity.getId());
            }
        }
        if (layers != null) {
            for (int i = 0; i < layers.size(); i++) {
                MapEditorLayerDTO dto = layers.get(i);
                LayerEntity entity = new LayerEntity();
                entity.setNavigationMapId(navMapId);
                entity.setName(dto.getName());
                entity.setVisible(dto.getVisible() == null || dto.getVisible());
                entity.setOrdinal(dto.getOrdinal() != null ? dto.getOrdinal() : i + 1);
                entity.setProperties(dto.getProperties());
                if (dto.getLayerGroupId() != null) {
                    entity.setLayerGroupId(mapping.get(dto.getLayerGroupId()));
                }
                layerRepository.save(entity);
                if (dto.getId() != null) {
                    mapping.put(dto.getId(), entity.getId());
                }
                mapping.put(String.valueOf(entity.getId()), entity.getId());
            }
        }
        return mapping;
    }

    private void remapElementLayerIds(MapEditorSaveDTO saveDTO, Map<String, Long> layerIdMapping) {
        if (layerIdMapping.isEmpty()) {
            return;
        }
        if (saveDTO.getPoints() != null) {
            saveDTO.getPoints().forEach(p -> p.setLayerId(remapLayerId(p.getLayerId(), layerIdMapping)));
        }
        if (saveDTO.getPaths() != null) {
            saveDTO.getPaths().forEach(p -> p.setLayerId(remapLayerId(p.getLayerId(), layerIdMapping)));
        }
        if (saveDTO.getLocations() != null) {
            saveDTO.getLocations().forEach(l -> l.setLayerId(remapLayerId(l.getLayerId(), layerIdMapping)));
        }
    }

    private Long remapLayerId(Long originalId, Map<String, Long> layerIdMapping) {
        if (originalId == null) {
            return null;
        }
        return layerIdMapping.getOrDefault(String.valueOf(originalId), originalId);
    }

    private void validateElementLayouts(MapEditorSaveDTO saveDTO) {
        if (saveDTO.getPoints() != null) {
            for (PointDTO point : saveDTO.getPoints()) {
                validateLayoutJson(point.getLayout(), "point", point.getPointId());
            }
        }
        if (saveDTO.getPaths() != null) {
            for (PathDTO path : saveDTO.getPaths()) {
                validatePathLayout(path.getLayout(), path.getPathId());
            }
        }
        if (saveDTO.getLocations() != null) {
            for (LocationDTO location : saveDTO.getLocations()) {
                validateLayoutJson(location.getLayout(), "location", location.getLocationId());
            }
        }
    }

    private void validateLayoutJson(String layout, String type, String businessId) {
        if (layout == null || layout.isBlank()) {
            return;
        }
        Map<String, Object> root;
        try {
            root = objectMapper.readValue(layout, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("保存失败：" + type + " layout 非法，id=" + businessId);
        }
        validateCoordinateIfPresent(root.get("x"), type, businessId, "x");
        validateCoordinateIfPresent(root.get("y"), type, businessId, "y");
        validateCoordinateIfPresent(root.get("z"), type, businessId, "z");
    }

    private void validatePathLayout(String layout, String pathId) {
        if (layout == null || layout.isBlank()) {
            return;
        }
        Map<String, Object> root;
        try {
            root = objectMapper.readValue(layout, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("保存失败：path layout 非法，pathId=" + pathId);
        }
        Object controlPoints = root.get("controlPoints");
        if (controlPoints instanceof List<?> points) {
            for (Object item : points) {
                if (!(item instanceof Map<?, ?> cp)) {
                    throw new IllegalArgumentException("保存失败：path layout.controlPoints 非法，pathId=" + pathId);
                }
                validateCoordinateIfPresent(cp.get("x"), "path", pathId, "controlPoint.x");
                validateCoordinateIfPresent(cp.get("y"), "path", pathId, "controlPoint.y");
            }
        }
    }

    private void validateCoordinateIfPresent(Object value, String type, String businessId, String field) {
        if (value == null) return;
        try {
            Double.parseDouble(String.valueOf(value));
        } catch (Exception e) {
            throw new IllegalArgumentException("保存失败：" + type + " layout." + field + " 非数字，id=" + businessId);
        }
    }

    private void validateMapId(String mapId) {
        if (mapId == null || mapId.isBlank()) {
            throw new IllegalArgumentException("mapId 不能为空");
        }
        if (!mapId.matches(MAP_ID_PATTERN)) {
            throw new IllegalArgumentException("mapId 格式非法，仅允许字母、数字、下划线、中划线，长度1-64");
        }
    }

}
