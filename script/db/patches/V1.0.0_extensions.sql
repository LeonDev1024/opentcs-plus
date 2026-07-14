-- ============================================================
-- OpenTCS Plus Baseline Extensions (greenfield consolidation)
-- 合并自: v2.1 raster、v2.4 base layer、map version、brand 表
-- 适用于在 factory_model v2.0 基表之上的空库初始化
-- ============================================================

USE opentcsplus;
SET NAMES utf8mb4;

-- navigation_map: 移除 legacy 字段，补齐栅格/版本相关列
ALTER TABLE tcs_navigation_map
    DROP COLUMN map_type;

ALTER TABLE tcs_navigation_map
    ADD COLUMN vehicle_type_id BIGINT COMMENT '车辆类型ID（必填，对应vehicle_type.id)' AFTER floor_number,
    ADD COLUMN rotation DECIMAL(10,4) DEFAULT 0 COMMENT '地图旋转角度(度)' AFTER origin_y,
    ADD COLUMN raster_url VARCHAR(500) COMMENT '栅格地图OSS存储路径' AFTER rotation,
    ADD COLUMN raster_version INT DEFAULT 0 COMMENT '栅格地图版本号' AFTER raster_url,
    ADD COLUMN raster_width INT COMMENT '栅格地图宽度(像素)' AFTER raster_version,
    ADD COLUMN raster_height INT COMMENT '栅格地图高度(像素)' AFTER raster_width,
    ADD COLUMN raster_resolution DECIMAL(12,6) COMMENT '栅格地图分辨率(米/像素)' AFTER raster_height,
    ADD COLUMN yaml_origin JSON COMMENT 'YAML原始origin参数 [ox, oy, angle] (米,度)' AFTER raster_resolution,
    ADD COLUMN yaml_url VARCHAR(500) COMMENT 'YAML文件OSS存储路径' AFTER yaml_origin,
    ADD COLUMN map_origin JSON COMMENT '地图在工厂坐标系下的原点偏移 [x, y, angle] (毫米,度)' AFTER yaml_url,
    ADD COLUMN map_version VARCHAR(50) NOT NULL DEFAULT '1.0' COMMENT '地图版本号' AFTER map_origin;

ALTER TABLE tcs_navigation_map
    ALTER COLUMN status SET DEFAULT '1';

CREATE INDEX idx_navigation_map_version ON tcs_navigation_map(map_version);
CREATE INDEX idx_navigation_map_status ON tcs_navigation_map(status);
CREATE INDEX idx_path_layer ON tcs_path(layer_id);

CREATE TABLE IF NOT EXISTS tcs_navigation_map_history (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '所属地图ID',
    map_version VARCHAR(50) NOT NULL COMMENT '地图版本号',
    snapshot_url VARCHAR(500) COMMENT 'JSON快照文件路径',
    change_summary VARCHAR(500) COMMENT '变更说明',
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    CONSTRAINT fk_history_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES tcs_navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT uk_history_map_version UNIQUE (navigation_map_id, map_version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导航地图历史版本表';

CREATE INDEX idx_history_navigation_map_id ON tcs_navigation_map_history(navigation_map_id);
CREATE INDEX idx_history_map_version ON tcs_navigation_map_history(map_version);

CREATE TABLE IF NOT EXISTS tcs_brand (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(100) NOT NULL COMMENT '品牌名称',
    code        VARCHAR(50)           COMMENT '品牌代码',
    logo        VARCHAR(255)          COMMENT 'Logo URL',
    description VARCHAR(500)          COMMENT '描述',
    contact     VARCHAR(200)          COMMENT '联系方式',
    enabled     TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用',
    sort        INT                   DEFAULT 0 COMMENT '排序',
    create_by   BIGINT                COMMENT '创建者',
    create_time DATETIME              COMMENT '创建时间',
    update_by   BIGINT                COMMENT '更新者',
    update_time DATETIME              COMMENT '更新时间',
    del_flag    CHAR(1)               DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆品牌';
