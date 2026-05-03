-- ============================================================
-- OpenTCS Plus Docker 初始化脚本
-- 按依赖顺序执行所有 DDL
-- ============================================================

CREATE DATABASE IF NOT EXISTS opentcs
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE opentcs;
