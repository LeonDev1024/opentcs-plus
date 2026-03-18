# OpenTCS Plus 工厂模型架构设计

## 1. 背景与目标

### 1.1 背景
OpenTCS Plus 需要支持多楼层工厂的跨区域调度需求，替代原有的单层地图模型。

### 1.2 目标
- 支持工厂多楼层/多区域管理
- 实现跨楼层（电梯/提升机）调度
- 支持室内外混合地图

---

## 2. 总体架构设计

### 2.1 概念模型

```
┌─────────────────────────────────────────────────────────────┐
│                    FactoryModel (工厂模型)                    │
├─────────────────────────────────────────────────────────────┤
│  id / name / description / modelVersion                     │
│  scale / coordinateSystem / lengthUnit                     │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              NavigationMap (导航地图)                  │   │
│  │  ┌──────────┬──────────┬──────────┬──────────┐   │   │
│  │  │  Map 1   │  Map 2   │  Map 3   │  Map 4   │   │   │
│  │  │ 一楼车间 │ 二楼车间 │ 仓库区   │ 室外道路 │   │   │
│  │  │ Floor:1  │ Floor:2  │ Floor:1  │ OUTDOOR  │   │   │
│  │  └──────────┴──────────┴──────────┴──────────┘   │   │
│  │                                                      │   │
│  │  ┌────────────────────────────────────────────┐    │   │
│  │  │    CrossLayerConnection (跨层连接)          │    │   │
│  │  │    电梯A: 1F↔2F / 传送带: 1F A区→1F B区   │    │   │
│  │  └────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. 坐标系统设计

### 3.1 设计原则

```
场景坐标 (Scene Coordinate) - 全局坐标系
    │
    └── 地图坐标 (Map Coordinate) - 局部坐标系
            │
            └── 画布坐标 (Canvas Coordinate) - 前端显示
```

### 3.2 坐标说明

| 层级 | 字段 | 单位 | 说明 |
|------|------|------|------|
| 工厂 | FactoryModel.scale | px/m | 比例尺，默认 50px = 1米 |
| 工厂 | FactoryModel.coordinateSystem | - | 坐标系：RIGHT_HAND / LEFT_HAND |
| 工厂 | FactoryModel.lengthUnit | - | 长度单位：METER / CENTIMETER / MILLIMETER |
| 地图 | NavigationMap.originX/Y | mm | 地图原点相对于场景原点的偏移 |
| 地图 | NavigationMap.rotation | ° | 地图相对于场景方向的旋转角度 |
| 点位 | Point.x/y/z | mm | 相对于地图原点的坐标 |

### 3.3 坐标转换公式

```
点位场景坐标计算:

场景X = 地图.originX + (点.xPosition * cos(θ) - 点.yPosition * sin(θ))
场景Y = 地图.originY + (点.xPosition * sin(θ) + 点.yPosition * cos(θ))

