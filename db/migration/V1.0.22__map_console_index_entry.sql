-- 地图控制台入口改为标准 index 包装页

UPDATE sys_menu SET component = 'map/scene/console/index'
WHERE menu_id = 4012
   OR component IN ('map/scene/console/MapEditorTabs', 'map/console/MapEditorTabs', 'deploy/map-editor/MapEditorTabs');
