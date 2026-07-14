-- 主数据页归入 map/scene/data

UPDATE sys_menu SET component = 'map/scene/data/index'
WHERE component IN ('map/console/data/index', 'map/scene/data/index');

UPDATE sys_menu SET component = 'map/scene/data/index', menu_name = '主数据管理'
WHERE menu_id = 4013;
