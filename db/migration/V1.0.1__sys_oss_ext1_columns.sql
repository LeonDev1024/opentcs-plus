-- 补齐 OSS 表与 Java 实体 SysOss / SysOssConfig 的 ext1 字段
ALTER TABLE sys_oss
    ADD COLUMN ext1 VARCHAR(500) NULL COMMENT '扩展字段' AFTER url;

ALTER TABLE sys_oss_config
    ADD COLUMN ext1 VARCHAR(500) NULL COMMENT '扩展字段' AFTER status;
