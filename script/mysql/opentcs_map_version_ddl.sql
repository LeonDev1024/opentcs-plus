-- ============================================================
-- OpenTCS Plus 语义地图版本管理 SQL
-- ============================================================

USE opentcs;

-- ============================================================
-- 1. navigation_map 表增加版本管理字段
-- ============================================================
ALTER TABLE navigation_map
ADD COLUMN map_version VARCHAR(50) NOT NULL DEFAULT '1.0' COMMENT '地图版本号',
ADD COLUMN status CHAR(1) NOT NULL DEFAULT '0' COMMENT '地图状态: 0-草稿, 1-已发布';

-- 创建索引
CREATE INDEX idx_navigation_map_version ON navigation_map(map_version);
CREATE INDEX idx_navigation_map_status ON navigation_map(status);

-- ============================================================
-- 2. 新建导航地图历史版本表
-- ============================================================
DROP TABLE IF EXISTS navigation_map_history;
CREATE TABLE navigation_map_history (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '所属地图ID',
    map_version VARCHAR(50) NOT NULL COMMENT '地图版本号',
    snapshot_url VARCHAR(500) COMMENT 'JSON快照文件路径',
    change_summary VARCHAR(500) COMMENT '变更说明',
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    CONSTRAINT fk_history_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT uk_history_map_version UNIQUE (navigation_map_id, map_version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导航地图历史版本表';

-- 创建索引
CREATE INDEX idx_history_navigation_map_id ON navigation_map_history(navigation_map_id);
CREATE INDEX idx_history_map_version ON navigation_map_history(map_version);

-- ============================================================
-- 3. 更新现有数据（将已存在地图初始化为已发布状态）
-- ============================================================
UPDATE navigation_map SET status = '1' WHERE status = '0' OR status IS NULL;

-- ============================================================
-- 4. 初始化新字段默认值
-- ============================================================
ALTER TABLE navigation_map ALTER COLUMN map_version DROP DEFAULT;
ALTER TABLE navigation_map ALTER COLUMN map_version SET DEFAULT '1.0';
ALTER TABLE navigation_map ALTER COLUMN status DROP DEFAULT;
ALTER TABLE navigation_map ALTER COLUMN status SET DEFAULT '1';
