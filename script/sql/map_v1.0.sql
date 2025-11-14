SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 工程与版本 ------------------------------------------------------------------
CREATE TABLE map_project (
     id                 BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
     tenant_id          BIGINT UNSIGNED NOT NULL COMMENT '租户ID（与系统租户对齐）',
     code               VARCHAR(64) NOT NULL COMMENT '地图工程编码（同租户唯一）',
     name               VARCHAR(128) NOT NULL COMMENT '地图工程名称',
     description        VARCHAR(512) NULL COMMENT '工程描述',
     crs_srid           INT NULL COMMENT '坐标系 SRID（如 3857/4326/本地）',
     unit               ENUM('meter','degree','pixel') NOT NULL DEFAULT 'meter' COMMENT '地图单位（米/经纬度/像素）',
     origin_x           DOUBLE NULL COMMENT '地图原点X（工程坐标）',
     origin_y           DOUBLE NULL COMMENT '地图原点Y（工程坐标）',
     rotation_deg       DECIMAL(8,3) NULL COMMENT '地图整体旋转角（度）',
     bounds_minx        DOUBLE NULL COMMENT '地图边界最小X',
     bounds_miny        DOUBLE NULL COMMENT '地图边界最小Y',
     bounds_maxx        DOUBLE NULL COMMENT '地图边界最大X',
     bounds_maxy        DOUBLE NULL COMMENT '地图边界最大Y',
     current_version_id BIGINT UNSIGNED NULL COMMENT '当前发布版本ID（指向map_version）',
     is_active          TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用 1启用 0停用',
     remark             VARCHAR(512) NULL COMMENT '备注',

     created_by         BIGINT UNSIGNED NULL COMMENT '创建人',
     created_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
     updated_by         BIGINT UNSIGNED NULL COMMENT '更新人',
     updated_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

     UNIQUE KEY uk_project_tenant_code (tenant_id, code),
     KEY idx_project_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地图工程（跨版本元信息）';

CREATE TABLE map_version (
      id             BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
      project_id     BIGINT UNSIGNED NOT NULL COMMENT '工程ID（map_project.id）',
      version_no     INT NOT NULL COMMENT '版本号（递增）',
      status         ENUM('draft','published','archived') NOT NULL DEFAULT 'draft' COMMENT '版本状态',
      changelog      VARCHAR(1024) NULL COMMENT '版本变更说明',
      source_type    ENUM('editor','import') NOT NULL DEFAULT 'editor' COMMENT '版本来源（编辑/导入）',
      source_uri     VARCHAR(512) NULL COMMENT '来源地址（文件/服务）',
      checksum       VARCHAR(128) NULL COMMENT '版本校验值',
      published_at   DATETIME(3) NULL COMMENT '发布时间',
      is_locked      TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否加锁（防误改）',
      lock_owner     BIGINT UNSIGNED NULL COMMENT '锁定人',

      created_by     BIGINT UNSIGNED NULL COMMENT '创建人',
      created_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
      updated_by     BIGINT UNSIGNED NULL COMMENT '更新人',
      updated_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

      UNIQUE KEY uk_version_project_no (project_id, version_no),
      KEY idx_version_project (project_id),
      CONSTRAINT fk_version_project FOREIGN KEY (project_id) REFERENCES map_project(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地图版本（可发布/回滚）';

ALTER TABLE map_project
    ADD CONSTRAINT fk_project_current_version FOREIGN KEY (current_version_id) REFERENCES map_version(id) ON DELETE SET NULL;

-- 图层（可选） -----------------------------------------------------------------
CREATE TABLE map_layer (
      id             BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
      version_id     BIGINT UNSIGNED NOT NULL COMMENT '版本ID（map_version.id）',
      code           VARCHAR(64) NOT NULL COMMENT '图层编码（版本内唯一）',
      name           VARCHAR(128) NOT NULL COMMENT '图层名称',
      type           ENUM('vector','raster','tile','grid') NOT NULL DEFAULT 'vector' COMMENT '图层类型',
      z_index        INT NOT NULL DEFAULT 0 COMMENT '图层层级',
      visible_default TINYINT(1) NOT NULL DEFAULT 1 COMMENT '默认可见',
      opacity        DECIMAL(4,3) NOT NULL DEFAULT 1.000 COMMENT '透明度（0-1）',
      style_json     JSON NULL COMMENT '渲染样式（JSON）',

      created_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
      updated_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

      UNIQUE KEY uk_layer_version_code (version_id, code),
      KEY idx_layer_version (version_id),
      CONSTRAINT fk_layer_version FOREIGN KEY (version_id) REFERENCES map_version(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地图图层（控制显示/样式）';

-- Point（点） -----------------------------------------------------------------
CREATE TABLE map_point (
     id             BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
     version_id     BIGINT UNSIGNED NOT NULL COMMENT '版本ID（map_version.id）',
     code           VARCHAR(64) NOT NULL COMMENT '点编码（版本内唯一）',
     name           VARCHAR(128) NULL COMMENT '点名称',
     category       ENUM('PARK','HALT','REPORT','CHARGER','STATION','CHECKPOINT') NOT NULL DEFAULT 'HALT' COMMENT '点类别（停车/停止/上报/充电/工位/检查点）',
     position       POINT NULL COMMENT 'GIS坐标（POINT，单位见工程单位与SRID）',
     heading_deg    DECIMAL(6,2) NULL COMMENT '朝向角（度）',
     precise_x      DOUBLE NULL COMMENT '精细坐标X（像素/相机坐标等）',
     precise_y      DOUBLE NULL COMMENT '精细坐标Y（像素/相机坐标等）',
     properties     JSON NULL COMMENT '属性（openTCS Point properties）',
     layer_id       BIGINT UNSIGNED NULL COMMENT '所属图层ID',

     created_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
     updated_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

     UNIQUE KEY uk_point_version_code (version_id, code),
     KEY idx_point_version (version_id),
     SPATIAL INDEX spx_point_pos (position),
     KEY idx_point_layer (layer_id),

     CONSTRAINT fk_point_version FOREIGN KEY (version_id) REFERENCES map_version(id) ON DELETE CASCADE,
     CONSTRAINT fk_point_layer   FOREIGN KEY (layer_id)   REFERENCES map_layer(id)   ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Point（点）';

-- Path（路径/段） --------------------------------------------------------------
CREATE TABLE map_path (
      id                 BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
      version_id         BIGINT UNSIGNED NOT NULL COMMENT '版本ID（map_version.id）',
      code               VARCHAR(64) NOT NULL COMMENT '路径编码（版本内唯一）',
      name               VARCHAR(128) NULL COMMENT '路径名称',
      from_point_id      BIGINT UNSIGNED NOT NULL COMMENT '起点ID（map_point.id）',
      to_point_id        BIGINT UNSIGNED NOT NULL COMMENT '终点ID（map_point.id）',
      geometry           LINESTRING NULL COMMENT '几何线（LINESTRING）',
      length_m           DOUBLE NULL COMMENT '长度（米），可存储计算结果',
      max_fwd_speed_mps  DOUBLE NULL COMMENT '最大正向速度（m/s）',
      max_rev_speed_mps  DOUBLE NULL COMMENT '最大反向速度（m/s）',
      locked             TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否锁定（openTCS Path.locked）',
      bidirectional      TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否双向（0单向 1双向）',
      properties         JSON NULL COMMENT '属性（openTCS Path properties）',
      layer_id           BIGINT UNSIGNED NULL COMMENT '所属图层ID',

      created_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
      updated_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

      UNIQUE KEY uk_path_version_code (version_id, code),
      KEY idx_path_version (version_id),
      KEY idx_path_points (from_point_id, to_point_id),
      SPATIAL INDEX spx_path_geom (geometry),
      KEY idx_path_layer (layer_id),

      CONSTRAINT fk_path_version FOREIGN KEY (version_id) REFERENCES map_version(id) ON DELETE CASCADE,
      CONSTRAINT fk_path_from    FOREIGN KEY (from_point_id) REFERENCES map_point(id) ON DELETE RESTRICT,
      CONSTRAINT fk_path_to      FOREIGN KEY (to_point_id)   REFERENCES map_point(id) ON DELETE RESTRICT,
      CONSTRAINT fk_path_layer   FOREIGN KEY (layer_id)      REFERENCES map_layer(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Path（路径/段）';

-- LocationType（位置类型） -----------------------------------------------------
CREATE TABLE map_location_type (
                                   id             BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                   version_id     BIGINT UNSIGNED NOT NULL COMMENT '版本ID（map_version.id）',
                                   code           VARCHAR(64) NOT NULL COMMENT '位置类型编码（版本内唯一）',
                                   name           VARCHAR(128) NOT NULL COMMENT '位置类型名称',
                                   allowed_ops    JSON NULL COMMENT '许可操作集合（openTCS AllowedOperations）',
                                   properties     JSON NULL COMMENT '属性',

                                   created_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                   updated_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

                                   UNIQUE KEY uk_loctype_version_code (version_id, code),
                                   KEY idx_loctype_version (version_id),
                                   CONSTRAINT fk_loctype_version FOREIGN KEY (version_id) REFERENCES map_version(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LocationType（位置类型）';

-- Location（位置/工位） --------------------------------------------------------
CREATE TABLE map_location (
                              id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                              version_id       BIGINT UNSIGNED NOT NULL COMMENT '版本ID（map_version.id）',
                              code             VARCHAR(64) NOT NULL COMMENT '位置编码（版本内唯一）',
                              name             VARCHAR(128) NULL COMMENT '位置名称',
                              type_id          BIGINT UNSIGNED NOT NULL COMMENT '位置类型ID（map_location_type.id）',
                              position         POINT NULL COMMENT '位置几何中心坐标（可与Point分离）',
                              properties       JSON NULL COMMENT '属性（openTCS Location properties）',
                              layer_id         BIGINT UNSIGNED NULL COMMENT '所属图层ID',

                              created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                              updated_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

                              UNIQUE KEY uk_loc_version_code (version_id, code),
                              KEY idx_loc_version (version_id),
                              KEY idx_loc_type (type_id),
                              SPATIAL INDEX spx_loc_pos (position),
                              KEY idx_loc_layer (layer_id),

                              CONSTRAINT fk_loc_version FOREIGN KEY (version_id) REFERENCES map_version(id) ON DELETE CASCADE,
                              CONSTRAINT fk_loc_type    FOREIGN KEY (type_id)    REFERENCES map_location_type(id) ON DELETE RESTRICT,
                              CONSTRAINT fk_loc_layer   FOREIGN KEY (layer_id)   REFERENCES map_layer(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Location（位置/工位）';

-- Link（位置-点连结，openTCS LocationLink） -----------------------------------
CREATE TABLE map_link (
      id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
      version_id       BIGINT UNSIGNED NOT NULL COMMENT '版本ID（map_version.id）',
      location_id      BIGINT UNSIGNED NOT NULL COMMENT '位置ID（map_location.id）',
      point_id         BIGINT UNSIGNED NOT NULL COMMENT '点ID（map_point.id）',
      allowed_ops      JSON NULL COMMENT '此连接许可操作（可覆盖位置类型默认）',
      properties       JSON NULL COMMENT '属性',

      created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
      updated_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

      UNIQUE KEY uk_link_version_loc_point (version_id, location_id, point_id),
      KEY idx_link_version (version_id),
      KEY idx_link_location (location_id),
      KEY idx_link_point (point_id),

      CONSTRAINT fk_link_version  FOREIGN KEY (version_id)  REFERENCES map_version(id)   ON DELETE CASCADE,
      CONSTRAINT fk_link_location FOREIGN KEY (location_id) REFERENCES map_location(id)  ON DELETE CASCADE,
      CONSTRAINT fk_link_point    FOREIGN KEY (point_id)    REFERENCES map_point(id)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Link（位置-点关联，LocationLink）';

-- Block（互斥区/资源组，openTCS Block） ---------------------------------------
CREATE TABLE map_block (
      id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
      version_id       BIGINT UNSIGNED NOT NULL COMMENT '版本ID（map_version.id）',
      code             VARCHAR(64) NOT NULL COMMENT '互斥区编码（版本内唯一）',
      name             VARCHAR(128) NULL COMMENT '互斥区名称',
      kind             ENUM('SIMPLE','CROSS','MERGE','CUSTOM') NOT NULL DEFAULT 'SIMPLE' COMMENT '互斥区类型',
      properties       JSON NULL COMMENT '属性',

      created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
      updated_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

      UNIQUE KEY uk_block_version_code (version_id, code),
      KEY idx_block_version (version_id),
      CONSTRAINT fk_block_version FOREIGN KEY (version_id) REFERENCES map_version(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Block（互斥区/资源组）';

-- Block 成员（支持点/段作为互斥资源） -----------------------------------------
CREATE TABLE map_block_member (
     id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
     block_id         BIGINT UNSIGNED NOT NULL COMMENT '互斥区ID（map_block.id）',
     member_type      ENUM('POINT','PATH') NOT NULL COMMENT '成员类型（点/段）',
     member_id        BIGINT UNSIGNED NOT NULL COMMENT '成员主键ID（指向对应表）',

     created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',

     UNIQUE KEY uk_block_member (block_id, member_type, member_id),
     KEY idx_block (block_id),

     CONSTRAINT fk_block_member_block FOREIGN KEY (block_id) REFERENCES map_block(id) ON DELETE CASCADE
    -- 业务侧保证 member_id 指向对应表；如需 FK，可拆两张表（block_member_point / block_member_path）
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Block成员（点/段）';

-- 区域（禁行/限速/作业区） ----------------------------------------------------
CREATE TABLE map_area (
      id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
      version_id       BIGINT UNSIGNED NOT NULL COMMENT '版本ID（map_version.id）',
      code             VARCHAR(64) NOT NULL COMMENT '区域编码（版本内唯一）',
      name             VARCHAR(128) NULL COMMENT '区域名称',
      kind             ENUM('NO_GO','SLOW','WORKCELL','ZONE') NOT NULL DEFAULT 'ZONE' COMMENT '区域类型（禁行/限速/作业/普通）',
      geometry         POLYGON NULL COMMENT '区域几何面（POLYGON）',
      speed_limit_mps  DOUBLE NULL COMMENT '限速（m/s），仅限速区生效',
      priority         INT NULL COMMENT '区域优先级（冲突决策辅助）',
      properties       JSON NULL COMMENT '属性',
      layer_id         BIGINT UNSIGNED NULL COMMENT '所属图层ID',

      created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
      updated_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

      UNIQUE KEY uk_area_version_code (version_id, code),
      KEY idx_area_version (version_id),
      SPATIAL INDEX spx_area_geom (geometry),
      KEY idx_area_layer (layer_id),

      CONSTRAINT fk_area_version FOREIGN KEY (version_id) REFERENCES map_version(id) ON DELETE CASCADE,
      CONSTRAINT fk_area_layer   FOREIGN KEY (layer_id)   REFERENCES map_layer(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Area（禁行/限速/作业区）';

-- 注记/布局元素（对齐 openTCS LayoutElement 能力） -----------------------------
CREATE TABLE map_layout (
     id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
     version_id       BIGINT UNSIGNED NOT NULL COMMENT '版本ID（map_version.id）',
     ref_type         ENUM('POINT','PATH','LOCATION','AREA','FREE') NOT NULL COMMENT '关联类型（或FREE自由注记）',
     ref_id           BIGINT UNSIGNED NULL COMMENT '关联对象ID（FREE可空）',
     kind             ENUM('text','shape','icon','measurement','hint') NOT NULL COMMENT '注记种类',
     geometry         GEOMETRY NULL COMMENT '几何（点/线/面均可）',
     text             VARCHAR(512) NULL COMMENT '文本内容',
     style_json       JSON NULL COMMENT '样式JSON',
     layer_id         BIGINT UNSIGNED NULL COMMENT '所属图层ID',

     created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
     updated_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

     KEY idx_layout_version (version_id),
     SPATIAL INDEX spx_layout_geom (geometry),
     KEY idx_layout_layer (layer_id),

     CONSTRAINT fk_layout_version FOREIGN KEY (version_id) REFERENCES map_version(id) ON DELETE CASCADE,
     CONSTRAINT fk_layout_layer   FOREIGN KEY (layer_id)   REFERENCES map_layer(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Layout（注记/布局元素）';

-- 导入任务/变更审计（工程化） ---------------------------------------------------
CREATE TABLE map_import_job (
     id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
     project_id       BIGINT UNSIGNED NOT NULL COMMENT '工程ID（map_project.id）',
     status           ENUM('pending','running','failed','succeeded') NOT NULL DEFAULT 'pending' COMMENT '任务状态',
     format           ENUM('svg','dxf','geojson','tms','image','otcs') NOT NULL COMMENT '导入格式',
     file_path        VARCHAR(512) NOT NULL COMMENT '文件路径或URI',
     options_json     JSON NULL COMMENT '导入选项（JSON）',
     result_message   VARCHAR(1024) NULL COMMENT '结果信息',
     created_by       BIGINT UNSIGNED NULL COMMENT '创建人',
     created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
     finished_at      DATETIME(3) NULL COMMENT '完成时间',

     KEY idx_job_project (project_id),
     KEY idx_job_status (status),
     CONSTRAINT fk_job_project FOREIGN KEY (project_id) REFERENCES map_project(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导入任务（SVG/DXF/GeoJSON/TMS/OTCS）';

CREATE TABLE map_change_audit (
      id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
      version_id       BIGINT UNSIGNED NOT NULL COMMENT '版本ID（map_version.id）',
      entity_type      VARCHAR(64) NOT NULL COMMENT '实体类型（POINT/PATH/LOCATION/AREA/...）',
      entity_id        BIGINT UNSIGNED NOT NULL COMMENT '实体主键ID',
      action           ENUM('insert','update','delete','publish','rollback') NOT NULL COMMENT '操作类型',
      before_json      JSON NULL COMMENT '变更前快照',
      after_json       JSON NULL COMMENT '变更后快照',
      created_by       BIGINT UNSIGNED NULL COMMENT '操作者',
      created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',

      KEY idx_audit_version (version_id),
      KEY idx_audit_entity (entity_type, entity_id),
      CONSTRAINT fk_audit_version FOREIGN KEY (version_id) REFERENCES map_version(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='变更审计（版本内实体差异）';