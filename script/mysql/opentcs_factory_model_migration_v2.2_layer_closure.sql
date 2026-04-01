-- OpenTCS Plus
-- v2.2 图层闭环增量迁移脚本（适用于已有数据库）
-- 目标：
-- 1) path/location 增加 layer_id
-- 2) 补充与 factory_layer 的外键关联
-- 3) 增加必要索引，提升按 layer 查询性能

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- path: 增加 layer_id 列（兼容低版本 MySQL）
-- ============================================================
SET @path_layer_col_exists := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'path'
      AND COLUMN_NAME = 'layer_id'
);
SET @sql_add_path_layer_col := IF(
    @path_layer_col_exists = 0,
    'ALTER TABLE path ADD COLUMN layer_id BIGINT COMMENT ''归属图层ID'' AFTER navigation_map_id',
    'SELECT "path.layer_id already exists"'
);
PREPARE stmt_add_path_layer_col FROM @sql_add_path_layer_col;
EXECUTE stmt_add_path_layer_col;
DEALLOCATE PREPARE stmt_add_path_layer_col;

-- path: 增加索引（兼容低版本 MySQL）
SET @idx_path_layer_exists := (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'path'
      AND INDEX_NAME = 'idx_path_layer'
);
SET @sql_add_idx_path_layer := IF(
    @idx_path_layer_exists = 0,
    'CREATE INDEX idx_path_layer ON path(layer_id)',
    'SELECT "idx_path_layer already exists"'
);
PREPARE stmt_add_idx_path_layer FROM @sql_add_idx_path_layer;
EXECUTE stmt_add_idx_path_layer;
DEALLOCATE PREPARE stmt_add_idx_path_layer;

-- path: 增加 layout 列（若不存在）
SET @path_layout_col_exists := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'path'
      AND COLUMN_NAME = 'layout'
);
SET @sql_add_path_layout_col := IF(
    @path_layout_col_exists = 0,
    'ALTER TABLE path ADD COLUMN layout JSON COMMENT ''路径布局（connectionType + controlPoints）'' AFTER is_blocked',
    'SELECT "path.layout already exists"'
);
PREPARE stmt_add_path_layout_col FROM @sql_add_path_layout_col;
EXECUTE stmt_add_path_layout_col;
DEALLOCATE PREPARE stmt_add_path_layout_col;

-- path: 增加外键（若不存在）
SET @fk_path_layer_exists := (
    SELECT COUNT(1)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'path'
      AND CONSTRAINT_NAME = 'fk_path_layer'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql_path_fk := IF(
    @fk_path_layer_exists = 0,
    'ALTER TABLE path ADD CONSTRAINT fk_path_layer FOREIGN KEY (layer_id) REFERENCES factory_layer(id) ON DELETE SET NULL',
    'SELECT "fk_path_layer already exists"'
);
PREPARE stmt_path_fk FROM @sql_path_fk;
EXECUTE stmt_path_fk;
DEALLOCATE PREPARE stmt_path_fk;

-- ============================================================
-- location: 增加 layer_id 列（兼容低版本 MySQL）
-- ============================================================
SET @location_layer_col_exists := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'location'
      AND COLUMN_NAME = 'layer_id'
);
SET @sql_add_location_layer_col := IF(
    @location_layer_col_exists = 0,
    'ALTER TABLE location ADD COLUMN layer_id BIGINT COMMENT ''归属图层ID'' AFTER navigation_map_id',
    'SELECT "location.layer_id already exists"'
);
PREPARE stmt_add_location_layer_col FROM @sql_add_location_layer_col;
EXECUTE stmt_add_location_layer_col;
DEALLOCATE PREPARE stmt_add_location_layer_col;

-- location: 增加索引（兼容低版本 MySQL）
SET @idx_location_layer_exists := (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'location'
      AND INDEX_NAME = 'idx_location_layer'
);
SET @sql_add_idx_location_layer := IF(
    @idx_location_layer_exists = 0,
    'CREATE INDEX idx_location_layer ON location(layer_id)',
    'SELECT "idx_location_layer already exists"'
);
PREPARE stmt_add_idx_location_layer FROM @sql_add_idx_location_layer;
EXECUTE stmt_add_idx_location_layer;
DEALLOCATE PREPARE stmt_add_idx_location_layer;

-- location: 增加 layout 列（若不存在）
SET @location_layout_col_exists := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'location'
      AND COLUMN_NAME = 'layout'
);
SET @sql_add_location_layout_col := IF(
    @location_layout_col_exists = 0,
    'ALTER TABLE location ADD COLUMN layout JSON COMMENT ''位置布局数据'' AFTER is_occupied',
    'SELECT "location.layout already exists"'
);
PREPARE stmt_add_location_layout_col FROM @sql_add_location_layout_col;
EXECUTE stmt_add_location_layout_col;
DEALLOCATE PREPARE stmt_add_location_layout_col;

-- point: 增加 layout 列（若不存在）
SET @point_layout_col_exists := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'point'
      AND COLUMN_NAME = 'layout'
);
SET @sql_add_point_layout_col := IF(
    @point_layout_col_exists = 0,
    'ALTER TABLE point ADD COLUMN layout JSON COMMENT ''点位布局数据'' AFTER label',
    'SELECT "point.layout already exists"'
);
PREPARE stmt_add_point_layout_col FROM @sql_add_point_layout_col;
EXECUTE stmt_add_point_layout_col;
DEALLOCATE PREPARE stmt_add_point_layout_col;

-- location: 增加外键（若不存在）
SET @fk_location_layer_exists := (
    SELECT COUNT(1)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'location'
      AND CONSTRAINT_NAME = 'fk_location_layer'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql_location_fk := IF(
    @fk_location_layer_exists = 0,
    'ALTER TABLE location ADD CONSTRAINT fk_location_layer FOREIGN KEY (layer_id) REFERENCES factory_layer(id) ON DELETE SET NULL',
    'SELECT "fk_location_layer already exists"'
);
PREPARE stmt_location_fk FROM @sql_location_fk;
EXECUTE stmt_location_fk;
DEALLOCATE PREPARE stmt_location_fk;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 数据回填建议（可选，按实际场景执行）
-- ============================================================
-- 1) 已有 path/location 若希望绑定默认图层，可按 navigation_map_id 选第一条 layer 回填：
-- UPDATE path p
-- JOIN (
--   SELECT navigation_map_id, MIN(id) AS default_layer_id
--   FROM factory_layer
--   GROUP BY navigation_map_id
-- ) fl ON fl.navigation_map_id = p.navigation_map_id
-- SET p.layer_id = fl.default_layer_id
-- WHERE p.layer_id IS NULL;
--
-- UPDATE location l
-- JOIN (
--   SELECT navigation_map_id, MIN(id) AS default_layer_id
--   FROM factory_layer
--   GROUP BY navigation_map_id
-- ) fl ON fl.navigation_map_id = l.navigation_map_id
-- SET l.layer_id = fl.default_layer_id
-- WHERE l.layer_id IS NULL;

