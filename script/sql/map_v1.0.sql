# 建筑物
CREATE TABLE building (
     id BIGSERIAL PRIMARY KEY,                          -- 建筑ID
     code VARCHAR(50) NOT NULL,                         -- 建筑编码（如：HOSPITAL_01）
     name VARCHAR(100) NOT NULL,                        -- 建筑名称（如：住院部）
     type VARCHAR(20) NOT NULL DEFAULT 'MEDICAL',       -- 建筑类型：MEDICAL-医疗, ADMIN-行政, TECH-医技
     address VARCHAR(200),                              -- 建筑地址
     total_floors INTEGER NOT NULL,                     -- 总楼层数
     underground_floors INTEGER DEFAULT 0,              -- 地下楼层数
     description TEXT,                                  -- 建筑描述
     properties JSONB,                                  -- 扩展属性（如：建筑高度、建筑面积等）
     is_active BOOLEAN NOT NULL DEFAULT true,           -- 是否激活
     created_by VARCHAR(50),                            -- 创建人
     created_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 创建时间
     updated_by VARCHAR(50),                            -- 更新人
     updated_time TIMESTAMPTZ NOT NULL DEFAULT NOW()    -- 更新时间
);

COMMENT ON TABLE building IS '建筑信息表';
COMMENT ON COLUMN building.id IS '建筑ID，主键自增';
COMMENT ON COLUMN building.code IS '建筑编码，唯一标识符，如：HOSPITAL_01';
COMMENT ON COLUMN building.name IS '建筑名称，如：住院部、门诊部';
COMMENT ON COLUMN building.type IS '建筑类型：MEDICAL-医疗建筑, ADMIN-行政建筑, TECH-医技建筑, OTHER-其他';
COMMENT ON COLUMN building.address IS '建筑具体地址';
COMMENT ON COLUMN building.total_floors IS '建筑总楼层数';
COMMENT ON COLUMN building.underground_floors IS '地下楼层数量';
COMMENT ON COLUMN building.description IS '建筑详细描述';
COMMENT ON COLUMN building.properties IS '扩展属性，JSON格式，存储建筑高度、建筑面积等自定义属性';
COMMENT ON COLUMN building.is_active IS '是否激活状态，false表示停用';
COMMENT ON COLUMN building.created_by IS '记录创建人';
COMMENT ON COLUMN building.created_time IS '记录创建时间';
COMMENT ON COLUMN building.updated_by IS '最后更新人';
COMMENT ON COLUMN building.updated_time IS '最后更新时间';

# 楼层
CREATE TABLE floor (
     id BIGSERIAL PRIMARY KEY,                          -- 楼层ID
     building_id BIGINT NOT NULL,                       -- 所属建筑ID
     floor_number INTEGER NOT NULL,                     -- 楼层数字编号（-1, 0, 1, 2...）
     floor_code VARCHAR(20) NOT NULL,                   -- 楼层编码（B1, 1F, 2F）
     floor_name VARCHAR(100) NOT NULL,                  -- 楼层名称（如：一楼门诊区）
     floor_type VARCHAR(20) NOT NULL DEFAULT 'GENERAL', -- 楼层类型：GENERAL-普通, UNDERGROUND-地下, SPECIAL-特殊
     description TEXT,                                  -- 楼层描述
     layout_width DECIMAL(10,2) NOT NULL,               -- 楼层布局宽度（米）
     layout_height DECIMAL(10,2) NOT NULL,              -- 楼层布局高度（米）
     scale DECIMAL(10,4) NOT NULL DEFAULT 1.0000,       -- 楼层比例尺
     properties JSONB,                                  -- 扩展属性
     is_active BOOLEAN NOT NULL DEFAULT true,           -- 是否激活
     created_by VARCHAR(50),                            -- 创建人
     created_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 创建时间
     updated_by VARCHAR(50),                            -- 更新人
     updated_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 更新时间
     FOREIGN KEY (building_id) REFERENCES building(id) ON DELETE CASCADE
);

