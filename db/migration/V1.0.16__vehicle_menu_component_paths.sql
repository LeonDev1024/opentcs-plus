-- 车辆管理（menu_id=2000）下二级菜单组件路径

UPDATE sys_menu SET component = 'vehicle/brand/index', path = 'brand'
WHERE menu_id = 2011;

UPDATE sys_menu SET component = 'vehicle/type/index', path = 'type'
WHERE menu_id = 2012;

UPDATE sys_menu SET component = 'vehicle/list/index', path = 'list'
WHERE menu_id = 2013;

-- 兜底：历史 component 路径
UPDATE sys_menu SET component = 'vehicle/brand/index'
WHERE component IN ('deploy/device/brand/index', 'opentcs/vehicle/brand/index');

UPDATE sys_menu SET component = 'vehicle/type/index'
WHERE component IN ('deploy/device/type/index', 'opentcs/vehicle/type/index');

UPDATE sys_menu SET component = 'vehicle/list/index'
WHERE component IN ('deploy/device/list/index', 'opentcs/vehicle/index');
