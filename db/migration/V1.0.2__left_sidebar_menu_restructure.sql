-- OpenTCS Plus 左侧边栏菜单重构
-- 关闭顶部导航后，左侧边栏一级菜单调整为：
--   车辆管理 / 任务管理 / 地图管理 / 监控管理 / 系统监控 / 系统管理

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
VALUES (2011, '品牌管理', 2000, 1, 'brand', 'deploy/device/brand/index', '', 1, 0, 'C', '0', '0', 'vehicle:brand:list', 'pinpai',
  103, 1, NOW(), NULL, NULL, '品牌管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2012, '车辆类型', 2000, 2, 'type', 'deploy/device/type/index', '', 1, 0, 'C', '0', '0', 'vehicle:type:list', 'model',
  103, 1, NOW(), NULL, NULL, '车辆类型菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2013, '机器人管理', 2000, 3, 'robot', 'deploy/device/list/index', '', 1, 0, 'C', '0', '0', 'vehicle:list:list', 'jiqi-ren',
  103, 1, NOW(), NULL, NULL, '机器人管理菜单');

-- 2. 任务管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3000, '任务管理', 0, 2, 'task', NULL, '', 1, 0, 'M', '0', '0', '', 'my-task',
  103, 1, NOW(), NULL, NULL, '任务管理目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3011, '任务运维管理', 3000, 1, 'operation', 'ops/order/index', '', 1, 0, 'C', '0', '0', 'ops:order:list', 'my-task',
  103, 1, NOW(), NULL, NULL, '任务运维管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3012, '任务模版管理', 3000, 2, 'template', 'deploy/task-config/template/index', '', 1, 0, 'C', '0', '0', 'task:template:list', 'edit',
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
VALUES (4011, '场景管理', 4000, 1, 'scene', 'deploy/factory/model/index', '', 1, 0, 'C', '0', '0', 'factory:model:list', 'factory',
  103, 1, NOW(), NULL, NULL, '场景管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4012, '地图控制台', 4000, 2, 'console', 'deploy/map-editor/MapEditorTabs', '', 1, 0, 'C', '0', '0', 'factory:map:editor', 'map-model',
  103, 1, NOW(), NULL, NULL, '地图控制台菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4013, '地图数据管理', 4000, 3, 'data', 'deploy/factory/map/index', '', 1, 0, 'C', '0', '0', 'factory:map:list', 'map',
  103, 1, NOW(), NULL, NULL, '地图数据管理菜单');

-- 4. 监控管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4500, '监控管理', 0, 4, 'monitoring', NULL, '', 1, 0, 'M', '0', '0', '', 'monitoring-screen',
  103, 1, NOW(), NULL, NULL, '监控管理目录');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4511, '场景监控', 4500, 1, 'scene', 'ops/monitor/operationsdesk/index', '', 1, 0, 'C', '0', '0', 'ops:monitor:scene', 'monitoring-screen',
  103, 1, NOW(), NULL, NULL, '场景监控菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4512, '锁资源监控', 4500, 2, 'lock', 'ops/monitor/lock/index', '', 1, 0, 'C', '0', '0', 'ops:monitor:lock', 'lock',
  103, 1, NOW(), NULL, NULL, '锁资源监控菜单');

-- 5. 若依框架菜单放到业务菜单之后
UPDATE sys_menu
SET parent_id = 0, order_num = 90, path = 'monitor', visible = '0', icon = 'shishi-jiankong'
WHERE menu_id = 2;

UPDATE sys_menu
SET parent_id = 0, order_num = 91, path = 'system', visible = '0', icon = 'system'
WHERE menu_id = 1;
