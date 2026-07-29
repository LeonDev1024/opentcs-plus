-- 地图模型去掉 Block：清理 BLOCK 锁并删除 tcs_block
DELETE FROM tcs_resource_lock_audit WHERE resource_type = 'BLOCK';
DELETE FROM tcs_resource_lock WHERE resource_type = 'BLOCK';
DROP TABLE IF EXISTS tcs_block;
