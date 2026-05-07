-- ============================================================
-- OpenTCS Plus 全量 Schema 初始化
-- 自动合并生成，执行顺序已按依赖关系排列
-- ============================================================

USE opentcs;


-- ---- opentcs_system_ddl_v2.0.sql ----
-- ============================================================
-- OpenTCS Plus 系统管理模块 SQL (全新创建)
-- 版本: v2.0
-- 描述: 系统管理相关表结构，包含用户、角色、菜单、部门等
-- ============================================================


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


-- ============================================================
-- 删除所有相关表（按外键依赖顺序）
-- ============================================================
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS vehicle_type;

-- ============================================================
-- 车辆类型表 (VehicleType) - 配置表，完整审计字段
-- ============================================================
CREATE TABLE vehicle_type (
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
CREATE TABLE vehicle (
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
    CONSTRAINT fk_vehicle_type FOREIGN KEY (vehicle_type_id) REFERENCES vehicle_type(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 插入默认车辆类型
INSERT INTO vehicle_type (name, length, width, height, max_velocity, max_reverse_velocity, energy_level, allowed_orders, allowed_peripheral_operations) VALUES
('默认车型', 1000.00, 500.00, 500.00, 2000.00, 1000.00, 100.00, '[]', '[]');


-- ---- opentcs_factory_model_ddl_v2.0.sql ----
-- ============================================================
-- OpenTCS Plus 工厂模型 SQL (全新创建)
-- ============================================================


-- ============================================================
-- 删除所有相关表（按外键依赖顺序）
-- ============================================================
DROP TABLE IF EXISTS elevator_schedule;
DROP TABLE IF EXISTS cross_layer_connection;
DROP TABLE IF EXISTS location;
DROP TABLE IF EXISTS block;
DROP TABLE IF EXISTS point;
DROP TABLE IF EXISTS path;
DROP TABLE IF EXISTS factory_layer;
DROP TABLE IF EXISTS factory_layer_group;
DROP TABLE IF EXISTS location_type;
DROP TABLE IF EXISTS navigation_map;
DROP TABLE IF EXISTS factory_model;

-- ============================================================
-- 工厂模型表 (FactoryModel)
-- ============================================================
CREATE TABLE factory_model (
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
CREATE TABLE navigation_map (
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
    CONSTRAINT fk_navigation_map_factory FOREIGN KEY (factory_model_id) REFERENCES factory_model(id) ON DELETE CASCADE,
    CONSTRAINT uk_navigation_map_factory_map UNIQUE (factory_model_id, map_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导航地图表';

-- ============================================================
-- 位置类型表 (LocationType) - 全局共享，不按工厂隔离
-- ============================================================
CREATE TABLE location_type (
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
CREATE TABLE factory_layer_group (
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
    CONSTRAINT fk_factory_layer_group_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图层组表';

-- ============================================================
-- 图层表 (Layer)
-- ============================================================
CREATE TABLE factory_layer (
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
    CONSTRAINT fk_factory_layer_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_factory_layer_layer_group FOREIGN KEY (layer_group_id) REFERENCES factory_layer_group(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图层表';

-- ============================================================
-- 点位表 (Point)
-- ============================================================
CREATE TABLE point (
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
    CONSTRAINT fk_point_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_point_layer FOREIGN KEY (layer_id) REFERENCES factory_layer(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点位表';

-- ============================================================
-- 路径表 (Path)
-- ============================================================
CREATE TABLE path (
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
    CONSTRAINT fk_path_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_path_layer FOREIGN KEY (layer_id) REFERENCES factory_layer(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路径表';

-- ============================================================
-- 位置表 (Location)
-- ============================================================
CREATE TABLE location (
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
    CONSTRAINT fk_location_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_location_layer FOREIGN KEY (layer_id) REFERENCES factory_layer(id) ON DELETE SET NULL,
    CONSTRAINT fk_location_type FOREIGN KEY (location_type_id) REFERENCES location_type(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='位置表';

-- ============================================================
-- 区块表 (Block)
-- ============================================================
CREATE TABLE block (
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
    CONSTRAINT fk_block_factory FOREIGN KEY (factory_model_id) REFERENCES factory_model(id) ON DELETE CASCADE,
    CONSTRAINT fk_block_navigation_map FOREIGN KEY (navigation_map_id) REFERENCES navigation_map(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区块表';

-- ============================================================
-- 跨层连接表 (CrossLayerConnection)
-- ============================================================
CREATE TABLE cross_layer_connection (
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
    CONSTRAINT fk_clc_factory FOREIGN KEY (factory_model_id) REFERENCES factory_model(id) ON DELETE CASCADE,
    CONSTRAINT fk_clc_source_map FOREIGN KEY (source_navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE,
    CONSTRAINT fk_clc_dest_map FOREIGN KEY (dest_navigation_map_id) REFERENCES navigation_map(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跨层连接表';

-- ============================================================
-- 电梯调度记录表 (ElevatorSchedule)
-- ============================================================
CREATE TABLE elevator_schedule (
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
CREATE INDEX idx_point_navigation_map ON point(navigation_map_id);
CREATE INDEX idx_point_layer ON point(layer_id);
CREATE INDEX idx_path_navigation_map ON path(navigation_map_id);
CREATE INDEX idx_location_navigation_map ON location(navigation_map_id);
CREATE INDEX idx_block_factory ON block(factory_model_id);
CREATE INDEX idx_block_navigation_map ON block(navigation_map_id);
CREATE INDEX idx_clc_factory ON cross_layer_connection(factory_model_id);
CREATE INDEX idx_elevator_connection ON elevator_schedule(connection_id);
CREATE INDEX idx_elevator_vehicle ON elevator_schedule(vehicle_id);
CREATE INDEX idx_elevator_status ON elevator_schedule(status);


-- ---- opentcs_transport_order_ddl.sql ----
-- 运输订单表
CREATE TABLE IF NOT EXISTS transport_order (
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


-- ---- opentcs_factory_model_ddl_v2.1_raster.sql ----
-- ============================================================
-- OpenTCS Plus 增量迁移 DDL
-- 导航地图表字段变更
-- ============================================================

-- Step 1: 删除 map_type 字段（目前只支持2D栅格地图）
ALTER TABLE navigation_map
DROP COLUMN IF EXISTS map_type;

-- Step 2: 添加车辆类型ID和地图定位参数字段
ALTER TABLE navigation_map
ADD COLUMN vehicle_type_id BIGINT COMMENT '车辆类型ID（必填，对应vehicle_type.id)' AFTER floor_number,
ADD COLUMN origin_x DECIMAL(12,4) DEFAULT 0 COMMENT '地图原点X坐标(毫米)' AFTER vehicle_type_id,
ADD COLUMN origin_y DECIMAL(12,4) DEFAULT 0 COMMENT '地图原点Y坐标(毫米)' AFTER origin_x,
ADD COLUMN rotation DECIMAL(10,4) DEFAULT 0 COMMENT '地图旋转角度(度)' AFTER origin_y;

-- Step 3: 添加栅格底图相关字段
ALTER TABLE navigation_map
ADD COLUMN raster_url VARCHAR(500) COMMENT '栅格地图OSS存储路径' AFTER rotation,
ADD COLUMN raster_version INT DEFAULT 0 COMMENT '栅格地图版本号' AFTER raster_url,
ADD COLUMN raster_width INT COMMENT '栅格地图宽度(像素)' AFTER raster_version,
ADD COLUMN raster_height INT COMMENT '栅格地图高度(像素)' AFTER raster_width,
ADD COLUMN raster_resolution DECIMAL(12,6) COMMENT '栅格地图分辨率(米/像素)' AFTER raster_height;


-- ---- opentcs_factory_model_migration_v2.2_layer_closure.sql ----
-- OpenTCS Plus
-- v2.2 图层闭环增量迁移脚本（适用于已有数据库）
-- 目标：
-- 1) path/location 增加 layer_id
-- 2) 补充与 factory_layer 的外键关联
-- 3) 增加必要索引，提升按 layer 查询性能

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- path: 增加 layer_id 列（兼容低版本 MySQL）
-- ============================================================
SET @path_layer_col_exists := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'path'
      AND COLUMN_NAME = 'layer_id'
);
SET @sql_add_path_layer_col := IF(
    @path_layer_col_exists = 0,
    'ALTER TABLE path ADD COLUMN layer_id BIGINT COMMENT ''归属图层ID'' AFTER navigation_map_id',
    'SELECT "path.layer_id already exists"'
);
PREPARE stmt_add_path_layer_col FROM @sql_add_path_layer_col;
EXECUTE stmt_add_path_layer_col;
DEALLOCATE PREPARE stmt_add_path_layer_col;

-- path: 增加索引（兼容低版本 MySQL）
SET @idx_path_layer_exists := (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'path'
      AND INDEX_NAME = 'idx_path_layer'
);
SET @sql_add_idx_path_layer := IF(
    @idx_path_layer_exists = 0,
    'CREATE INDEX idx_path_layer ON path(layer_id)',
    'SELECT "idx_path_layer already exists"'
);
PREPARE stmt_add_idx_path_layer FROM @sql_add_idx_path_layer;
EXECUTE stmt_add_idx_path_layer;
DEALLOCATE PREPARE stmt_add_idx_path_layer;

-- path: 增加 layout 列（若不存在）
SET @path_layout_col_exists := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'path'
      AND COLUMN_NAME = 'layout'
);
SET @sql_add_path_layout_col := IF(
    @path_layout_col_exists = 0,
    'ALTER TABLE path ADD COLUMN layout JSON COMMENT ''路径布局（connectionType + controlPoints）'' AFTER is_blocked',
    'SELECT "path.layout already exists"'
);
PREPARE stmt_add_path_layout_col FROM @sql_add_path_layout_col;
EXECUTE stmt_add_path_layout_col;
DEALLOCATE PREPARE stmt_add_path_layout_col;

-- path: 增加外键（若不存在）
SET @fk_path_layer_exists := (
    SELECT COUNT(1)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'path'
      AND CONSTRAINT_NAME = 'fk_path_layer'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql_path_fk := IF(
    @fk_path_layer_exists = 0,
    'ALTER TABLE path ADD CONSTRAINT fk_path_layer FOREIGN KEY (layer_id) REFERENCES factory_layer(id) ON DELETE SET NULL',
    'SELECT "fk_path_layer already exists"'
);
PREPARE stmt_path_fk FROM @sql_path_fk;
EXECUTE stmt_path_fk;
DEALLOCATE PREPARE stmt_path_fk;

-- ============================================================
-- location: 增加 layer_id 列（兼容低版本 MySQL）
-- ============================================================
SET @location_layer_col_exists := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'location'
      AND COLUMN_NAME = 'layer_id'
);
SET @sql_add_location_layer_col := IF(
    @location_layer_col_exists = 0,
    'ALTER TABLE location ADD COLUMN layer_id BIGINT COMMENT ''归属图层ID'' AFTER navigation_map_id',
    'SELECT "location.layer_id already exists"'
);
PREPARE stmt_add_location_layer_col FROM @sql_add_location_layer_col;
EXECUTE stmt_add_location_layer_col;
DEALLOCATE PREPARE stmt_add_location_layer_col;

-- location: 增加索引（兼容低版本 MySQL）
SET @idx_location_layer_exists := (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'location'
      AND INDEX_NAME = 'idx_location_layer'
);
SET @sql_add_idx_location_layer := IF(
    @idx_location_layer_exists = 0,
    'CREATE INDEX idx_location_layer ON location(layer_id)',
    'SELECT "idx_location_layer already exists"'
);
PREPARE stmt_add_idx_location_layer FROM @sql_add_idx_location_layer;
EXECUTE stmt_add_idx_location_layer;
DEALLOCATE PREPARE stmt_add_idx_location_layer;

-- location: 增加 layout 列（若不存在）
SET @location_layout_col_exists := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'location'
      AND COLUMN_NAME = 'layout'
);
SET @sql_add_location_layout_col := IF(
    @location_layout_col_exists = 0,
    'ALTER TABLE location ADD COLUMN layout JSON COMMENT ''位置布局数据'' AFTER is_occupied',
    'SELECT "location.layout already exists"'
);
PREPARE stmt_add_location_layout_col FROM @sql_add_location_layout_col;
EXECUTE stmt_add_location_layout_col;
DEALLOCATE PREPARE stmt_add_location_layout_col;

-- point: 增加 layout 列（若不存在）
SET @point_layout_col_exists := (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'point'
      AND COLUMN_NAME = 'layout'
);
SET @sql_add_point_layout_col := IF(
    @point_layout_col_exists = 0,
    'ALTER TABLE point ADD COLUMN layout JSON COMMENT ''点位布局数据'' AFTER label',
    'SELECT "point.layout already exists"'
);
PREPARE stmt_add_point_layout_col FROM @sql_add_point_layout_col;
EXECUTE stmt_add_point_layout_col;
DEALLOCATE PREPARE stmt_add_point_layout_col;

-- location: 增加外键（若不存在）
SET @fk_location_layer_exists := (
    SELECT COUNT(1)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'location'
      AND CONSTRAINT_NAME = 'fk_location_layer'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @sql_location_fk := IF(
    @fk_location_layer_exists = 0,
    'ALTER TABLE location ADD CONSTRAINT fk_location_layer FOREIGN KEY (layer_id) REFERENCES factory_layer(id) ON DELETE SET NULL',
    'SELECT "fk_location_layer already exists"'
);
PREPARE stmt_location_fk FROM @sql_location_fk;
EXECUTE stmt_location_fk;
DEALLOCATE PREPARE stmt_location_fk;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 数据回填建议（可选，按实际场景执行）
-- ============================================================
-- 1) 已有 path/location 若希望绑定默认图层，可按 navigation_map_id 选第一条 layer 回填：
-- UPDATE path p
-- JOIN (
--   SELECT navigation_map_id, MIN(id) AS default_layer_id
--   FROM factory_layer
--   GROUP BY navigation_map_id
-- ) fl ON fl.navigation_map_id = p.navigation_map_id
-- SET p.layer_id = fl.default_layer_id
-- WHERE p.layer_id IS NULL;
--
-- UPDATE location l
-- JOIN (
--   SELECT navigation_map_id, MIN(id) AS default_layer_id
--   FROM factory_layer
--   GROUP BY navigation_map_id
-- ) fl ON fl.navigation_map_id = l.navigation_map_id
-- SET l.layer_id = fl.default_layer_id
-- WHERE l.layer_id IS NULL;



-- ---- opentcs_factory_model_migration_v2.3_layout_backfill.sql ----
-- OpenTCS Plus
-- v2.3 历史数据 layout 回填脚本（幂等）
-- 目标：将 point/path/location 的历史数据补齐到 layout，统一布局真源

SET NAMES utf8mb4;

-- ============================================================
-- point: layout 为空时，从结构化字段回填
-- ============================================================
UPDATE point
SET layout = JSON_OBJECT(
    'layerId', layer_id,
    'x', x_position,
    'y', y_position,
    'z', z_position,
    'editorProps', JSON_OBJECT(
        'radius', radius,
        'label', label
    )
)
WHERE (layout IS NULL OR JSON_LENGTH(layout) = 0)
  AND del_flag = '0';

-- ============================================================
-- location: layout 为空时，从结构化字段回填
-- ============================================================
UPDATE location
SET layout = JSON_OBJECT(
    'layerId', layer_id,
    'x', position_x,
    'y', position_y,
    'z', position_z
)
WHERE (layout IS NULL OR JSON_LENGTH(layout) = 0)
  AND del_flag = '0';

-- ============================================================
-- path: layout 为空时，从结构化字段回填最小布局
-- 说明：旧数据若无控制点，先写入 connectionType，controlPoints 置空数组
-- ============================================================
UPDATE path
SET layout = JSON_OBJECT(
    'layerId', layer_id,
    'connectionType', 'DIRECT',
    'controlPoints', JSON_ARRAY()
)
WHERE (layout IS NULL OR JSON_LENGTH(layout) = 0)
  AND del_flag = '0';



-- ---- opentcs_factory_model_add_path_layout_ddl.sql ----
-- ============================================================
-- OpenTCS Plus 增量迁移 DDL
-- 路径表新增 layout 字段
-- 用于持久化前端编辑的 path.layoutControlPoints
-- ============================================================


ALTER TABLE path
    ADD COLUMN layout JSON COMMENT '路径布局（connectionType + controlPoints）'
    AFTER properties;



-- ---- opentcs_factory_model_ddl_v2.4_base_layer.sql ----
-- ============================================================
-- OpenTCS Plus 增量迁移 DDL
-- 地图原点与栅格底图字段优化
-- ============================================================

-- Step 1: 添加栅格底图 YAML 相关字段
ALTER TABLE navigation_map
ADD COLUMN yaml_origin JSON COMMENT 'YAML原始origin参数 [ox, oy, angle] (米,度)' AFTER raster_resolution,
ADD COLUMN yaml_url VARCHAR(500) COMMENT 'YAML文件OSS存储路径' AFTER yaml_origin;

-- Step 2: 添加地图原点 JSON 字段（替代 origin_x, origin_y, rotation，保留旧字段兼容）
ALTER TABLE navigation_map
ADD COLUMN map_origin JSON COMMENT '地图在工厂坐标系下的原点偏移 [x, y, angle] (毫米,度)' AFTER yaml_url;


-- ---- opentcs_map_version_ddl.sql ----
-- ============================================================
-- OpenTCS Plus 语义地图版本管理 SQL
-- ============================================================


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


-- ---- sys_menu.sql ----
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '系统管理', 0, 10, 'system', null, '', 1, 0, 'M', '0', '0', '', 'system', 103, 1, '2026-03-16 09:23:42', 1, '2026-03-17 14:38:59', '系统管理目录');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2, '系统监控', 0, 9, 'monitor', null, '', 1, 0, 'M', '0', '0', '', 'monitor', 103, 1, '2026-03-16 09:23:42', 1, '2026-03-17 14:38:52', '系统监控目录');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (6, '租户管理', 0, 2, 'tenant', null, '', 1, 0, 'M', '1', '1', '', 'chart', 103, 1, '2026-03-16 09:23:42', 1, '2026-03-17 14:39:08', '租户管理目录');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 103, 1, '2026-03-16 09:23:43', null, null, '用户管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 103, 1, '2026-03-16 09:23:43', null, null, '角色管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 103, 1, '2026-03-16 09:23:43', null, null, '菜单管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 103, 1, '2026-03-16 09:23:43', null, null, '部门管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 103, 1, '2026-03-16 09:23:43', null, null, '岗位管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 103, 1, '2026-03-16 09:23:43', null, null, '字典管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 103, 1, '2026-03-16 09:23:43', null, null, '参数设置菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 103, 1, '2026-03-16 09:23:44', null, null, '通知公告菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (108, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '0', '0', '', 'log', 103, 1, '2026-03-16 09:23:44', null, null, '日志管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 103, 1, '2026-03-16 09:23:44', null, null, '在线用户菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 103, 1, '2026-03-16 09:23:44', null, null, '缓存监控菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (117, 'Admin监控', 2, 5, 'Admin', 'monitor/admin/index', '', 1, 0, 'C', '0', '0', 'monitor:admin:list', 'dashboard', 103, 1, '2026-03-16 09:23:45', null, null, 'Admin监控菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (118, '文件管理', 1, 10, 'oss', 'system/oss/index', '', 1, 0, 'C', '0', '0', 'system:oss:list', 'upload', 103, 1, '2026-03-16 09:23:45', null, null, '文件管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (120, '任务调度中心', 2, 6, 'snailjob', 'monitor/snailjob/index', '', 1, 0, 'C', '0', '0', 'monitor:snailjob:list', 'job', 103, 1, '2026-03-16 09:23:45', null, null, 'SnailJob控制台菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (121, '租户管理', 6, 1, 'tenant', 'system/tenant/index', '', 1, 0, 'C', '0', '0', 'system:tenant:list', 'list', 103, 1, '2026-03-16 09:23:44', null, null, '租户管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (122, '租户套餐管理', 6, 2, 'tenantPackage', 'system/tenantPackage/index', '', 1, 0, 'C', '0', '0', 'system:tenantPackage:list', 'form', 103, 1, '2026-03-16 09:23:44', null, null, '租户套餐管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (123, '客户端管理', 1, 11, 'client', 'system/client/index', '', 1, 0, 'C', '0', '0', 'system:client:list', 'international', 103, 1, '2026-03-16 09:23:44', null, null, '客户端管理菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (130, '分配用户', 1, 2, 'role-auth/user/:roleId', 'system/role/authUser', '', 1, 1, 'C', '1', '0', 'system:role:edit', '#', 103, 1, '2026-03-16 09:23:45', null, null, '/system/role');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (131, '分配角色', 1, 1, 'user-auth/role/:userId', 'system/user/authRole', '', 1, 1, 'C', '1', '0', 'system:user:edit', '#', 103, 1, '2026-03-16 09:23:45', null, null, '/system/user');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (132, '字典数据', 1, 6, 'dict-data/index/:dictId', 'system/dict/data', '', 1, 1, 'C', '1', '0', 'system:dict:list', '#', 103, 1, '2026-03-16 09:23:45', null, null, '/system/dict');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (133, '文件配置管理', 1, 10, 'oss-config/index', 'system/oss/config', '', 1, 1, 'C', '1', '0', 'system:ossConfig:list', '#', 103, 1, '2026-03-16 09:23:45', null, null, '/system/oss');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (500, '操作日志', 108, 1, 'operlog', 'system/management/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 103, 1, '2026-03-16 09:23:46', null, null, '操作日志菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (501, '登录日志', 108, 2, 'logininfor', 'system/management/logininfor/index', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 103, 1, '2026-03-16 09:23:46', null, null, '登录日志菜单');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1001, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 103, 1, '2026-03-16 09:23:46', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1002, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 103, 1, '2026-03-16 09:23:46', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1003, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 103, 1, '2026-03-16 09:23:46', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1004, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 103, 1, '2026-03-16 09:23:46', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1005, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 103, 1, '2026-03-16 09:23:46', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1006, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1007, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1008, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1009, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1010, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1011, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1012, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1013, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1014, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1015, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1016, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1017, '部门查询', 103, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1018, '部门新增', 103, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1019, '部门修改', 103, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1020, '部门删除', 103, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1021, '岗位查询', 104, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1022, '岗位新增', 104, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1023, '岗位修改', 104, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1024, '岗位删除', 104, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1025, '岗位导出', 104, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1026, '字典查询', 105, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1027, '字典新增', 105, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1028, '字典修改', 105, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1029, '字典删除', 105, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1030, '字典导出', 105, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1031, '参数查询', 106, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1032, '参数新增', 106, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1033, '参数修改', 106, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1034, '参数删除', 106, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1035, '参数导出', 106, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1036, '公告查询', 107, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1037, '公告新增', 107, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1038, '公告修改', 107, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1039, '公告删除', 107, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1040, '操作查询', 500, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1041, '操作删除', 500, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1042, '日志导出', 500, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1043, '登录查询', 501, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1044, '登录删除', 501, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1045, '日志导出', 501, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1046, '在线查询', 109, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1047, '批量强退', 109, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1048, '单条强退', 109, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1050, '账户解锁', 501, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1061, '客户端管理查询', 123, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:query', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1062, '客户端管理新增', 123, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:add', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1063, '客户端管理修改', 123, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:edit', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1064, '客户端管理删除', 123, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:remove', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1065, '客户端管理导出', 123, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:export', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1600, '文件查询', 118, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:query', '#', 103, 1, '2026-03-16 09:23:53', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1601, '文件上传', 118, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:upload', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1602, '文件下载', 118, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:download', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1603, '文件删除', 118, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:remove', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1606, '租户查询', 121, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:query', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1607, '租户新增', 121, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:add', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1608, '租户修改', 121, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:edit', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1609, '租户删除', 121, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:remove', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1610, '租户导出', 121, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:export', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1611, '租户套餐查询', 122, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:query', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1612, '租户套餐新增', 122, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:add', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1613, '租户套餐修改', 122, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:edit', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1614, '租户套餐删除', 122, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:remove', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1615, '租户套餐导出', 122, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:export', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1620, '配置列表', 118, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:list', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1621, '配置添加', 118, 6, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:add', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1622, '配置编辑', 118, 6, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:edit', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1623, '配置删除', 118, 6, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:remove', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033477517960044545, '地图管理', 0, 2, 'map', null, null, 1, 0, 'M', '0', '0', null, 'model', 103, 1, '2026-03-16 17:36:35', 1, '2026-03-17 14:00:36', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033478641265958914, '工厂管理', 2033477517960044545, 1, 'factory', 'opentcs/factory/index', null, 1, 0, 'C', '0', '0', null, 'build', 103, 1, '2026-03-16 17:41:03', 1, '2026-03-17 10:20:33', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033484413261602817, '地图列表', 2033477517960044545, 3, 'maplist', 'opentcs/map/index', null, 1, 0, 'C', '0', '0', null, 'map', 103, 1, '2026-03-16 18:03:59', 1, '2026-03-17 16:52:16', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033484413261602818, '地图新增', 2033484413261602817, 1, '#', '', '', 1, 0, 'F', '0', '0', 'opentcs:map:add', '#', 103, 1, '2026-03-19 13:00:00', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033484413261602819, '地图编辑', 2033484413261602817, 2, '#', '', '', 1, 0, 'F', '0', '0', 'opentcs:map:edit', '#', 103, 1, '2026-03-19 13:00:00', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033484413261602820, '地图删除', 2033484413261602817, 3, '#', '', '', 1, 0, 'F', '0', '0', 'opentcs:map:remove', '#', 103, 1, '2026-03-19 13:00:00', null, null, '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033536149259051009, '路径管理', 2033477517960044545, 4, 'path', null, null, 1, 0, 'M', '0', '0', null, 'dashed-link', 103, 1, '2026-03-16 21:29:34', 1, '2026-03-16 21:32:04', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033536315756142593, '区域管理', 2033477517960044545, 6, 'block', 'opentcs/factory/block', null, 1, 0, 'C', '0', '0', null, 'rule-region', 103, 1, '2026-03-16 21:30:14', 1, '2026-03-16 21:32:28', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033536484035813377, '跨层连接', 2033477517960044545, 5, 'connections', 'opentcs/factory/connections', null, 1, 0, 'C', '0', '0', null, 'dashed-link', 103, 1, '2026-03-16 21:30:54', 1, '2026-03-16 21:32:17', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033730612053004290, '站点类型', 2033477517960044545, 2, 'locationtype', 'opentcs/map/location/index', null, 1, 0, 'C', '0', '0', null, 'maxkey', 103, 1, '2026-03-17 10:22:17', 1, '2026-03-17 11:08:17', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033785761135878145, 'AMR管理', 0, 1, 'vehicle', null, null, 1, 0, 'M', '0', '0', null, 'agv', 103, 1, '2026-03-17 14:01:26', 1, '2026-03-17 14:05:31', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033787339884486658, 'AMR类型', 2033785761135878145, 1, 'vehicletype', 'opentcs/vehicle/type/index', null, 1, 0, 'C', '0', '0', null, 'logininfor', 103, 1, '2026-03-17 14:07:42', 1, '2026-03-17 14:07:42', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033787597502832641, 'AMR列表', 2033785761135878145, 2, 'vehiclelist', 'opentcs/vehicle/index', null, 1, 0, 'C', '0', '0', null, 'list', 103, 1, '2026-03-17 14:08:44', 1, '2026-03-17 16:19:28', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033794847118274562, '任务编排', 0, 3, 'template', null, null, 1, 0, 'M', '0', '0', null, 'category', 103, 1, '2026-03-17 14:37:32', 1, '2026-03-17 14:37:44', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033795781630177282, '运维管理', 0, 4, 'ops', null, null, 1, 0, 'M', '0', '0', null, 'guide', 103, 1, '2026-03-17 14:41:15', 1, '2026-03-17 14:41:15', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033796119204540417, 'AMR管理', 2033795781630177282, 1, 'amrops', 'opentcs/ops/amr/index', null, 1, 0, 'C', '0', '0', null, 'agv', 103, 1, '2026-03-17 14:42:36', 1, '2026-03-17 14:42:57', '');
INSERT INTO opentcs.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033796453461209089, '订单管理', 2033795781630177282, 1, 'orderops', 'opentcs/ops/order/index', null, 1, 0, 'C', '0', '0', null, 'build', 103, 1, '2026-03-17 14:43:55', 1, '2026-03-17 14:43:55', '');


-- ---- fix_utf8_mojibake_sys_menu.sql ----
-- =============================================================================
-- 现象：接口 JSON 里 meta.title 等为 "AMRç®¡ç†" 类乱码，而 msg「操作成功」正常。
-- 原因：多为「UTF-8 字节被按 latin1 读成错字，再被 UTF-8 编码写入」的双层错码。
--       表仍是 utf8mb4，但磁盘上的字节不是「正常中文 UTF-8」。
--       以 HEX 为准：正确「管理」应为 E7AEA1E79086；错码常为 C3A7C2AE...（更长）。
--       客户端若仍显示像中文，请以 HEX 与接口返回对照，勿仅凭肉眼。
--       JDBC 的 SET NAMES 不能纠正已写错的字节。
--
-- 执行前务必备份：
--   mysqldump -u root -p --default-character-set=utf8mb4 opentcs sys_menu > sys_menu_backup.sql
--
-- 客户端请使用：mysql --default-character-set=utf8mb4 -u root -p opentcs
-- =============================================================================

-- 1) 表结构：menu_name / remark 应为 utf8mb4
-- SHOW CREATE TABLE sys_menu\G

-- 2) 抽样看 HEX。正常「管理」UTF-8 的 HEX 约为 E7AEA1E79086（6 字节）
-- SELECT menu_id, menu_name, HEX(menu_name) AS hex_name FROM sys_menu ORDER BY menu_id DESC LIMIT 15;

-- 3) 预览修复（不修改数据）：确认 `fixed` 列是否为预期中文，再考虑第 4 步
--    若本来就是正常中文的行，fixed 会变乱，切勿对全表盲目 UPDATE
SELECT menu_id,
       menu_name AS current_value,
       CONVERT(CAST(CONVERT(menu_name USING latin1) AS BINARY) USING utf8mb4) AS fixed_preview
FROM sys_menu
WHERE menu_name IS NOT NULL AND menu_name <> ''
LIMIT 30;

-- 4) 仅在第 3 步预览正确时执行（可先 START TRANSACTION; ... ROLLBACK; 试跑）
--    若库中「部分行已是正常中文」，不要全表 UPDATE，可只对疑似乱码行加 WHERE（示例，需自行调整）：
--    WHERE menu_name REGEXP '[çåäæèéêëìíîïðñòóôõö]' OR HEX(menu_name) LIKE '%C3%A7%';
-- START TRANSACTION;
-- UPDATE sys_menu
-- SET menu_name = CONVERT(CAST(CONVERT(menu_name USING latin1) AS BINARY) USING utf8mb4),
--     remark = CASE
--         WHEN remark IS NULL OR remark = '' THEN remark
--         ELSE CONVERT(CAST(CONVERT(remark USING latin1) AS BINARY) USING utf8mb4)
--     END;
-- COMMIT;

-- 5) 若表/列仍为 latin1，在数据修复后可统一（按需）
-- ALTER TABLE sys_menu CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


-- ---- opentcs_menu_v3.0_nav_restructure.sql ----
-- ============================================================
-- OpenTCS Plus 菜单结构重构 v3.0
-- 按顶部导航五大模块重组：首页 / 部署管理 / 运维管理 / 运营分析 / 系统管理
-- 执行前请确保新版前端视图文件已部署
-- ============================================================

-- ----------------------------------------------------------------
-- 清理旧的 OpenTCS 业务菜单（保留若依框架原生菜单 id <= 999）
-- ----------------------------------------------------------------
DELETE FROM sys_menu WHERE menu_id BETWEEN 2000 AND 4999;

-- ================================================================
-- 1. 首页（复用若依原有 /index 路由，无需新增菜单）
-- ================================================================

-- ================================================================
-- 2. 部署管理（顶级目录）
-- ================================================================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2000, '部署管理', 0, 2, 'deploy', NULL, '', 1, 0, 'M', '0', '0', '', 'deploy',
  103, 1, NOW(), NULL, NULL, '部署管理目录');

-- 2.1 设备管理（二级目录）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2001, '设备管理', 2000, 1, 'device', NULL, '', 1, 0, 'M', '0', '0', '', 'robot',
  103, 1, NOW(), NULL, NULL, '设备管理目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2011, 'AMR品牌', 2001, 1, 'brand', 'deploy/device/brand/index', '', 1, 0, 'C', '0', '0', 'vehicle:brand:list', 'brand',
  103, 1, NOW(), NULL, NULL, 'AMR品牌管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2012, 'AMR型号', 2001, 2, 'type', 'deploy/device/type/index', '', 1, 0, 'C', '0', '0', 'vehicle:type:list', 'model',
  103, 1, NOW(), NULL, NULL, 'AMR型号管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2013, 'AMR列表', 2001, 3, 'list', 'deploy/device/list/index', '', 1, 0, 'C', '0', '0', 'vehicle:list:list', 'list',
  103, 1, NOW(), NULL, NULL, 'AMR列表管理菜单');

-- 2.2 工厂管理（二级目录）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2002, '工厂管理', 2000, 2, 'factory', NULL, '', 1, 0, 'M', '0', '0', '', 'tree',
  103, 1, NOW(), NULL, NULL, '工厂管理目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2021, '工厂模型', 2002, 1, 'model', 'deploy/factory/model/index', '', 1, 0, 'C', '0', '0', 'factory:model:list', 'tree-table',
  103, 1, NOW(), NULL, NULL, '工厂模型管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2022, '站点类型', 2002, 2, 'location-type', 'deploy/factory/location-type/index', '', 1, 0, 'C', '0', '0', 'factory:locationType:list', 'location',
  103, 1, NOW(), NULL, NULL, '站点类型管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2023, '地图管理', 2002, 3, 'map', 'deploy/factory/map/index', '', 1, 0, 'C', '0', '0', 'factory:map:list', 'map',
  103, 1, NOW(), NULL, NULL, '地图管理菜单');

-- 2.3 任务配置（二级目录）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2003, '任务配置', 2000, 3, 'task-config', NULL, '', 1, 0, 'M', '0', '0', '', 'form',
  103, 1, NOW(), NULL, NULL, '任务配置目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2031, '任务模板配置', 2003, 1, 'template', 'deploy/task-config/template/index', '', 1, 0, 'C', '0', '0', 'task:template:list', 'edit',
  103, 1, NOW(), NULL, NULL, '任务模板配置菜单');

-- ================================================================
-- 3. 运维管理（顶级目录）
-- ================================================================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3000, '运维管理', 0, 3, 'ops', NULL, '', 1, 0, 'M', '0', '0', '', 'tool',
  103, 1, NOW(), NULL, NULL, '运维管理目录');

-- 3.1 运维管理（二级目录：AMR + 订单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3001, '运维管理', 3000, 1, 'management', NULL, '', 1, 0, 'M', '0', '0', '', 'tool',
  103, 1, NOW(), NULL, NULL, '运维管理子目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3011, 'AMR运维管理', 3001, 1, 'amr', 'ops/amr/index', '', 1, 0, 'C', '0', '0', 'ops:amr:list', 'robot',
  103, 1, NOW(), NULL, NULL, 'AMR运维管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3012, '订单任务管理', 3001, 2, 'order', 'ops/order/index', '', 1, 0, 'C', '0', '0', 'ops:order:list', 'order',
  103, 1, NOW(), NULL, NULL, '订单任务管理菜单');

-- 3.2 实时监控（二级目录）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3003, '实时监控', 3000, 3, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor',
  103, 1, NOW(), NULL, NULL, '实时监控目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3031, '监控大屏', 3003, 1, 'live', 'ops/monitor/live/index', '', 1, 0, 'C', '0', '0', 'ops:monitor:live', 'fullscreen',
  103, 1, NOW(), NULL, NULL, '监控大屏菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3032, '锁资源监控', 3003, 2, 'lock', 'ops/monitor/lock/index', '', 1, 0, 'C', '0', '0', 'ops:monitor:lock', 'lock',
  103, 1, NOW(), NULL, NULL, '锁资源监控菜单');

-- ================================================================
-- 4. 运营分析（顶级目录）
-- ================================================================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4000, '运营分析', 0, 4, 'analytics', NULL, '', 1, 0, 'M', '0', '0', '', 'chart',
  103, 1, NOW(), NULL, NULL, '运营分析目录');

-- 4.1 统计分析（二级目录）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4001, '统计分析', 4000, 1, 'stats', NULL, '', 1, 0, 'M', '0', '0', '', 'chart',
  103, 1, NOW(), NULL, NULL, '统计分析目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4011, '任务统计', 4001, 1, 'task', 'analytics/stats/task/index', '', 1, 0, 'C', '0', '0', 'analytics:stats:task', 'form',
  103, 1, NOW(), NULL, NULL, '任务统计菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4012, 'AMR运行统计', 4001, 2, 'amr', 'analytics/stats/amr/index', '', 1, 0, 'C', '0', '0', 'analytics:stats:amr', 'robot',
  103, 1, NOW(), NULL, NULL, 'AMR运行统计菜单');

-- ================================================================
-- 5. 系统管理 & 系统监控 order_num 调整（排到最后）
-- ================================================================
UPDATE sys_menu SET order_num = 5 WHERE menu_id = 1;   -- 系统管理
UPDATE sys_menu SET order_num = 6 WHERE menu_id = 2;   -- 系统监控

-- ================================================================
-- 地图编辑器隐藏路由（不在菜单中显示，从地图管理页面跳转访问）
-- component 路径指向原有的 MapEditorTabs.vue
-- ================================================================
-- 注意：地图编辑器通过前端 constantRoutes 中的 hidden 路由加载，
--       无需在 sys_menu 中配置


-- ---- opentcs_menu_v3.2_ops_grouping.sql ----
-- ============================================================
-- OpenTCS Plus 菜单迁移 v3.2  ⚠️ 此文件包含错误，请执行 v3.3 修复！
-- 错误说明：
--   步骤 2 的 UPDATE 指向了 menu_id=3001（子目录本身）而非叶子节点
--   (3011/3012)，执行后造成 management 双层嵌套，URL 变为
--   /ops/management/management/amr。请勿重复执行本文件。
--   修复脚本：opentcs_menu_v3.3_fix_ops_nesting.sql
-- ============================================================

-- 1. 新增"运维管理"二级子目录（使用未占用的 id 3004）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3004, '运维管理', 3000, 1, 'management', NULL, '', 1, 0, 'M', '0', '0', '', 'tool',
  103, 1, NOW(), NULL, NULL, '运维管理子目录');

-- 2. ⚠️ 错误：应更新 3011/3012（叶子），实际更新了 3001（子目录）
--    正确写法见 v3.3 修复脚本
UPDATE sys_menu SET parent_id = 3004, order_num = 1 WHERE menu_id = 3001;
UPDATE sys_menu SET parent_id = 3004, order_num = 2 WHERE menu_id = 3002;

-- 3. 实时监控子目录 order_num 调整为 2
UPDATE sys_menu SET order_num = 2 WHERE menu_id = 3003;


-- ---- opentcs_menu_v3.3_fix_ops_nesting.sql ----
-- ============================================================
-- OpenTCS Plus 菜单修复 v3.3
-- 修复 v3.2 引入的"运维管理"子目录双层嵌套问题
--
-- 问题来源：v3.2 错误地将 menu_id=3001（子目录本身）移到 3004 下，
--   导致路径变成 /ops/management/management/amr（URL 双倍）
--   且侧边栏出现两层"运维管理"标题。
--
-- 修复目标：
--   3000 (ops) → 3004 (management) → 3011 (amr)
--                                  → 3012 (order)
--              → 3003 (monitor)   → 3031 (live)
--                                  → 3032 (lock)
-- ============================================================

-- 1. 将叶子节点直接挂到 3004（去掉中间的 3001 层）
UPDATE sys_menu SET parent_id = 3004, order_num = 1 WHERE menu_id = 3011;
UPDATE sys_menu SET parent_id = 3004, order_num = 2 WHERE menu_id = 3012;

-- 2. 删除被错误嵌套的旧子目录（3001 已无子节点，可安全删除）
DELETE FROM sys_menu WHERE menu_id = 3001;


-- ---- opentcs_menu_v3.4_system_restructure.sql ----
-- ============================================================
-- OpenTCS Plus 菜单重构 v3.4
-- 系统管理顶部导航下新增两个二级子目录：
--   /system/management → 原 /system 下所有菜单
--   /system/monitor    → 原 /monitor 下所有菜单（id=2 移入 id=1）
--
-- 执行前提：v3.0 已执行（id=1 order_num=5, id=2 order_num=6）
-- ============================================================

-- 1. 新增"系统管理"子目录（id=5001），归入顶级系统管理（id=1）下
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (5001, '系统管理', 1, 1, 'management', NULL, '', 1, 0, 'M', '0', '0', '', 'system',
  103, 1, NOW(), NULL, NULL, '系统管理子目录');

-- 2. 将原 id=1 的直接子菜单（100-108、118、123）移入新子目录 5001
UPDATE sys_menu
SET parent_id = 5001
WHERE menu_id IN (100, 101, 102, 103, 104, 105, 106, 107, 108, 118, 123);

-- 3. 将"系统监控"(id=2) 从顶级移入 id=1 下，作为二级子目录
--    path 保持 'monitor'，order_num 改为 2（在 5001 之后）
UPDATE sys_menu
SET parent_id = 1, order_num = 2
WHERE menu_id = 2;


-- ---- opentcs_menu_v3.5_component_paths.sql ----
-- ============================================================
-- OpenTCS Plus 菜单组件路径更新 v3.5
-- 与前端 views 目录重构同步：
--   system/* → system/management/*
--   monitor/* → system/monitor/*
-- ============================================================

-- 1. 系统管理（100-108、118、123）: system/* → system/management/*
UPDATE sys_menu SET component = 'system/management/user/index' WHERE menu_id = 100;
UPDATE sys_menu SET component = 'system/management/role/index' WHERE menu_id = 101;
UPDATE sys_menu SET component = 'system/management/menu/index' WHERE menu_id = 102;
UPDATE sys_menu SET component = 'system/management/dept/index' WHERE menu_id = 103;
UPDATE sys_menu SET component = 'system/management/post/index' WHERE menu_id = 104;
UPDATE sys_menu SET component = 'system/management/dict/index' WHERE menu_id = 105;
UPDATE sys_menu SET component = 'system/management/config/index' WHERE menu_id = 106;
UPDATE sys_menu SET component = 'system/management/notice/index' WHERE menu_id = 107;
UPDATE sys_menu SET component = 'system/management/oss/index' WHERE menu_id = 118;
UPDATE sys_menu SET component = 'system/management/client/index' WHERE menu_id = 123;

-- 2. 系统监控（2、109、113、117、120）: monitor/* → system/monitor/*
UPDATE sys_menu SET component = 'system/monitor/online/index' WHERE menu_id = 109;
UPDATE sys_menu SET component = 'system/monitor/cache/index' WHERE menu_id = 113;
UPDATE sys_menu SET component = 'system/monitor/admin/index' WHERE menu_id = 117;
UPDATE sys_menu SET component = 'system/monitor/snailjob/index' WHERE menu_id = 120;

-- 3. 日志管理（500、501）: monitor/* → system/management/*
UPDATE sys_menu SET component = 'system/management/operlog/index' WHERE menu_id = 500;
UPDATE sys_menu SET component = 'system/management/logininfor/index' WHERE menu_id = 501;

-- 4. 租户管理（121、122）: system/tenant* → system/management/tenant*
UPDATE sys_menu SET component = 'system/management/tenant/index' WHERE menu_id = 121;
UPDATE sys_menu SET component = 'system/management/tenantPackage/index' WHERE menu_id = 122;

-- 5. 业务页面（130-133）子菜单: system/* → system/management/*
UPDATE sys_menu SET component = 'system/management/role/authUser' WHERE menu_id = 130;
UPDATE sys_menu SET component = 'system/management/user/authRole' WHERE menu_id = 131;
UPDATE sys_menu SET component = 'system/management/dict/data' WHERE menu_id = 132;
UPDATE sys_menu SET component = 'system/management/oss/config' WHERE menu_id = 133;

