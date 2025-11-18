-- OpenTCS 地图模型表 (核心表)
CREATE TABLE plant_model (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    model_version VARCHAR(50) NOT NULL DEFAULT '1.0',
    -- 地图属性
    length_unit VARCHAR(20) DEFAULT 'mm',
    scale DECIMAL(10, 6) DEFAULT 1.000000,
    layout_width DECIMAL(12, 4) DEFAULT 0.0000,
    layout_height DECIMAL(12, 4) DEFAULT 0.0000,
    -- 状态管理
    model_state VARCHAR(20) DEFAULT 'UNLOADED',
    properties JSONB,
    -- 审计字段
    created_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

COMMENT ON TABLE plant_model IS 'OpenTCS地图模型表';
COMMENT ON COLUMN plant_model.name IS '地图模型名称，唯一标识';
COMMENT ON COLUMN plant_model.model_version IS '模型版本';
COMMENT ON COLUMN plant_model.length_unit IS '长度单位：mm, cm, m';
COMMENT ON COLUMN plant_model.scale IS '比例尺';
COMMENT ON COLUMN plant_model.layout_width IS '布局宽度';
COMMENT ON COLUMN plant_model.layout_height IS '布局高度';
COMMENT ON COLUMN plant_model.model_state IS '地图状态：UNLOADED, LOADING, LOADED, LOCKED, ERROR';
COMMENT ON COLUMN plant_model.properties IS '扩展属性';

-- 点位表 (Point)
CREATE TABLE point (
    id BIGSERIAL PRIMARY KEY,
    plant_model_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    -- 坐标信息
    x_position DECIMAL(12, 4) NOT NULL,
    y_position DECIMAL(12, 4) NOT NULL,
    z_position DECIMAL(12, 4) DEFAULT 0.0000,
    vehicle_orientation DECIMAL(8, 4) DEFAULT 0.0000,
    -- 点位属性
    type VARCHAR(50) NOT NULL DEFAULT 'HALT_POSITION',
    radius DECIMAL(8, 4) DEFAULT 0.0000,
    -- 状态管理
    locked BOOLEAN DEFAULT FALSE,
    is_blocked BOOLEAN DEFAULT FALSE,
    is_occupied BOOLEAN DEFAULT FALSE,
    -- 扩展信息
    label VARCHAR(500),
    properties JSONB,
    -- 审计字段
    created_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_point_plant_model_name UNIQUE (plant_model_id, name)
);

COMMENT ON TABLE point IS '点位表';
COMMENT ON COLUMN point.name IS '点位名称';
COMMENT ON COLUMN point.x_position IS 'X坐标';
COMMENT ON COLUMN point.y_position IS 'Y坐标';
COMMENT ON COLUMN point.z_position IS 'Z坐标';
COMMENT ON COLUMN point.vehicle_orientation IS '车辆方向角度（弧度）';
COMMENT ON COLUMN point.type IS '点位类型：HALT_POSITION, PARK_POSITION, REPORT_POSITION';
COMMENT ON COLUMN point.radius IS '点位半径';
COMMENT ON COLUMN point.locked IS '是否被锁定';
COMMENT ON COLUMN point.is_blocked IS '是否被阻塞';
COMMENT ON COLUMN point.is_occupied IS '是否被占用';

-- 路径表 (Path)
CREATE TABLE path (
     id BIGSERIAL PRIMARY KEY,
     plant_model_id BIGINT NOT NULL,
     name VARCHAR(255) NOT NULL,
     source_point_id BIGINT NOT NULL,
     dest_point_id BIGINT NOT NULL,
    -- 路径属性
     length DECIMAL(12, 4) NOT NULL,
     max_velocity DECIMAL(8, 4),
     max_reverse_velocity DECIMAL(8, 4),
     routing_type VARCHAR(50) DEFAULT 'BIDIRECTIONAL',
    -- 状态管理
     locked BOOLEAN DEFAULT FALSE,
     is_blocked BOOLEAN DEFAULT FALSE,
    -- 扩展信息
     properties JSONB,
    -- 审计字段
     created_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     updated_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
     FOREIGN KEY (source_point_id) REFERENCES point(id) ON DELETE CASCADE,
     FOREIGN KEY (dest_point_id) REFERENCES point(id) ON DELETE CASCADE,
     CONSTRAINT uk_path_plant_model_name UNIQUE (plant_model_id, name)
);

COMMENT ON TABLE path IS '路径表';
COMMENT ON COLUMN path.name IS '路径名称';
COMMENT ON COLUMN path.source_point_id IS '起始点位ID';
COMMENT ON COLUMN path.dest_point_id IS '目标点位ID';
COMMENT ON COLUMN path.length IS '路径长度';
COMMENT ON COLUMN path.max_velocity IS '最大允许速度';
COMMENT ON COLUMN path.max_reverse_velocity IS '最大反向速度';
COMMENT ON COLUMN path.routing_type IS '路径方向类型：BIDIRECTIONAL, FORWARD, BACKWARD';
COMMENT ON COLUMN path.locked IS '是否被锁定';
COMMENT ON COLUMN path.is_blocked IS '是否被阻塞';

-- 位置类型表 (LocationType)
CREATE TABLE location_type (
     id BIGSERIAL PRIMARY KEY,
     plant_model_id BIGINT NOT NULL,
     name VARCHAR(255) NOT NULL,
    -- 操作权限
     allowed_operations JSONB,
     allowed_peripheral_operations JSONB,
    -- 扩展属性
     properties JSONB,
    -- 审计字段
     created_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     updated_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
     CONSTRAINT uk_location_type_plant_model_name UNIQUE (plant_model_id, name)
);

COMMENT ON TABLE location_type IS '位置类型表';
COMMENT ON COLUMN location_type.name IS '位置类型名称';
COMMENT ON COLUMN location_type.allowed_operations IS '允许的操作列表：LOAD, UNLOAD, NOP等';
COMMENT ON COLUMN location_type.allowed_peripheral_operations IS '允许的外围设备操作';

-- 位置表 (Location)
CREATE TABLE location (
     id BIGSERIAL PRIMARY KEY,
     plant_model_id BIGINT NOT NULL,
     location_type_id BIGINT NOT NULL,
     name VARCHAR(255) NOT NULL,
    -- 坐标信息
     x_position DECIMAL(12, 4) NOT NULL,
     y_position DECIMAL(12, 4) NOT NULL,
     z_position DECIMAL(12, 4) DEFAULT 0.0000,
     vehicle_orientation DECIMAL(8, 4) DEFAULT 0.0000,
    -- 状态管理
     locked BOOLEAN DEFAULT FALSE,
     is_occupied BOOLEAN DEFAULT FALSE,
    -- 扩展信息
     label VARCHAR(500),
     properties JSONB,
    -- 审计字段
     created_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     updated_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
     FOREIGN KEY (location_type_id) REFERENCES location_type(id) ON DELETE CASCADE,
     CONSTRAINT uk_location_plant_model_name UNIQUE (plant_model_id, name)
);

COMMENT ON TABLE location IS '位置表';
COMMENT ON COLUMN location.name IS '位置名称';
COMMENT ON COLUMN location.x_position IS 'X坐标';
COMMENT ON COLUMN location.y_position IS 'Y坐标';
COMMENT ON COLUMN location.z_position IS 'Z坐标';
COMMENT ON COLUMN location.vehicle_orientation IS '车辆方向';
COMMENT ON COLUMN location.locked IS '是否被锁定';
COMMENT ON COLUMN location.is_occupied IS '是否被占用';

-- 区块表 (Block)
CREATE TABLE block (
     id BIGSERIAL PRIMARY KEY,
     plant_model_id BIGINT NOT NULL,
     name VARCHAR(255) NOT NULL,
     type VARCHAR(50) NOT NULL DEFAULT 'SINGLE',
    -- 区块成员
     members JSONB,
    -- 区块属性
     color VARCHAR(20),
     properties JSONB,
    -- 审计字段
     created_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     updated_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
     CONSTRAINT uk_block_plant_model_name UNIQUE (plant_model_id, name)
);

COMMENT ON TABLE block IS '区块表';
COMMENT ON COLUMN block.name IS '区块名称';
COMMENT ON COLUMN block.type IS '区块类型：SINGLE, GROUP';
COMMENT ON COLUMN block.members IS '区块成员（点位、路径、位置等元素的名称集合）';
COMMENT ON COLUMN block.color IS '区块显示颜色';

-- 车辆类型表 (VehicleType)
CREATE TABLE vehicle_type (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL UNIQUE,
    -- 车辆尺寸
     length DECIMAL(8, 4) NOT NULL,
     width DECIMAL(8, 4) NOT NULL,
     height DECIMAL(8, 4) NOT NULL,
    -- 性能参数
    max_velocity DECIMAL(8, 4) NOT NULL,
    max_reverse_velocity DECIMAL(8, 4),
    energy_level DECIMAL(8, 4),
    -- 操作能力
    allowed_orders JSONB,
    allowed_peripheral_operations JSONB,
    -- 扩展属性
    properties JSONB,
    -- 审计字段
    created_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE vehicle_type IS '车辆类型表';
COMMENT ON COLUMN vehicle_type.name IS '车辆类型名称';
COMMENT ON COLUMN vehicle_type.length IS '车辆长度';
COMMENT ON COLUMN vehicle_type.width IS '车辆宽度';
COMMENT ON COLUMN vehicle_type.height IS '车辆高度';
COMMENT ON COLUMN vehicle_type.max_velocity IS '最大速度';
COMMENT ON COLUMN vehicle_type.max_reverse_velocity IS '最大反向速度';
COMMENT ON COLUMN vehicle_type.energy_level IS '能量级别';

-- 车辆表 (Vehicle)
CREATE TABLE vehicle (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    vehicle_type_id BIGINT NOT NULL,
    -- 车辆状态
    current_position VARCHAR(255),
    next_position VARCHAR(255),
    state VARCHAR(50) DEFAULT 'UNKNOWN',
    integration_level VARCHAR(50) DEFAULT 'TO_BE_IGNORED',
    -- 能源和进程
    energy_level DECIMAL(8, 4),
    current_transport_order VARCHAR(255),
    -- 扩展属性
    properties JSONB,
    -- 审计字段
    created_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_type_id) REFERENCES vehicle_type(id)
);

COMMENT ON TABLE vehicle IS '车辆表';
COMMENT ON COLUMN vehicle.name IS '车辆名称';
COMMENT ON COLUMN vehicle.state IS '车辆状态：UNKNOWN, UNAVAILABLE, IDLE, CHARGING, WORKING, ERROR';
COMMENT ON COLUMN vehicle.integration_level IS '集成级别：TO_BE_IGNORED, TO_BE_NOTICED, TO_BE_RESPECTED, TO_BE_UTILIZED';
COMMENT ON COLUMN vehicle.energy_level IS '能量级别';
COMMENT ON COLUMN vehicle.current_transport_order IS '当前运输订单';

-- 运输订单表 (TransportOrder)
CREATE TABLE transport_order (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL UNIQUE,
    -- 订单状态
     state VARCHAR(50) DEFAULT 'RAW',
     intended_vehicle VARCHAR(255),
     processing_vehicle VARCHAR(255),
    -- 订单内容
     destinations JSONB NOT NULL,
    -- 时间管理
     creation_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
     finished_time TIMESTAMPTZ,
     deadline TIMESTAMPTZ,
    -- 扩展属性
     properties JSONB,
     FOREIGN KEY (intended_vehicle) REFERENCES vehicle(name),
     FOREIGN KEY (processing_vehicle) REFERENCES vehicle(name)
);

COMMENT ON TABLE transport_order IS '运输订单表';
COMMENT ON COLUMN transport_order.name IS '订单名称';
COMMENT ON COLUMN transport_order.state IS '订单状态：RAW, ACTIVE, FINISHED, FAILED';
COMMENT ON COLUMN transport_order.destinations IS '目的地序列';
COMMENT ON COLUMN transport_order.creation_time IS '创建时间';
COMMENT ON COLUMN transport_order.finished_time IS '完成时间';
COMMENT ON COLUMN transport_order.deadline IS '截止时间';