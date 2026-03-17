-- ============================================================
-- OpenTCS Plus 车辆管理 SQL
-- ============================================================

USE opentcs;

-- ============================================================
-- 删除所有相关表（按外键依赖顺序）
-- ============================================================
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS vehicle_type;

-- ============================================================
-- 车辆类型表 (VehicleType) - 配置表，完整审计字段
-- ============================================================
CREATE TABLE vehicle_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '车辆类型名称',
    length DECIMAL(10,2) DEFAULT NULL COMMENT '车辆长度(mm)',
    width DECIMAL(10,2) DEFAULT NULL COMMENT '车辆宽度(mm)',
    height DECIMAL(10,2) DEFAULT NULL COMMENT '车辆高度(mm)',
    max_velocity DECIMAL(10,2) DEFAULT NULL COMMENT '最大速度(mm/s)',
    max_reverse_velocity DECIMAL(10,2) DEFAULT NULL COMMENT '最大反向速度(mm/s)',
    energy_level DECIMAL(10,2) DEFAULT NULL COMMENT '能量级别(0-100)',
    allowed_orders JSON COMMENT '允许的订单类型',
    allowed_peripheral_operations JSON COMMENT '允许的外围设备操作',
    properties JSON COMMENT '扩展属性',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_vehicle_type PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆类型表';

-- ============================================================
-- 车辆表 (Vehicle) - 业务主表，完整审计字段
-- ============================================================
CREATE TABLE vehicle (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '车辆名称',
    vin_code VARCHAR(50) DEFAULT NULL COMMENT '车辆VIN码',
    vehicle_type_id BIGINT NOT NULL COMMENT '车辆类型ID',
    current_position VARCHAR(100) DEFAULT NULL COMMENT '当前位置(点位ID)',
    next_position VARCHAR(100) DEFAULT NULL COMMENT '下一个位置(点位ID)',
    state VARCHAR(30) DEFAULT 'UNKNOWN' COMMENT '车辆状态: UNKNOWN, UNAVAILABLE, IDLE, CHARGING, WORKING, ERROR',
    integration_level VARCHAR(30) DEFAULT 'TO_BE_IGNORED' COMMENT '集成级别: TO_BE_IGNORED, TO_BE_NOTICED, TO_BE_RESPECTED, TO_BE_UTILIZED',
    energy_level DECIMAL(10,2) DEFAULT 100.00 COMMENT '能量级别(0-100)',
    current_transport_order VARCHAR(100) DEFAULT NULL COMMENT '当前运输订单ID',
    properties JSON COMMENT '扩展属性',
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT DEFAULT 0 COMMENT '乐观锁版本',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_vehicle PRIMARY KEY (id),
    CONSTRAINT fk_vehicle_type FOREIGN KEY (vehicle_type_id) REFERENCES vehicle_type(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 插入默认车辆类型
INSERT INTO vehicle_type (name, length, width, height, max_velocity, max_reverse_velocity, energy_level, allowed_orders, allowed_peripheral_operations) VALUES
('默认车型', 1000.00, 500.00, 500.00, 2000.00, 1000.00, 100.00, '[]', '[]');
