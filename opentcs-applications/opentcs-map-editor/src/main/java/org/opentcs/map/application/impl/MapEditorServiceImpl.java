package org.opentcs.map.application.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.persistence.entity.*;
import org.opentcs.kernel.persistence.mapper.NavigationMapHistoryMapper;
import org.opentcs.kernel.persistence.service.*;
import org.opentcs.map.application.IMapEditorService;
import org.opentcs.map.domain.converter.MapElementConverter;
import org.opentcs.map.domain.dto.*;
import org.opentcs.map.domain.vo.LoadModelVO;
import org.opentcs.map.utils.MapVersionUtil;
import org.opentcs.common.core.dto.PathLayoutControlPointTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    private final FactoryModelDomainService factoryModelDomainService;
    private final NavigationMapDomainService navigationMapDomainService;
    private final NavigationMapHistoryMapper navigationMapHistoryMapper;
    private final PointDomainService pointDomainService;
    private final PathDomainService pathDomainService;
    private final LocationDomainService locationDomainService;
    private final ObjectMapper objectMapper;

    /**
     * 地图状态：草稿
     */
    private static final String STATUS_DRAFT = "0";
    /**
     * 地图状态：已发布
     */
    private static final String STATUS_PUBLISHED = "1";

    @Override
    public MapEditorDTO load(LoadModelVO loadModelVO) {
        String mapId = loadModelVO.getMapId();

        // 获取导航地图基本信息（根据地图编号查询）
        NavigationMapDTO navMapDTO = navigationMapDomainService.selectByMapId(mapId);
        if (navMapDTO == null) {
            return null;
        }
        Long navMapId = navMapDTO.getId();

        // 获取工厂模型信息
        FactoryModelEntity factoryModel = factoryModelDomainService.selectById(navMapDTO.getFactoryModelId());
        if (factoryModel == null) {
            return null;
        }

        // 查询地图元素
        List<PointEntity> points = pointDomainService.listByMap(navMapId);
        List<PathEntity> paths = pathDomainService.listByMap(navMapId);
        hydratePathLayout(paths);
        List<LocationEntity> locations = locationDomainService.selectByNavigationMapId(navMapId);

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
        MapEditorDTO dto = new MapEditorDTO();
        dto.setName(navMapDTO.getName());
        dto.setMapId(navMapDTO.getMapId());
        dto.setFactoryModelId(navMapDTO.getFactoryModelId());
        dto.setFactoryName(factoryModel.getName());
        dto.setOriginX(navMapDTO.getOriginX());
        dto.setOriginY(navMapDTO.getOriginY());
        dto.setRotation(navMapDTO.getRotation());
        dto.setMapVersion(navMapDTO.getMapVersion());
        dto.setStatus(navMapDTO.getStatus());
        dto.setPoints(MapElementConverter.toPointDTOList(points));
        dto.setPaths(MapElementConverter.toPathDTOList(paths));
        dto.setLocations(MapElementConverter.toLocationDTOList(locations));
        dto.setData(jsonData);

        log.info("加载导航地图完成: {}, 版本: {}, 状态: {}, 点位: {}, 路径: {}, 位置: {}",
                navMapDTO.getName(), navMapDTO.getMapVersion(), navMapDTO.getStatus(),
                points.size(), paths.size(), locations.size());

        return dto;
    }

    private void hydratePathLayout(List<PathEntity> paths) {
        if (paths == null || paths.isEmpty()) {
            return;
        }
        for (PathEntity path : paths) {
            if (path.getLayoutControlPoints() != null && !path.getLayoutControlPoints().isEmpty()) {
                continue;
            }
            if (path.getLayout() == null || path.getLayout().isBlank()) {
                continue;
            }
            try {
                String layoutJson = path.getLayout().trim();
                // 兼容历史数据：layout 可能只是一个 controlPoints 数组
                if (layoutJson.startsWith("[")) {
                    List<PathLayoutControlPointTO> controlPoints = objectMapper.readValue(
                        layoutJson,
                        new TypeReference<List<PathLayoutControlPointTO>>() {
                        }
                    );
                    path.setLayoutControlPoints(controlPoints);
                }
                else {
                    PathLayoutPersist persist = objectMapper.readValue(layoutJson, PathLayoutPersist.class);
                    path.setConnectionType(persist.connectionType);
                    path.setLayoutControlPoints(persist.controlPoints);
                }
            } catch (Exception e) {
                // layout 只用于前端重建几何形状：尽量不影响其它模型加载
                log.warn("解析 path.layout 失败, pathId={}, layout={}", path.getPathId(), path.getLayout(), e);
            }
        }
    }

    /**
     * path.layout JSON 持久化结构
     *
     * <p>与 openTCS 的 {@code PathCreationTO.Layout} 对齐：connectionType + controlPoints。
     * 当前系统中 layerId 暂未落库，因此这里先不持久化 layerId。</p>
     */
    private static class PathLayoutPersist {
        public String connectionType;
        public List<PathLayoutControlPointTO> controlPoints;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(MapEditorSaveDTO saveDTO) {
        String mapId = saveDTO.getMapId();
        log.info("保存地图模型: {}, 版本: {}", mapId, saveDTO.getMapVersion());

        // 获取导航地图
        NavigationMapDTO navMapDTO = navigationMapDomainService.selectByMapId(mapId);
        if (navMapDTO == null) {
            throw new RuntimeException("地图不存在: " + mapId);
        }
        Long navMapId = navMapDTO.getId();

        // 计算新版本号
        String currentVersion = navMapDTO.getMapVersion();
        String newVersion = MapVersionUtil.getNextVersion(currentVersion);
        log.info("版本升级: {} -> {}", currentVersion, newVersion);

        // 1. 更新语义表（point / path / location）
        // 先删除旧数据，再插入新数据
        if (saveDTO.getPoints() != null) {
            pointDomainService.removeByMap(navMapId);
            for (PointDTO pointDTO : saveDTO.getPoints()) {
                PointEntity point = MapElementConverter.toPointEntity(pointDTO);
                point.setId(null); // 新增
                point.setNavigationMapId(navMapId);
                pointDomainService.save(point);
            }
        }

        if (saveDTO.getPaths() != null) {
            pathDomainService.removeByMap(navMapId);
            for (PathDTO pathDTO : saveDTO.getPaths()) {
                PathEntity path = MapElementConverter.toPathEntity(pathDTO);
                path.setId(null);
                path.setNavigationMapId(navMapId);
                // Persist path layout for subsequent load.
                if (path.getLayoutControlPoints() != null) {
                    try {
                        PathLayoutPersist persist = new PathLayoutPersist();
                        persist.connectionType = path.getConnectionType() != null ? path.getConnectionType() : "DIRECT";
                        persist.controlPoints = path.getLayoutControlPoints();
                        path.setLayout(objectMapper.writeValueAsString(persist));
                    } catch (Exception e) {
                        throw new RuntimeException("序列化 path.layout 失败, pathId=" + path.getPathId(), e);
                    }
                }
                pathDomainService.save(path);
            }
        }

        if (saveDTO.getLocations() != null) {
            locationDomainService.removeByMap(navMapId);
            for (LocationDTO locationDTO : saveDTO.getLocations()) {
                LocationEntity location = MapElementConverter.toLocationEntity(locationDTO);
                location.setId(null);
                location.setNavigationMapId(navMapId);
                locationDomainService.save(location);
            }
        }

        // 2. 生成并保存 JSON 快照（只保存 data 字段，不保存点路径位置）
        String snapshotUrl = saveJsonSnapshot(saveDTO.getData(), navMapDTO.getFactoryModelId(), mapId, newVersion);

        // 3. 记录历史版本
        NavigationMapHistoryEntity history = new NavigationMapHistoryEntity();
        history.setNavigationMapId(navMapId);
        history.setMapVersion(newVersion);
        history.setSnapshotUrl(snapshotUrl);
        history.setChangeSummary(saveDTO.getName());
        navigationMapHistoryMapper.insert(history);

        // 4. 更新 navigation_map 的版本号和状态
        NavigationMapEntity navMapEntity = new NavigationMapEntity();
        navMapEntity.setId(navMapId);
        navMapEntity.setMapVersion(newVersion);
        navMapEntity.setStatus(STATUS_DRAFT); // 保存后仍为草稿状态
        navigationMapDomainService.updateById(navMapEntity);

        log.info("保存地图完成: {} -> v{}", mapId, newVersion);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean publish(Long mapId) {
        log.info("发布地图: {}", mapId);

        // 查询地图
        NavigationMapEntity navMap = navigationMapDomainService.getById(mapId);
        if (navMap == null) {
            throw new RuntimeException("地图不存在: " + mapId);
        }

        // 已经是发布状态
        if (STATUS_PUBLISHED.equals(navMap.getStatus())) {
            log.info("地图已经是发布状态: {}", mapId);
            return true;
        }

        // 更新为发布状态
        navMap.setStatus(STATUS_PUBLISHED);
        navigationMapDomainService.updateById(navMap);

        log.info("发布地图完成: {} -> v{}", mapId, navMap.getMapVersion());
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
        FactoryModelEntity entity = factoryModelDomainService.selectById(id);
        if (entity == null) {
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
            if (jsonData == null || jsonData.isEmpty()) {
                jsonData = "{}";
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
}
