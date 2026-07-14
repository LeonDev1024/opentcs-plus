-- 前端地图视图：deploy/factory、deploy/map-editor → map

UPDATE sys_menu SET component = 'map/scene/index'
WHERE component IN ('deploy/factory/model/index');

UPDATE sys_menu SET component = 'map/console/MapEditorTabs'
WHERE component IN ('deploy/map-editor/MapEditorTabs');

UPDATE sys_menu SET component = 'map/scene/data/index'
WHERE component IN ('deploy/factory/map/index');

UPDATE sys_menu SET component = 'map/scene/location-type/index'
WHERE component IN ('deploy/factory/location-type/index');

-- 新增地图列表、区域列表菜单（若不存在）
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4014, '地图列表', 4000, 3, 'maps', 'map/scene/maps/index', '', 1, 0, 'C', '0', '0', 'factory:map:list', 'list',
  103, 1, NOW(), NULL, NULL, '地图列表菜单');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4015, '区域列表', 4000, 4, 'areas', 'map/scene/areas/index', '', 1, 0, 'C', '0', '0', 'factory:block:list', 'area',
  103, 1, NOW(), NULL, NULL, '区域列表菜单');

UPDATE sys_menu SET order_num = 5 WHERE menu_id = 4013;
