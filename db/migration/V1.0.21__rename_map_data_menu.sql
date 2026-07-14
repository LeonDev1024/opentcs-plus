-- 菜单名称：地图数据管理 / 主数据管理 → 地图数据

UPDATE sys_menu SET menu_name = '地图数据'
WHERE menu_id = 4013
   OR menu_name IN ('地图数据管理', '主数据管理');
