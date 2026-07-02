-- ============================================================
-- OpenTCS Plus Docker 初始化：仅创建数据库
-- 表结构由 Flyway 服务（db/migration）负责
-- ============================================================

CREATE DATABASE IF NOT EXISTS opentcsplus
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 兼容旧部署包中的 opentcs 库名（只创建，不写入 schema）
CREATE DATABASE IF NOT EXISTS opentcs
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
