-- 品牌管理不再维护官网字段。
SET @drop_brand_website := (
  SELECT IF(
    (SELECT COUNT(*)
       FROM INFORMATION_SCHEMA.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'tcs_brand'
        AND COLUMN_NAME = 'website') > 0,
    'ALTER TABLE tcs_brand DROP COLUMN website',
    'SELECT 1'
  )
);

PREPARE stmt FROM @drop_brand_website;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
