-- Flyway Repeatable: 系统菜单种子数据（最终态）
USE opentcsplus;
SET NAMES utf8mb4;

-- ---- sys_menu.sql ----
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '系统管理', 0, 10, 'system', null, '', 1, 0, 'M', '0', '0', '', 'system', 103, 1, '2026-03-16 09:23:42', 1, '2026-03-17 14:38:59', '系统管理目录');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2, '系统监控', 0, 9, 'monitor', null, '', 1, 0, 'M', '0', '0', '', 'monitor', 103, 1, '2026-03-16 09:23:42', 1, '2026-03-17 14:38:52', '系统监控目录');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (6, '租户管理', 0, 2, 'tenant', null, '', 1, 0, 'M', '1', '1', '', 'chart', 103, 1, '2026-03-16 09:23:42', 1, '2026-03-17 14:39:08', '租户管理目录');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 103, 1, '2026-03-16 09:23:43', null, null, '用户管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 103, 1, '2026-03-16 09:23:43', null, null, '角色管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 103, 1, '2026-03-16 09:23:43', null, null, '菜单管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 103, 1, '2026-03-16 09:23:43', null, null, '部门管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 103, 1, '2026-03-16 09:23:43', null, null, '岗位管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 103, 1, '2026-03-16 09:23:43', null, null, '字典管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 103, 1, '2026-03-16 09:23:43', null, null, '参数设置菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 103, 1, '2026-03-16 09:23:44', null, null, '通知公告菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (108, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '0', '0', '', 'log', 103, 1, '2026-03-16 09:23:44', null, null, '日志管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 103, 1, '2026-03-16 09:23:44', null, null, '在线用户菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 103, 1, '2026-03-16 09:23:44', null, null, '缓存监控菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (118, '文件管理', 1, 10, 'oss', 'system/oss/index', '', 1, 0, 'C', '0', '0', 'system:oss:list', 'upload', 103, 1, '2026-03-16 09:23:45', null, null, '文件管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (120, '定时任务', 2, 3, 'job', 'monitor/job/index', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 103, 1, '2026-03-16 09:23:45', null, null, 'Quartz定时任务管理');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (134, '定时任务日志', 2, 7, 'job-log', 'system/monitor/job/log', '', 1, 1, 'C', '1', '0', 'monitor:job:list', '#', 103, 1, '2026-03-16 09:23:45', null, null, '定时任务执行日志');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (121, '租户管理', 6, 1, 'tenant', 'system/tenant/index', '', 1, 0, 'C', '0', '0', 'system:tenant:list', 'list', 103, 1, '2026-03-16 09:23:44', null, null, '租户管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (122, '租户套餐管理', 6, 2, 'tenantPackage', 'system/tenantPackage/index', '', 1, 0, 'C', '0', '0', 'system:tenantPackage:list', 'form', 103, 1, '2026-03-16 09:23:44', null, null, '租户套餐管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (123, '客户端管理', 1, 11, 'client', 'system/client/index', '', 1, 0, 'C', '0', '0', 'system:client:list', 'international', 103, 1, '2026-03-16 09:23:44', null, null, '客户端管理菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (130, '分配用户', 1, 2, 'role-auth/user/:roleId', 'system/role/authUser', '', 1, 1, 'C', '1', '0', 'system:role:edit', '#', 103, 1, '2026-03-16 09:23:45', null, null, '/system/role');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (131, '分配角色', 1, 1, 'user-auth/role/:userId', 'system/user/authRole', '', 1, 1, 'C', '1', '0', 'system:user:edit', '#', 103, 1, '2026-03-16 09:23:45', null, null, '/system/user');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (132, '字典数据', 1, 6, 'dict-data/index/:dictId', 'system/dict/data', '', 1, 1, 'C', '1', '0', 'system:dict:list', '#', 103, 1, '2026-03-16 09:23:45', null, null, '/system/dict');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (133, '文件配置管理', 1, 10, 'oss-config/index', 'system/oss/config', '', 1, 1, 'C', '1', '0', 'system:ossConfig:list', '#', 103, 1, '2026-03-16 09:23:45', null, null, '/system/oss');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (500, '操作日志', 108, 1, 'operlog', 'system/management/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 103, 1, '2026-03-16 09:23:46', null, null, '操作日志菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (501, '登录日志', 108, 2, 'logininfor', 'system/management/logininfor/index', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 103, 1, '2026-03-16 09:23:46', null, null, '登录日志菜单');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1001, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 103, 1, '2026-03-16 09:23:46', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1002, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 103, 1, '2026-03-16 09:23:46', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1003, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 103, 1, '2026-03-16 09:23:46', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1004, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 103, 1, '2026-03-16 09:23:46', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1005, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 103, 1, '2026-03-16 09:23:46', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1006, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1007, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1008, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1009, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1010, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1011, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1012, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 103, 1, '2026-03-16 09:23:47', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1013, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1014, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1015, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1016, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1017, '部门查询', 103, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1018, '部门新增', 103, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1019, '部门修改', 103, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 103, 1, '2026-03-16 09:23:48', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1020, '部门删除', 103, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1021, '岗位查询', 104, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1022, '岗位新增', 104, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1023, '岗位修改', 104, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1024, '岗位删除', 104, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1025, '岗位导出', 104, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1026, '字典查询', 105, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1027, '字典新增', 105, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 103, 1, '2026-03-16 09:23:49', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1028, '字典修改', 105, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1029, '字典删除', 105, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1030, '字典导出', 105, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1031, '参数查询', 106, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1032, '参数新增', 106, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1033, '参数修改', 106, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1034, '参数删除', 106, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 103, 1, '2026-03-16 09:23:50', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1035, '参数导出', 106, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1036, '公告查询', 107, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1037, '公告新增', 107, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1038, '公告修改', 107, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1039, '公告删除', 107, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1040, '操作查询', 500, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1041, '操作删除', 500, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 103, 1, '2026-03-16 09:23:51', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1042, '日志导出', 500, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1043, '登录查询', 501, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1044, '登录删除', 501, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1045, '日志导出', 501, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1046, '在线查询', 109, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1047, '批量强退', 109, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1048, '单条强退', 109, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1050, '账户解锁', 501, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 103, 1, '2026-03-16 09:23:52', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1061, '客户端管理查询', 123, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:query', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1062, '客户端管理新增', 123, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:add', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1063, '客户端管理修改', 123, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:edit', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1064, '客户端管理删除', 123, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:remove', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1065, '客户端管理导出', 123, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:export', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1600, '文件查询', 118, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:query', '#', 103, 1, '2026-03-16 09:23:53', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1601, '文件上传', 118, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:upload', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1602, '文件下载', 118, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:download', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1603, '文件删除', 118, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:remove', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1606, '租户查询', 121, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:query', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1607, '租户新增', 121, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:add', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1608, '租户修改', 121, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:edit', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1609, '租户删除', 121, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:remove', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1610, '租户导出', 121, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:export', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1611, '租户套餐查询', 122, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:query', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1612, '租户套餐新增', 122, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:add', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1613, '租户套餐修改', 122, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:edit', '#', 103, 1, '2026-03-16 09:23:55', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1614, '租户套餐删除', 122, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:remove', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1615, '租户套餐导出', 122, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:export', '#', 103, 1, '2026-03-16 09:23:56', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1620, '配置列表', 118, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:list', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1621, '配置添加', 118, 6, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:add', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1622, '配置编辑', 118, 6, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:edit', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1623, '配置删除', 118, 6, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:remove', '#', 103, 1, '2026-03-16 09:23:54', null, null, '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033785761135878145, 'AMR管理', 0, 1, 'vehicle', null, null, 1, 0, 'M', '0', '0', null, 'agv', 103, 1, '2026-03-17 14:01:26', 1, '2026-03-17 14:05:31', '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033787339884486658, 'AMR类型', 2033785761135878145, 1, 'vehicletype', 'vehicle/type/index', null, 1, 0, 'C', '0', '0', null, 'logininfor', 103, 1, '2026-03-17 14:07:42', 1, '2026-03-17 14:07:42', '');
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2033787597502832641, 'AMR列表', 2033785761135878145, 2, 'vehiclelist', 'vehicle/list/index', null, 1, 0, 'C', '0', '0', null, 'list', 103, 1, '2026-03-17 14:08:44', 1, '2026-03-17 16:19:28', '');


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
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2000, '部署管理', 0, 2, 'deploy', NULL, '', 1, 0, 'M', '0', '0', '', 'deploy',
  103, 1, NOW(), NULL, NULL, '部署管理目录');

-- 2.1 设备管理（二级目录）
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2001, '设备管理', 2000, 1, 'device', NULL, '', 1, 0, 'M', '0', '0', '', 'robot',
  103, 1, NOW(), NULL, NULL, '设备管理目录');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2011, 'AMR品牌', 2001, 1, 'brand', 'vehicle/brand/index', '', 1, 0, 'C', '0', '0', 'vehicle:brand:list', 'brand',
  103, 1, NOW(), NULL, NULL, 'AMR品牌管理菜单');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2012, 'AMR型号', 2001, 2, 'type', 'vehicle/type/index', '', 1, 0, 'C', '0', '0', 'vehicle:type:list', 'model',
  103, 1, NOW(), NULL, NULL, 'AMR型号管理菜单');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2013, 'AMR列表', 2001, 3, 'list', 'vehicle/list/index', '', 1, 0, 'C', '0', '0', 'vehicle:list:list', 'list',
  103, 1, NOW(), NULL, NULL, 'AMR列表管理菜单');

