-- 极简任务模板：模板号 + 名称 + 起点/终点 + 默认优先级
CREATE TABLE IF NOT EXISTS tcs_task_template (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    code          VARCHAR(64)  NOT NULL COMMENT '模板号',
    name          VARCHAR(100) NOT NULL COMMENT '模板名称',
    source_point  VARCHAR(128) NOT NULL COMMENT '起点点位ID',
    dest_point    VARCHAR(128) NOT NULL COMMENT '终点点位ID',
    priority      INT                   DEFAULT NULL COMMENT '默认优先级，越大越优先',
    enabled       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用',
    remark        VARCHAR(200)          DEFAULT NULL COMMENT '备注',
    create_by     BIGINT                COMMENT '创建者',
    create_time   DATETIME              COMMENT '创建时间',
    update_by     BIGINT                COMMENT '更新者',
    update_time   DATETIME              COMMENT '更新时间',
    del_flag      CHAR(1)               DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
    PRIMARY KEY (id),
    KEY idx_task_template_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务模板';
