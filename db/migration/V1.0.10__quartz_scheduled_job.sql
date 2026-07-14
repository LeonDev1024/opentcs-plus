-- 定时任务：Quartz 表结构 + 菜单改造（任务调度中心 → 定时任务）

CREATE TABLE IF NOT EXISTS `sys_job` (
  `job_id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name`        varchar(64)  NOT NULL DEFAULT ''    COMMENT '任务名称',
  `job_group`       varchar(64)  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target`   varchar(500) NOT NULL               COMMENT '调用目标（beanName.method(params)）',
  `cron_expression` varchar(255)          DEFAULT ''    COMMENT 'cron表达式',
  `misfire_policy`  varchar(20)           DEFAULT '3'   COMMENT '错误策略（1立即 2执行一次 3放弃）',
  `concurrent`      char(1)               DEFAULT '1'   COMMENT '并发（0允许 1禁止）',
  `status`          char(1)               DEFAULT '0'   COMMENT '状态（0正常 1暂停）',
  `create_by`       varchar(64)           DEFAULT ''    COMMENT '创建者',
  `create_time`     datetime              DEFAULT NULL  COMMENT '创建时间',
  `update_by`       varchar(64)           DEFAULT ''    COMMENT '更新者',
  `update_time`     datetime              DEFAULT NULL  COMMENT '更新时间',
  `remark`          varchar(500)          DEFAULT ''    COMMENT '备注',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='定时任务调度表';

CREATE TABLE IF NOT EXISTS `sys_job_log` (
  `job_log_id`      bigint       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `job_name`        varchar(64)  NOT NULL               COMMENT '任务名称',
  `job_group`       varchar(64)  NOT NULL               COMMENT '任务组名',
  `invoke_target`   varchar(500) NOT NULL               COMMENT '调用目标',
  `job_message`     varchar(500)          DEFAULT NULL  COMMENT '日志信息',
  `status`          char(1)               DEFAULT '0'   COMMENT '状态（0成功 1失败）',
  `exception_info`  varchar(2000)         DEFAULT ''    COMMENT '异常信息',
  `create_time`     datetime              DEFAULT NULL  COMMENT '执行时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB COMMENT='定时任务日志表';

-- 菜单 120：SnailJob 任务调度中心 → Quartz 定时任务
UPDATE sys_menu
SET menu_name = '定时任务',
    order_num = 3,
    path = 'job',
    component = 'system/monitor/job/index',
    perms = 'monitor:job:list',
    icon = 'job',
    remark = 'Quartz定时任务管理'
WHERE menu_id = 120;

-- 隐藏路由：执行日志
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (134, '定时任务日志', 2, 7, 'job-log', 'system/monitor/job/log', '', 1, 1, 'C', '1', '0', 'monitor:job:list', '#', 103, 1, NOW(), NULL, NULL, '定时任务执行日志');

-- 管理员角色授权隐藏菜单
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 134);