COMMENT ON TABLE floor IS '楼层信息表';
COMMENT ON COLUMN floor.id IS '楼层ID，主键自增';
COMMENT ON COLUMN floor.building_id IS '所属建筑ID，外键关联building表';
COMMENT ON COLUMN floor.floor_number IS '楼层数字编号，负数表示地下楼层，0表示地面层，正数表示地上楼层';
COMMENT ON COLUMN floor.floor_code IS '楼层编码，如：B1、1F、2F，用于显示';
COMMENT ON COLUMN floor.floor_name IS '楼层名称，如：一楼门诊区、二楼住院区';
COMMENT ON COLUMN floor.floor_type IS '楼层类型：GENERAL-普通楼层, UNDERGROUND-地下楼层, SPECIAL-特殊楼层（如手术层）';
COMMENT ON COLUMN floor.description IS '楼层功能描述';
COMMENT ON COLUMN floor.layout_width IS '楼层布局宽度，单位：米';
COMMENT ON COLUMN floor.layout_height IS '楼层布局高度，单位：米';
COMMENT ON COLUMN floor.scale IS '楼层地图比例尺';
COMMENT ON COLUMN floor.properties IS '扩展属性，JSON格式，存储楼层特殊属性';
COMMENT ON COLUMN floor.is_active IS '是否激活状态';
COMMENT ON COLUMN floor.created_by IS '记录创建人';
COMMENT ON COLUMN floor.created_time IS '记录创建时间';
COMMENT ON COLUMN floor.updated_by IS '最后更新人';
COMMENT ON COLUMN floor.updated_time IS '最后更新时间';

-- 索引
CREATE INDEX idx_floor_building ON floor(building_id);
CREATE INDEX idx_floor_number ON floor(floor_number);
CREATE UNIQUE INDEX uk_floor_building_code ON floor(building_id, floor_code);

# 地图模型 (PlantModel)
CREATE TABLE plant_model (
     id BIGSERIAL PRIMARY KEY,                          -- 地图ID
     building_id BIGINT NOT NULL,                       -- 所属建筑ID
     floor_id BIGINT NOT NULL,                          -- 所属楼层ID
     name VARCHAR(100) NOT NULL,                        -- 地图名称
     model_version VARCHAR(50) NOT NULL DEFAULT '1.0',  -- 地图版本
     scale DECIMAL(10,4) NOT NULL DEFAULT 1.0000,       -- 地图比例尺
     layout_width DECIMAL(10,2) NOT NULL,               -- 地图布局宽度（米）
     layout_height DECIMAL(10,2) NOT NULL,              -- 地图布局高度（米）
     crs VARCHAR(100) DEFAULT 'Local',                  -- 坐标参考系统
     bounds GEOMETRY(POLYGON,4326),                     -- 地图边界
     model_state VARCHAR(20) DEFAULT 'UNLOADED',        -- 地图状态
     description TEXT,                                  -- 地图描述
     properties JSONB,                                  -- 扩展属性
     created_by VARCHAR(50),                            -- 创建人
     created_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 创建时间
     updated_by VARCHAR(50),                            -- 更新人
     updated_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 更新时间
     version BIGINT DEFAULT 0,                          -- 版本号（乐观锁）
     FOREIGN KEY (building_id) REFERENCES building(id) ON DELETE CASCADE,
     FOREIGN KEY (floor_id) REFERENCES floor(id) ON DELETE CASCADE
);

COMMENT ON TABLE plant_model IS '地图模型表，每个楼层对应一个地图模型';
COMMENT ON COLUMN plant_model.id IS '地图ID，主键自增';
COMMENT ON COLUMN plant_model.building_id IS '所属建筑ID，外键关联building表';
COMMENT ON COLUMN plant_model.floor_id IS '所属楼层ID，外键关联floor表';
COMMENT ON COLUMN plant_model.name IS '地图名称，如：住院部一楼地图';
COMMENT ON COLUMN plant_model.model_version IS '地图版本号，用于版本管理';
COMMENT ON COLUMN plant_model.scale IS '地图比例尺';
COMMENT ON COLUMN plant_model.layout_width IS '地图布局宽度，单位：米';
COMMENT ON COLUMN plant_model.layout_height IS '地图布局高度，单位：米';
COMMENT ON COLUMN plant_model.crs IS '坐标参考系统，默认Local表示局部坐标系';
COMMENT ON COLUMN plant_model.bounds IS '地图边界范围，PostGIS多边形几何类型';
COMMENT ON COLUMN plant_model.model_state IS '地图状态：UNLOADED-未加载, LOADING-加载中, LOADED-已加载, LOCKED-锁定, ERROR-错误';
COMMENT ON COLUMN plant_model.description IS '地图详细描述';
COMMENT ON COLUMN plant_model.properties IS '扩展属性，JSON格式，存储地图自定义属性';
COMMENT ON COLUMN plant_model.created_by IS '记录创建人';
COMMENT ON COLUMN plant_model.created_time IS '记录创建时间';
COMMENT ON COLUMN plant_model.updated_by IS '最后更新人';
COMMENT ON COLUMN plant_model.updated_time IS '最后更新时间';
COMMENT ON COLUMN plant_model.version IS '版本号，用于乐观锁控制';

