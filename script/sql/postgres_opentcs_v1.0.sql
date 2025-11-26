-- OpenTCS 地图模型表 (核心表)
CREATE TABLE plant_model (
    id BIGSERIAL PRIMARY KEY,
    map_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE,
    model_version VARCHAR(50) NOT NULL DEFAULT '1.0',
    -- 状态管理
    status      char        default '0'::bpchar,
    properties JSONB,
    -- 审计字段
    create_dept BIGINT,
    create_by BIGINT,
    update_by BIGINT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    model_version VARCHAR(1000)  NULL ,
    del_flag      char        default '0'::bpchar
);

COMMENT ON TABLE plant_model IS 'OpenTCS地图模型表';
COMMENT ON COLUMN plant_model.map_id IS '地图模型唯一标识符';
COMMENT ON COLUMN plant_model.name IS '地图模型名称，唯一标识';
COMMENT ON COLUMN plant_model.model_version IS '模型版本';
COMMENT ON COLUMN plant_model.length_unit IS '长度单位：mm, cm, m';
COMMENT ON COLUMN plant_model.scale IS '比例尺';
COMMENT ON COLUMN plant_model.layout_width IS '布局宽度';
COMMENT ON COLUMN plant_model.layout_height IS '布局高度';
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
    create_dept BIGINT,
    create_by BIGINT,
    update_by BIGINT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_point_plant_model_name UNIQUE (id, name)
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
    create_dept BIGINT,
    create_by BIGINT,
    update_by BIGINT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    FOREIGN KEY (source_point_id) REFERENCES point(id) ON DELETE CASCADE,
    FOREIGN KEY (dest_point_id) REFERENCES point(id) ON DELETE CASCADE,
    CONSTRAINT uk_path_plant_model_name UNIQUE (id, name)
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
    create_dept BIGINT,
    create_by BIGINT,
    update_by BIGINT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_location_type_plant_model_name UNIQUE (id, name)
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
    create_dept BIGINT,
    create_by BIGINT,
    update_by BIGINT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    FOREIGN KEY (location_type_id) REFERENCES location_type(id) ON DELETE CASCADE,
    CONSTRAINT uk_location_plant_model_name UNIQUE (id, name)
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
    create_dept BIGINT,
    create_by BIGINT,
    update_by BIGINT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_block_plant_model_name UNIQUE (id, name)
);

COMMENT ON TABLE block IS '区块表';
COMMENT ON COLUMN block.name IS '区块名称';
COMMENT ON COLUMN block.type IS '区块类型：SINGLE, GROUP';
COMMENT ON COLUMN block.members IS '区块成员（点位、路径、位置等元素的名称集合）';
COMMENT ON COLUMN block.color IS '区块显示颜色';

CREATE TABLE visual_layout (
     id BIGSERIAL PRIMARY KEY,
     plant_model_id BIGINT NOT NULL UNIQUE,  -- 添加 UNIQUE 约束确保 1:1
     name VARCHAR(255) NOT NULL DEFAULT 'Default Layout',
     scale_x DECIMAL(10, 6) NOT NULL DEFAULT 50.0,
     scale_y DECIMAL(10, 6) NOT NULL DEFAULT 50.0,
    -- 扩展属性
    properties JSONB DEFAULT '{}',
    -- 审计字段
    create_dept BIGINT,
    create_by BIGINT,
    update_by BIGINT,
    create_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE
);

COMMENT ON TABLE visual_layout IS '视觉布局表，与地图模型 1:1 对应';
COMMENT ON COLUMN visual_layout.plant_model_id IS '关联的地图模型ID (1:1关系)';
COMMENT ON COLUMN visual_layout.name IS '视觉布局名称';
COMMENT ON COLUMN visual_layout.scale_x IS 'X轴缩放比例';
COMMENT ON COLUMN visual_layout.scale_y IS 'Y轴缩放比例';
COMMENT ON COLUMN visual_layout.properties IS '扩展属性';

CREATE TABLE layer_group (
    id BIGSERIAL PRIMARY KEY,
    visual_layout_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    -- 可视化属性
    visible BOOLEAN DEFAULT TRUE,
    ordinal INTEGER DEFAULT 0,
    -- 扩展属性
    properties JSONB,
    -- 审计字段
    create_dept BIGINT,
    create_by BIGINT,
    update_by BIGINT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visual_layout_id) REFERENCES visual_layout(id) ON DELETE CASCADE,
    CONSTRAINT uk_layer_group_visual_layout_name UNIQUE (visual_layout_id, name)
);

COMMENT ON TABLE layer_group IS '图层组表';
COMMENT ON COLUMN layer_group.visual_layout_id IS '关联的视觉布局ID';
COMMENT ON COLUMN layer_group.name IS '图层组名称';
COMMENT ON COLUMN layer_group.visible IS '是否可见';
COMMENT ON COLUMN layer_group.ordinal IS '显示顺序';
COMMENT ON COLUMN layer_group.properties IS '扩展属性';

CREATE TABLE layer (
     id BIGSERIAL PRIMARY KEY,
     visual_layout_id BIGINT NOT NULL,
     layer_group_id BIGINT,
     name VARCHAR(255) NOT NULL,
    -- 可视化属性
    visible BOOLEAN DEFAULT TRUE,
    ordinal INTEGER DEFAULT 0,
    -- 扩展属性
    properties JSONB,
    -- 审计字段
    create_dept BIGINT,
    create_by BIGINT,
    update_by BIGINT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visual_layout_id) REFERENCES visual_layout(id) ON DELETE CASCADE,
    FOREIGN KEY (layer_group_id) REFERENCES layer_group(id) ON DELETE SET NULL,
    CONSTRAINT uk_layer_visual_layout_name UNIQUE (visual_layout_id, name)
);

COMMENT ON TABLE layer IS '图层表';
COMMENT ON COLUMN layer.visual_layout_id IS '关联的视觉布局ID';
COMMENT ON COLUMN layer.layer_group_id IS '关联的图层组ID（可为空，表示不属于任何组）';
COMMENT ON COLUMN layer.name IS '图层名称';
COMMENT ON COLUMN layer.visible IS '是否可见';
COMMENT ON COLUMN layer.ordinal IS '显示顺序';
COMMENT ON COLUMN layer.properties IS '扩展属性';