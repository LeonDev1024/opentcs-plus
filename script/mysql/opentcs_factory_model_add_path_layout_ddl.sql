-- ============================================================
-- OpenTCS Plus 增量迁移 DDL
-- 路径表新增 layout 字段
-- 用于持久化前端编辑的 path.layoutControlPoints
-- ============================================================

USE opentcs;

ALTER TABLE path
    ADD COLUMN layout JSON COMMENT '路径布局（connectionType + controlPoints）'
    AFTER properties;

