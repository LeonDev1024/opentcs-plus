-- 地图管理二级菜单调整：删除地图列表，区域列表改为区域管理，地图数据管理改名为地图数据

DELETE FROM sys_role_menu WHERE menu_id = 4014;
DELETE FROM sys_menu WHERE menu_id = 4014;

UPDATE sys_menu
SET menu_name = '区域管理',
    order_num = 3,
    path = 'areas',
    component = 'map/scene/areas/index',
    perms = 'factory:block:list',
    icon = 'area',
    remark = '区域管理菜单'
WHERE menu_id = 4015;

UPDATE sys_menu
SET menu_name = '地图数据',
    order_num = 4,
    path = 'data',
    component = 'map/scene/data/index',
    remark = '地图数据菜单'
WHERE menu_id = 4013;

-- 兼容旧名称
UPDATE sys_menu
SET menu_name = '地图数据', order_num = 4
WHERE menu_id = 4013
   OR menu_name IN ('地图数据管理', '主数据管理');
