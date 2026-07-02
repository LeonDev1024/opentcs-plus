-- 车辆型号关联品牌字段
-- 代码中的 VehicleTypeEntity / VehicleTypeMapper 已使用 brand_id，
-- 这里补齐历史库缺失字段，避免查询车辆型号时报 Unknown column 't.brand_id'。

SET @schema_name := DATABASE();

SET @sql := IF(
  (SELECT COUNT(*)
   FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = @schema_name
     AND TABLE_NAME = 'tcs_vehicle_type'
     AND COLUMN_NAME = 'brand_id') = 0,
  'ALTER TABLE tcs_vehicle_type ADD COLUMN brand_id BIGINT NULL COMMENT ''所属品牌ID'' AFTER id',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  (SELECT COUNT(*)
   FROM information_schema.STATISTICS
   WHERE TABLE_SCHEMA = @schema_name
     AND TABLE_NAME = 'tcs_vehicle_type'
     AND INDEX_NAME = 'idx_vehicle_type_brand_id') = 0,
  'CREATE INDEX idx_vehicle_type_brand_id ON tcs_vehicle_type (brand_id)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
