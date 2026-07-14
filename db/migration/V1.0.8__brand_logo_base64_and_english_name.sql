-- 品牌：英文名称、Logo 改为 Base64 存储

SET @db_name = DATABASE();

SET @add_english_name = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = @db_name
        AND TABLE_NAME = 'tcs_brand'
        AND COLUMN_NAME = 'english_name'
    ),
    'SELECT 1',
    'ALTER TABLE tcs_brand ADD COLUMN english_name VARCHAR(100) NULL COMMENT ''英文名称'' AFTER name'
  )
);
PREPARE stmt FROM @add_english_name;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @modify_logo = (
  SELECT IF(
    EXISTS(
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = @db_name
        AND TABLE_NAME = 'tcs_brand'
        AND COLUMN_NAME = 'logo'
        AND DATA_TYPE = 'mediumtext'
    ),
    'SELECT 1',
    'ALTER TABLE tcs_brand MODIFY COLUMN logo MEDIUMTEXT NULL COMMENT ''品牌缩略图 Base64'''
  )
);
PREPARE stmt FROM @modify_logo;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
