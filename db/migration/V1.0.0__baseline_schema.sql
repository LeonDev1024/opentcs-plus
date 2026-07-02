-- ============================================================
-- OpenTCS Plus Flyway Baseline Schema V1.0.0
-- 由 script/db/assemble-baseline.sh 自动生成，请勿手工编辑
-- 源: script/mysql + script/db/patches/V1.0.0_extensions.sql
-- ============================================================

SET NAMES utf8mb4;


-- ---- opentcs_system_ddl_v2.0.sql ----
-- ============================================================
-- OpenTCS Plus 系统管理模块 SQL (全新创建)
-- 版本: v2.0
-- 描述: 系统管理相关表结构，包含用户、角色、菜单、部门等
-- ============================================================

USE opentcsplus;

-- ============================================================
-- 删除所有相关表（按外键依赖顺序）
-- ============================================================
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_user_post;
DROP TABLE IF EXISTS sys_role_dept;
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_social;
DROP TABLE IF EXISTS sys_logininfor;
DROP TABLE IF EXISTS sys_oper_log;
DROP TABLE IF EXISTS sys_notice;
DROP TABLE IF EXISTS sys_oss;
DROP TABLE IF EXISTS sys_oss_config;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_post;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS sys_dict_data;
DROP TABLE IF EXISTS sys_dict_type;
DROP TABLE IF EXISTS sys_dept;
DROP TABLE IF EXISTS sys_config;
DROP TABLE IF EXISTS sys_client;

