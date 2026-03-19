# 工厂模型架构实现行动计划

## 概述

本文档为工厂模型架构的具体实施行动计划，包括多楼层地图支持和栅格底图功能。

---

## 一、数据库准备

### 1.1 基础表结构

- **文件**: `script/mysql/opentcs_factory_model_ddl_v2.0.sql`
- **操作**: 在 MySQL 数据库中执行该脚本

### 1.2 增量迁移（新功能）

- **文件**: `script/mysql/opentcs_factory_model_ddl_v2.1_raster.sql`
- **操作**: 执行增量迁移，添加栅格底图字段
- **新增字段**:
  - `raster_url` - 栅格地图 OSS 存储路径
  - `raster_version` - 栅格地图版本号
  - `raster_width` - 栅格地图宽度
  - `raster_height` - 栅格地图高度
  - `raster_resolution` - 栅格地图分辨率

### 1.3 待规划（地图偏移和旋转）

- **文件**: 后续版本
- **新增字段**（规划中）:
  - `rotation` - 地图旋转角度（度）

---

## 二、坐标系统设计

### 2.1 设计原则

```
场景坐标 (Scene Coordinate) - 全局坐标系
    │
    └── 地图坐标 (Map Coordinate) - 局部坐标系
            │
            └── 画布坐标 (Canvas Coordinate) - 前端显示
```

### 2.2 坐标转换公式

```
点位场景坐标计算:

场景X = 地图.originX + (点.xPosition * cos(θ) - 点.yPosition * sin(θ))
场景Y = 地图.originY + (点.xPosition * sin(θ) + 点.yPosition * cos(θ))

其中 θ = 地图.rotation (弧度)
```

### 2.3 字段说明

| 字段 | 位置 | 单位 | 用途 |
|------|------|------|------|
| `FactoryModel.scale` | 工厂模型 | px/m | 比例尺 |
| `NavigationMap.originX/Y` | 导航地图 | mm | 地图原点相对于场景原点的偏移 |
| `NavigationMap.rotation` | 导航地图 | ° | 地图旋转角度（待实现）|
| `Point.x/y/z` | 点位 | mm | 相对于地图原点的坐标 |

---

## 三、代码开发任务清单

### 3.1 实体类开发 ✅ 已完成

| 文件 | 状态 |
|------|------|
| FactoryModelEntity (含 scale/coordinateSystem) | ✅ |
| NavigationMapEntity (含 originX/Y + 栅格底图) | ✅ |
| PointEntity (统一坐标) | ✅ |
| CrossLayerConnectionEntity | ✅ |
| ElevatorScheduleEntity | ✅ |

### 3.2 栅格底图功能 ✅ 已完成

| 任务 | 状态 | 说明 |
|------|------|------|
| 后端 Entity 扩展 | ✅ | 新增 rasterUrl/Version/Width/Height/Resolution |
| 后端 DTO 扩展 | ✅ | 新增相同字段 |
| 前端类型扩展 | ✅ | NavigationMapVO/Form |
| 新增地图对话框 | ✅ | 支持上传 map.yaml + map.pgm |
| 地图列表显示 | ✅ | 显示底图版本号 |
| 编辑器导入功能 | ✅ | 更新底图时版本号+1 |

### 3.3 地图偏移和旋转 ⚙️ 待规划

| 任务 | 状态 | 说明 |
|------|------|------|
| 后端 Entity 扩展 | ⏳ | 新增 rotation 字段 |
| 前端类型扩展 | ⏳ | 支持设置偏移和旋转 |
| 地图属性编辑 | ⏳ | 对话框中支持编辑 |
| 坐标转换工具 | ⏳ | 局部坐标转全局坐标 |

### 3.4 Mapper 开发 ✅ 已完成

### 3.5 Service 开发 ✅ 已完成

### 3.6 Controller 开发 ✅ 已完成

### 3.7 路由算法开发 ✅ 已完成

| 文件 | 说明 |
|------|------|
| GlobalRoutingGraph | 全局路由图 |
| LocalRoutingGraph | 局部路由图 |
| CrossFloorRouter | 跨楼层路径规划 |

---

## 四、构建验证

```bash
mvn clean compile -DskipTests
```

---

## 五、前置依赖

1. **数据库**: MySQL 8.0+
2. **对象存储**: OSS 配置（已配置）
3. **开发环境**: JDK 21, Maven 3.8+

---

## 六、实施优先级

### Phase 1: 栅格底图功能 ✅ 已完成

- [x] 地图创建时支持导入 PGM 底图
- [x] 底图上传到 OSS
- [x] 版本号管理
- [x] 编辑器中显示底图

### Phase 2: 地图偏移和旋转 ⏳ 待规划

- [ ] 数据库添加 rotation 字段
- [ ] 前端支持设置地图偏移和旋转
- [ ] 多地图统一显示支持
- [ ] 调度计算坐标转换

---

## 七、相关文档

- [概念说明](./MapEditorConcept.md) - 核心概念和参数体系
- [技术方案](./FactoryModelDesign.md) - 详细技术设计