-- 2.2 工厂管理（二级目录）
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2002, '工厂管理', 2000, 2, 'factory', NULL, '', 1, 0, 'M', '0', '0', '', 'tree',
  103, 1, NOW(), NULL, NULL, '工厂管理目录');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2021, '工厂模型', 2002, 1, 'model', 'map/scene/index', '', 1, 0, 'C', '0', '0', 'factory:model:list', 'tree-table',
  103, 1, NOW(), NULL, NULL, '工厂模型管理菜单');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2022, '站点类型', 2002, 2, 'location-type', 'map/scene/location-type/index', '', 1, 0, 'C', '0', '0', 'factory:locationType:list', 'location',
  103, 1, NOW(), NULL, NULL, '站点类型管理菜单');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2023, '地图管理', 2002, 3, 'map', 'map/scene/data/index', '', 1, 0, 'C', '0', '0', 'factory:map:list', 'map',
  103, 1, NOW(), NULL, NULL, '地图管理菜单');

-- 2.3 任务配置（二级目录）
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2003, '任务配置', 2000, 3, 'task-config', NULL, '', 1, 0, 'M', '0', '0', '', 'form',
  103, 1, NOW(), NULL, NULL, '任务配置目录');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2031, '任务模板配置', 2003, 1, 'template', 'task/template/index', '', 1, 0, 'C', '0', '0', 'task:template:list', 'edit',
  103, 1, NOW(), NULL, NULL, '任务模板配置菜单');

