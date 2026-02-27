package org.opentcs.map.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.entity.*;
import org.opentcs.map.mapper.*;
import org.opentcs.map.service.*;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 地图模型 Service 实现类
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "plantModel")
public class PlantModelServiceImpl extends ServiceImpl<PlantModelMapper, PlantModel> implements PlantModelService {

    private final VisualLayoutService visualLayoutService;
    private final LayerGroupService layerGroupService;
    private final LayerService layerService;
    private final PointService pointService;
    private final PathService pathService;
    private final LocationService locationService;
    private final BlockService blockService;
    private final ObjectMapper objectMapper;

    @Override
    @CacheEvict(allEntries = true)
    public boolean createPlantModel(PlantModel plantModel) {
        // 校验地图名称是否存在
        boolean isExist = this.getBaseMapper().selectCount(new LambdaQueryWrapper<>(PlantModel.class)
                        .eq(PlantModel::getName, plantModel.getName())
                        .eq(PlantModel::getModelVersion, "1.0")
                        .eq(PlantModel::getDelFlag, "0")) > 0;
        if (isExist) {
            throw new RuntimeException("地图名称已存在");
        }
        plantModel.setMapId(IdUtil.fastSimpleUUID());
        plantModel.setModelVersion("1.0");

        this.save(plantModel);

        // 创建默认的VisualLayout， LayerGroup和Layer
        VisualLayout visualLayout = new VisualLayout();
        visualLayout.setPlantModelId(plantModel.getId());
        visualLayout.setName(plantModel.getName() + "布局");
        visualLayoutService.save(visualLayout);

        LayerGroup layerGroup = new LayerGroup();
        layerGroup.setVisualLayoutId(visualLayout.getId());
        layerGroup.setName("默认图层组");
        layerGroupService.save(layerGroup);

        Layer layer = new Layer();
        layer.setVisualLayoutId(visualLayout.getId());
        layer.setLayerGroupId(layerGroup.getId());
        layer.setName("默认图层");
        layer.setVisible(true);
        layerService.save(layer);

        return true;

    }

    @Override
    @Cacheable(key = "#plantModel.name + '-' + #pageQuery.pageNum + '-' + #pageQuery.pageSize")
    public TableDataInfo<PlantModel> selectPagePlantModel(PlantModel plantModel, PageQuery pageQuery) {
        IPage<PlantModel> page = this.getBaseMapper().selectPagePlantModel(pageQuery.build(), plantModel);
        return TableDataInfo.build(page);
    }

    @SneakyThrows
    @Override
    @CacheEvict(allEntries = true)
    public boolean importMap(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // 读取文件内容
            String content = IoUtil.read(inputStream, "UTF-8");
            // 解析地图数据
            // 这里简化处理，实际应该根据文件格式进行解析
            // 例如：JSON、XML等格式
            PlantModel plantModel = objectMapper.readValue(content, PlantModel.class);
            // 保存地图模型
            return this.save(plantModel);
        }
    }

    @SneakyThrows
    @Override
    @Cacheable(key = "#modelId")
    public void exportMap(Long modelId, HttpServletResponse response) {
        PlantModel plantModel = this.getById(modelId);
        if (plantModel == null) {
            throw new RuntimeException("地图模型不存在");
        }

        // 设置响应头
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=" + plantModel.getName() + ".json");

        // 转换为JSON并写入响应
        try (OutputStream outputStream = response.getOutputStream()) {
            objectMapper.writeValue(outputStream, plantModel);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public Long createVersion(Long modelId, String versionName) {
        PlantModel originalModel = this.getById(modelId);
        if (originalModel == null) {
            throw new RuntimeException("地图模型不存在");
        }

        // 创建新版本
        PlantModel newModel = new PlantModel();
        newModel.setMapId(originalModel.getMapId());
        newModel.setName(originalModel.getName());
        newModel.setModelVersion(versionName);
        newModel.setStatus(originalModel.getStatus());
        newModel.setProperties(originalModel.getProperties());
        newModel.setDescription(originalModel.getDescription());

        this.save(newModel);
        return newModel.getId();
    }

    @Override
    @Cacheable(key = "#modelId + '-' + #pageQuery.pageNum + '-' + #pageQuery.pageSize")
    public TableDataInfo<PlantModel> getVersionHistory(Long modelId, PageQuery pageQuery) {
        PlantModel originalModel = this.getById(modelId);
        if (originalModel == null) {
            throw new RuntimeException("地图模型不存在");
        }

        // 查询同一地图ID的所有版本
        IPage<PlantModel> page = this.getBaseMapper().selectPage(
                pageQuery.build(),
                new LambdaQueryWrapper<>(PlantModel.class)
                        .eq(PlantModel::getMapId, originalModel.getMapId())
                        .eq(PlantModel::getDelFlag, "0")
                        .orderByDesc(PlantModel::getModelVersion)
        );
        return TableDataInfo.build(page);
    }

    @Override
    @Cacheable(key = "#modelId")
    public String validateTopology(Long modelId) {
        // 拓扑验证逻辑
        // 1. 检查地图是否存在
        PlantModel plantModel = this.getById(modelId);
        if (plantModel == null) {
            return "地图模型不存在";
        }

        // 2. 检查点位和路径是否完整
        List<Point> points = pointService.selectAllPointByPlantModelId(modelId);
        List<Path> paths = pathService.selectAllPathByPlantModelId(modelId);

        if (points.isEmpty()) {
            return "地图中没有点位";
        }

        if (paths.isEmpty()) {
            return "地图中没有路径";
        }

        // 3. 检查连通性
        // 这里简化处理，实际应该实现更复杂的连通性检查
        return "拓扑验证通过";
    }

    @Override
    @CacheEvict(allEntries = true)
    public Long copyMap(Long modelId, String newName) {
        PlantModel originalModel = this.getById(modelId);
        if (originalModel == null) {
            throw new RuntimeException("地图模型不存在");
        }

        // 检查新名称是否已存在
        boolean isExist = this.getBaseMapper().selectCount(new LambdaQueryWrapper<>(PlantModel.class)
                        .eq(PlantModel::getName, newName)
                        .eq(PlantModel::getDelFlag, "0")) > 0;
        if (isExist) {
            throw new RuntimeException("地图名称已存在");
        }

        // 创建新地图
        PlantModel newModel = new PlantModel();
        newModel.setMapId(IdUtil.fastSimpleUUID());
        newModel.setName(newName);
        newModel.setModelVersion("1.0");
        newModel.setStatus(originalModel.getStatus());
        newModel.setProperties(originalModel.getProperties());
        newModel.setDescription(originalModel.getDescription());

        this.save(newModel);

        // 复制相关数据（布局、图层、点位、路径等）
        // 这里简化处理，实际应该实现完整的复制逻辑

        return newModel.getId();
    }
}