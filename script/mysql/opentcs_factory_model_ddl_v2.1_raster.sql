-- ============================================================
-- OpenTCS Plus 增量迁移 DDL
-- 导航地图表字段变更
-- ============================================================

-- Step 1: 删除 map_type 字段（目前只支持2D栅格地图）
ALTER TABLE navigation_map
DROP COLUMN IF EXISTS map_type;

-- Step 2: 添加 AMR 型号和地图定位参数字段
ALTER TABLE navigation_map
ADD COLUMN amr_model VARCHAR(100) COMMENT 'AMR型号（必填，对应vehicle_type.name)' AFTER floor_number,
ADD COLUMN origin_x DECIMAL(12,4) DEFAULT 0 COMMENT '地图原点X坐标(毫米)' AFTER amr_model,
ADD COLUMN origin_y DECIMAL(12,4) DEFAULT 0 COMMENT '地图原点Y坐标(毫米)' AFTER origin_x,
ADD COLUMN rotation DECIMAL(10,4) DEFAULT 0 COMMENT '地图旋转角度(度)' AFTER origin_y;

-- Step 3: 添加栅格底图相关字段
ALTER TABLE navigation_map
ADD COLUMN raster_url VARCHAR(500) COMMENT '栅格地图OSS存储路径' AFTER rotation,
ADD COLUMN raster_version INT DEFAULT 0 COMMENT '栅格地图版本号' AFTER raster_url,
ADD COLUMN raster_width INT COMMENT '栅格地图宽度(像素)' AFTER raster_version,
ADD COLUMN raster_height INT COMMENT '栅格地图高度(像素)' AFTER raster_width,
ADD COLUMN raster_resolution DECIMAL(12,6) COMMENT '栅格地图分辨率(米/像素)' AFTER raster_height;