其中 θ = 地图.rotation (弧度)
```

### 3.4 设计原理

1. **场景原点固定** - 工厂/场景的原点 (0, 0) 是全局参考点
2. **地图独立定位** - 每个地图可设置相对于场景的偏移 (originX/Y) 和旋转 (rotation)
3. **点位局部坐标** - 点位坐标存储的是相对于地图原点的坐标
4. **运行时转换** - 调度计算时将局部坐标转换为全局场景坐标
5. **前端画布原点** - 编辑器画布原点固定 (0, 0)，显示地图内容

---

## 4. 数据模型详细设计

### 4.1 数据库表结构

```sql
-- ============================================================
-- 工厂模型表 (FactoryModel)
-- ============================================================
CREATE TABLE factory_model (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    factory_id VARCHAR(64) NOT NULL COMMENT '工厂唯一标识符',
    name VARCHAR(255) NOT NULL COMMENT '工厂名称',
    model_version VARCHAR(50) NOT NULL DEFAULT '1.0' COMMENT '模型版本',
    -- 比例尺设置
    scale DECIMAL(10,4) NOT NULL DEFAULT 50.0 COMMENT '比例尺 (px/m)：像素/米，默认50像素=1米',
    -- 坐标系设置
    coordinate_system VARCHAR(50) DEFAULT 'RIGHT_HAND' COMMENT '坐标系：RIGHT_HAND右手系/LEFT_HAND左手系',
    length_unit VARCHAR(20) DEFAULT 'METER' COMMENT '长度单位：METER/CENTIMETER/MILLIMETER',
    -- 扩展属性
    properties JSON COMMENT '扩展属性',
    description VARCHAR(1000) COMMENT '描述',
    -- 审计字段
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 0,
    del_flag CHAR(1) DEFAULT '0',
    status CHAR(1) DEFAULT '0',
    CONSTRAINT uk_factory_model_factory_id UNIQUE (factory_id),
    CONSTRAINT uk_factory_model_name UNIQUE (name),
    CONSTRAINT pk_factory_model PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工厂模型表';

-- ============================================================
-- 导航地图表 (NavigationMap)
-- ============================================================
CREATE TABLE navigation_map (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    factory_model_id BIGINT NOT NULL COMMENT '所属工厂模型ID',
    map_id VARCHAR(64) NOT NULL COMMENT '地图唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '地图名称（如：一楼车间、室外道路）',
    floor_number INT COMMENT '楼层号（负数表示地下，0表示1楼）',
    amr_model VARCHAR(100) COMMENT 'AMR型号（必填，对应vehicle_type.name）',
    -- 地图定位参数（相对于场景原点，用于多地图统一显示）
    origin_x DECIMAL(12,4) DEFAULT 0 COMMENT '地图原点X坐标（毫米，相对于场景原点）',
    origin_y DECIMAL(12,4) DEFAULT 0 COMMENT '地图原点Y坐标（毫米，相对于场景原点）',
    rotation DECIMAL(10,4) DEFAULT 0 COMMENT '地图旋转角度（度，相对于场景方向）',
    -- 栅格底图（2D PGM障碍物地图，仅用于可视化）
    raster_url VARCHAR(500) COMMENT '栅格地图OSS存储路径',
    raster_version INT DEFAULT 0 COMMENT '栅格地图版本号',
    raster_width INT COMMENT '栅格地图宽度（像素）',
    raster_height INT COMMENT '栅格地图高度（像素）',
    raster_resolution DECIMAL(12,6) COMMENT '栅格地图分辨率（米/像素）',
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_dept BIGINT,
    create_by BIGINT,
    update_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 0,
    del_flag CHAR(1) DEFAULT '0',
    status CHAR(1) DEFAULT '0',
    CONSTRAINT pk_navigation_map PRIMARY KEY (id),
    CONSTRAINT fk_navigation_map_factory FOREIGN KEY (factory_model_id) REFERENCES factory_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_navigation_map_factory_map UNIQUE (factory_model_id, map_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导航地图表';

-- ============================================================
-- 图层组表 (LayerGroup)
-- ============================================================
CREATE TABLE factory_layer_group (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '所属导航地图ID',
    name VARCHAR(255) NOT NULL COMMENT '图层组名称',
    visible TINYINT(1) DEFAULT 1 COMMENT '是否可见',
    ordinal INT DEFAULT 0 COMMENT '显示顺序',
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_factory_layer_group PRIMARY KEY (id),
    CONSTRAINT fk_factory_layer_group_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT uk_factory_layer_group_map_name UNIQUE (navigation_map_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图层组表';

-- ============================================================
-- 图层表 (Layer)
-- ============================================================
CREATE TABLE factory_layer (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '所属导航地图ID',
    layer_group_id BIGINT COMMENT '所属图层组ID',
    name VARCHAR(255) NOT NULL COMMENT '图层名称',
    visible TINYINT(1) DEFAULT 1 COMMENT '是否可见',
    ordinal INT DEFAULT 0 COMMENT '显示顺序',
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_factory_layer PRIMARY KEY (id),
    CONSTRAINT fk_factory_layer_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_factory_layer_layer_group FOREIGN KEY (layer_group_id) REFERENCES factory_layer_group(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图层表';

-- ============================================================
-- 点位表 (Point)
-- ============================================================
CREATE TABLE point (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '归属导航地图ID',
    layer_id BIGINT COMMENT '归属图层ID',
    point_id VARCHAR(255) NOT NULL COMMENT '点位唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '点位名称',
    -- 坐标（前端显示和后端计算共用，无需转换）
    x_position DECIMAL(12,4) NOT NULL COMMENT 'X坐标',
    y_position DECIMAL(12,4) NOT NULL COMMENT 'Y坐标',
    z_position DECIMAL(12,4) DEFAULT 0 COMMENT 'Z坐标（楼层高度）',
    vehicle_orientation DECIMAL(8,4) DEFAULT 0 COMMENT '车辆方向角度',
    -- 点位属性
    type VARCHAR(50) NOT NULL DEFAULT 'HALT_POSITION' COMMENT '点位类型：HALT_POSITION/PARK_POSITION/REPORT_POSITION/CHARGE_POSITION/ELEVATOR_WAIT',
    radius DECIMAL(8,4) DEFAULT 0 COMMENT '点位半径',
    -- 状态管理
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_blocked TINYINT(1) DEFAULT 0 COMMENT '是否被阻塞',
    is_occupied TINYINT(1) DEFAULT 0 COMMENT '是否被占用',
    -- 扩展
    label VARCHAR(500) COMMENT '标签',
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_point PRIMARY KEY (id),
    CONSTRAINT fk_point_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_point_layer FOREIGN KEY (layer_id) REFERENCES factory_layer(id) ON DELETE SET NULL,
    CONSTRAINT uk_point_map_point UNIQUE (navigation_map_id, point_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点位表';

-- ============================================================
-- 路径表 (Path)
-- ============================================================
CREATE TABLE path (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '归属导航地图ID',
    path_id VARCHAR(255) NOT NULL COMMENT '路径唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '路径名称',
    source_point_id VARCHAR(255) NOT NULL COMMENT '起始点位标识',
    dest_point_id VARCHAR(255) NOT NULL COMMENT '目标点位标识',
    -- 路径属性
    length DECIMAL(12,4) NOT NULL COMMENT '路径长度(米)',
    max_velocity DECIMAL(8,4) COMMENT '最大允许速度(米/秒)',
    max_reverse_velocity DECIMAL(8,4) COMMENT '最大反向速度(米/秒)',
    routing_type VARCHAR(50) DEFAULT 'BIDIRECTIONAL' COMMENT '路径方向类型',
    -- 状态
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_blocked TINYINT(1) DEFAULT 0 COMMENT '是否被阻塞',
    -- 扩展
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_path PRIMARY KEY (id),
    CONSTRAINT fk_path_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT uk_path_map_path UNIQUE (navigation_map_id, path_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路径表';

-- ============================================================
-- 位置类型表 (LocationType)
-- ============================================================
CREATE TABLE location_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    factory_model_id BIGINT NOT NULL COMMENT '所属工厂ID（全局）',
    name VARCHAR(255) NOT NULL COMMENT '位置类型名称',
    allowed_operations JSON COMMENT '允许的操作列表',
    allowed_peripheral_operations JSON COMMENT '允许的外围设备操作',
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_location_type PRIMARY KEY (id),
    CONSTRAINT fk_location_type_factory FOREIGN KEY (factory_model_id) REFERENCES factory_model(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='位置类型表';

-- ============================================================
-- 位置表 (Location)
-- ============================================================
CREATE TABLE location (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '归属导航地图ID',
    location_type_id BIGINT NOT NULL COMMENT '位置类型ID',
    location_id VARCHAR(255) NOT NULL COMMENT '位置唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '位置名称',
    -- 坐标
    position_x DECIMAL(12,4) COMMENT 'X坐标',
    position_y DECIMAL(12,4) COMMENT 'Y坐标',
    position_z DECIMAL(12,4) DEFAULT 0 COMMENT 'Z坐标',
    -- 状态
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_occupied TINYINT(1) DEFAULT 0 COMMENT '是否被占用',
    -- 扩展
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_location PRIMARY KEY (id),
    CONSTRAINT fk_location_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_location_type FOREIGN KEY (location_type_id) REFERENCES location_type(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='位置表';

-- ============================================================
-- 区块表 (Block)
-- ============================================================
CREATE TABLE block (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    factory_model_id BIGINT NOT NULL COMMENT '所属工厂ID',
    navigation_map_id BIGINT COMMENT '所属地图（单楼层区域可指定）',
    name VARCHAR(255) NOT NULL COMMENT '区块名称',
    type VARCHAR(50) NOT NULL DEFAULT 'SINGLE' COMMENT '区块类型：SINGLE/GROUP',
    members JSON COMMENT '成员点位的point_id列表',
    color VARCHAR(20) COMMENT '区块显示颜色',
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_block PRIMARY KEY (id),
    CONSTRAINT fk_block_factory FOREIGN KEY (factory_model_id) REFERENCES factory_model(id) ON DELETE CASCADE,
    CONSTRAINT fk_block_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区块表';

-- ============================================================
-- 跨层连接表 (CrossLayerConnection)
-- ============================================================
CREATE TABLE cross_layer_connection (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    factory_model_id BIGINT NOT NULL COMMENT '所属工厂ID',
    connection_id VARCHAR(64) NOT NULL COMMENT '连接唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '连接名称，如：电梯A、1号提升机',
    connection_type VARCHAR(50) NOT NULL COMMENT 'ELEVATOR/CONVEYOR/PHYSICAL_DOOR',
    -- 连接属性
    source_navigation_map_id BIGINT NOT NULL COMMENT '源地图ID',
    source_point_id VARCHAR(255) NOT NULL COMMENT '源点位ID',
    source_floor INT NOT NULL COMMENT '源楼层',
    dest_navigation_map_id BIGINT NOT NULL COMMENT '目标地图ID',
    dest_point_id VARCHAR(255) NOT NULL COMMENT '目标点位ID',
    dest_floor INT NOT NULL COMMENT '目标楼层',
    -- 电梯/传送带属性
    capacity INT DEFAULT 1 COMMENT '容量（电梯可同时承载车辆数）',
    max_weight DECIMAL(10,2) COMMENT '最大承重(kg)',
    travel_time INT COMMENT '运行时间（秒）',
    -- 状态
    available TINYINT(1) DEFAULT 1 COMMENT '是否可用',
    current_load INT DEFAULT 0 COMMENT '当前负载',
    -- 扩展
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_cross_layer_connection PRIMARY KEY (id),
    CONSTRAINT fk_clc_factory FOREIGN KEY (factory_model_id) REFERENCES factory_model(id) ON DELETE CASCADE,
    CONSTRAINT fk_clc_source_map FOREIGN KEY (source_navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_clc_dest_map FOREIGN KEY (dest_navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT uk_clc_connection_id UNIQUE (factory_model_id, connection_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跨层连接表';

-- ============================================================
-- 电梯调度记录表 (ElevatorSchedule)
-- ============================================================
CREATE TABLE elevator_schedule (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    connection_id VARCHAR(64) NOT NULL COMMENT '跨层连接ID',
    vehicle_id BIGINT COMMENT '预约车辆ID',
    vehicle_name VARCHAR(255) COMMENT '预约车辆名称',
    source_floor INT NOT NULL COMMENT '源楼层',
    dest_floor INT NOT NULL COMMENT '目标楼层',
    schedule_type VARCHAR(50) DEFAULT 'RESERVE' COMMENT '调度类型：RESERVE/CANCEL/COMPLETE',
    pickup_time DATETIME COMMENT '预计接载时间',
    delivery_time DATETIME COMMENT '预计送达时间',
    actual_pickup_time DATETIME COMMENT '实际接载时间',
    actual_delivery_time DATETIME COMMENT '实际送达时间',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT '状态：PENDING/RUNNING/COMPLETED/CANCELLED',
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_elevator_schedule PRIMARY KEY (id),
    INDEX idx_connection_id (connection_id),
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_schedule_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电梯调度记录表';
```

---

## 5. Java 实体类设计

### 5.1 FactoryModelEntity
```java
@Data
@TableName("factory_model")
public class FactoryModelEntity extends BusinessEntity {
    private Long id;
    private String factoryId;
    private String name;
    private String modelVersion;

    // 比例尺设置
    private BigDecimal scale;  // 比例尺 (px/m)：像素/米，默认50
    private String coordinateSystem;  // 坐标系：RIGHT_HAND/LEFT_HAND
    private String lengthUnit;  // 长度单位：METER/CENTIMETER/MILLIMETER

    private String properties;
    private String description;
    private String status;
}
```

### 5.2 NavigationMapEntity
```java
@Data
@TableName("navigation_map")
public class NavigationMapEntity extends BusinessEntity {
    private Long id;
    private Long factoryModelId;
    private String mapId;
    private String name;
    private Integer floorNumber;  // 1, 2, 3... 或 null(室外)
    private String mapType;       // INDOOR/OUTDOOR/MIXED

    // PGM导入时使用：识别原始图纸的原点位置
    private BigDecimal originX;
    private BigDecimal originY;

    private String properties;
    private String status;
}
```

### 5.3 PointEntity
```java
@Data
@TableName("point")
public class PointEntity extends DataEntity {
    private Long id;
    private Long navigationMapId;  // 归属导航地图
    private Long layerId;
    private String pointId;
    private String name;

    // 统一坐标：前端显示和后端计算共用，无需转换
    private BigDecimal xPosition;
    private BigDecimal yPosition;
    private BigDecimal zPosition;  // 楼层高度

    private BigDecimal vehicleOrientation;
    private String type;  // HALT_POSITION/PARK_POSITION/REPORT_POSITION/CHARGE_POSITION/ELEVATOR_WAIT
    private BigDecimal radius;
    private Boolean locked;
    private Boolean isBlocked;
    private Boolean isOccupied;
    private String label;
    private String properties;
}
```

---

## 6. API 设计

### 6.1 工厂模型 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /factory/model/list | 获取工厂模型列表 |
| GET | /factory/model/{id} | 获取工厂模型详情 |
| POST | /factory/model/create | 创建工厂模型 |
| PUT | /factory/model/update | 更新工厂模型 |
| DELETE | /factory/model/{id} | 删除工厂模型 |

### 6.2 导航地图 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /factory/map/list | 获取导航地图列表 |
| GET | /factory/map/list/{factoryId} | 获取工厂下所有地图 |
| GET | /factory/map/{id} | 获取地图详情（含点、路径） |
| GET | /factory/map/floor/{factoryId}/{floorNumber} | 根据楼层获取地图 |
| POST | /factory/map/create | 创建导航地图 |
| PUT | /factory/map/update | 更新导航地图 |
| DELETE | /factory/map/{id} | 删除导航地图 |

### 6.3 跨层连接 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /factory/connection/list | 获取跨层连接列表 |
| GET | /factory/connection/list/{factoryId} | 获取工厂下所有跨层连接 |
| GET | /factory/connection/available/{factoryId} | 获取可用跨层连接 |
| GET | /factory/connection/{id} | 获取跨层连接详情 |
| POST | /factory/connection/create | 创建跨层连接 |
| PUT | /factory/connection/update | 更新跨层连接 |
| DELETE | /factory/connection/{id} | 删除跨层连接 |
| POST | /factory/connection/{connectionId}/reserve | 预留电梯 |
| POST | /factory/connection/{connectionId}/release | 释放电梯 |

---

## 7. 核心算法设计

### 7.1 分层路由图

```
┌─────────────────────────────────────────────────────────────┐
│                  GlobalRoutingGraph (全局路由图)             │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐ │
│  │  Map 1      │     │  Map 2      │     │  Map 3      │ │
│  │  (一楼)     │◄───►│  (二楼)     │◄───►│  (三楼)     │ │
│  │  A* Graph   │ 电梯 │  A* Graph   │ 电梯 │  A* Graph   │ │
│  └─────────────┘     └─────────────┘     └─────────────┘ │
│         │                   │                   │          │
│         └───────────────────┼───────────────────┘          │
│                             │                              │
│                    ┌────────▼────────┐                    │
│                    │  CrossLayer     │                    │
│                    │  Edges         │                    │
│                    └─────────────────┘                    │
└─────────────────────────────────────────────────────────────┘
```

### 7.2 跨楼层路径规划

```java
public class CrossFloorRouter {

    public List<String> planRoute(String sourcePointId, String destPointId) {
        // 1. 获取起点和终点所在的地图
        // 2. 同楼层：使用 A* 算法
        // 3. 跨楼层：查找可用电梯，构建分段路径
    }
}
```

---

## 8. 实现状态

### Phase 1: 数据模型 ✅ 已完成
- [x] factory_model 表（含比例尺）
- [x] navigation_map 表（含 originX/Y）
- [x] cross_layer_connection 表
- [x] elevator_schedule 表

### Phase 2: 核心服务 ✅ 已完成
- [x] FactoryModelService
- [x] NavigationMapService
- [x] CrossLayerConnectionService

### Phase 3: 路由算法 ✅ 已完成
- [x] GlobalRoutingGraph
- [x] LocalRoutingGraph
- [x] CrossFloorRouter

### Phase 4: API ✅ 已完成
- [x] FactoryModelController
- [x] NavigationMapController
- [x] CrossLayerConnectionController
