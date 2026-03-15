-- ============================================================
-- OpenTCS MySQL 数据库脚本 (优化版)
-- 数据库名: opentcs
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS opentcs DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE opentcs;

-- ============================================================
-- 地图模型表 (PlantModel) - 业务主表，保留完整审计字段
-- ============================================================
DROP TABLE IF EXISTS plant_model;
CREATE TABLE plant_model (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    map_id VARCHAR(255) NOT NULL COMMENT '地图模型唯一标识符',
    name VARCHAR(255) NOT NULL COMMENT '地图模型名称',
    model_version VARCHAR(50) NOT NULL DEFAULT '1.0' COMMENT '模型版本',
    properties JSON COMMENT '扩展属性',
    -- 审计字段
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT DEFAULT 0 COMMENT '乐观锁版本',
    description VARCHAR(1000) COMMENT '备注',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    status CHAR(1) DEFAULT '0' COMMENT '状态',
    CONSTRAINT uk_plant_model_map_id UNIQUE (map_id),
    CONSTRAINT uk_plant_model_name UNIQUE (name),
    CONSTRAINT pk_plant_model PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OpenTCS地图模型表';

-- ============================================================
-- 点位表 (Point) - 地图数据表，简化审计字段
-- ============================================================
DROP TABLE IF EXISTS point;
CREATE TABLE point (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plant_model_id BIGINT NOT NULL COMMENT '所属地图模型ID',
    point_id VARCHAR(255) NOT NULL COMMENT '点位唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '点位名称',
    -- 坐标信息
    x_position DECIMAL(12,4) NOT NULL COMMENT 'X坐标',
    y_position DECIMAL(12,4) NOT NULL COMMENT 'Y坐标',
    z_position DECIMAL(12,4) DEFAULT 0.0000 COMMENT 'Z坐标',
    vehicle_orientation DECIMAL(8,4) DEFAULT 0.0000 COMMENT '车辆方向角度',
    -- 点位属性
    type VARCHAR(50) NOT NULL DEFAULT 'HALT_POSITION' COMMENT '点位类型：HALT_POSITION, PARK_POSITION, REPORT_POSITION',
    radius DECIMAL(8,4) DEFAULT 0.0000 COMMENT '点位半径',
    -- 状态管理
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_blocked TINYINT(1) DEFAULT 0 COMMENT '是否被阻塞',
    is_occupied TINYINT(1) DEFAULT 0 COMMENT '是否被占用',
    -- 扩展信息
    label VARCHAR(500) COMMENT '标签',
    properties JSON COMMENT '扩展属性',
    -- 审计字段（简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_point PRIMARY KEY (id),
    CONSTRAINT fk_point_plant_model FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_point_model_id_point_id UNIQUE (plant_model_id, point_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点位表';

-- ============================================================
-- 路径表 (Path) - 地图数据表，简化审计字段
-- ============================================================
DROP TABLE IF EXISTS path;
CREATE TABLE path (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plant_model_id BIGINT NOT NULL COMMENT '所属地图模型ID',
    path_id VARCHAR(255) NOT NULL COMMENT '路径唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '路径名称',
    source_point_id VARCHAR(255) NOT NULL COMMENT '起始点位标识',
    dest_point_id VARCHAR(255) NOT NULL COMMENT '目标点位标识',
    -- 路径属性
    length DECIMAL(12,4) NOT NULL COMMENT '路径长度',
    max_velocity DECIMAL(8,4) COMMENT '最大允许速度',
    max_reverse_velocity DECIMAL(8,4) COMMENT '最大反向速度',
    routing_type VARCHAR(50) DEFAULT 'BIDIRECTIONAL' COMMENT '路径方向类型',
    -- 状态管理
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_blocked TINYINT(1) DEFAULT 0 COMMENT '是否被阻塞',
    -- 扩展信息
    properties JSON COMMENT '扩展属性',
    -- 审计字段（简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_path PRIMARY KEY (id),
    CONSTRAINT fk_path_plant_model FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_path_model_id_path_id UNIQUE (plant_model_id, path_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路径表';

-- ============================================================
-- 位置类型表 (LocationType) - 配置表，保留完整审计字段
-- ============================================================
DROP TABLE IF EXISTS location_type;
CREATE TABLE location_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plant_model_id BIGINT NOT NULL COMMENT '所属地图模型ID',
    name VARCHAR(255) NOT NULL COMMENT '位置类型名称',
    -- 操作权限
    allowed_operations JSON COMMENT '允许的操作列表',
    allowed_peripheral_operations JSON COMMENT '允许的外围设备操作',
    -- 扩展属性
    properties JSON COMMENT '扩展属性',
    -- 审计字段（简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_location_type PRIMARY KEY (id),
    CONSTRAINT fk_location_type_plant_model FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_location_type_model_name UNIQUE (plant_model_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='位置类型表';

-- ============================================================
-- 位置表 (Location) - 地图数据表，简化审计字段
-- ============================================================
DROP TABLE IF EXISTS location;
CREATE TABLE location (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plant_model_id BIGINT NOT NULL COMMENT '所属地图模型ID',
    location_type_id BIGINT NOT NULL COMMENT '位置类型ID',
    location_id VARCHAR(255) NOT NULL COMMENT '位置唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '位置名称',
    -- 坐标信息
    position_x DECIMAL(12,4) NOT NULL COMMENT 'X坐标',
    position_y DECIMAL(12,4) NOT NULL COMMENT 'Y坐标',
    position_z DECIMAL(12,4) DEFAULT 0.0000 COMMENT 'Z坐标',
    vehicle_orientation DECIMAL(8,4) DEFAULT 0.0000 COMMENT '车辆方向',
    -- 状态管理
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_occupied TINYINT(1) DEFAULT 0 COMMENT '是否被占用',
    -- 扩展信息
    label VARCHAR(500) COMMENT '标签',
    properties JSON COMMENT '扩展属性',
    -- 审计字段（简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_location PRIMARY KEY (id),
    CONSTRAINT fk_location_plant_model FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    CONSTRAINT fk_location_type FOREIGN KEY (location_type_id) REFERENCES location_type(id) ON DELETE CASCADE,
    CONSTRAINT uk_location_model_id UNIQUE (plant_model_id, location_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='位置表';

-- ============================================================
-- 区块表 (Block) - 地图数据表，简化审计字段
-- ============================================================
DROP TABLE IF EXISTS block;
CREATE TABLE block (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plant_model_id BIGINT NOT NULL COMMENT '所属地图模型ID',
    block_id VARCHAR(255) NOT NULL COMMENT '区块唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '区块名称',
    type VARCHAR(50) NOT NULL DEFAULT 'SINGLE' COMMENT '区块类型：SINGLE, GROUP',
    -- 区块成员
    members JSON COMMENT '区块成员',
    -- 区块属性
    color VARCHAR(20) COMMENT '区块显示颜色',
    properties JSON COMMENT '扩展属性',
    -- 审计字段（简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_block PRIMARY KEY (id),
    CONSTRAINT fk_block_plant_model FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_block_model_id UNIQUE (plant_model_id, block_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区块表';

-- ============================================================
-- 视觉布局表 (VisualLayout) - 地图配置表，简化审计字段
-- ============================================================
DROP TABLE IF EXISTS visual_layout;
CREATE TABLE visual_layout (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plant_model_id BIGINT NOT NULL COMMENT '所属地图模型ID',
    name VARCHAR(255) NOT NULL DEFAULT 'Default Layout' COMMENT '视觉布局名称',
    scale_x DECIMAL(10,6) NOT NULL DEFAULT 50.0 COMMENT 'X轴缩放比例',
    scale_y DECIMAL(10,6) NOT NULL DEFAULT 50.0 COMMENT 'Y轴缩放比例',
    -- 扩展属性
    properties JSON DEFAULT (JSON_OBJECT()) COMMENT '扩展属性',
    -- 审计字段（简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_visual_layout PRIMARY KEY (id),
    CONSTRAINT fk_visual_layout_plant_model FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_visual_layout_plant_model UNIQUE (plant_model_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视觉布局表';

-- ============================================================
-- 图层组表 (LayerGroup) - 地图配置表，简化审计字段
-- ============================================================
DROP TABLE IF EXISTS layer_group;
CREATE TABLE layer_group (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    visual_layout_id BIGINT NOT NULL COMMENT '所属视觉布局ID',
    name VARCHAR(255) NOT NULL COMMENT '图层组名称',
    -- 可视化属性
    visible TINYINT(1) DEFAULT 1 COMMENT '是否可见',
    ordinal INT DEFAULT 0 COMMENT '显示顺序',
    -- 扩展属性
    properties JSON COMMENT '扩展属性',
    -- 审计字段（简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_layer_group PRIMARY KEY (id),
    CONSTRAINT fk_layer_group_visual_layout FOREIGN KEY (visual_layout_id) REFERENCES visual_layout(id) ON DELETE CASCADE,
    CONSTRAINT uk_layer_group_visual_layout_name UNIQUE (visual_layout_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图层组表';

-- ============================================================
-- 图层表 (Layer) - 地图配置表，简化审计字段
-- ============================================================
DROP TABLE IF EXISTS layer;
CREATE TABLE layer (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    visual_layout_id BIGINT NOT NULL COMMENT '所属视觉布局ID',
    layer_group_id BIGINT COMMENT '所属图层组ID',
    name VARCHAR(255) NOT NULL COMMENT '图层名称',
    -- 可视化属性
    visible TINYINT(1) DEFAULT 1 COMMENT '是否可见',
    ordinal INT DEFAULT 0 COMMENT '显示顺序',
    -- 扩展属性
    properties JSON COMMENT '扩展属性',
    -- 审计字段（简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_layer PRIMARY KEY (id),
    CONSTRAINT fk_layer_visual_layout FOREIGN KEY (visual_layout_id) REFERENCES visual_layout(id) ON DELETE CASCADE,
    CONSTRAINT fk_layer_layer_group FOREIGN KEY (layer_group_id) REFERENCES layer_group(id) ON DELETE SET NULL,
    CONSTRAINT uk_layer_visual_layout_name UNIQUE (visual_layout_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图层表';

-- ============================================================
-- 地图模型历史版本快照表 (PlantModelHistory) - 历史表，最简化
-- ============================================================
DROP TABLE IF EXISTS plant_model_history;
CREATE TABLE plant_model_history (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plant_model_id BIGINT NOT NULL COMMENT '所属地图模型ID',
    model_version VARCHAR(50) NOT NULL COMMENT '业务版本号',
    file_url VARCHAR(500) COMMENT '快照文件路径',
    snapshot_type VARCHAR(50) DEFAULT 'EDITOR_JSON' COMMENT '快照类型',
    change_summary VARCHAR(1000) COMMENT '修改说明',
    properties JSON COMMENT '扩展属性',
    -- 审计字段（最简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT pk_plant_model_history PRIMARY KEY (id),
    CONSTRAINT fk_plant_model_history_plant_model FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地图模型历史版本快照表';

-- ============================================================
-- 车辆类型表 (VehicleType) - 配置表，简化审计字段
-- ============================================================
DROP TABLE IF EXISTS vehicle_type;
CREATE TABLE vehicle_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(255) NOT NULL COMMENT '车辆类型名称',
    length DECIMAL(8,4) NOT NULL COMMENT '车辆长度',
    width DECIMAL(8,4) NOT NULL COMMENT '车辆宽度',
    height DECIMAL(8,4) NOT NULL COMMENT '车辆高度',
    max_velocity DECIMAL(8,4) NOT NULL COMMENT '最大速度',
    max_reverse_velocity DECIMAL(8,4) COMMENT '最大反向速度',
    energy_level DECIMAL(8,4) COMMENT '能量级别',
    allowed_orders JSON COMMENT '允许的订单',
    allowed_peripheral_operations JSON COMMENT '允许的外围设备操作',
    -- 扩展属性
    properties JSON COMMENT '扩展属性',
    -- 审计字段（简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_vehicle_type PRIMARY KEY (id),
    CONSTRAINT uk_vehicle_type_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆类型表';

-- ============================================================
-- 车辆表 (Vehicle) - 业务主表，保留完整审计字段
-- ============================================================
DROP TABLE IF EXISTS vehicle;
CREATE TABLE vehicle (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(255) NOT NULL COMMENT '车辆名称',
    vehicle_type_id BIGINT NOT NULL COMMENT '车辆类型ID',
    current_position VARCHAR(255) COMMENT '当前位置',
    next_position VARCHAR(255) COMMENT '下一个位置',
    state VARCHAR(50) DEFAULT 'UNKNOWN' COMMENT '车辆状态',
    integration_level VARCHAR(50) DEFAULT 'TO_BE_IGNORED' COMMENT '集成级别',
    energy_level DECIMAL(8,4) COMMENT '能量级别',
    current_transport_order VARCHAR(255) COMMENT '当前运输订单',
    properties JSON COMMENT '扩展属性',
    -- 审计字段（保留完整）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_dept BIGINT COMMENT '创建部门',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_vehicle PRIMARY KEY (id),
    CONSTRAINT fk_vehicle_type FOREIGN KEY (vehicle_type_id) REFERENCES vehicle_type(id),
    CONSTRAINT uk_vehicle_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

-- ============================================================
-- 运输订单表 (TransportOrder) - 业务主表，保留完整审计字段
-- ============================================================
DROP TABLE IF EXISTS transport_order;
CREATE TABLE transport_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(255) NOT NULL COMMENT '订单编号',
    name VARCHAR(255) COMMENT '订单名称',
    source_point_id VARCHAR(255) NOT NULL COMMENT '起点点位ID',
    dest_point_id VARCHAR(255) NOT NULL COMMENT '目标点位ID',
    vehicle_id BIGINT COMMENT '分配的车辆ID',
    state VARCHAR(50) DEFAULT 'RAW' COMMENT '订单状态：RAW, ACTIVE, FINISHED, FAILED, CANCELLED',
    priority INT DEFAULT 0 COMMENT '优先级',
    deadline DATETIME COMMENT '截止时间',
    properties JSON COMMENT '扩展属性',
    -- 审计字段（保留完整）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_dept BIGINT COMMENT '创建部门',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_transport_order PRIMARY KEY (id),
    CONSTRAINT uk_transport_order_no UNIQUE (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运输订单表';

-- ============================================================
-- 订单历史表 (OrderHistory) - 历史表，最简化
-- ============================================================
DROP TABLE IF EXISTS order_history;
CREATE TABLE order_history (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    event_type VARCHAR(50) NOT NULL COMMENT '事件类型：CREATED, DISPATCHED, STARTED, COMPLETED, FAILED, CANCELLED',
    vehicle_id BIGINT COMMENT '相关车辆ID',
    description VARCHAR(500) COMMENT '事件描述',
    properties JSON COMMENT '扩展属性',
    -- 审计字段（最简化）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT pk_order_history PRIMARY KEY (id),
    CONSTRAINT fk_order_history_order FOREIGN KEY (order_id) REFERENCES transport_order(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单历史表';

-- ============================================================
-- 索引创建
-- ============================================================

-- point表索引
CREATE INDEX idx_point_model_id ON point(plant_model_id);
CREATE INDEX idx_point_type ON point(type);
CREATE INDEX idx_point_name ON point(name);

-- path表索引
CREATE INDEX idx_path_model_id ON path(plant_model_id);
CREATE INDEX idx_path_source ON path(source_point_id);
CREATE INDEX idx_path_dest ON path(dest_point_id);

-- location表索引
CREATE INDEX idx_location_model_id ON location(plant_model_id);
CREATE INDEX idx_location_type ON location(location_type_id);

-- block表索引
CREATE INDEX idx_block_model_id ON block(plant_model_id);

-- vehicle表索引
CREATE INDEX idx_vehicle_type ON vehicle(vehicle_type_id);
CREATE INDEX idx_vehicle_state ON vehicle(state);
CREATE INDEX idx_vehicle_integration ON vehicle(integration_level);

-- transport_order表索引
CREATE INDEX idx_order_state ON transport_order(state);
CREATE INDEX idx_order_vehicle ON transport_order(vehicle_id);
CREATE INDEX idx_order_deadline ON transport_order(deadline);

-- order_history表索引
CREATE INDEX idx_history_order_id ON order_history(order_id);
CREATE INDEX idx_history_event_type ON order_history(event_type);
CREATE INDEX idx_history_create_time ON order_history(create_time);

-- ============================================================
-- 初始化数据
-- ============================================================

-- 初始化默认车辆类型
INSERT INTO vehicle_type (name, length, width, height, max_velocity, max_reverse_velocity, energy_level, create_time) VALUES
('默认AGV', 2.0, 1.0, 1.5, 2.0, 1.0, 100.0, NOW());

-- 初始化默认地图模型
INSERT INTO plant_model (map_id, name, model_version, description, create_time) VALUES
('default', '默认地图', '1.0', '系统默认地图模型', NOW());