-- ============================================================
-- 系统授权表 (SysClient)
-- ============================================================
CREATE TABLE sys_client (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    client_id       VARCHAR(64)     NULL COMMENT '客户端ID',
    client_key      VARCHAR(32)     NULL COMMENT '客户端Key',
    client_secret   VARCHAR(255)    NULL COMMENT '客户端密钥',
    grant_type      VARCHAR(255)    NULL COMMENT '授权类型',
    device_type     VARCHAR(32)     NULL COMMENT '设备类型',
    active_timeout  INT             DEFAULT 1800 COMMENT 'token活跃超时时间(秒)',
    timeout         INT             DEFAULT 604800 COMMENT 'token固定超时时间(秒)',
    status          CHAR(1)         DEFAULT '0' COMMENT '状态(0正常 1停用)',
    del_flag        CHAR(1)         DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT pk_sys_client PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统授权表';

-- ============================================================
-- 参数配置表 (SysConfig)
-- ============================================================
CREATE TABLE sys_config (
    config_id       BIGINT          NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    config_name     VARCHAR(100)    DEFAULT '' COMMENT '参数名称',
    config_key      VARCHAR(100)    DEFAULT '' COMMENT '参数键名',
    config_value    VARCHAR(500)    DEFAULT '' COMMENT '参数键值',
    config_type     CHAR(1)         DEFAULT 'N' COMMENT '系统内置(Y是 N否)',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    NULL COMMENT '备注',
    CONSTRAINT pk_sys_config PRIMARY KEY (config_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

-- ============================================================
-- 部门表 (SysDept)
-- ============================================================
CREATE TABLE sys_dept (
    dept_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    parent_id       BIGINT          DEFAULT 0 COMMENT '父部门ID',
    ancestors       VARCHAR(500)    DEFAULT '' COMMENT '祖级列表',
    dept_name       VARCHAR(30)     DEFAULT '' COMMENT '部门名称',
    dept_category   VARCHAR(100)    NULL COMMENT '部门类别编码',
    order_num       INT             DEFAULT 0 COMMENT '显示顺序',
    leader          BIGINT          NULL COMMENT '负责人',
    phone           VARCHAR(11)     NULL COMMENT '联系电话',
    email           VARCHAR(50)     NULL COMMENT '邮箱',
    status          CHAR(1)         DEFAULT '0' COMMENT '部门状态(0正常 1停用)',
    del_flag        CHAR(1)         DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT pk_sys_dept PRIMARY KEY (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ============================================================
-- 字典类型表 (SysDictType)
-- ============================================================
CREATE TABLE sys_dict_type (
    dict_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    dict_name       VARCHAR(100)    DEFAULT '' COMMENT '字典名称',
    dict_type       VARCHAR(100)    DEFAULT '' COMMENT '字典类型',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    NULL COMMENT '备注',
    CONSTRAINT pk_sys_dict_type PRIMARY KEY (dict_id),
    CONSTRAINT uk_sys_dict_type_dict_type UNIQUE (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ============================================================
-- 字典数据表 (SysDictData)
-- ============================================================
CREATE TABLE sys_dict_data (
    dict_code       BIGINT          NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    dict_sort       INT             DEFAULT 0 COMMENT '字典排序',
    dict_label      VARCHAR(100)    DEFAULT '' COMMENT '字典标签',
    dict_value      VARCHAR(100)    DEFAULT '' COMMENT '字典键值',
    dict_type       VARCHAR(100)    DEFAULT '' COMMENT '字典类型',
    css_class       VARCHAR(100)    NULL COMMENT '样式属性',
    list_class      VARCHAR(100)    NULL COMMENT '表格回显样式',
    is_default      CHAR(1)         DEFAULT 'N' COMMENT '是否默认(Y是 N否)',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    NULL COMMENT '备注',
    CONSTRAINT pk_sys_dict_data PRIMARY KEY (dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ============================================================
-- 系统访问记录表 (SysLogininfor)
-- ============================================================
CREATE TABLE sys_logininfor (
    info_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    user_name       VARCHAR(50)     DEFAULT '' COMMENT '用户账号',
    client_key      VARCHAR(32)     DEFAULT '' COMMENT '客户端Key',
    device_type     VARCHAR(32)     DEFAULT '' COMMENT '设备类型',
    ipaddr          VARCHAR(128)    DEFAULT '' COMMENT '登录IP地址',
    login_location  VARCHAR(255)    DEFAULT '' COMMENT '登录地点',
    browser         VARCHAR(50)     DEFAULT '' COMMENT '浏览器类型',
    os              VARCHAR(50)     DEFAULT '' COMMENT '操作系统',
    status          CHAR(1)         DEFAULT '0' COMMENT '登录状态(0成功 1失败)',
    msg             VARCHAR(255)    DEFAULT '' COMMENT '提示消息',
    login_time      DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    CONSTRAINT pk_sys_logininfor PRIMARY KEY (info_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录表';

CREATE INDEX idx_sys_logininfor_login_time ON sys_logininfor(login_time);
CREATE INDEX idx_sys_logininfor_status ON sys_logininfor(status);

-- ============================================================
-- 菜单权限表 (SysMenu)
-- ============================================================
CREATE TABLE sys_menu (
    menu_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    menu_name       VARCHAR(50)     NOT NULL COMMENT '菜单名称',
    parent_id       BIGINT          DEFAULT 0 COMMENT '父菜单ID',
    order_num       INT             DEFAULT 0 COMMENT '显示顺序',
    path            VARCHAR(200)    DEFAULT '' COMMENT '路由地址',
    component       VARCHAR(255)    NULL COMMENT '组件路径',
    query_param     VARCHAR(255)    NULL COMMENT '路由参数',
    is_frame        INT             DEFAULT 1 COMMENT '是否为外链(0是 1否)',
    is_cache        INT             DEFAULT 0 COMMENT '是否缓存(0缓存 1不缓存)',
    menu_type       CHAR(1)         DEFAULT '' COMMENT '菜单类型(M目录 C菜单 F按钮)',
    visible         CHAR(1)         DEFAULT '0' COMMENT '显示状态(0显示 1隐藏)',
    status          CHAR(1)         DEFAULT '0' COMMENT '菜单状态(0正常 1停用)',
    perms           VARCHAR(100)    NULL COMMENT '权限标识',
    icon            VARCHAR(100)    DEFAULT '#' COMMENT '菜单图标',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    CONSTRAINT pk_sys_menu PRIMARY KEY (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ============================================================
-- 通知公告表 (SysNotice)
-- ============================================================
CREATE TABLE sys_notice (
    notice_id       BIGINT          NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    notice_title    VARCHAR(50)     NOT NULL COMMENT '公告标题',
    notice_type     CHAR(1)         NOT NULL COMMENT '公告类型(1通知 2公告)',
    notice_content  TEXT            NULL COMMENT '公告内容',
    status          CHAR(1)         DEFAULT '0' COMMENT '公告状态(0正常 1关闭)',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(255)     NULL COMMENT '备注',
    CONSTRAINT pk_sys_notice PRIMARY KEY (notice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- ============================================================
-- 操作日志记录表 (SysOperLog)
-- ============================================================
CREATE TABLE sys_oper_log (
    oper_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    title           VARCHAR(50)     DEFAULT '' COMMENT '模块标题',
    business_type   INT             DEFAULT 0 COMMENT '业务类型(0其它 1新增 2修改 3删除)',
    method          VARCHAR(100)    DEFAULT '' COMMENT '方法名称',
    request_method  VARCHAR(10)     DEFAULT '' COMMENT '请求方式',
    operator_type   INT             DEFAULT 0 COMMENT '操作类别(0其它 1后台用户 2手机端用户)',
    oper_name       VARCHAR(50)     DEFAULT '' COMMENT '操作人员',
    dept_name       VARCHAR(50)     DEFAULT '' COMMENT '部门名称',
    oper_url        VARCHAR(255)    DEFAULT '' COMMENT '请求URL',
    oper_ip         VARCHAR(128)    DEFAULT '' COMMENT '主机地址',
    oper_location   VARCHAR(255)    DEFAULT '' COMMENT '操作地点',
    oper_param      VARCHAR(4000)   DEFAULT '' COMMENT '请求参数',
    json_result     VARCHAR(4000)   DEFAULT '' COMMENT '返回参数',
    status          INT             DEFAULT 0 COMMENT '操作状态(0正常 1异常)',
    error_msg       VARCHAR(4000)   DEFAULT '' COMMENT '错误消息',
    oper_time       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    cost_time       BIGINT          DEFAULT 0 COMMENT '消耗时间(毫秒)',
    CONSTRAINT pk_sys_oper_log PRIMARY KEY (oper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录表';

CREATE INDEX idx_sys_oper_log_business_type ON sys_oper_log(business_type);
CREATE INDEX idx_sys_oper_log_oper_time ON sys_oper_log(oper_time);
CREATE INDEX idx_sys_oper_log_status ON sys_oper_log(status);

-- ============================================================
-- OSS对象存储表 (SysOss)
-- ============================================================
CREATE TABLE sys_oss (
    oss_id          BIGINT          NOT NULL AUTO_INCREMENT COMMENT '对象存储主键',
    file_name       VARCHAR(255)    NOT NULL COMMENT '文件名',
    original_name   VARCHAR(255)    NOT NULL COMMENT '原文件名',
    file_suffix     VARCHAR(10)     NOT NULL COMMENT '文件后缀名',
    url             VARCHAR(500)    NOT NULL COMMENT 'URL地址',
    service         VARCHAR(20)     DEFAULT 'minio' COMMENT '服务商',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '上传人',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新人',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT pk_sys_oss PRIMARY KEY (oss_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OSS对象存储表';

-- ============================================================
-- 对象存储配置表 (SysOssConfig)
-- ============================================================
CREATE TABLE sys_oss_config (
    oss_config_id   BIGINT          NOT NULL AUTO_INCREMENT COMMENT '配置主键',
    config_key      VARCHAR(20)     NOT NULL COMMENT '配置Key',
    access_key      VARCHAR(255)    NULL COMMENT 'AccessKey',
    secret_key      VARCHAR(255)    NULL COMMENT 'SecretKey',
    bucket_name     VARCHAR(255)    NULL COMMENT '桶名称',
    prefix          VARCHAR(255)    NULL COMMENT '前缀',
    endpoint        VARCHAR(255)    NULL COMMENT '访问站点',
    domain          VARCHAR(255)    NULL COMMENT '自定义域名',
    is_https        CHAR(1)         DEFAULT 'N' COMMENT '是否HTTPS(Y是 N否)',
    region          VARCHAR(255)    NULL COMMENT '区域',
    access_policy   CHAR(1)         DEFAULT '1' COMMENT '桶权限类型(0=private 1=public 2=custom)',
    status          CHAR(1)         DEFAULT '1' COMMENT '是否默认(0=是 1=否)',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    NULL COMMENT '备注',
    CONSTRAINT pk_sys_oss_config PRIMARY KEY (oss_config_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对象存储配置表';

-- ============================================================
-- 岗位信息表 (SysPost)
-- ============================================================
CREATE TABLE sys_post (
    post_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    dept_id         BIGINT          NOT NULL COMMENT '部门ID',
    post_code       VARCHAR(64)     NOT NULL COMMENT '岗位编码',
    post_category   VARCHAR(100)    NULL COMMENT '岗位类别编码',
    post_name       VARCHAR(50)     NOT NULL COMMENT '岗位名称',
    post_sort       INT             NOT NULL COMMENT '显示顺序',
    status          CHAR(1)         NOT NULL COMMENT '状态(0正常 1停用)',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    NULL COMMENT '备注',
    CONSTRAINT pk_sys_post PRIMARY KEY (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位信息表';

-- ============================================================
-- 角色信息表 (SysRole)
-- ============================================================
CREATE TABLE sys_role (
    role_id             BIGINT          NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name           VARCHAR(30)     NOT NULL COMMENT '角色名称',
    role_key            VARCHAR(100)    NOT NULL COMMENT '角色权限字符串',
    role_sort           INT             NOT NULL COMMENT '显示顺序',
    data_scope          CHAR(1)         DEFAULT '1' COMMENT '数据范围(1全部 2自定 3本部门 4本部门及以下 5仅本人 6本人及部门)',
    menu_check_strictly TINYINT(1)      DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
    dept_check_strictly TINYINT(1)      DEFAULT 1 COMMENT '部门树选择项是否关联显示',
    status              CHAR(1)         NOT NULL COMMENT '角色状态(0正常 1停用)',
    del_flag            CHAR(1)         DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    create_dept         BIGINT          NULL COMMENT '创建部门',
    create_by           BIGINT          NULL COMMENT '创建者',
    create_time         DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by           BIGINT          NULL COMMENT '更新者',
    update_time         DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark              VARCHAR(500)    NULL COMMENT '备注',
    CONSTRAINT pk_sys_role PRIMARY KEY (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ============================================================
-- 角色和部门关联表 (SysRoleDept)
-- ============================================================
CREATE TABLE sys_role_dept (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    dept_id BIGINT NOT NULL COMMENT '部门ID',
    CONSTRAINT pk_sys_role_dept PRIMARY KEY (role_id, dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和部门关联表';

-- ============================================================
-- 角色和菜单关联表 (SysRoleMenu)
-- ============================================================
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    CONSTRAINT pk_sys_role_menu PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

-- ============================================================
-- 社会化关系表 (SysSocial)
-- ============================================================
CREATE TABLE sys_social (
    id                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id            BIGINT          NOT NULL COMMENT '用户ID',
    auth_id            VARCHAR(255)    NOT NULL COMMENT '平台+平台唯一ID',
    source             VARCHAR(255)    NOT NULL COMMENT '用户来源',
    open_id            VARCHAR(255)    NULL COMMENT '平台编号唯一ID',
    user_name          VARCHAR(30)     NOT NULL COMMENT '登录账号',
    nick_name          VARCHAR(30)     DEFAULT '' COMMENT '用户昵称',
    email              VARCHAR(255)    DEFAULT '' COMMENT '用户邮箱',
    avatar             VARCHAR(500)    DEFAULT '' COMMENT '头像地址',
    access_token       VARCHAR(2000)   NOT NULL COMMENT '授权令牌',
    expire_in          INT             NULL COMMENT '令牌有效期',
    refresh_token      VARCHAR(255)    NULL COMMENT '刷新令牌',
    access_code        VARCHAR(2000)   NULL COMMENT '授权Code',
    union_id           VARCHAR(255)    NULL COMMENT 'UnionID',
    scope              VARCHAR(255)    NULL COMMENT '授权范围',
    token_type         VARCHAR(255)    NULL COMMENT '令牌类型',
    id_token           VARCHAR(2000)   NULL COMMENT 'ID令牌',
    mac_algorithm      VARCHAR(255)    NULL COMMENT '小米平台附带属性',
    mac_key            VARCHAR(255)    NULL COMMENT '小米平台附带属性',
    code               VARCHAR(255)    NULL COMMENT '授权Code',
    oauth_token        VARCHAR(255)    NULL COMMENT 'Twitter附带属性',
    oauth_token_secret VARCHAR(255)    NULL COMMENT 'Twitter附带属性',
    create_dept        BIGINT          NULL COMMENT '创建部门',
    create_by          BIGINT          NULL COMMENT '创建者',
    create_time        DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by          BIGINT          NULL COMMENT '更新者',
    update_time        DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag           CHAR(1)         DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    CONSTRAINT pk_sys_social PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社会化关系表';

-- ============================================================
-- 用户信息表 (SysUser)
-- ============================================================
CREATE TABLE sys_user (
    user_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    dept_id         BIGINT          NULL COMMENT '部门ID',
    user_name       VARCHAR(30)     NOT NULL COMMENT '用户账号',
    nick_name       VARCHAR(30)     NOT NULL COMMENT '用户昵称',
    user_type       VARCHAR(10)     DEFAULT 'sys_user' COMMENT '用户类型',
    email           VARCHAR(50)     DEFAULT '' COMMENT '用户邮箱',
    phonenumber     VARCHAR(11)     DEFAULT '' COMMENT '手机号码',
    sex             CHAR(1)         DEFAULT '0' COMMENT '用户性别(0男 1女 2未知)',
    avatar          BIGINT          NULL COMMENT '头像ID',
    password        VARCHAR(100)    DEFAULT '' COMMENT '密码',
    status          CHAR(1)         DEFAULT '0' COMMENT '帐号状态(0正常 1停用)',
    del_flag        CHAR(1)         DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    login_ip        VARCHAR(128)    DEFAULT '' COMMENT '最后登录IP',
    login_date      DATETIME        NULL COMMENT '最后登录时间',
    create_dept     BIGINT          NULL COMMENT '创建部门',
    create_by       BIGINT          NULL COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       BIGINT          NULL COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    NULL COMMENT '备注',
    CONSTRAINT pk_sys_user PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ============================================================
-- 用户与岗位关联表 (SysUserPost)
-- ============================================================
CREATE TABLE sys_user_post (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '岗位ID',
    CONSTRAINT pk_sys_user_post PRIMARY KEY (user_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与岗位关联表';

-- ============================================================
-- 用户和角色关联表 (SysUserRole)
-- ============================================================
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    CONSTRAINT pk_sys_user_role PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';


-- ---- opentcs_vehicle_ddl_v2.0.sql ----
-- ============================================================
-- OpenTCS Plus 车辆管理 SQL
-- ============================================================

USE opentcsplus;

-- ============================================================
-- 删除所有相关表（按外键依赖顺序）
-- ============================================================
DROP TABLE IF EXISTS tcs_vehicle;
DROP TABLE IF EXISTS tcs_vehicle_type;

-- ============================================================
-- 车辆类型表 (VehicleType) - 配置表，完整审计字段
-- ============================================================
CREATE TABLE tcs_vehicle_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '车辆类型名称',
    length DECIMAL(10,2) DEFAULT NULL COMMENT '车辆长度(mm)',
    width DECIMAL(10,2) DEFAULT NULL COMMENT '车辆宽度(mm)',
    height DECIMAL(10,2) DEFAULT NULL COMMENT '车辆高度(mm)',
    max_velocity DECIMAL(10,2) DEFAULT NULL COMMENT '最大速度(mm/s)',
    max_reverse_velocity DECIMAL(10,2) DEFAULT NULL COMMENT '最大反向速度(mm/s)',
    energy_level DECIMAL(10,2) DEFAULT NULL COMMENT '能量级别(0-100)',
    allowed_orders JSON COMMENT '允许的订单类型',
    allowed_peripheral_operations JSON COMMENT '允许的外围设备操作',
    properties JSON COMMENT '扩展属性',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_vehicle_type PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆类型表';

-- ============================================================
-- 车辆表 (Vehicle) - 业务主表，完整审计字段
-- ============================================================
CREATE TABLE tcs_vehicle (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '车辆名称',
    vin_code VARCHAR(50) DEFAULT NULL COMMENT '车辆VIN码',
    vehicle_type_id BIGINT NOT NULL COMMENT '车辆类型ID',
    current_position VARCHAR(100) DEFAULT NULL COMMENT '当前位置(点位ID)',
    next_position VARCHAR(100) DEFAULT NULL COMMENT '下一个位置(点位ID)',
    state VARCHAR(30) DEFAULT 'UNKNOWN' COMMENT '车辆状态: UNKNOWN, UNAVAILABLE, IDLE, CHARGING, WORKING, ERROR',
    integration_level VARCHAR(30) DEFAULT 'TO_BE_IGNORED' COMMENT '集成级别: TO_BE_IGNORED, TO_BE_NOTICED, TO_BE_RESPECTED, TO_BE_UTILIZED',
    energy_level DECIMAL(10,2) DEFAULT 100.00 COMMENT '能量级别(0-100)',
    current_transport_order VARCHAR(100) DEFAULT NULL COMMENT '当前运输订单ID',
    properties JSON COMMENT '扩展属性',
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT DEFAULT 0 COMMENT '乐观锁版本',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    CONSTRAINT pk_vehicle PRIMARY KEY (id),
    CONSTRAINT fk_vehicle_type FOREIGN KEY (vehicle_type_id) REFERENCES tcs_vehicle_type(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 插入默认车辆类型
INSERT INTO tcs_vehicle_type (name, length, width, height, max_velocity, max_reverse_velocity, energy_level, allowed_orders, allowed_peripheral_operations) VALUES
('默认车型', 1000.00, 500.00, 500.00, 2000.00, 1000.00, 100.00, '[]', '[]');


-- ---- opentcs_factory_model_ddl_v2.0.sql ----
-- ============================================================
-- OpenTCS Plus 工厂模型 SQL (全新创建)
-- ============================================================

USE opentcsplus;

-- ============================================================
-- 删除所有相关表（按外键依赖顺序）
-- ============================================================
DROP TABLE IF EXISTS tcs_elevator_schedule;
DROP TABLE IF EXISTS tcs_cross_layer_connection;
DROP TABLE IF EXISTS tcs_location;
DROP TABLE IF EXISTS tcs_block;
DROP TABLE IF EXISTS tcs_point;
DROP TABLE IF EXISTS tcs_path;
DROP TABLE IF EXISTS tcs_factory_layer;
DROP TABLE IF EXISTS tcs_factory_layer_group;
DROP TABLE IF EXISTS tcs_location_type;
DROP TABLE IF EXISTS tcs_navigation_map;
DROP TABLE IF EXISTS tcs_factory_model;

-- ============================================================
-- 工厂模型表 (FactoryModel)
-- ============================================================
CREATE TABLE tcs_factory_model (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    factory_id VARCHAR(64) NOT NULL COMMENT '工厂唯一标识符',
    name VARCHAR(255) NOT NULL COMMENT '工厂名称',
    model_version VARCHAR(50) NOT NULL DEFAULT '1.0' COMMENT '模型版本',
    scale DECIMAL(10,4) NOT NULL DEFAULT 50.0 COMMENT '比例尺 (px/m)',
    coordinate_system VARCHAR(50) DEFAULT 'RIGHT_HAND' COMMENT '坐标系',
    length_unit VARCHAR(20) DEFAULT 'METER' COMMENT '长度单位',
    properties JSON COMMENT '扩展属性',
    description VARCHAR(1000) COMMENT '描述',
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 0,
    del_flag CHAR(1) DEFAULT '0',
    status CHAR(1) DEFAULT '0',
    CONSTRAINT uk_factory_model_factory_id UNIQUE (factory_id),
    CONSTRAINT uk_factory_model_name UNIQUE (name),
    CONSTRAINT pk_factory_model PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工厂模型表';

-- ============================================================
-- 导航地图表 (NavigationMap)
-- ============================================================
CREATE TABLE tcs_navigation_map (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    factory_model_id BIGINT NOT NULL COMMENT '所属工厂模型ID',
    map_id VARCHAR(64) NOT NULL COMMENT '地图唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '地图名称',
    floor_number INT COMMENT '楼层号',
    map_type VARCHAR(50) NOT NULL DEFAULT 'INDOOR' COMMENT '地图类型',
    origin_x DECIMAL(12,4) DEFAULT 0 COMMENT 'PGM原点X',
    origin_y DECIMAL(12,4) DEFAULT 0 COMMENT 'PGM原点Y',
    properties JSON COMMENT '扩展属性',
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 0,
    del_flag CHAR(1) DEFAULT '0',
    status CHAR(1) DEFAULT '0',
    CONSTRAINT pk_navigation_map PRIMARY KEY (id),
    CONSTRAINT fk_navigation_map_factory FOREIGN KEY (factory_model_id) REFERENCES tcs_factory_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_navigation_map_factory_map UNIQUE (factory_model_id, map_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导航地图表';

-- ============================================================
-- 位置类型表 (LocationType) - 全局共享，不按工厂隔离
-- ============================================================
CREATE TABLE tcs_location_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(255) NOT NULL COMMENT '位置类型名称',
    allowed_operations JSON COMMENT '允许的操作列表',
    allowed_peripheral_operations JSON COMMENT '允许的外围设备操作',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_location_type PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='位置类型表';

-- ============================================================
-- 图层组表 (LayerGroup)
-- ============================================================
CREATE TABLE tcs_factory_layer_group (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '所属导航地图ID',
    name VARCHAR(255) NOT NULL COMMENT '图层组名称',
    visible TINYINT(1) DEFAULT 1 COMMENT '是否可见',
    ordinal INT DEFAULT 0 COMMENT '显示顺序',
    properties JSON COMMENT '扩展属性',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_factory_layer_group PRIMARY KEY (id),
    CONSTRAINT fk_factory_layer_group_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES tcs_navigation_map(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图层组表';

-- ============================================================
-- 图层表 (Layer)
-- ============================================================
CREATE TABLE tcs_factory_layer (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '所属导航地图ID',
    layer_group_id BIGINT COMMENT '所属图层组ID',
    name VARCHAR(255) NOT NULL COMMENT '图层名称',
    visible TINYINT(1) DEFAULT 1 COMMENT '是否可见',
    ordinal INT DEFAULT 0 COMMENT '显示顺序',
    properties JSON COMMENT '扩展属性',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_factory_layer PRIMARY KEY (id),
    CONSTRAINT fk_factory_layer_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES tcs_navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_factory_layer_layer_group FOREIGN KEY (layer_group_id) REFERENCES tcs_factory_layer_group(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图层表';

-- ============================================================
-- 点位表 (Point)
-- ============================================================
CREATE TABLE tcs_point (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '归属导航地图ID',
    layer_id BIGINT COMMENT '归属图层ID',
    point_id VARCHAR(255) NOT NULL COMMENT '点位唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '点位名称',
    x_position DECIMAL(12,4) NOT NULL COMMENT 'X坐标',
    y_position DECIMAL(12,4) NOT NULL COMMENT 'Y坐标',
    z_position DECIMAL(12,4) DEFAULT 0 COMMENT 'Z坐标',
    vehicle_orientation DECIMAL(8,4) DEFAULT 0 COMMENT '车辆方向角度',
    type VARCHAR(50) NOT NULL DEFAULT 'HALT_POSITION' COMMENT '点位类型',
    radius DECIMAL(8,4) DEFAULT 0 COMMENT '点位半径',
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_blocked TINYINT(1) DEFAULT 0 COMMENT '是否被阻塞',
    is_occupied TINYINT(1) DEFAULT 0 COMMENT '是否被占用',
    label VARCHAR(500) COMMENT '标签',
    layout JSON COMMENT '点位布局数据',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_point PRIMARY KEY (id),
    CONSTRAINT fk_point_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES tcs_navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_point_layer FOREIGN KEY (layer_id) REFERENCES tcs_factory_layer(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点位表';

-- ============================================================
-- 路径表 (Path)
-- ============================================================
CREATE TABLE tcs_path (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '归属导航地图ID',
    layer_id BIGINT COMMENT '归属图层ID',
    path_id VARCHAR(255) NOT NULL COMMENT '路径唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '路径名称',
    source_point_id VARCHAR(255) NOT NULL COMMENT '起始点位标识',
    dest_point_id VARCHAR(255) NOT NULL COMMENT '目标点位标识',
    length DECIMAL(12,4) NOT NULL COMMENT '路径长度',
    max_velocity DECIMAL(8,4) COMMENT '最大允许速度',
    max_reverse_velocity DECIMAL(8,4) COMMENT '最大反向速度',
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_blocked TINYINT(1) DEFAULT 0 COMMENT '是否被阻塞',
    layout JSON COMMENT '路径布局（connectionType + controlPoints）',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_path PRIMARY KEY (id),
    CONSTRAINT fk_path_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES tcs_navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_path_layer FOREIGN KEY (layer_id) REFERENCES tcs_factory_layer(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路径表';

-- ============================================================
-- 位置表 (Location)
-- ============================================================
CREATE TABLE tcs_location (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    navigation_map_id BIGINT NOT NULL COMMENT '归属导航地图ID',
    layer_id BIGINT COMMENT '归属图层ID',
    location_type_id BIGINT NOT NULL COMMENT '位置类型ID',
    location_id VARCHAR(255) NOT NULL COMMENT '位置唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '位置名称',
    position_x DECIMAL(12,4) COMMENT 'X坐标',
    position_y DECIMAL(12,4) COMMENT 'Y坐标',
    position_z DECIMAL(12,4) DEFAULT 0 COMMENT 'Z坐标',
    locked TINYINT(1) DEFAULT 0 COMMENT '是否被锁定',
    is_occupied TINYINT(1) DEFAULT 0 COMMENT '是否被占用',
    layout JSON COMMENT '位置布局数据',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_location PRIMARY KEY (id),
    CONSTRAINT fk_location_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES tcs_navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_location_layer FOREIGN KEY (layer_id) REFERENCES tcs_factory_layer(id) ON DELETE SET NULL,
    CONSTRAINT fk_location_type FOREIGN KEY (location_type_id) REFERENCES tcs_location_type(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='位置表';

-- ============================================================
-- 区块表 (Block)
-- ============================================================
CREATE TABLE tcs_block (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    factory_model_id BIGINT NOT NULL COMMENT '所属工厂ID',
    navigation_map_id BIGINT COMMENT '所属地图ID',
    name VARCHAR(255) NOT NULL COMMENT '区块名称',
    type VARCHAR(50) NOT NULL DEFAULT 'SINGLE' COMMENT '区块类型',
    members JSON COMMENT '成员点位的point_id列表',
    color VARCHAR(20) COMMENT '区块显示颜色',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_block PRIMARY KEY (id),
    CONSTRAINT fk_block_factory FOREIGN KEY (factory_model_id) REFERENCES tcs_factory_model(id) ON DELETE CASCADE,
    CONSTRAINT fk_block_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES tcs_navigation_map(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区块表';

-- ============================================================
-- 跨层连接表 (CrossLayerConnection)
-- ============================================================
CREATE TABLE tcs_cross_layer_connection (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    factory_model_id BIGINT NOT NULL COMMENT '所属工厂ID',
    connection_id VARCHAR(64) NOT NULL COMMENT '连接唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '连接名称',
    connection_type VARCHAR(50) NOT NULL COMMENT 'ELEVATOR/CONVEYOR/PHYSICAL_DOOR',
    source_navigation_map_id BIGINT NOT NULL COMMENT '源地图ID',
    source_point_id VARCHAR(255) NOT NULL COMMENT '源点位ID',
    source_floor INT NOT NULL COMMENT '源楼层',
    dest_navigation_map_id BIGINT NOT NULL COMMENT '目标地图ID',
    dest_point_id VARCHAR(255) NOT NULL COMMENT '目标点位ID',
    dest_floor INT NOT NULL COMMENT '目标楼层',
    capacity INT DEFAULT 1 COMMENT '容量',
    max_weight DECIMAL(10,2) COMMENT '最大承重',
    travel_time INT COMMENT '运行时间（秒）',
    available TINYINT(1) DEFAULT 1 COMMENT '是否可用',
    current_load INT DEFAULT 0 COMMENT '当前负载',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    CONSTRAINT pk_cross_layer_connection PRIMARY KEY (id),
    CONSTRAINT fk_clc_factory FOREIGN KEY (factory_model_id) REFERENCES tcs_factory_model(id) ON DELETE CASCADE,
    CONSTRAINT fk_clc_source_map FOREIGN KEY (source_navigation_map_id) REFERENCES tcs_navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_clc_dest_map FOREIGN KEY (dest_navigation_map_id) REFERENCES tcs_navigation_map(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跨层连接表';

-- ============================================================
-- 电梯调度记录表 (ElevatorSchedule)
-- ============================================================
CREATE TABLE tcs_elevator_schedule (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    connection_id VARCHAR(64) NOT NULL COMMENT '跨层连接ID',
    vehicle_id BIGINT COMMENT '预约车辆ID',
    vehicle_name VARCHAR(255) COMMENT '预约车辆名称',
    source_floor INT NOT NULL COMMENT '源楼层',
    dest_floor INT NOT NULL COMMENT '目标楼层',
    schedule_type VARCHAR(50) DEFAULT 'RESERVE' COMMENT '调度类型',
    pickup_time DATETIME COMMENT '预计接载时间',
    delivery_time DATETIME COMMENT '预计送达时间',
    actual_pickup_time DATETIME COMMENT '实际接载时间',
    actual_delivery_time DATETIME COMMENT '实际送达时间',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT '状态',
    properties JSON COMMENT '扩展属性',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_elevator_schedule PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电梯调度记录表';

-- 创建索引
CREATE INDEX idx_point_navigation_map ON tcs_point(navigation_map_id);
CREATE INDEX idx_point_layer ON tcs_point(layer_id);
CREATE INDEX idx_path_navigation_map ON tcs_path(navigation_map_id);
CREATE INDEX idx_location_navigation_map ON tcs_location(navigation_map_id);
CREATE INDEX idx_block_factory ON tcs_block(factory_model_id);
CREATE INDEX idx_block_navigation_map ON tcs_block(navigation_map_id);
CREATE INDEX idx_clc_factory ON tcs_cross_layer_connection(factory_model_id);
CREATE INDEX idx_elevator_connection ON tcs_elevator_schedule(connection_id);
CREATE INDEX idx_elevator_vehicle ON tcs_elevator_schedule(vehicle_id);
CREATE INDEX idx_elevator_status ON tcs_elevator_schedule(status);


-- ---- opentcs_transport_order_ddl.sql ----
-- 运输订单表
CREATE TABLE IF NOT EXISTS tcs_transport_order (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) DEFAULT NULL COMMENT '订单名称',
    order_no VARCHAR(100) DEFAULT NULL COMMENT '订单编号',
    state VARCHAR(50) DEFAULT 'RAW' COMMENT '订单状态：RAW, ACTIVE, FINISHED, FAILED',
    intended_vehicle VARCHAR(100) DEFAULT NULL COMMENT '指定车辆',
    processing_vehicle VARCHAR(100) DEFAULT NULL COMMENT '处理车辆',
    vehicle_vin VARCHAR(100) DEFAULT NULL COMMENT '车辆VIN',
    destinations TEXT DEFAULT NULL COMMENT '目的地序列',
    creation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    finished_time DATETIME DEFAULT NULL COMMENT '完成时间',
    deadline DATETIME DEFAULT NULL COMMENT '截止时间',
    properties TEXT DEFAULT NULL COMMENT '扩展属性',
    -- 审计字段
    create_dept BIGINT COMMENT '创建部门',
    create_by BIGINT COMMENT '创建者',
    update_by BIGINT COMMENT '更新者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 0,
    del_flag CHAR(1) DEFAULT '0',
    status CHAR(1) DEFAULT '0',
    INDEX idx_order_no (order_no),
    INDEX idx_processing_vehicle (processing_vehicle),
    INDEX idx_state (state)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运输订单表';

-- ---- V1.0.0_extensions.sql ----
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
CREATE INDEX idx_location_layer ON tcs_location(layer_id);

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
    website     VARCHAR(255)          COMMENT '官网',
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
