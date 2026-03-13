-- XXL-JOB v3.3.0
-- PostgreSQL init script
-- 支持 Spring AI 功能

-- Create Tables

-- 1. Job Info Table
CREATE TABLE xxl_job_info (
  id                        SERIAL PRIMARY KEY,
  job_group                 INT NOT NULL,
  job_desc                  VARCHAR(255) NOT NULL,
  add_time                  TIMESTAMP DEFAULT NULL,
  update_time               TIMESTAMP DEFAULT NULL,
  author                    VARCHAR(64) DEFAULT NULL,
  alarm_email               VARCHAR(255) DEFAULT NULL,
  schedule_type             VARCHAR(50) NOT NULL DEFAULT 'NONE',
  schedule_conf             VARCHAR(128) DEFAULT NULL,
  misfire_strategy          VARCHAR(50) NOT NULL DEFAULT 'DO_NOTHING',
  executor_route_strategy   VARCHAR(50) DEFAULT NULL,
  executor_handler          VARCHAR(255) DEFAULT NULL,
  executor_param            VARCHAR(512) DEFAULT NULL,
  executor_block_strategy   VARCHAR(50) DEFAULT NULL,
  executor_timeout          INT NOT NULL DEFAULT 0,
  executor_fail_retry_count INT NOT NULL DEFAULT 0,
  glue_type                 VARCHAR(50) NOT NULL,
  glue_source               TEXT,
  glue_remark               VARCHAR(128) DEFAULT NULL,
  glue_updatetime           TIMESTAMP DEFAULT NULL,
  child_jobid               VARCHAR(255) DEFAULT NULL,
  trigger_status            SMALLINT NOT NULL DEFAULT 0,
  trigger_last_time         BIGINT NOT NULL DEFAULT 0,
  trigger_next_time         BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE xxl_job_info IS '任务信息表';
COMMENT ON COLUMN xxl_job_info.id IS '主键';
COMMENT ON COLUMN xxl_job_info.job_group IS '执行器主键ID';
COMMENT ON COLUMN xxl_job_info.job_desc IS '任务描述';
COMMENT ON COLUMN xxl_job_info.add_time IS '添加时间';
COMMENT ON COLUMN xxl_job_info.update_time IS '更新时间';
COMMENT ON COLUMN xxl_job_info.author IS '作者';
COMMENT ON COLUMN xxl_job_info.alarm_email IS '报警邮件';
COMMENT ON COLUMN xxl_job_info.schedule_type IS '调度类型';
COMMENT ON COLUMN xxl_job_info.schedule_conf IS '调度配置，值含义取决于调度类型';
COMMENT ON COLUMN xxl_job_info.misfire_strategy IS '调度过期策略';
COMMENT ON COLUMN xxl_job_info.executor_route_strategy IS '执行器路由策略';
COMMENT ON COLUMN xxl_job_info.executor_handler IS '执行器任务handler';
COMMENT ON COLUMN xxl_job_info.executor_param IS '执行器任务参数';
COMMENT ON COLUMN xxl_job_info.executor_block_strategy IS '阻塞处理策略';
COMMENT ON COLUMN xxl_job_info.executor_timeout IS '任务执行超时时间，单位秒';
COMMENT ON COLUMN xxl_job_info.executor_fail_retry_count IS '失败重试次数';
COMMENT ON COLUMN xxl_job_info.glue_type IS 'GLUE类型';
COMMENT ON COLUMN xxl_job_info.glue_source IS 'GLUE源代码';
COMMENT ON COLUMN xxl_job_info.glue_remark IS 'GLUE备注';
COMMENT ON COLUMN xxl_job_info.glue_updatetime IS 'GLUE更新时间';
COMMENT ON COLUMN xxl_job_info.child_jobid IS '子任务ID，多个逗号分隔';
COMMENT ON COLUMN xxl_job_info.trigger_status IS '调度状态：0-停止，1-运行';
COMMENT ON COLUMN xxl_job_info.trigger_last_time IS '上次调度时间';
COMMENT ON COLUMN xxl_job_info.trigger_next_time IS '下次调度时间';

CREATE INDEX idx_xxl_job_info_job_group ON xxl_job_info(job_group);
CREATE INDEX idx_xxl_job_info_trigger_status ON xxl_job_info(trigger_status);
CREATE INDEX idx_xxl_job_info_trigger_next_time ON xxl_job_info(trigger_next_time);

-- 2. Job Log Table
CREATE TABLE xxl_job_log (
  id                        BIGSERIAL PRIMARY KEY,
  job_group                 INT NOT NULL,
  job_id                    INT NOT NULL,
  executor_address          VARCHAR(255) DEFAULT NULL,
  executor_handler          VARCHAR(255) DEFAULT NULL,
  executor_param            VARCHAR(512) DEFAULT NULL,
  executor_sharding_param   VARCHAR(20) DEFAULT NULL,
  executor_fail_retry_count INT NOT NULL DEFAULT 0,
  trigger_time              TIMESTAMP DEFAULT NULL,
  trigger_code              INT NOT NULL,
  handle_time               TIMESTAMP DEFAULT NULL,
  handle_code               INT NOT NULL,
  alarm_status              SMALLINT NOT NULL DEFAULT 0,
  process_id                VARCHAR(255) DEFAULT NULL,
  app_name                  VARCHAR(255) DEFAULT NULL,
  log_data                  TEXT
);

COMMENT ON TABLE xxl_job_log IS '任务日志表';
COMMENT ON COLUMN xxl_job_log.id IS '主键';
COMMENT ON COLUMN xxl_job_log.job_group IS '执行器主键ID';
COMMENT ON COLUMN xxl_job_log.job_id IS '任务ID';
COMMENT ON COLUMN xxl_job_log.executor_address IS '执行器地址';
COMMENT ON COLUMN xxl_job_log.executor_handler IS '执行器任务handler';
COMMENT ON COLUMN xxl_job_log.executor_param IS '执行器任务参数';
COMMENT ON COLUMN xxl_job_log.executor_sharding_param IS '执行器任务分片参数';
COMMENT ON COLUMN xxl_job_log.executor_fail_retry_count IS '失败重试次数';
COMMENT ON COLUMN xxl_job_log.trigger_time IS '调度时间';
COMMENT ON COLUMN xxl_job_log.trigger_code IS '调度结果';
COMMENT ON COLUMN xxl_job_log.handle_time IS '执行时间';
COMMENT ON COLUMN xxl_job_log.handle_code IS '执行结果';
COMMENT ON COLUMN xxl_job_log.alarm_status IS '告警状态：0-默认，1-无需告警，2-告警成功，3-告警失败';
COMMENT ON COLUMN xxl_job_log.process_id IS '执行器进程ID';
COMMENT ON COLUMN xxl_job_log.app_name IS '执行器应用名称';
COMMENT ON COLUMN xxl_job_log.log_data IS '日志详情';

CREATE INDEX idx_xxl_job_log_job_group ON xxl_job_log(job_group);
CREATE INDEX idx_xxl_job_log_job_id ON xxl_job_log(job_id);
CREATE INDEX idx_xxl_job_log_trigger_time ON xxl_job_log(trigger_time);
CREATE INDEX idx_xxl_job_log_handle_code ON xxl_job_log(handle_code);

-- 3. Job Log Report Table
CREATE TABLE xxl_job_log_report (
  id            SERIAL PRIMARY KEY,
  trigger_day   DATE NOT NULL,
  running_count INT NOT NULL DEFAULT 0,
  suc_count     INT NOT NULL DEFAULT 0,
  fail_count    INT NOT NULL DEFAULT 0,
  update_time   TIMESTAMP DEFAULT NULL
);

COMMENT ON TABLE xxl_job_log_report IS '任务日志报表';
COMMENT ON COLUMN xxl_job_log_report.id IS '主键';
COMMENT ON COLUMN xxl_job_log_report.trigger_day IS '调度日期';
COMMENT ON COLUMN xxl_job_log_report.running_count IS '运行中任务数';
COMMENT ON COLUMN xxl_job_log_report.suc_count IS '执行成功任务数';
COMMENT ON COLUMN xxl_job_log_report.fail_count IS '执行失败任务数';
COMMENT ON COLUMN xxl_job_log_report.update_time IS '更新时间';

CREATE INDEX idx_xxl_job_log_report_trigger_day ON xxl_job_log_report(trigger_day);

-- 4. Job Log Glue Table
CREATE TABLE xxl_job_logglue (
  id          SERIAL PRIMARY KEY,
  job_id      INT NOT NULL,
  glue_type   VARCHAR(50) DEFAULT NULL,
  glue_source TEXT,
  glue_remark VARCHAR(128) NOT NULL,
  add_time    TIMESTAMP DEFAULT NULL,
  update_time TIMESTAMP DEFAULT NULL
);

COMMENT ON TABLE xxl_job_logglue IS '任务GLUE日志';
COMMENT ON COLUMN xxl_job_logglue.id IS '主键';
COMMENT ON COLUMN xxl_job_logglue.job_id IS '任务ID';
COMMENT ON COLUMN xxl_job_logglue.glue_type IS 'GLUE类型';
COMMENT ON COLUMN xxl_job_logglue.glue_source IS 'GLUE源代码';
COMMENT ON COLUMN xxl_job_logglue.glue_remark IS 'GLUE备注';
COMMENT ON COLUMN xxl_job_logglue.add_time IS '添加时间';
COMMENT ON COLUMN xxl_job_logglue.update_time IS '更新时间';

CREATE INDEX idx_xxl_job_logglue_job_id ON xxl_job_logglue(job_id);

-- 5. Job Registry Table
CREATE TABLE xxl_job_registry (
  id            SERIAL PRIMARY KEY,
  registry_group VARCHAR(50) NOT NULL,
  registry_key   VARCHAR(255) NOT NULL,
  registry_value VARCHAR(255) NOT NULL,
  update_time    TIMESTAMP DEFAULT NULL
);

COMMENT ON TABLE xxl_job_registry IS '执行器注册表';
COMMENT ON COLUMN xxl_job_registry.id IS '主键';
COMMENT ON COLUMN xxl_job_registry.registry_group IS '注册组';
COMMENT ON COLUMN xxl_job_registry.registry_key IS '注册键';
COMMENT ON COLUMN xxl_job_registry.registry_value IS '注册值';
COMMENT ON COLUMN xxl_job_registry.update_time IS '更新时间';

CREATE INDEX idx_xxl_job_registry_registry_group ON xxl_job_registry(registry_group);
CREATE INDEX idx_xxl_job_registry_registry_key ON xxl_job_registry(registry_key);
CREATE INDEX idx_xxl_job_registry_update_time ON xxl_job_registry(update_time);

-- 6. Job Group Table
CREATE TABLE xxl_job_group (
  id           SERIAL PRIMARY KEY,
  app_name     VARCHAR(64) NOT NULL,
  title        VARCHAR(12) NOT NULL,
  address_type SMALLINT NOT NULL DEFAULT 0,
  address_list TEXT,
  update_time  TIMESTAMP DEFAULT NULL
);

COMMENT ON TABLE xxl_job_group IS '执行器信息表';
COMMENT ON COLUMN xxl_job_group.id IS '主键';
COMMENT ON COLUMN xxl_job_group.app_name IS '执行器AppName';
COMMENT ON COLUMN xxl_job_group.title IS '执行器名称';
COMMENT ON COLUMN xxl_job_group.address_type IS '执行器地址类型：0=自动注册、1=手动录入';
COMMENT ON COLUMN xxl_job_group.address_list IS '执行器地址列表，多地址逗号分隔';
COMMENT ON COLUMN xxl_job_group.update_time IS '更新时间';

-- 7. Job User Table
CREATE TABLE xxl_job_user (
  id         SERIAL PRIMARY KEY,
  username   VARCHAR(50) NOT NULL,
  password   VARCHAR(50) NOT NULL,
  role       SMALLINT NOT NULL,
  permission VARCHAR(255) DEFAULT NULL
);

COMMENT ON TABLE xxl_job_user IS '用户表';
COMMENT ON COLUMN xxl_job_user.id IS '主键';
COMMENT ON COLUMN xxl_job_user.username IS '账号';
COMMENT ON COLUMN xxl_job_user.password IS '密码';
COMMENT ON COLUMN xxl_job_user.role IS '角色：1-普通用户、2-管理员';
COMMENT ON COLUMN xxl_job_user.permission IS '权限：执行器ID列表，多个逗号分割';

CREATE INDEX idx_xxl_job_user_username ON xxl_job_user(username);

-- 8. Job Lock Table
CREATE TABLE xxl_job_lock (
  lock_name VARCHAR(50) NOT NULL PRIMARY KEY
);

COMMENT ON TABLE xxl_job_lock IS '锁表';
COMMENT ON COLUMN xxl_job_lock.lock_name IS '锁名称';

-- Insert Initial Data

-- Insert default job group
INSERT INTO xxl_job_group(app_name, title, address_type, address_list, update_time) 
VALUES ('xxl-job-executor-sample', '示例执行器', 0, NULL, NOW());

-- Insert default user
INSERT INTO xxl_job_user(username, password, role, permission) 
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 2, NULL);

-- Insert locks
INSERT INTO xxl_job_lock(lock_name) VALUES ('schedule_lock');
INSERT INTO xxl_job_lock(lock_name) VALUES ('registry_lock');
INSERT INTO xxl_job_lock(lock_name) VALUES ('report_lock');