-- ================================================================
-- 3. 运维管理（顶级目录）
-- ================================================================
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3000, '运维管理', 0, 3, 'ops', NULL, '', 1, 0, 'M', '0', '0', '', 'tool',
  103, 1, NOW(), NULL, NULL, '运维管理目录');

-- 3.1 运维管理（二级目录：AMR + 订单）
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3001, '运维管理', 3000, 1, 'management', NULL, '', 1, 0, 'M', '0', '0', '', 'tool',
  103, 1, NOW(), NULL, NULL, '运维管理子目录');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3011, 'AMR运维管理', 3001, 1, 'amr', 'vehicle/amr/index', '', 1, 0, 'C', '0', '0', 'ops:amr:list', 'robot',
  103, 1, NOW(), NULL, NULL, 'AMR运维管理菜单');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3012, '订单任务管理', 3001, 2, 'order', 'task/operation/index', '', 1, 0, 'C', '0', '0', 'ops:order:list', 'order',
  103, 1, NOW(), NULL, NULL, '订单任务管理菜单');

-- 3.2 实时监控（二级目录）
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3003, '实时监控', 3000, 3, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor',
  103, 1, NOW(), NULL, NULL, '实时监控目录');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3031, '监控大屏', 3003, 1, 'live', 'monitor/live/index', '', 1, 0, 'C', '0', '0', 'ops:monitor:live', 'fullscreen',
  103, 1, NOW(), NULL, NULL, '监控大屏菜单');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3032, '锁资源监控', 3003, 2, 'lock', 'monitor/lock/index', '', 1, 0, 'C', '0', '0', 'ops:monitor:lock', 'lock',
  103, 1, NOW(), NULL, NULL, '锁资源监控菜单');

-- ================================================================
-- 4. 运营分析（顶级目录）
-- ================================================================
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4000, '运营分析', 0, 4, 'analytics', NULL, '', 1, 0, 'M', '0', '0', '', 'chart',
  103, 1, NOW(), NULL, NULL, '运营分析目录');

