-- ============================================================
-- OpenTCS Plus 增量迁移 DDL
-- 地图原点与栅格底图字段优化
-- ============================================================

-- Step 1: 添加栅格底图 YAML 相关字段
ALTER TABLE navigation_map
ADD COLUMN yaml_origin JSON COMMENT 'YAML原始origin参数 [ox, oy, angle] (米,度)' AFTER raster_resolution,
ADD COLUMN yaml_url VARCHAR(500) COMMENT 'YAML文件OSS存储路径' AFTER yaml_origin;

-- Step 2: 添加地图原点 JSON 字段（替代 origin_x, origin_y, rotation，保留旧字段兼容）
ALTER TABLE navigation_map
ADD COLUMN map_origin JSON COMMENT '地图在工厂坐标系下的原点偏移 [x, y, angle] (毫米,度)' AFTER yaml_url;