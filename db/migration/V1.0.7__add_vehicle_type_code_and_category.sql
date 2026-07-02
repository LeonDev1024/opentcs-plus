-- 车辆类型：类型编码、车辆形态

SET @db_name = DATABASE();

SET @add_code = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = @db_name
        AND TABLE_NAME = 'tcs_vehicle_type'
        AND COLUMN_NAME = 'code'
    ),
    'SELECT 1',
    'ALTER TABLE tcs_vehicle_type ADD COLUMN code VARCHAR(64) NULL COMMENT ''类型编码'' AFTER brand_id'
  )
);
PREPARE stmt FROM @add_code;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_category = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = @db_name
        AND TABLE_NAME = 'tcs_vehicle_type'
        AND COLUMN_NAME = 'category'
    ),
    'SELECT 1',
    'ALTER TABLE tcs_vehicle_type ADD COLUMN category VARCHAR(32) NULL COMMENT ''车辆形态'' AFTER name'
  )
);
PREPARE stmt FROM @add_category;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_code_index = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.STATISTICS
      WHERE TABLE_SCHEMA = @db_name
        AND TABLE_NAME = 'tcs_vehicle_type'
        AND INDEX_NAME = 'uk_vehicle_type_code'
    ),
    'SELECT 1',
    'CREATE UNIQUE INDEX uk_vehicle_type_code ON tcs_vehicle_type (code)'
  )
);
PREPARE stmt FROM @add_code_index;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
