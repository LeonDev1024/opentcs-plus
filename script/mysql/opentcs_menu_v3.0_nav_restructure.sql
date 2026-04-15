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
