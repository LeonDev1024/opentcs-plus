-- ============================================================
-- OpenTCS Plus 演示数据
-- 包含：brand 表 DDL 补丁 + 示例工厂/地图/点位/车辆
-- ============================================================

USE opentcsplus;

-- ---------------------------------------------------------------
-- brand 表（缺失的 DDL 补丁）
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tcs_brand (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(100) NOT NULL COMMENT '品牌名称',
    code        VARCHAR(50)           COMMENT '品牌代码',
    logo        VARCHAR(255)          COMMENT 'Logo URL',
    description VARCHAR(500)          COMMENT '描述',
    contact     VARCHAR(200)          COMMENT '联系方式',
    enabled     TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用',
    sort        INT                   DEFAULT 0 COMMENT '排序',
    create_by   BIGINT                COMMENT '创建者',
    create_time DATETIME              COMMENT '创建时间',
    update_by   BIGINT                COMMENT '更新者',
    update_time DATETIME              COMMENT '更新时间',
    del_flag    CHAR(1)               DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆品牌';

-- ---------------------------------------------------------------
-- 演示品牌
-- ---------------------------------------------------------------
INSERT INTO tcs_brand (id, name, code, description, enabled, sort, create_by, create_time, update_by, update_time, del_flag)
VALUES
    (1, '演示品牌', 'DEMO', '仿真演示用品牌', 1, 1, 1, NOW(), 1, NOW(), '0')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ---------------------------------------------------------------
-- 演示车辆类型（依赖 brand）
-- ---------------------------------------------------------------
INSERT INTO tcs_vehicle_type (id, brand_id, name, length, width, height, max_velocity, max_reverse_velocity, energy_level, allowed_orders, allowed_peripheral_operations, create_by, create_time, update_by, update_time, del_flag)
VALUES
    (1, 1, '标准仿真车型', 0.80, 0.60, 0.50, 2.0, 0.5, 100.0, '["NOP", "LOAD_CARGO", "UNLOAD_CARGO"]', '[]', 1, NOW(), 1, NOW(), '0')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ---------------------------------------------------------------
-- 演示车辆（2台仿真车）
-- ---------------------------------------------------------------
INSERT INTO tcs_vehicle (id, name, vehicle_type_id, state, integration_level, energy_level, create_by, create_time, update_by, update_time, del_flag)
VALUES
    (1, 'AGV-SIM-001', 1, 'IDLE', 'TO_BE_UTILIZED', 100.0, 1, NOW(), 1, NOW(), '0'),
    (2, 'AGV-SIM-002', 1, 'IDLE', 'TO_BE_UTILIZED', 100.0, 1, NOW(), 1, NOW(), '0')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ---------------------------------------------------------------
-- 演示工厂模型
-- ---------------------------------------------------------------
INSERT INTO tcs_factory_model (id, factory_id, name, description, create_by, create_time, update_by, update_time, del_flag)
VALUES
    (1, 'DEMO-FACTORY-001', '演示仓库', '10点位标准演示地图', 1, NOW(), 1, NOW(), '0')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ---------------------------------------------------------------
-- 演示导航地图（一楼）
-- ---------------------------------------------------------------
INSERT INTO tcs_navigation_map (id, factory_model_id, map_id, name, floor_number, status, map_version, create_by, create_time, update_by, update_time, del_flag)
VALUES
    (1, 1, 'demo-floor-1', '一楼导航图', 1, '0', '1.0.0', 1, NOW(), 1, NOW(), '0')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ---------------------------------------------------------------
-- 演示图层组和图层
-- ---------------------------------------------------------------
INSERT INTO tcs_factory_layer_group (id, navigation_map_id, name, visible, ordinal, create_by, create_time, update_by, update_time, del_flag)
VALUES
    (1, 1, '默认图层组', 1, 1, 1, NOW(), 1, NOW(), '0')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO tcs_factory_layer (id, navigation_map_id, layer_group_id, name, visible, ordinal, create_by, create_time, update_by, update_time, del_flag)
VALUES
    (1, 1, 1, '主图层', 1, 1, 1, NOW(), 1, NOW(), '0')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ---------------------------------------------------------------
-- 演示点位（10个点，2行5列网格布局，间距 2000mm）
-- 坐标单位：mm
-- ---------------------------------------------------------------
INSERT INTO tcs_point (navigation_map_id, point_id, name, x_position, y_position, z_position, pose_theta, type, layer_id, create_by, create_time, update_by, update_time, del_flag)
VALUES
    (1, 'P001', '入库点-1',  1000,  1000, 0, 0, 'HALT_POSITION', 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'P002', '转运点-2',  3000,  1000, 0, 0, 'HALT_POSITION', 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'P003', '转运点-3',  5000,  1000, 0, 0, 'HALT_POSITION', 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'P004', '转运点-4',  7000,  1000, 0, 0, 'HALT_POSITION', 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'P005', '出库点-5',  9000,  1000, 0, 0, 'HALT_POSITION', 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'P006', '入库点-6',  1000,  3000, 0, 0, 'HALT_POSITION', 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'P007', '转运点-7',  3000,  3000, 0, 0, 'HALT_POSITION', 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'P008', '转运点-8',  5000,  3000, 0, 0, 'HALT_POSITION', 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'P009', '转运点-9',  7000,  3000, 0, 0, 'HALT_POSITION', 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'P010', '出库点-10', 9000,  3000, 0, 0, 'HALT_POSITION', 1, 1, NOW(), 1, NOW(), '0');

-- ---------------------------------------------------------------
-- 演示路径（行方向 + 竖向连接，双向）
-- 上排：P001-P002-P003-P004-P005 双向
-- 下排：P006-P007-P008-P009-P010 双向
-- 竖连：P001-P006, P003-P008, P005-P010
-- ---------------------------------------------------------------
INSERT INTO tcs_path (navigation_map_id, path_id, name, source_point_id, dest_point_id, length, max_velocity, max_reverse_velocity, locked, layer_id, create_by, create_time, update_by, update_time, del_flag)
VALUES
    -- 上排正向
    (1, 'PA001', 'P001→P002', 'P001', 'P002', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA002', 'P002→P003', 'P002', 'P003', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA003', 'P003→P004', 'P003', 'P004', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA004', 'P004→P005', 'P004', 'P005', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    -- 上排反向
    (1, 'PA005', 'P002→P001', 'P002', 'P001', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA006', 'P003→P002', 'P003', 'P002', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA007', 'P004→P003', 'P004', 'P003', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA008', 'P005→P004', 'P005', 'P004', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    -- 下排正向
    (1, 'PA009', 'P006→P007', 'P006', 'P007', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA010', 'P007→P008', 'P007', 'P008', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA011', 'P008→P009', 'P008', 'P009', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA012', 'P009→P010', 'P009', 'P010', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    -- 下排反向
    (1, 'PA013', 'P007→P006', 'P007', 'P006', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA014', 'P008→P007', 'P008', 'P007', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA015', 'P009→P008', 'P009', 'P008', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA016', 'P010→P009', 'P010', 'P009', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    -- 竖向连接（双向）
    (1, 'PA017', 'P001→P006', 'P001', 'P006', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA018', 'P006→P001', 'P006', 'P001', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA019', 'P003→P008', 'P003', 'P008', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA020', 'P008→P003', 'P008', 'P003', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA021', 'P005→P010', 'P005', 'P010', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0'),
    (1, 'PA022', 'P010→P005', 'P010', 'P005', 2000, 2.0, 0.5, 0, 1, 1, NOW(), 1, NOW(), '0');
