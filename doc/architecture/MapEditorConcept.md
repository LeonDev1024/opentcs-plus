# OpenTCS Plus 地图编辑器核心概念说明

## 1. 概述

本文档定义 OpenTCS Plus 地图编辑器的核心概念和参数体系，实现多地图统一坐标系管理。

---

## 2. 核心概念

### 2.1 层级结构

```
┌─────────────────────────────────────────────────────────────┐
│                    FactoryModel (工厂模型)                    │
│                    场景 / 工厂                                │
├─────────────────────────────────────────────────────────────┤
│  属性: name, scale, coordinateSystem                        │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │          NavigationMap (导航地图)                      │   │
│  │          地图 / 楼层区域                                │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │  属性: name, floorNumber, originX/Y, rotation       │   │
│  │        rasterUrl, rasterVersion                      │   │
│  │                                                     │   │
│  │  ┌───────────┐  ┌───────────┐  ┌───────────┐     │   │
│  │  │   Point   │  │   Path    │  │  Location │     │   │
│  │  │   点位    │  │   路径    │  │   位置    │     │   │
│  │  └───────────┘  └───────────┘  └───────────┘     │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 概念对照表

| 概念 | 英文 | 说明 |
|------|------|------|
| 工厂模型 | FactoryModel | 整个调度系统的物理环境，包含多个导航地图 |
| 导航地图 | NavigationMap | 一个独立的地图区域，可以是楼层、车间、室外道路等 |
| 场景坐标 | Scene Coordinate | 全局坐标系，相对于工厂原点的绝对坐标 |
| 地图坐标 | Map Coordinate | 相对于地图原点的局部坐标 |

---

## 3. 坐标系统

### 3.1 坐标层级

```
场景坐标 (Scene Coordinate) - 全局坐标系
    │
    └── 地图坐标 (Map Coordinate) - 局部坐标系
            │
            └── 画布坐标 (Canvas Coordinate) - 前端显示
```

### 3.2 地图定位参数

每个导航地图包含以下定位参数：

| 参数 | 字段 | 单位 | 说明 |
|------|------|------|------|
| 地图原点X | originX | mm | AMR地图原点相对于场景原点的X偏移 |
| 地图原点Y | originY | mm | AMR地图原点相对于场景原点的Y偏移 |
| 地图旋转角度 | rotation | ° | AMR地图相对于场景方向的旋转角度 |

### 3.3 坐标转换

```
场景坐标计算公式:

场景X = 地图.originX + (点.xPosition * cos(θ) - 点.yPosition * sin(θ))
场景Y = 地图.originY + (点.xPosition * sin(θ) + 点.yPosition * cos(θ))

其中 θ = 地图.rotation (弧度)
```

### 3.4 设计原理

1. **场景原点固定** - 工厂/场景的原点 (0, 0) 是全局参考点
2. **地图独立定位** - 每个地图可以设置相对于场景的偏移和旋转
3. **点位局部坐标** - 点位坐标存储的是相对于地图原点的坐标
4. **运行时转换** - 调度计算时将局部坐标转换为全局场景坐标

---

## 4. 栅格底图

### 4.1 概念

栅格底图是导入的 PGM 格式障碍物地图，用于：
- 为编辑器提供背景参考
- 帮助用户准确定位点位
- 不参与调度计算，仅用于可视化

### 4.2 参数

| 参数 | 字段 | 单位 | 说明 |
|------|------|------|------|
| 底图URL | rasterUrl | - | OSS 存储路径 |
| 底图版本 | rasterVersion | - | 每次更新自动+1 |
| 底图宽度 | rasterWidth | px | PGM 图像宽度 |
| 底图高度 | rasterHeight | px | PGM 图像高度 |
| 分辨率 | rasterResolution | m/px | 每个像素代表的米数 |

### 4.3 坐标系说明

PGM 导入时从 map.yaml 解析：
- **resolution**: 分辨率（米/像素），用于按比例显示 PGM

> 注意：PGM 仅作为背景参考，不参与调度计算。用户可在编辑器中通过"栅格坐标校准"功能手动调整显示位置。

---

## 5. 地图元素

### 5.1 点位 (Point)

| 属性 | 说明 |
|------|------|
| pointId | 点位唯一标识 |
| name | 点位名称 |
| xPosition | X坐标（相对于地图原点，单位：mm）|
| yPosition | Y坐标（相对于地图原点，单位：mm）|
| zPosition | Z坐标（楼层高度，单位：mm）|
| type | 点位类型：HALT_POSITION, PARK_POSITION, REPORT_POSITION, CHARGE_POSITION, ELEVATOR_WAIT |

### 5.2 路径 (Path)

| 属性 | 说明 |
|------|------|
| pathId | 路径唯一标识 |
| sourcePointId | 起始点位 |
| destPointId | 目标点位 |
| length | 路径长度（mm）|
| maxVelocity | 最大速度（mm/s）|
| routingType | 方向类型：BIDIRECTIONAL, FORWARD, BACKWARD |

### 5.3 位置 (Location)

| 属性 | 说明 |
|------|------|
| locationId | 位置唯一标识 |
| locationTypeId | 位置类型ID |
| positionX | X坐标（相对于地图原点）|
| positionY | Y坐标（相对于地图原点）|
| positionZ | Z坐标（楼层高度）|

### 5.4 图层 (Layer)

| 属性 | 说明 |
|------|------|
| name | 图层名称 |
| layerGroupId | 图层组ID |
| visible | 是否可见 |
| ordinal | 显示顺序 |

---

## 6. 跨层连接

### 6.1 概念

跨层连接用于连接不同楼层的地图，实现电梯、提升机等设备的调度。

### 6.2 参数

| 参数 | 说明 |
|------|------|
| connectionId | 连接唯一标识 |
| connectionType | 连接类型：ELEVATOR, CONVEYOR, PHYSICAL_DOOR |
| sourceNavigationMapId | 源地图ID |
| sourcePointId | 源点位ID |
| destNavigationMapId | 目标地图ID |
| destPointId | 目标点位ID |
| capacity | 容量 |
| travelTime | 运行时间（秒）|

---

## 7. 单位规范

| 类型 | 单位 | 说明 |
|------|------|------|
| 坐标 | mm | 毫米 |
| 距离 | mm | 毫米 |
| 速度 | mm/s | 毫米/秒 |
| 角度 | ° | 度 |
| 分辨率 | m/px | 米/像素 |
| 地图偏移 | mm | 毫米 |

---

## 8. 与 OpenTCS 原版的差异

| 维度 | OpenTCS 原版 | OpenTCS Plus |
|------|-------------|--------------|
| 模型结构 | PlantModel（单层） | FactoryModel → NavigationMap（多层）|
| 坐标系统 | 单一坐标系 | 多地图独立定位 |
| 地图偏移 | 不支持 | 支持 originX/Y/rotation |
| 底图存储 | XML文件 | 数据库 + OSS |
| 多楼层 | Layer 实现 | NavigationMap + CrossLayerConnection |

---

## 9. 参考

- OpenTCS Kernel 核心架构