-- 索引
CREATE INDEX idx_plant_model_building ON plant_model(building_id);
CREATE INDEX idx_plant_model_floor ON plant_model(floor_id);
CREATE INDEX idx_plant_model_state ON plant_model(model_state);
CREATE INDEX idx_plant_model_bounds ON plant_model USING GIST (bounds);

# 扩展的点位模型 (Point)
CREATE TABLE point (
     id BIGSERIAL PRIMARY KEY,                          -- 点位ID
     plant_model_id BIGINT NOT NULL,                    -- 所属地图ID
     name VARCHAR(100) NOT NULL,                        -- 点位名称
     code VARCHAR(50) NOT NULL,                         -- 点位编码
     point_type VARCHAR(20) NOT NULL DEFAULT 'HALT_POSITION', -- 点位类型
     position GEOMETRY(POINT,4326) NOT NULL,            -- 点位位置
     x_position DECIMAL(10,4) NOT NULL,                 -- X坐标
     y_position DECIMAL(10,4) NOT NULL,                 -- Y坐标
     z_position DECIMAL(10,4) DEFAULT 0.0000,           -- Z坐标
     vehicle_orientation DECIMAL(5,2) DEFAULT 0.00,     -- 车辆方向
     radius DECIMAL(6,3) DEFAULT 0.000,                 -- 点位半径
     functional_type VARCHAR(50),                       -- 功能类型
     room_number VARCHAR(50),                           -- 房间号
     department VARCHAR(100),                           -- 所属科室
     properties JSONB,                                  -- 扩展属性
     is_locked BOOLEAN NOT NULL DEFAULT false,          -- 是否锁定
     lock_holder VARCHAR(100),                          -- 锁定持有者
     occupied_by VARCHAR(100),                          -- 被占用车辆
     occupied_time TIMESTAMPTZ,                         -- 占用时间
     created_by VARCHAR(50),                            -- 创建人
     created_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 创建时间
     updated_by VARCHAR(50),                            -- 更新人
     updated_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 更新时间
     version BIGINT DEFAULT 0,                          -- 版本号
     FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE
);

COMMENT ON TABLE point IS '点位信息表';
COMMENT ON COLUMN point.id IS '点位ID，主键自增';
COMMENT ON COLUMN point.plant_model_id IS '所属地图ID，外键关联plant_model表';
COMMENT ON COLUMN point.name IS '点位名称，如：护士站点位、药房点位';
COMMENT ON COLUMN point.code IS '点位编码，唯一标识符，如：POINT_001';
COMMENT ON COLUMN point.point_type IS '点位类型：HALT_POSITION-停车点, REPORT_POSITION-报告点, PARKING_POSITION-停车位, CHARGING_POSITION-充电点, LOADING_POSITION-装载点, UNLOADING_POSITION-卸载点, ELEVATOR_WAIT-电梯等待点';
COMMENT ON COLUMN point.position IS '点位几何位置，PostGIS点类型';
COMMENT ON COLUMN point.x_position IS 'X坐标，单位：米';
COMMENT ON COLUMN point.y_position IS 'Y坐标，单位：米';
COMMENT ON COLUMN point.z_position IS 'Z坐标，单位：米，用于三维场景';
COMMENT ON COLUMN point.vehicle_orientation IS '车辆方向角度，单位：弧度';
COMMENT ON COLUMN point.radius IS '点位半径，单位：米，用于碰撞检测';
COMMENT ON COLUMN point.functional_type IS '功能类型：RECEPTION-接待台, NURSE_STATION-护士站, PHARMACY-药房, CONSULTATION_ROOM-诊室, WARD-病房';
COMMENT ON COLUMN point.room_number IS '房间号码，如：301、502';
COMMENT ON COLUMN point.department IS '所属科室，如：内科、外科、急诊科';
COMMENT ON COLUMN point.properties IS '扩展属性，JSON格式，存储点位自定义属性';
COMMENT ON COLUMN point.is_locked IS '是否被锁定，锁定后不能被占用';
COMMENT ON COLUMN point.lock_holder IS '锁定持有者，记录锁定该点位的用户或系统';
COMMENT ON COLUMN point.occupied_by IS '被哪个车辆占用';
COMMENT ON COLUMN point.occupied_time IS '占用开始时间';
COMMENT ON COLUMN point.created_by IS '记录创建人';
COMMENT ON COLUMN point.created_time IS '记录创建时间';
COMMENT ON COLUMN point.updated_by IS '最后更新人';
COMMENT ON COLUMN point.updated_time IS '最后更新时间';
COMMENT ON COLUMN point.version IS '版本号，用于乐观锁控制';

