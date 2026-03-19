-- ============================================================
-- OpenTCS Plus 工厂模型 SQL (全新创建)
-- ============================================================

USE opentcs;

-- ============================================================
-- 删除所有相关表（按外键依赖顺序）
-- ============================================================
DROP TABLE IF EXISTS elevator_schedule;
DROP TABLE IF EXISTS cross_layer_connection;
DROP TABLE IF EXISTS location;
DROP TABLE IF EXISTS block;
DROP TABLE IF EXISTS point;
DROP TABLE IF EXISTS path;
DROP TABLE IF EXISTS factory_layer;
DROP TABLE IF EXISTS factory_layer_group;
DROP TABLE IF EXISTS location_type;
DROP TABLE IF EXISTS navigation_map;
DROP TABLE IF EXISTS factory_model;

-- ============================================================
-- 工厂模型表 (FactoryModel)
-- ============================================================
CREATE TABLE factory_model (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    factory_id VARCHAR(64) NOT NULL COMMENT '工厂唯一标识符',
    name VARCHAR(255) NOT NULL COMMENT '工厂名称',
    model_version VARCHAR(50) NOT NULL DEFAULT '1.0' COMMENT '模型版本',
    scale DECIMAL(10,4) NOT NULL DEFAULT 50.0 COMMENT '比例尺 (px/m)',
    coordinate_system VARCHAR(50) DEFAULT 'RIGHT_HAND' COMMENT '坐标系',
    length_unit VARCHAR(20) DEFAULT 'METER' COMMENT '长度单位',
    properties JSON COMMENT '扩展属性',
    description VARCHAR(1000) COMMENT '描述',
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
    name VARCHAR(255) NOT NULL COMMENT '地图名称',
    floor_number INT COMMENT '楼层号',
    map_type VARCHAR(50) NOT NULL DEFAULT 'INDOOR' COMMENT '地图类型',
    origin_x DECIMAL(12,4) DEFAULT 0 COMMENT 'PGM原点X',
    origin_y DECIMAL(12,4) DEFAULT 0 COMMENT 'PGM原点Y',
    properties JSON COMMENT '扩展属性',
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
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
-- 位置类型表 (LocationType) - 全局共享，不按工厂隔离
-- ============================================================
CREATE TABLE location_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(255) NOT NULL COMMENT '位置类型名称',
    allowed_operations JSON COMMENT '允许的操作列表',
    allowed_peripheral_operations JSON COMMENT '允许的外围设备操作',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_location_type PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='位置类型表';

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
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_factory_layer_group PRIMARY KEY (id),
    CONSTRAINT fk_factory_layer_group_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE
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
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
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
    x_position DECIMAL(12,4) NOT NULL COMMENT 'X坐标',
    y_position DECIMAL(12,4) NOT NULL COMMENT 'Y坐标',
    z_position DECIMAL(12,4) DEFAULT 0 COMMENT 'Z坐标',
    vehicle_orientation DECIMAL(8,4) DEFAULT 0 COMMENT '车辆方向角度',
    type VARCHAR(50) NOT NULL DEFAULT 'HALT_POSITION' COMMENT '点位类型',
    radius DECIMAL(8,4) DEFAULT 0 COMMENT '点位半径',
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_blocked TINYINT(1) DEFAULT 0 COMMENT '是否被阻塞',
    is_occupied TINYINT(1) DEFAULT 0 COMMENT '是否被占用',
    label VARCHAR(500) COMMENT '标签',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_point PRIMARY KEY (id),
    CONSTRAINT fk_point_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_point_layer FOREIGN KEY (layer_id) REFERENCES factory_layer(id) ON DELETE SET NULL
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
    length DECIMAL(12,4) NOT NULL COMMENT '路径长度',
    max_velocity DECIMAL(8,4) COMMENT '最大允许速度',
    max_reverse_velocity DECIMAL(8,4) COMMENT '最大反向速度',
    routing_type VARCHAR(50) DEFAULT 'BIDIRECTIONAL' COMMENT '路径方向类型',
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_blocked TINYINT(1) DEFAULT 0 COMMENT '是否被阻塞',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_path PRIMARY KEY (id),
    CONSTRAINT fk_path_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路径表';

-- ============================================================
-- 位置表 (Location)
-- ============================================================
CREATE TABLE location (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '归属导航地图ID',
    location_type_id BIGINT NOT NULL COMMENT '位置类型ID',
    location_id VARCHAR(255) NOT NULL COMMENT '位置唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '位置名称',
    position_x DECIMAL(12,4) COMMENT 'X坐标',
    position_y DECIMAL(12,4) COMMENT 'Y坐标',
    position_z DECIMAL(12,4) DEFAULT 0 COMMENT 'Z坐标',
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_occupied TINYINT(1) DEFAULT 0 COMMENT '是否被占用',
    properties JSON COMMENT '扩展属性',
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
    navigation_map_id BIGINT COMMENT '所属地图ID',
    name VARCHAR(255) NOT NULL COMMENT '区块名称',
    type VARCHAR(50) NOT NULL DEFAULT 'SINGLE' COMMENT '区块类型',
    members JSON COMMENT '成员点位的point_id列表',
    color VARCHAR(20) COMMENT '区块显示颜色',
    properties JSON COMMENT '扩展属性',
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
    name VARCHAR(255) NOT NULL COMMENT '连接名称',
    connection_type VARCHAR(50) NOT NULL COMMENT 'ELEVATOR/CONVEYOR/PHYSICAL_DOOR',
    source_navigation_map_id BIGINT NOT NULL COMMENT '源地图ID',
    source_point_id VARCHAR(255) NOT NULL COMMENT '源点位ID',
    source_floor INT NOT NULL COMMENT '源楼层',
    dest_navigation_map_id BIGINT NOT NULL COMMENT '目标地图ID',
    dest_point_id VARCHAR(255) NOT NULL COMMENT '目标点位ID',
    dest_floor INT NOT NULL COMMENT '目标楼层',
    capacity INT DEFAULT 1 COMMENT '容量',
    max_weight DECIMAL(10,2) COMMENT '最大承重',
    travel_time INT COMMENT '运行时间（秒）',
    available TINYINT(1) DEFAULT 1 COMMENT '是否可用',
    current_load INT DEFAULT 0 COMMENT '当前负载',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_cross_layer_connection PRIMARY KEY (id),
    CONSTRAINT fk_clc_factory FOREIGN KEY (factory_model_id) REFERENCES factory_model(id) ON DELETE CASCADE,
    CONSTRAINT fk_clc_source_map FOREIGN KEY (source_navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_clc_dest_map FOREIGN KEY (dest_navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE
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
    schedule_type VARCHAR(50) DEFAULT 'RESERVE' COMMENT '调度类型',
    pickup_time DATETIME COMMENT '预计接载时间',
    delivery_time DATETIME COMMENT '预计送达时间',
    actual_pickup_time DATETIME COMMENT '实际接载时间',
    actual_delivery_time DATETIME COMMENT '实际送达时间',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT '状态',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_elevator_schedule PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电梯调度记录表';

-- 创建索引
CREATE INDEX idx_point_navigation_map ON point(navigation_map_id);
CREATE INDEX idx_point_layer ON point(layer_id);
CREATE INDEX idx_path_navigation_map ON path(navigation_map_id);
CREATE INDEX idx_location_navigation_map ON location(navigation_map_id);
CREATE INDEX idx_block_factory ON block(factory_model_id);
CREATE INDEX idx_block_navigation_map ON block(navigation_map_id);
CREATE INDEX idx_clc_factory ON cross_layer_connection(factory_model_id);
CREATE INDEX idx_elevator_connection ON elevator_schedule(connection_id);
CREATE INDEX idx_elevator_vehicle ON elevator_schedule(vehicle_id);
CREATE INDEX idx_elevator_status ON elevator_schedule(status);
