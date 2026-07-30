-- I2: 重建 Block 表，支持地图编辑器资源互斥分组建模。
-- 成员以 JSON 字符串数组存储（Point/Path name）。

CREATE TABLE IF NOT EXISTS tcs_block (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '所属地图ID',
    factory_model_id BIGINT NULL COMMENT '所属工厂ID（冗余，便于查询）',
    block_id VARCHAR(64) NOT NULL COMMENT '区块业务唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '区块名称',
    type VARCHAR(50) NOT NULL DEFAULT 'SINGLE_VEHICLE_ONLY' COMMENT 'SINGLE_VEHICLE_ONLY / SAME_DIRECTION_ONLY',
    members JSON COMMENT '成员资源名称列表（Point/Path name）',
    color VARCHAR(20) DEFAULT '#F44336' COMMENT '画布高亮颜色',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_block PRIMARY KEY (id),
    CONSTRAINT uk_block_map_block_id UNIQUE (navigation_map_id, block_id),
    CONSTRAINT fk_block_navigation_map FOREIGN KEY (navigation_map_id)
        REFERENCES tcs_navigation_map(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区块表（资源互斥规则）';

CREATE INDEX idx_block_navigation_map ON tcs_block(navigation_map_id);