-- 索引
CREATE INDEX idx_point_plant_model ON point(plant_model_id);
CREATE INDEX idx_point_position ON point USING GIST (position);
CREATE INDEX idx_point_type ON point(point_type);
CREATE INDEX idx_point_functional_type ON point(functional_type);
CREATE INDEX idx_point_department ON point(department);
CREATE INDEX idx_point_locked ON point(is_locked);
CREATE UNIQUE INDEX uk_point_plant_model_code ON point(plant_model_id, code);

# 扩展的路径模型 (Path)
CREATE TABLE path (
     id BIGSERIAL PRIMARY KEY,                          -- 路径ID
     plant_model_id BIGINT NOT NULL,                    -- 所属地图ID
     name VARCHAR(100) NOT NULL,                        -- 路径名称
     code VARCHAR(50) NOT NULL,                         -- 路径编码
     source_point_id BIGINT NOT NULL,                   -- 起始点位ID
     dest_point_id BIGINT NOT NULL,                     -- 目标点位ID
     trajectory GEOMETRY(LINESTRING,4326),              -- 路径轨迹
     length DECIMAL(10,4) NOT NULL,                     -- 路径长度
     routing_type VARCHAR(20) NOT NULL DEFAULT 'BIDIRECTIONAL', -- 路径方向类型
     path_type VARCHAR(20) NOT NULL DEFAULT 'CORRIDOR', -- 路径类型
     max_velocity DECIMAL(6,3),                         -- 最大速度
     max_reverse_velocity DECIMAL(6,3),                 -- 最大反向速度
     min_width DECIMAL(6,3),                            -- 最小宽度
     max_slope DECIMAL(5,2),                            -- 最大坡度
     access_level VARCHAR(20) DEFAULT 'PUBLIC',         -- 访问权限级别
     properties JSONB,                                  -- 扩展属性
     is_locked BOOLEAN NOT NULL DEFAULT false,          -- 是否锁定
     lock_holder VARCHAR(100),                          -- 锁定持有者
     created_by VARCHAR(50),                            -- 创建人
     created_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 创建时间
     updated_by VARCHAR(50),                            -- 更新人
     updated_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 更新时间
     version BIGINT DEFAULT 0,                          -- 版本号
     FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
     FOREIGN KEY (source_point_id) REFERENCES point(id) ON DELETE CASCADE,
     FOREIGN KEY (dest_point_id) REFERENCES point(id) ON DELETE CASCADE
);

COMMENT ON TABLE path IS '路径信息表';
COMMENT ON COLUMN path.id IS '路径ID，主键自增';
COMMENT ON COLUMN path.plant_model_id IS '所属地图ID，外键关联plant_model表';
COMMENT ON COLUMN path.name IS '路径名称，如：主走廊路径、电梯间通道';
COMMENT ON COLUMN path.code IS '路径编码，唯一标识符，如：PATH_001';
COMMENT ON COLUMN path.source_point_id IS '起始点位ID，外键关联point表';
COMMENT ON COLUMN path.dest_point_id IS '目标点位ID，外键关联point表';
COMMENT ON COLUMN path.trajectory IS '路径几何轨迹，PostGIS线串类型';
COMMENT ON COLUMN path.length IS '路径长度，单位：米';
COMMENT ON COLUMN path.routing_type IS '路径方向类型：BIDIRECTIONAL-双向, FORWARD-正向单向, BACKWARD-反向单向';
COMMENT ON COLUMN path.path_type IS '路径类型：CORRIDOR-走廊, STAIRCASE-楼梯, ELEVATOR-电梯, ESCALATOR-扶梯, DOORWAY-门口, HALLWAY-过道';
COMMENT ON COLUMN path.max_velocity IS '最大允许速度，单位：米/秒';
COMMENT ON COLUMN path.max_reverse_velocity IS '最大反向速度，单位：米/秒';
COMMENT ON COLUMN path.min_width IS '路径最小宽度，单位：米';
COMMENT ON COLUMN path.max_slope IS '路径最大坡度，单位：度';
COMMENT ON COLUMN path.access_level IS '访问权限级别：PUBLIC-公共区域, RESTRICTED-限制区域, STAFF_ONLY-员工专用, PATIENT_ONLY-患者专用';
COMMENT ON COLUMN path.properties IS '扩展属性，JSON格式，存储路径自定义属性';
COMMENT ON COLUMN path.is_locked IS '是否被锁定，锁定后不能通行';
COMMENT ON COLUMN path.lock_holder IS '锁定持有者';
COMMENT ON COLUMN path.created_by IS '记录创建人';
COMMENT ON COLUMN path.created_time IS '记录创建时间';
COMMENT ON COLUMN path.updated_by IS '最后更新人';
COMMENT ON COLUMN path.updated_time IS '最后更新时间';
COMMENT ON COLUMN path.version IS '版本号，用于乐观锁控制';