-- 4.1 统计分析（二级目录）
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4001, '统计分析', 4000, 1, 'stats', NULL, '', 1, 0, 'M', '0', '0', '', 'chart',
  103, 1, NOW(), NULL, NULL, '统计分析目录');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4011, '任务统计', 4001, 1, 'task', 'analytics/stats/task/index', '', 1, 0, 'C', '0', '0', 'analytics:stats:task', 'form',
  103, 1, NOW(), NULL, NULL, '任务统计菜单');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
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
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
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
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
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

-- 2. 系统监控（2、109、113、120）: monitor/* → system/monitor/*
UPDATE sys_menu SET component = 'system/monitor/online/index' WHERE menu_id = 109;
UPDATE sys_menu SET component = 'system/monitor/cache/index' WHERE menu_id = 113;
UPDATE sys_menu SET component = 'system/monitor/job/index' WHERE menu_id = 120;

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


-- ---- opentcs_menu_v3.6_left_sidebar_restructure.sql ----
-- ============================================================
-- OpenTCS Plus 菜单结构重构 v3.6
-- 关闭顶部导航后，左侧边栏一级菜单调整为：
--   车辆管理 / 任务管理 / 地图管理 / 监控管理 / 系统监控 / 系统管理
-- ============================================================

-- 清理旧 OpenTCS 业务菜单及其角色授权（保留若依框架原生菜单 id <= 999）
DELETE FROM sys_role_menu WHERE menu_id BETWEEN 2000 AND 4999;
DELETE FROM sys_menu WHERE menu_id BETWEEN 2000 AND 4999;

-- 隐藏历史遗留业务根菜单，避免与新菜单重复显示。
UPDATE sys_menu
SET visible = '1'
WHERE parent_id = 0
  AND menu_id > 999
  AND path IN ('deploy', 'ops', 'analytics', 'map', 'template', 'vehicle');

-- 1. 车辆管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2000, '车辆管理', 0, 1, 'vehicle', NULL, '', 1, 0, 'M', '0', '0', '', 'agv',
  103, 1, NOW(), NULL, NULL, '车辆管理目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2011, '品牌管理', 2000, 1, 'brand', 'vehicle/brand/index', '', 1, 0, 'C', '0', '0', 'vehicle:brand:list', 'pinpai',
  103, 1, NOW(), NULL, NULL, '品牌管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2012, '车辆型号', 2000, 2, 'type', 'vehicle/type/index', '', 1, 0, 'C', '0', '0', 'vehicle:type:list', 'model',
  103, 1, NOW(), NULL, NULL, '车辆型号菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2013, '机器人列表', 2000, 3, 'list', 'vehicle/list/index', '', 1, 0, 'C', '0', '0', 'vehicle:list:list', 'jiqi-ren',
  103, 1, NOW(), NULL, NULL, '机器人列表菜单');

-- 2. 任务管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3000, '任务管理', 0, 2, 'task', NULL, '', 1, 0, 'M', '0', '0', '', 'my-task',
  103, 1, NOW(), NULL, NULL, '任务管理目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3011, '任务运维管理', 3000, 1, 'operation', 'task/operation/index', '', 1, 0, 'C', '0', '0', 'ops:order:list', 'my-task',
  103, 1, NOW(), NULL, NULL, '任务运维管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3012, '任务模版管理', 3000, 2, 'template', 'task/template/index', '', 1, 0, 'C', '0', '0', 'task:template:list', 'edit',
  103, 1, NOW(), NULL, NULL, '任务模版管理菜单');

-- 3. 地图管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4000, '地图管理', 0, 3, 'map', NULL, '', 1, 0, 'M', '0', '0', '', 'map',
  103, 1, NOW(), NULL, NULL, '地图管理目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4011, '场景管理', 4000, 1, 'scene', 'map/scene/index', '', 1, 0, 'C', '0', '0', 'factory:model:list', 'factory',
  103, 1, NOW(), NULL, NULL, '场景管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4012, '地图控制台', 4000, 2, 'console', 'map/scene/console/index', '', 1, 0, 'C', '0', '0', 'factory:map:editor', 'map-model',
  103, 1, NOW(), NULL, NULL, '地图控制台菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4015, '区域管理', 4000, 3, 'areas', 'map/scene/areas/index', '', 1, 0, 'C', '0', '0', 'factory:block:list', 'area',
  103, 1, NOW(), NULL, NULL, '区域管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4013, '地图数据', 4000, 4, 'data', 'map/scene/data/index', '', 1, 0, 'C', '0', '0', 'factory:map:list', 'map',
  103, 1, NOW(), NULL, NULL, '地图数据菜单');

-- 4. 监控管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4500, '监控管理', 0, 4, 'monitoring', NULL, '', 1, 0, 'M', '0', '0', '', 'monitoring-screen',
  103, 1, NOW(), NULL, NULL, '监控管理目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4511, '场景监控', 4500, 1, 'scene', 'monitor/operationsdesk/index', '', 1, 0, 'C', '0', '0', 'ops:monitor:scene', 'monitoring-screen',
  103, 1, NOW(), NULL, NULL, '场景监控菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4512, '锁资源监控', 4500, 2, 'lock', 'monitor/lock/index', '', 1, 0, 'C', '0', '0', 'ops:monitor:lock', 'lock',
  103, 1, NOW(), NULL, NULL, '锁资源监控菜单');

-- 5. 若依框架菜单放到业务菜单之后
UPDATE sys_menu
SET parent_id = 0, order_num = 90, path = 'monitor', visible = '0', icon = 'shishi-jiankong'
WHERE menu_id = 2;

UPDATE sys_menu
SET parent_id = 0, order_num = 91, path = 'system', visible = '0', icon = 'system'
WHERE menu_id = 1;


-- ---- opentcs_menu_v3.7_consolidate_legacy_vehicle_menu.sql ----
-- 归并历史车辆菜单，兼容早期库中的超长 ID vehicle 菜单。
SET @legacy_vehicle_root_id := (
  SELECT MIN(menu_id)
  FROM sys_menu
  WHERE parent_id = 0
    AND path = 'vehicle'
    AND menu_id <> 2000
);

SET @vehicle_root_id := COALESCE(@legacy_vehicle_root_id, 2000);

DELETE FROM sys_role_menu
WHERE @legacy_vehicle_root_id IS NOT NULL
  AND menu_id IN (2000, 2011, 2012, 2013);

DELETE FROM sys_menu
WHERE @legacy_vehicle_root_id IS NOT NULL
  AND menu_id IN (2011, 2012, 2013, 2000);

UPDATE sys_menu
SET menu_name = '车辆管理',
    parent_id = 0,
    order_num = 1,
    path = 'vehicle',
    component = NULL,
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'agv',
    remark = '车辆管理目录'
WHERE menu_id = @vehicle_root_id;

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
SELECT 2011, '品牌管理', @vehicle_root_id, 1, 'brand', 'vehicle/brand/index', '',
  1, 0, 'C', '0', '0', 'vehicle:brand:list', 'pinpai',
  103, 1, NOW(), NULL, NULL, '品牌管理菜单'
WHERE NOT EXISTS (
  SELECT 1
  FROM sys_menu
  WHERE parent_id = @vehicle_root_id
    AND (component IN ('vehicle/brand/index', 'vehicle/brand/index')
      OR menu_name IN ('AMR品牌', '品牌管理'))
);

UPDATE sys_menu
SET parent_id = @vehicle_root_id,
    order_num = 1,
    menu_name = '品牌管理',
    path = 'brand',
    component = 'vehicle/brand/index',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'vehicle:brand:list',
    icon = 'pinpai',
    remark = '品牌管理菜单'
WHERE parent_id = @vehicle_root_id
  AND (component IN ('vehicle/brand/index', 'vehicle/brand/index')
    OR menu_name IN ('AMR品牌', '品牌管理'));

UPDATE sys_menu
SET parent_id = @vehicle_root_id,
    order_num = 2,
    menu_name = '车辆型号',
    path = 'type',
    component = 'vehicle/type/index',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'vehicle:type:list',
    icon = 'model',
    remark = '车辆型号菜单'
WHERE parent_id = @vehicle_root_id
  AND (component IN ('vehicle/type/index', 'vehicle/type/index')
    OR menu_name IN ('AMR型号', 'AMR类型', '车辆类型', '车辆型号'));

UPDATE sys_menu
SET parent_id = @vehicle_root_id,
    order_num = 3,
    menu_name = '机器人列表',
    path = 'list',
    component = 'vehicle/list/index',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'vehicle:list:list',
    icon = 'jiqi-ren',
    remark = '机器人列表菜单'
WHERE parent_id = @vehicle_root_id
  AND (component IN ('vehicle/list/index', 'vehicle/list/index')
    OR menu_name IN ('AMR列表', '机器人管理', '机器人列表'));


-- ---- opentcs_menu_v3.8_promote_system_groups_to_sidebar_roots.sql ----
-- 将原顶部“系统管理”下的二级分组提升为侧边栏一级菜单。
UPDATE sys_menu
SET parent_id = 0,
    order_num = 99,
    visible = '1',
    status = '0',
    icon = 'system',
    remark = '系统管理历史外层目录'
WHERE menu_id = 1;

UPDATE sys_menu
SET parent_id = 0,
    order_num = 90,
    menu_name = '系统监控',
    path = 'system/monitor',
    component = NULL,
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'shishi-jiankong',
    remark = '系统监控目录'
WHERE menu_id = 2;

UPDATE sys_menu
SET parent_id = 2
WHERE menu_id IN (109, 113, 120);

UPDATE sys_menu
SET parent_id = 0,
    order_num = 91,
    menu_name = '系统管理',
    path = 'system/management',
    component = NULL,
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'system',
    remark = '系统管理目录'
WHERE menu_id = 5001;

UPDATE sys_menu
SET parent_id = 5001
WHERE menu_id IN (100, 101, 102, 103, 104, 105, 106, 107, 108, 118, 123);

-- 移除已废弃的 Admin 监控菜单
DELETE FROM sys_role_menu WHERE menu_id = 117;
DELETE FROM sys_menu WHERE menu_id = 117 OR parent_id = 117;

-- Quartz 定时任务菜单
UPDATE sys_menu
SET menu_name = '定时任务',
    order_num = 3,
    path = 'job',
    component = 'system/monitor/job/index',
    perms = 'monitor:job:list',
    icon = 'job',
    remark = 'Quartz定时任务管理'
WHERE menu_id = 120;

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (134, '定时任务日志', 2, 7, 'job-log', 'system/monitor/job/log', '', 1, 1, 'C', '1', '0', 'monitor:job:list', '#', 103, 1, NOW(), NULL, NULL, '定时任务执行日志');

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 134);

-- 移除任务编排、运维管理（及下属 AMR管理/订单管理）
DELETE FROM sys_role_menu WHERE menu_id IN (2033796119204540417, 2033796453461209089, 2033795781630177282, 2033794847118274562, 3001, 3004);
DELETE FROM sys_menu WHERE menu_id IN (2033796119204540417, 2033796453461209089, 2033795781630177282, 2033794847118274562, 3001, 3004);
DELETE FROM sys_menu WHERE menu_id = 3000 AND menu_name = '运维管理';

DELETE rm FROM sys_role_menu rm
INNER JOIN sys_menu m ON rm.menu_id = m.menu_id
INNER JOIN sys_menu p ON m.parent_id = p.menu_id
WHERE p.menu_name = '运维管理' AND p.parent_id = 0
  AND m.menu_name IN ('AMR管理', '订单管理', 'AMR运维管理', '订单任务管理');

DELETE m FROM sys_menu m
INNER JOIN sys_menu p ON m.parent_id = p.menu_id
WHERE p.menu_name = '运维管理' AND p.parent_id = 0
  AND m.menu_name IN ('AMR管理', '订单管理', 'AMR运维管理', '订单任务管理');

DELETE FROM sys_role_menu WHERE menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name IN ('任务编排', '运维管理') AND parent_id = 0) t);
DELETE FROM sys_menu WHERE menu_name IN ('任务编排', '运维管理') AND parent_id = 0;

-- 移除旧版「地图管理」菜单树（雪花 ID，保留 menu_id=4000 新版）
DELETE FROM sys_role_menu WHERE menu_id IN (2033484413261602818, 2033484413261602819, 2033484413261602820);
DELETE FROM sys_menu WHERE menu_id IN (2033484413261602818, 2033484413261602819, 2033484413261602820);
DELETE rm FROM sys_role_menu rm INNER JOIN sys_menu m ON rm.menu_id = m.menu_id WHERE m.parent_id = 2033536149259051009;
DELETE FROM sys_menu WHERE parent_id = 2033536149259051009;
DELETE FROM sys_role_menu WHERE menu_id IN (2033478641265958914, 2033730612053004290, 2033484413261602817, 2033536149259051009, 2033536484035813377, 2033536315756142593);
DELETE FROM sys_menu WHERE menu_id IN (2033478641265958914, 2033730612053004290, 2033484413261602817, 2033536149259051009, 2033536484035813377, 2033536315756142593);
DELETE FROM sys_role_menu WHERE menu_id = 2033477517960044545;
DELETE FROM sys_menu WHERE menu_id = 2033477517960044545;

-- 前端视图领域路径（vehicle / task）
UPDATE sys_menu SET component = 'vehicle/brand/index', path = 'brand' WHERE menu_id = 2011;
UPDATE sys_menu SET component = 'vehicle/type/index', path = 'type' WHERE menu_id = 2012;
UPDATE sys_menu SET component = 'vehicle/list/index', path = 'list' WHERE menu_id = 2013;
UPDATE sys_menu SET component = 'vehicle/brand/index' WHERE component IN ('deploy/device/brand/index', 'opentcs/vehicle/brand/index');
UPDATE sys_menu SET component = 'vehicle/type/index' WHERE component IN ('deploy/device/type/index', 'opentcs/vehicle/type/index');
UPDATE sys_menu SET component = 'vehicle/list/index' WHERE component IN ('deploy/device/list/index', 'opentcs/vehicle/index');
UPDATE sys_menu SET component = 'task/operation/index', path = 'operation' WHERE menu_id = 3011;
UPDATE sys_menu SET component = 'task/template/index', path = 'template' WHERE menu_id = 3012;
UPDATE sys_menu SET component = 'task/template/index' WHERE component IN ('deploy/task-config/template/index');
UPDATE sys_menu SET component = 'task/operation/index' WHERE component IN ('ops/order/index');

-- 监控视图领域路径（monitor）
UPDATE sys_menu SET component = 'monitor/operationsdesk/index', path = 'scene' WHERE menu_id = 4511;
UPDATE sys_menu SET component = 'monitor/lock/index', path = 'lock' WHERE menu_id = 4512;
UPDATE sys_menu SET component = 'monitor/operationsdesk/index' WHERE component = 'ops/monitor/operationsdesk/index';
UPDATE sys_menu SET component = 'monitor/lock/index' WHERE component = 'ops/monitor/lock/index';
UPDATE sys_menu SET component = 'monitor/live/index' WHERE component = 'ops/monitor/live/index';

-- 地图视图目录（console 归入 scene，主数据归入 map/scene/data）
UPDATE sys_menu SET component = 'map/scene/console/index' WHERE menu_id = 4012;
UPDATE sys_menu SET component = 'map/scene/data/index', menu_name = '地图数据', order_num = 3, remark = '地图数据菜单' WHERE menu_id = 4013;
DELETE FROM sys_role_menu WHERE menu_id = 4014;
DELETE FROM sys_menu WHERE menu_id = 4014;

-- 地图模型极简化：删除位置类型 / Location / Block 相关菜单，仅保留点与路径。
DELETE FROM sys_role_menu
WHERE menu_id IN (
    SELECT menu_id FROM (
        SELECT menu_id
        FROM sys_menu
        WHERE menu_id IN (2022, 4015)
           OR perms IN (
                'factory:block:list',
                'factory:block:add',
                'factory:block:edit',
                'factory:block:remove',
                'opentcs:locationType:list',
                'opentcs:locationType:add',
                'opentcs:locationType:edit',
                'opentcs:locationType:remove',
                'opentcs:locationType:query',
                'factory:locationType:list'
           )
           OR path IN ('areas', 'location-type', 'location')
           OR component IN (
                'map/scene/areas/index',
                'map/scene/location-type/index',
                'deploy/factory/location-type/index'
           )
    ) t
);

DELETE FROM sys_menu
WHERE menu_id IN (2022, 4015)
   OR perms IN (
        'factory:block:list',
        'factory:block:add',
        'factory:block:edit',
        'factory:block:remove',
        'opentcs:locationType:list',
        'opentcs:locationType:add',
        'opentcs:locationType:edit',
        'opentcs:locationType:remove',
        'opentcs:locationType:query',
        'factory:locationType:list'
   )
   OR path IN ('areas', 'location-type', 'location')
   OR component IN (
        'map/scene/areas/index',
        'map/scene/location-type/index',
        'deploy/factory/location-type/index'
   );
