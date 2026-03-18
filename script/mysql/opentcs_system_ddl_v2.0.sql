-- ============================================================
-- OpenTCS Plus 系统管理模块 SQL (全新创建)
-- 版本: v2.0
-- 描述: 系统管理相关表结构，包含用户、角色、菜单、部门等
-- ============================================================

USE opentcs;

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
