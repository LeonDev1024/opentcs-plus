-- 地图视图目录调整：console 归入 scene，主数据页归入 map/console/data

UPDATE sys_menu SET component = 'map/scene/console/MapEditorTabs'
WHERE component IN ('map/console/MapEditorTabs', 'deploy/map-editor/MapEditorTabs');

UPDATE sys_menu SET component = 'map/console/data/index'
WHERE component IN ('map/scene/data/index', 'deploy/factory/map/index');

UPDATE sys_menu SET menu_name = '主数据管理' WHERE menu_id = 4013;
