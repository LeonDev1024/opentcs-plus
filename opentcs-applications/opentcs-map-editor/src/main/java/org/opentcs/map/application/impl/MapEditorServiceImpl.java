package org.opentcs.map.application.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opentcs.kernel.api.dto.NavigationMapDTO;
import org.opentcs.kernel.persistence.entity.*;
import org.opentcs.kernel.persistence.service.*;
import org.opentcs.map.application.IMapEditorService;
import org.opentcs.map.domain.bo.MapEditorBO;
import org.opentcs.map.domain.vo.LoadModelVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 地图编辑器应用服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MapEditorServiceImpl implements IMapEditorService {

    private final FactoryModelDomainService factoryModelDomainService;
    private final NavigationMapDomainService navigationMapDomainService;
    private final PointDomainService pointDomainService;
    private final PathDomainService pathDomainService;
    private final LocationDomainService locationDomainService;
    private final LocationTypeDomainService locationTypeDomainService;
    private final BlockDomainService blockDomainService;
    private final LayerGroupDomainService layerGroupDomainService;
    private final LayerDomainService layerDomainService;

    @Override
    public MapEditorBO load(LoadModelVO loadModelVO) {
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
        List<LocationEntity> locations = locationDomainService.selectByNavigationMapId(navMapId);
        List<LayerGroupEntity> layerGroups = layerGroupDomainService.selectByNavigationMapId(navMapId);
        List<LayerEntity> layers = layerDomainService.selectByNavigationMapId(navMapId);

        MapEditorBO bo = new MapEditorBO();
        bo.setId(navMapDTO.getId());
        bo.setName(navMapDTO.getName());
        bo.setMapId(navMapDTO.getMapId());
        bo.setFactoryModelId(navMapDTO.getFactoryModelId());
        bo.setFactoryName(factoryModel.getName());
        bo.setPoints(points);
        bo.setPaths(paths);
        bo.setLocations(locations);
        bo.setLayerGroups(layerGroups);
        bo.setLayers(layers);

        log.info("加载导航地图完成: {}, 点位: {}, 路径: {}, 位置: {}",
                navMapDTO.getName(), points.size(), paths.size(), locations.size());

        return bo;
    }

    @Override
    public Boolean save(MapEditorBO mapEditorBO) {
        // TODO: 实现保存逻辑
        log.info("保存地图模型: {}", mapEditorBO.getName());
        return true;
    }

    @Override
    public MapEditorBO importMap(MultipartFile file) {
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
}
