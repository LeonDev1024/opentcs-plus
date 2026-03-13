package org.opentcs.map.service.impl;

import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.opentcs.common.mybatis.core.page.PageQuery;
import org.opentcs.common.mybatis.core.page.TableDataInfo;
import org.opentcs.map.domain.bo.PlantModelBO;
import org.opentcs.map.domain.entity.Block;
import org.opentcs.map.domain.entity.Path;
import org.opentcs.map.domain.entity.PlantModel;
import org.opentcs.map.domain.entity.PlantModelHistory;
import org.opentcs.map.domain.entity.Point;
import org.opentcs.map.domain.entity.Location;
import org.opentcs.map.domain.entity.LocationType;
import org.opentcs.map.domain.entity.Block;
import org.opentcs.map.domain.entity.VisualLayout;
import org.opentcs.map.domain.entity.Layer;
import org.opentcs.map.domain.entity.LayerGroup;
import org.opentcs.map.mapper.PlantModelMapper;
import org.opentcs.map.importer.OpenTcsXmlImporter;
import org.opentcs.map.importer.OpenTcsXmlImporter.OpenTcsImportResult;
import org.opentcs.map.importer.OpenTcsXmlImporter.OpenTcsPath;
import org.opentcs.map.importer.OpenTcsXmlImporter.OpenTcsPoint;
import org.opentcs.map.importer.OpenTcsXmlImporter.OpenTcsLocation;
import org.opentcs.map.importer.OpenTcsXmlImporter.OpenTcsLocationType;
import org.opentcs.map.importer.OpenTcsXmlImporter.OpenTcsBlock;
import org.opentcs.map.service.BlockService;
import org.opentcs.map.service.LayerGroupService;
import org.opentcs.map.service.LayerService;
import org.opentcs.map.service.LocationService;
import org.opentcs.map.service.PathService;
import org.opentcs.map.service.PlantModelHistoryService;
import org.opentcs.map.service.PlantModelService;
import org.opentcs.map.service.PointService;
import org.opentcs.map.service.VisualLayoutService;
import org.opentcs.map.utils.ModelVersionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final PlantModelHistoryService plantModelHistoryService;

    /**
     * 地图编辑器文件存储根目录（相对当前工作目录）
     */
    @Value("${opentcs.map.storage-root:maps}")
    private String mapStorageRoot;

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
    public PlantModelBO importMap(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // 读取完整文件内容，基于首字符判断 XML / JSON，避免仅依赖文件名导致误判
            String content = IoUtil.read(inputStream, "UTF-8");
            String trimmed = content != null ? content.trim() : "";
            boolean isXml = !trimmed.isEmpty() && trimmed.charAt(0) == '<';

            if (isXml) {
                // 解析 openTCS XML 模型，仅构造内存模型，不直接落库
                OpenTcsXmlImporter importer = new OpenTcsXmlImporter();
                InputStream xmlStream = new java.io.ByteArrayInputStream(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                OpenTcsImportResult result = importer.parse(xmlStream);

                PlantModelBO bo = new PlantModelBO();
                bo.setPlantModelId(null);
                bo.setName(result.getModelName() != null ? result.getModelName() : "Imported-Model");
                bo.setModelVersion("1.0");

                // 点
                List<Point> pointList = new ArrayList<>();
                Map<String, OpenTcsPoint> pointByName = new HashMap<>();
                for (OpenTcsPoint op : result.getPoints()) {
                    Point p = new Point();
                    p.setName(op.name);
                    p.setXPosition(op.x);
                    p.setYPosition(op.y);
                    p.setZPosition(op.z);
                    p.setType(op.type);
                    pointList.add(p);
                    if (op.name != null) {
                        pointByName.put(op.name, op);
                    }
                }
                bo.setPoints(new java.util.HashSet<>(pointList));

                // 路径：如果 XML 中 length 为空或为 0，则根据两端点坐标计算几何长度（单位：mm），
                // 以保证与 openTCS 中路径长度显示一致。
                List<Path> pathList = new ArrayList<>();
                for (OpenTcsPath op : result.getPaths()) {
                    Path path = new Path();
                    path.setName(op.name);

                    java.math.BigDecimal effectiveLength = op.length;
                    if (effectiveLength == null || effectiveLength.compareTo(java.math.BigDecimal.ZERO) == 0) {
                        OpenTcsPoint src = pointByName.get(op.sourcePointName);
                        OpenTcsPoint dst = pointByName.get(op.destPointName);
                        if (src != null && dst != null
                            && src.x != null && src.y != null
                            && dst.x != null && dst.y != null) {
                            double dx = dst.x.subtract(src.x).doubleValue();
                            double dy = dst.y.subtract(src.y).doubleValue();
                            double len = Math.hypot(dx, dy);
                            // openTCS 使用 mm 作为长度单位，这里取整数 mm
                            effectiveLength = new java.math.BigDecimal(len).setScale(0, java.math.RoundingMode.HALF_UP);
                        }
                    }

                    path.setLength(effectiveLength);
                    path.setMaxVelocity(op.maxVelocity);
                    path.setMaxReverseVelocity(op.maxReverseVelocity);
                    // routingType 对应 openTCS 中 Path 的 RoutingType（BIDIRECTIONAL/FORWARD/BACKWARD）
                    path.setRoutingType(op.routingType);
                    // connectionType / 控制点 仅在内存中使用，用于前端重建几何形状
                    path.setConnectionType(op.connectionType);
                    if (op.controlPoints != null && !op.controlPoints.isEmpty()) {
                        List<org.opentcs.map.domain.entity.PathLayoutControlPoint> cps = new ArrayList<>();
                        for (org.opentcs.map.importer.OpenTcsXmlImporter.ControlPoint cp : op.controlPoints) {
                            org.opentcs.map.domain.entity.PathLayoutControlPoint dto = new org.opentcs.map.domain.entity.PathLayoutControlPoint();
                            dto.setX(cp.x);
                            dto.setY(cp.y);
                            cps.add(dto);
                        }
                        path.setLayoutControlPoints(cps);
                    }
                    pathList.add(path);
                }
                bo.setPaths(new java.util.HashSet<>(pathList));

                // 位置类型
                List<LocationType> locationTypeList = new ArrayList<>();
                for (OpenTcsLocationType ot : result.getLocationTypes()) {
                    LocationType lt = new LocationType();
                    lt.setName(ot.name);
                    locationTypeList.add(lt);
                }
                bo.setLocationTypes(new java.util.HashSet<>(locationTypeList));

                // 位置（业务点位）
                List<Location> locationList = new ArrayList<>();
                for (OpenTcsLocation ol : result.getLocations()) {
                    Location loc = new Location();
                    loc.setName(ol.name);
                    loc.setXPosition(ol.x);
                    loc.setYPosition(ol.y);
                    loc.setZPosition(ol.z);
                    // 暂不绑定具体 LocationType，保留 typeName 在扩展属性中
                    loc.setProperties(ol.typeName);
                    locationList.add(loc);
                }
                bo.setLocations(new java.util.HashSet<>(locationList));

                // 区域规则 Block（先仅保存名称、类型和成员名称，以便前端或后续保存时使用）
                List<Block> blockList = new ArrayList<>();
                for (OpenTcsBlock ob : result.getBlocks()) {
                    Block b = new Block();
                    b.setName(ob.name);
                    b.setType(ob.type);
                    // 成员路径和点名称用逗号分隔后放入 members 字段，供前端或持久化逻辑解析
                    java.util.List<String> members = new java.util.ArrayList<>();
                    members.addAll(ob.memberPathNames);
                    members.addAll(ob.memberPointNames);
                    b.setMembers(String.join(",", members));
                    blockList.add(b);
                }
                bo.setBlocks(new java.util.HashSet<>(blockList));

                bo.setVisualLayout(null);

                return bo;
            } else {
                // JSON 情况：反序列化为 PlantModel 再转换为 BO 返回
                PlantModel plantModel = objectMapper.readValue(content, PlantModel.class);
                PlantModelBO bo = new PlantModelBO();
                bo.setPlantModelId(plantModel.getId());
                bo.setName(plantModel.getName());
                bo.setModelVersion(plantModel.getModelVersion());
                bo.setPoints(new java.util.HashSet<>());
                bo.setPaths(new java.util.HashSet<>());
                bo.setLocations(new java.util.HashSet<>());
                bo.setBlocks(new java.util.HashSet<>());
                bo.setLocationTypes(new java.util.HashSet<>());
                bo.setVisualLayout(null);
                return bo;
            }
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

        // 创建新版本（按主键维度管理版本，不再使用 mapId）
        PlantModel newModel = new PlantModel();
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

        // 查询同一地图名称的所有版本（也可以改成只按同一主键 id 管理）
        IPage<PlantModel> page = this.getBaseMapper().selectPage(
                pageQuery.build(),
                new LambdaQueryWrapper<>(PlantModel.class)
                        .eq(PlantModel::getName, originalModel.getName())
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

        // 创建新地图（不再生成 mapId，仅按主键 id 管理）
        PlantModel newModel = new PlantModel();
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

    @Override
    @CacheEvict(allEntries = true)
    public boolean uploadEditorData(Long modelId, MultipartFile file) {
        PlantModel plantModel = this.getById(modelId);
        if (plantModel == null) {
            throw new RuntimeException("地图模型不存在");
        }

        // 计算新的业务版本号
        String currentVersion = plantModel.getModelVersion();
        String nextVersion = ModelVersionUtil.getNextModelVersion(currentVersion);
        plantModel.setModelVersion(nextVersion);
        this.updateById(plantModel);

        // 将上传文件读入内存，既用于存盘也用于解析 JSON
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("读取地图编辑器上传数据失败", e);
        }

        // 计算存储路径：maps/{id}/versions/map_v{version}.json
        String idPath = String.valueOf(plantModel.getId());
        String fileName = String.format("map_v%s.json", nextVersion);
        java.nio.file.Path baseDir = Paths.get(mapStorageRoot, idPath, "versions");
        java.nio.file.Path targetFile = baseDir.resolve(fileName);
        try {
            Files.createDirectories(baseDir);
            Files.write(targetFile, bytes);
        } catch (IOException e) {
            throw new RuntimeException("保存地图编辑器文件失败", e);
        }

        // 解析 JSON，按名称回写 point 坐标到数据库
        try {
            InputStream jsonIn = new java.io.ByteArrayInputStream(bytes);
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(jsonIn);
            com.fasterxml.jackson.databind.JsonNode elements = root.path("elements");
            com.fasterxml.jackson.databind.JsonNode pointsNode = elements.path("points");
            if (pointsNode.isArray()) {
                List<Point> pointsToUpdate = new ArrayList<>();
                for (com.fasterxml.jackson.databind.JsonNode pNode : pointsNode) {
                    String name = pNode.path("name").asText(null);
                    if (name == null || name.isEmpty()) {
                        continue;
                    }
                    java.math.BigDecimal x = pNode.hasNonNull("x")
                        ? new java.math.BigDecimal(pNode.get("x").asText())
                        : null;
                    java.math.BigDecimal y = pNode.hasNonNull("y")
                        ? new java.math.BigDecimal(pNode.get("y").asText())
                        : null;
                    if (x == null && y == null) {
                        continue;
                    }
                    Point existing = pointService.getOne(
                        new LambdaQueryWrapper<>(Point.class)
                            .eq(Point::getPlantModelId, modelId)
                            .eq(Point::getName, name),
                        false
                    );
                    if (existing != null) {
                        if (x != null) {
                            existing.setXPosition(x);
                        }
                        if (y != null) {
                            existing.setYPosition(y);
                        }
                        pointsToUpdate.add(existing);
                    }
                }
                if (!pointsToUpdate.isEmpty()) {
                    pointService.updateBatchById(pointsToUpdate);
                }
            }
        } catch (IOException e) {
            // 解析失败不影响快照保存和版本更新，只记录日志
            System.err.println("解析地图编辑器 JSON 失败，未回写坐标: " + e.getMessage());
        }

        // 记录历史快照
        PlantModelHistory history = new PlantModelHistory();
        history.setPlantModelId(plantModel.getId());
        history.setModelVersion(nextVersion);
        // 使用相对路径，前端如需下载可再组合为完整URL
        history.setFileUrl(Paths.get(mapStorageRoot, idPath, "versions", fileName).toString());
        history.setSnapshotType("EDITOR_JSON");
        plantModelHistoryService.recordHistory(history);

        return true;
    }
}