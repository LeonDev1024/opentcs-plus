-- ============================================================
-- OpenTCS Plus Schema Snapshot (auto-generated, DO NOT EDIT)
-- Generated: 2026-07-01T14:54:27Z
-- ============================================================

SET NAMES utf8mb4;

-- MySQL dump 10.13  Distrib 9.6.0, for macos26.4 (arm64)
--
-- Host: 127.0.0.1    Database: opentcsplus
-- ------------------------------------------------------
-- Server version	9.6.0

/*!50503 SET NAMES utf8mb4 */;

--
-- Table structure for table `flyway_schema_history`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_client`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_client` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_id` varchar(64) DEFAULT NULL COMMENT '客户端ID',
  `client_key` varchar(32) DEFAULT NULL COMMENT '客户端Key',
  `client_secret` varchar(255) DEFAULT NULL COMMENT '客户端密钥',
  `grant_type` varchar(255) DEFAULT NULL COMMENT '授权类型',
  `device_type` varchar(32) DEFAULT NULL COMMENT '设备类型',
  `active_timeout` int DEFAULT '1800' COMMENT 'token活跃超时时间(秒)',
  `timeout` int DEFAULT '604800' COMMENT 'token固定超时时间(秒)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统授权表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_config`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置(Y是 N否)',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_dept`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门ID',
  `ancestors` varchar(500) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `dept_category` varchar(100) DEFAULT NULL COMMENT '部门类别编码',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` bigint DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态(0正常 1停用)',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_dict_data`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认(Y是 N否)',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_dict_type`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `uk_sys_dict_type_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_logininfor`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `client_key` varchar(32) DEFAULT '' COMMENT '客户端Key',
  `device_type` varchar(32) DEFAULT '' COMMENT '设备类型',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态(0成功 1失败)',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_login_time` (`login_time`),
  KEY `idx_sys_logininfor_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_menu`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query_param` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链(0是 1否)',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存(0缓存 1不缓存)',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型(M目录 C菜单 F按钮)',
  `visible` char(1) DEFAULT '0' COMMENT '显示状态(0显示 1隐藏)',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态(0正常 1停用)',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2033796453461209090 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_notice`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型(1通知 2公告)',
  `notice_content` text COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态(0正常 1关闭)',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_oper_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型(0其它 1新增 2修改 3删除)',
  `method` varchar(100) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别(0其它 1后台用户 2手机端用户)',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(4000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(4000) DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态(0正常 1异常)',
  `error_msg` varchar(4000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间(毫秒)',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_business_type` (`business_type`),
  KEY `idx_sys_oper_log_oper_time` (`oper_time`),
  KEY `idx_sys_oper_log_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_oss`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oss` (
  `oss_id` bigint NOT NULL AUTO_INCREMENT COMMENT '对象存储主键',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `original_name` varchar(255) NOT NULL COMMENT '原文件名',
  `file_suffix` varchar(10) NOT NULL COMMENT '文件后缀名',
  `url` varchar(500) NOT NULL COMMENT 'URL地址',
  `ext1` varchar(500) DEFAULT NULL COMMENT '扩展字段',
  `service` varchar(20) DEFAULT 'minio' COMMENT '服务商',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '上传人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`oss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='OSS对象存储表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_oss_config`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oss_config` (
  `oss_config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置主键',
  `config_key` varchar(20) NOT NULL COMMENT '配置Key',
  `access_key` varchar(255) DEFAULT NULL COMMENT 'AccessKey',
  `secret_key` varchar(255) DEFAULT NULL COMMENT 'SecretKey',
  `bucket_name` varchar(255) DEFAULT NULL COMMENT '桶名称',
  `prefix` varchar(255) DEFAULT NULL COMMENT '前缀',
  `endpoint` varchar(255) DEFAULT NULL COMMENT '访问站点',
  `domain` varchar(255) DEFAULT NULL COMMENT '自定义域名',
  `is_https` char(1) DEFAULT 'N' COMMENT '是否HTTPS(Y是 N否)',
  `region` varchar(255) DEFAULT NULL COMMENT '区域',
  `access_policy` char(1) DEFAULT '1' COMMENT '桶权限类型(0=private 1=public 2=custom)',
  `status` char(1) DEFAULT '1' COMMENT '是否默认(0=是 1=否)',
  `ext1` varchar(500) DEFAULT NULL COMMENT '扩展字段',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`oss_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='对象存储配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_post`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_category` varchar(100) DEFAULT NULL COMMENT '岗位类别编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态(0正常 1停用)',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围(1全部 2自定 3本部门 4本部门及以下 5仅本人 6本人及部门)',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态(0正常 1停用)',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role_dept`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role_menu`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_social`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_social` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `auth_id` varchar(255) NOT NULL COMMENT '平台+平台唯一ID',
  `source` varchar(255) NOT NULL COMMENT '用户来源',
  `open_id` varchar(255) DEFAULT NULL COMMENT '平台编号唯一ID',
  `user_name` varchar(30) NOT NULL COMMENT '登录账号',
  `nick_name` varchar(30) DEFAULT '' COMMENT '用户昵称',
  `email` varchar(255) DEFAULT '' COMMENT '用户邮箱',
  `avatar` varchar(500) DEFAULT '' COMMENT '头像地址',
  `access_token` varchar(2000) NOT NULL COMMENT '授权令牌',
  `expire_in` int DEFAULT NULL COMMENT '令牌有效期',
  `refresh_token` varchar(255) DEFAULT NULL COMMENT '刷新令牌',
  `access_code` varchar(2000) DEFAULT NULL COMMENT '授权Code',
  `union_id` varchar(255) DEFAULT NULL COMMENT 'UnionID',
  `scope` varchar(255) DEFAULT NULL COMMENT '授权范围',
  `token_type` varchar(255) DEFAULT NULL COMMENT '令牌类型',
  `id_token` varchar(2000) DEFAULT NULL COMMENT 'ID令牌',
  `mac_algorithm` varchar(255) DEFAULT NULL COMMENT '小米平台附带属性',
  `mac_key` varchar(255) DEFAULT NULL COMMENT '小米平台附带属性',
  `code` varchar(255) DEFAULT NULL COMMENT '授权Code',
  `oauth_token` varchar(255) DEFAULT NULL COMMENT 'Twitter附带属性',
  `oauth_token_secret` varchar(255) DEFAULT NULL COMMENT 'Twitter附带属性',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社会化关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(10) DEFAULT 'sys_user' COMMENT '用户类型',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别(0男 1女 2未知)',
  `avatar` bigint DEFAULT NULL COMMENT '头像ID',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '帐号状态(0正常 1停用)',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user_post`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user_role`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_brand`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_brand` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '品牌名称',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '品牌代码',
  `logo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Logo URL',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `contact` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系方式',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `sort` int DEFAULT '0' COMMENT '排序',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆品牌';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_cross_layer_connection`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_cross_layer_connection` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `factory_model_id` bigint NOT NULL COMMENT '所属工厂ID',
  `connection_id` varchar(64) NOT NULL COMMENT '连接唯一标识',
  `name` varchar(255) NOT NULL COMMENT '连接名称',
  `connection_type` varchar(50) NOT NULL COMMENT 'ELEVATOR/CONVEYOR/PHYSICAL_DOOR',
  `source_navigation_map_id` bigint NOT NULL COMMENT '源地图ID',
  `source_point_id` varchar(255) NOT NULL COMMENT '源点位ID',
  `source_floor` int NOT NULL COMMENT '源楼层',
  `dest_navigation_map_id` bigint NOT NULL COMMENT '目标地图ID',
  `dest_point_id` varchar(255) NOT NULL COMMENT '目标点位ID',
  `dest_floor` int NOT NULL COMMENT '目标楼层',
  `capacity` int DEFAULT '1' COMMENT '容量',
  `max_weight` decimal(10,2) DEFAULT NULL COMMENT '最大承重',
  `travel_time` int DEFAULT NULL COMMENT '运行时间（秒）',
  `available` tinyint(1) DEFAULT '1' COMMENT '是否可用',
  `current_load` int DEFAULT '0' COMMENT '当前负载',
  `properties` json DEFAULT NULL COMMENT '扩展属性',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `del_flag` char(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_clc_source_map` (`source_navigation_map_id`),
  KEY `fk_clc_dest_map` (`dest_navigation_map_id`),
  KEY `idx_clc_factory` (`factory_model_id`),
  CONSTRAINT `fk_clc_dest_map` FOREIGN KEY (`dest_navigation_map_id`) REFERENCES `tcs_navigation_map` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_clc_factory` FOREIGN KEY (`factory_model_id`) REFERENCES `tcs_factory_model` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_clc_source_map` FOREIGN KEY (`source_navigation_map_id`) REFERENCES `tcs_navigation_map` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='跨层连接表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_elevator_schedule`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_elevator_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `connection_id` varchar(64) NOT NULL COMMENT '跨层连接ID',
  `vehicle_id` bigint DEFAULT NULL COMMENT '预约车辆ID',
  `vehicle_name` varchar(255) DEFAULT NULL COMMENT '预约车辆名称',
  `source_floor` int NOT NULL COMMENT '源楼层',
  `dest_floor` int NOT NULL COMMENT '目标楼层',
  `schedule_type` varchar(50) DEFAULT 'RESERVE' COMMENT '调度类型',
  `pickup_time` datetime DEFAULT NULL COMMENT '预计接载时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '预计送达时间',
  `actual_pickup_time` datetime DEFAULT NULL COMMENT '实际接载时间',
  `actual_delivery_time` datetime DEFAULT NULL COMMENT '实际送达时间',
  `status` varchar(50) DEFAULT 'PENDING' COMMENT '状态',
  `properties` json DEFAULT NULL COMMENT '扩展属性',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_elevator_connection` (`connection_id`),
  KEY `idx_elevator_vehicle` (`vehicle_id`),
  KEY `idx_elevator_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='电梯调度记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_factory_layer`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_factory_layer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `navigation_map_id` bigint NOT NULL COMMENT '所属导航地图ID',
  `layer_group_id` bigint DEFAULT NULL COMMENT '所属图层组ID',
  `name` varchar(255) NOT NULL COMMENT '图层名称',
  `visible` tinyint(1) DEFAULT '1' COMMENT '是否可见',
  `ordinal` int DEFAULT '0' COMMENT '显示顺序',
  `properties` json DEFAULT NULL COMMENT '扩展属性',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `del_flag` char(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_factory_layer_navigation_map` (`navigation_map_id`),
  KEY `fk_factory_layer_layer_group` (`layer_group_id`),
  CONSTRAINT `fk_factory_layer_layer_group` FOREIGN KEY (`layer_group_id`) REFERENCES `tcs_factory_layer_group` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_factory_layer_navigation_map` FOREIGN KEY (`navigation_map_id`) REFERENCES `tcs_navigation_map` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图层表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_factory_layer_group`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_factory_layer_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `navigation_map_id` bigint NOT NULL COMMENT '所属导航地图ID',
  `name` varchar(255) NOT NULL COMMENT '图层组名称',
  `visible` tinyint(1) DEFAULT '1' COMMENT '是否可见',
  `ordinal` int DEFAULT '0' COMMENT '显示顺序',
  `properties` json DEFAULT NULL COMMENT '扩展属性',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `del_flag` char(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_factory_layer_group_navigation_map` (`navigation_map_id`),
  CONSTRAINT `fk_factory_layer_group_navigation_map` FOREIGN KEY (`navigation_map_id`) REFERENCES `tcs_navigation_map` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图层组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_factory_model`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_factory_model` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `factory_id` varchar(64) NOT NULL COMMENT '工厂唯一标识符',
  `name` varchar(255) NOT NULL COMMENT '工厂名称',
  `model_version` varchar(50) NOT NULL DEFAULT '1.0' COMMENT '模型版本',
  `scale` decimal(10,4) NOT NULL DEFAULT '50.0000' COMMENT '比例尺 (px/m)',
  `coordinate_system` varchar(50) DEFAULT 'RIGHT_HAND' COMMENT '坐标系',
  `length_unit` varchar(20) DEFAULT 'METER' COMMENT '长度单位',
  `properties` json DEFAULT NULL COMMENT '扩展属性',
  `description` varchar(1000) DEFAULT NULL COMMENT '描述',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` int DEFAULT '0',
  `del_flag` char(1) DEFAULT '0',
  `status` char(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_factory_model_factory_id` (`factory_id`),
  UNIQUE KEY `uk_factory_model_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工厂模型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_navigation_map`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_navigation_map` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `factory_model_id` bigint NOT NULL COMMENT '所属工厂模型ID',
  `map_id` varchar(64) NOT NULL COMMENT '地图唯一标识',
  `name` varchar(255) NOT NULL COMMENT '地图名称',
  `floor_number` int DEFAULT NULL COMMENT '楼层号',
  `vehicle_type_id` bigint DEFAULT NULL COMMENT '车辆类型ID（必填，对应vehicle_type.id)',
  `origin_x` decimal(12,4) DEFAULT '0.0000' COMMENT 'PGM原点X',
  `origin_y` decimal(12,4) DEFAULT '0.0000' COMMENT 'PGM原点Y',
  `rotation` decimal(10,4) DEFAULT '0.0000' COMMENT '地图旋转角度(度)',
  `raster_url` varchar(500) DEFAULT NULL COMMENT '栅格地图OSS存储路径',
  `raster_version` int DEFAULT '0' COMMENT '栅格地图版本号',
  `raster_width` int DEFAULT NULL COMMENT '栅格地图宽度(像素)',
  `raster_height` int DEFAULT NULL COMMENT '栅格地图高度(像素)',
  `raster_resolution` decimal(12,6) DEFAULT NULL COMMENT '栅格地图分辨率(米/像素)',
  `yaml_origin` json DEFAULT NULL COMMENT 'YAML原始origin参数 [ox, oy, angle] (米,度)',
  `yaml_url` varchar(500) DEFAULT NULL COMMENT 'YAML文件OSS存储路径',
  `map_origin` json DEFAULT NULL COMMENT '地图在工厂坐标系下的原点偏移 [x, y, angle] (毫米,度)',
  `map_version` varchar(50) NOT NULL DEFAULT '1.0' COMMENT '地图版本号',
  `properties` json DEFAULT NULL COMMENT '扩展属性',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` int DEFAULT '0',
  `del_flag` char(1) DEFAULT '0',
  `status` char(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_navigation_map_factory_map` (`factory_model_id`,`map_id`),
  KEY `idx_navigation_map_version` (`map_version`),
  KEY `idx_navigation_map_status` (`status`),
  CONSTRAINT `fk_navigation_map_factory` FOREIGN KEY (`factory_model_id`) REFERENCES `tcs_factory_model` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='导航地图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_navigation_map_history`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_navigation_map_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `navigation_map_id` bigint NOT NULL COMMENT '所属地图ID',
  `map_version` varchar(50) NOT NULL COMMENT '地图版本号',
  `snapshot_url` varchar(500) DEFAULT NULL COMMENT 'JSON快照文件路径',
  `change_summary` varchar(500) DEFAULT NULL COMMENT '变更说明',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_history_map_version` (`navigation_map_id`,`map_version`),
  KEY `idx_history_navigation_map_id` (`navigation_map_id`),
  KEY `idx_history_map_version` (`map_version`),
  CONSTRAINT `fk_history_navigation_map` FOREIGN KEY (`navigation_map_id`) REFERENCES `tcs_navigation_map` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='导航地图历史版本表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_path`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_path` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `navigation_map_id` bigint NOT NULL COMMENT '归属导航地图ID',
  `layer_id` bigint DEFAULT NULL COMMENT '归属图层ID',
  `path_id` varchar(255) NOT NULL COMMENT '路径唯一标识',
  `name` varchar(255) NOT NULL COMMENT '路径名称',
  `source_point_id` varchar(255) NOT NULL COMMENT '起始点位标识',
  `dest_point_id` varchar(255) NOT NULL COMMENT '目标点位标识',
  `length` decimal(12,4) NOT NULL COMMENT '路径长度',
  `max_velocity` decimal(8,4) DEFAULT NULL COMMENT '最大允许速度',
  `max_reverse_velocity` decimal(8,4) DEFAULT NULL COMMENT '最大反向速度',
  `locked` tinyint(1) DEFAULT '0' COMMENT '是否被锁定',
  `is_blocked` tinyint(1) DEFAULT '0' COMMENT '是否被阻塞',
  `layout` json DEFAULT NULL COMMENT '路径布局（connectionType + controlPoints）',
  `properties` json DEFAULT NULL COMMENT '扩展属性',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `del_flag` char(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_path_navigation_map` (`navigation_map_id`),
  KEY `idx_path_layer` (`layer_id`),
  CONSTRAINT `fk_path_layer` FOREIGN KEY (`layer_id`) REFERENCES `tcs_factory_layer` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_path_navigation_map` FOREIGN KEY (`navigation_map_id`) REFERENCES `tcs_navigation_map` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='路径表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_point`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_point` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `navigation_map_id` bigint NOT NULL COMMENT '归属导航地图ID',
  `layer_id` bigint DEFAULT NULL COMMENT '归属图层ID',
  `point_id` varchar(255) NOT NULL COMMENT '点位唯一标识',
  `name` varchar(255) NOT NULL COMMENT '点位名称',
  `x_position` decimal(12,4) NOT NULL COMMENT 'X坐标',
  `y_position` decimal(12,4) NOT NULL COMMENT 'Y坐标',
  `z_position` decimal(12,4) DEFAULT '0.0000' COMMENT 'Z坐标',
  `vehicle_orientation` decimal(8,4) DEFAULT '0.0000' COMMENT '车辆方向角度',
  `type` varchar(50) NOT NULL DEFAULT 'HALT_POSITION' COMMENT '点位类型',
  `radius` decimal(8,4) DEFAULT '0.0000' COMMENT '点位半径',
  `locked` tinyint(1) DEFAULT '0' COMMENT '是否被锁定',
  `is_blocked` tinyint(1) DEFAULT '0' COMMENT '是否被阻塞',
  `is_occupied` tinyint(1) DEFAULT '0' COMMENT '是否被占用',
  `label` varchar(500) DEFAULT NULL COMMENT '标签',
  `layout` json DEFAULT NULL COMMENT '点位布局数据',
  `properties` json DEFAULT NULL COMMENT '扩展属性',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `del_flag` char(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_point_navigation_map` (`navigation_map_id`),
  KEY `idx_point_layer` (`layer_id`),
  CONSTRAINT `fk_point_layer` FOREIGN KEY (`layer_id`) REFERENCES `tcs_factory_layer` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_point_navigation_map` FOREIGN KEY (`navigation_map_id`) REFERENCES `tcs_navigation_map` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='点位表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_transport_order`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_transport_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '订单名称',
  `order_no` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '订单编号',
  `state` varchar(50) COLLATE utf8mb4_general_ci DEFAULT 'RAW' COMMENT '订单状态：RAW, ACTIVE, FINISHED, FAILED',
  `intended_vehicle` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '指定车辆',
  `processing_vehicle` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '处理车辆',
  `vehicle_vin` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '车辆VIN',
  `destinations` text COLLATE utf8mb4_general_ci COMMENT '目的地序列',
  `creation_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `finished_time` datetime DEFAULT NULL COMMENT '完成时间',
  `deadline` datetime DEFAULT NULL COMMENT '截止时间',
  `properties` text COLLATE utf8mb4_general_ci COMMENT '扩展属性',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` int DEFAULT '0',
  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT '0',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_processing_vehicle` (`processing_vehicle`),
  KEY `idx_state` (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运输订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_vehicle`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_vehicle` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '车辆名称',
  `vin_code` varchar(50) DEFAULT NULL COMMENT '车辆VIN码',
  `vehicle_type_id` bigint NOT NULL COMMENT '车辆类型ID',
  `current_position` varchar(100) DEFAULT NULL COMMENT '当前位置(点位ID)',
  `next_position` varchar(100) DEFAULT NULL COMMENT '下一个位置(点位ID)',
  `state` varchar(30) DEFAULT 'UNKNOWN' COMMENT '车辆状态: UNKNOWN, UNAVAILABLE, IDLE, CHARGING, WORKING, ERROR',
  `integration_level` varchar(30) DEFAULT 'TO_BE_IGNORED' COMMENT '集成级别: TO_BE_IGNORED, TO_BE_NOTICED, TO_BE_RESPECTED, TO_BE_UTILIZED',
  `energy_level` decimal(10,2) DEFAULT '100.00' COMMENT '能量级别(0-100)',
  `current_transport_order` varchar(100) DEFAULT NULL COMMENT '当前运输订单ID',
  `properties` json DEFAULT NULL COMMENT '扩展属性',
  `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int DEFAULT '0' COMMENT '乐观锁版本',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`),
  KEY `fk_vehicle_type` (`vehicle_type_id`),
  CONSTRAINT `fk_vehicle_type` FOREIGN KEY (`vehicle_type_id`) REFERENCES `tcs_vehicle_type` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='车辆表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tcs_vehicle_type`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tcs_vehicle_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `brand_id` bigint DEFAULT NULL COMMENT '所属品牌ID',
  `name` varchar(100) NOT NULL COMMENT '车辆类型名称',
  `length` decimal(10,2) DEFAULT NULL COMMENT '车辆长度(mm)',
  `width` decimal(10,2) DEFAULT NULL COMMENT '车辆宽度(mm)',
  `height` decimal(10,2) DEFAULT NULL COMMENT '车辆高度(mm)',
  `max_velocity` decimal(10,2) DEFAULT NULL COMMENT '最大速度(mm/s)',
  `max_reverse_velocity` decimal(10,2) DEFAULT NULL COMMENT '最大反向速度(mm/s)',
  `energy_level` decimal(10,2) DEFAULT NULL COMMENT '能量级别(0-100)',
  `allowed_orders` json DEFAULT NULL COMMENT '允许的订单类型',
  `allowed_peripheral_operations` json DEFAULT NULL COMMENT '允许的外围设备操作',
  `properties` json DEFAULT NULL COMMENT '扩展属性',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_vehicle_type_brand_id` (`brand_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='车辆类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'opentcsplus'
--


-- Dump completed on 2026-07-01 22:54:27