-- 索引
CREATE INDEX idx_path_plant_model ON path(plant_model_id);
CREATE INDEX idx_path_source_point ON path(source_point_id);
CREATE INDEX idx_path_dest_point ON path(dest_point_id);
CREATE INDEX idx_path_trajectory ON path USING GIST (trajectory);
CREATE INDEX idx_path_type ON path(path_type);
CREATE INDEX idx_path_routing_type ON path(routing_type);
CREATE UNIQUE INDEX uk_path_plant_model_code ON path(plant_model_id, code);

# 跨楼层连接模型 (FloorConnection)
CREATE TABLE floor_connection (
     id BIGSERIAL PRIMARY KEY,                          -- 连接ID
     name VARCHAR(100) NOT NULL,                        -- 连接名称
     code VARCHAR(50) NOT NULL,                         -- 连接编码
     connection_type VARCHAR(20) NOT NULL,              -- 连接类型
     from_plant_model_id BIGINT NOT NULL,               -- 起始地图ID
     from_point_id BIGINT NOT NULL,                     -- 起始点位ID
     to_plant_model_id BIGINT NOT NULL,                 -- 目标地图ID
     to_point_id BIGINT NOT NULL,                       -- 目标点位ID
     travel_time INTEGER,                               -- 通行时间（秒）
     capacity INTEGER DEFAULT 1,                        -- 同时通行容量
     current_usage INTEGER DEFAULT 0,                   -- 当前使用量
     access_level VARCHAR(20) DEFAULT 'PUBLIC',         -- 访问权限
     is_active BOOLEAN NOT NULL DEFAULT true,           -- 是否激活
     properties JSONB,                                  -- 扩展属性
     created_by VARCHAR(50),                            -- 创建人
     created_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 创建时间
     updated_by VARCHAR(50),                            -- 更新人
     updated_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 更新时间
     FOREIGN KEY (from_plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
     FOREIGN KEY (from_point_id) REFERENCES point(id) ON DELETE CASCADE,
     FOREIGN KEY (to_plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE,
     FOREIGN KEY (to_point_id) REFERENCES point(id) ON DELETE CASCADE
);

COMMENT ON TABLE floor_connection IS '跨楼层连接表，用于连接不同楼层的点位';
COMMENT ON COLUMN floor_connection.id IS '连接ID，主键自增';
COMMENT ON COLUMN floor_connection.name IS '连接名称，如：1号电梯、主楼梯';
COMMENT ON COLUMN floor_connection.code IS '连接编码，唯一标识符，如：ELEVATOR_01';
COMMENT ON COLUMN floor_connection.connection_type IS '连接类型：ELEVATOR-电梯, STAIRCASE-楼梯, ESCALATOR-扶梯, LIFT-升降机, RAMP-坡道';
COMMENT ON COLUMN floor_connection.from_plant_model_id IS '起始楼层地图ID，外键关联plant_model表';
COMMENT ON COLUMN floor_connection.from_point_id IS '起始点位ID，外键关联point表';
COMMENT ON COLUMN floor_connection.to_plant_model_id IS '目标楼层地图ID，外键关联plant_model表';
COMMENT ON COLUMN floor_connection.to_point_id IS '目标点位ID，外键关联point表';
COMMENT ON COLUMN floor_connection.travel_time IS '通行时间估计，单位：秒';
COMMENT ON COLUMN floor_connection.capacity IS '同时通行容量，单位：辆';
COMMENT ON COLUMN floor_connection.current_usage IS '当前使用量';
COMMENT ON COLUMN floor_connection.access_level IS '访问权限级别：PUBLIC-公共, STAFF_ONLY-员工专用, EMERGENCY_ONLY-紧急专用';
COMMENT ON COLUMN floor_connection.is_active IS '是否激活状态';
COMMENT ON COLUMN floor_connection.properties IS '扩展属性，JSON格式，存储连接特殊属性';
COMMENT ON COLUMN floor_connection.created_by IS '记录创建人';
COMMENT ON COLUMN floor_connection.created_time IS '记录创建时间';
COMMENT ON COLUMN floor_connection.updated_by IS '最后更新人';
COMMENT ON COLUMN floor_connection.updated_time IS '最后更新时间';

-- 索引
CREATE INDEX idx_floor_connection_from_plant ON floor_connection(from_plant_model_id);
CREATE INDEX idx_floor_connection_to_plant ON floor_connection(to_plant_model_id);
CREATE INDEX idx_floor_connection_type ON floor_connection(connection_type);
CREATE INDEX idx_floor_connection_active ON floor_connection(is_active);

# 功能区模型 (FunctionArea)
CREATE TABLE function_area (
     id BIGSERIAL PRIMARY KEY,                          -- 区域ID
     plant_model_id BIGINT NOT NULL,                    -- 所属地图ID
     name VARCHAR(100) NOT NULL,                        -- 区域名称
     code VARCHAR(50) NOT NULL,                         -- 区域编码
     area_type VARCHAR(20) NOT NULL,                    -- 区域类型
     boundary GEOMETRY(POLYGON,4326) NOT NULL,          -- 区域边界
     department VARCHAR(100),                           -- 所属科室
     functional_description TEXT,                       -- 功能描述
     access_level VARCHAR(20) DEFAULT 'PUBLIC',         -- 访问权限
     max_capacity INTEGER,                              -- 最大容量
     current_occupancy INTEGER DEFAULT 0,               -- 当前占用数
     properties JSONB,                                  -- 扩展属性
     is_active BOOLEAN NOT NULL DEFAULT true,           -- 是否激活
     created_by VARCHAR(50),                            -- 创建人
     created_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 创建时间
     updated_by VARCHAR(50),                            -- 更新人
     updated_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),   -- 更新时间
     FOREIGN KEY (plant_model_id) REFERENCES plant_model(id) ON DELETE CASCADE
);

COMMENT ON TABLE function_area IS '功能区信息表，用于划分医院内的功能区域';
COMMENT ON COLUMN function_area.id IS '区域ID，主键自增';
COMMENT ON COLUMN function_area.plant_model_id IS '所属地图ID，外键关联plant_model表';
COMMENT ON COLUMN function_area.name IS '区域名称，如：门诊区、住院区、急诊区';
COMMENT ON COLUMN function_area.code IS '区域编码，唯一标识符，如：AREA_OPD';
COMMENT ON COLUMN function_area.area_type IS '区域类型：OUTPATIENT-门诊, INPATIENT-住院, EMERGENCY-急诊, SURGERY-手术, PHARMACY-药房, ADMIN-行政, RESTRICTED-限制区域';
COMMENT ON COLUMN function_area.boundary IS '区域几何边界，PostGIS多边形类型';
COMMENT ON COLUMN function_area.department IS '所属科室，如：内科、外科、儿科';
COMMENT ON COLUMN function_area.functional_description IS '功能区详细描述';
COMMENT ON COLUMN function_area.access_level IS '访问权限级别：PUBLIC-公共, RESTRICTED-限制, STAFF_ONLY-员工专用, AUTHORIZED_ONLY-授权进入';
COMMENT ON COLUMN function_area.max_capacity IS '区域最大容量，单位：人或车辆';
COMMENT ON COLUMN function_area.current_occupancy IS '当前占用数量';
COMMENT ON COLUMN function_area.properties IS '扩展属性，JSON格式，存储区域特殊属性';
COMMENT ON COLUMN function_area.is_active IS '是否激活状态';
COMMENT ON COLUMN function_area.created_by IS '记录创建人';
COMMENT ON COLUMN function_area.created_time IS '记录创建时间';
COMMENT ON COLUMN function_area.updated_by IS '最后更新人';
COMMENT ON COLUMN function_area.updated_time IS '最后更新时间';

-- 索引
CREATE INDEX idx_function_area_plant_model ON functional_area(plant_model_id);
CREATE INDEX idx_function_area_boundary ON functional_area USING GIST (boundary);
CREATE INDEX idx_function_area_type ON functional_area(area_type);
CREATE INDEX idx_function_area_department ON functional_area(department);