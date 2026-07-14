-- 移除旧版一级「地图管理」及其二、三级子菜单（雪花 ID 菜单树）
-- 保留新版 menu_id=4000 的「地图管理」（场景管理/地图控制台/地图数据管理）

-- 1. 三级：地图列表按钮权限
DELETE FROM sys_role_menu WHERE menu_id IN (2033484413261602818, 2033484413261602819, 2033484413261602820);
DELETE FROM sys_menu WHERE menu_id IN (2033484413261602818, 2033484413261602819, 2033484413261602820);

-- 2. 路径管理下残留子菜单
DELETE rm FROM sys_role_menu rm
INNER JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE m.parent_id = 2033536149259051009;
DELETE FROM sys_menu WHERE parent_id = 2033536149259051009;

-- 3. 二级菜单
DELETE FROM sys_role_menu WHERE menu_id IN (
  2033478641265958914,
  2033730612053004290,
  2033484413261602817,
  2033536149259051009,
  2033536484035813377,
  2033536315756142593
);
DELETE FROM sys_menu WHERE menu_id IN (
  2033478641265958914,
  2033730612053004290,
  2033484413261602817,
  2033536149259051009,
  2033536484035813377,
  2033536315756142593
);

-- 4. 一级菜单
DELETE FROM sys_role_menu WHERE menu_id = 2033477517960044545;
DELETE FROM sys_menu WHERE menu_id = 2033477517960044545;

-- 5. 按名称兜底（仅旧版 opentcs/* 路径，避免误删 menu_id=4000 新菜单）
DELETE rm FROM sys_role_menu rm
INNER JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE m.menu_id IN (
  SELECT menu_id FROM (
    SELECT menu_id FROM sys_menu
    WHERE menu_id = 2033477517960044545
       OR parent_id = 2033477517960044545
       OR parent_id IN (
         SELECT menu_id FROM sys_menu WHERE parent_id = 2033477517960044545
       )
  ) t
);

DELETE FROM sys_menu
WHERE menu_id = 2033477517960044545
   OR parent_id = 2033477517960044545
   OR parent_id IN (
     SELECT menu_id FROM (
       SELECT menu_id FROM sys_menu WHERE parent_id = 2033477517960044545
     ) t
   );

DELETE FROM sys_role_menu
WHERE menu_id IN (
  SELECT menu_id FROM (
    SELECT menu_id FROM sys_menu
    WHERE parent_id = 0
      AND menu_name = '地图管理'
      AND menu_id <> 4000
      AND (component IS NULL OR component LIKE 'opentcs/%')
  ) t
);
DELETE FROM sys_menu
WHERE parent_id = 0
  AND menu_name = '地图管理'
  AND menu_id <> 4000
  AND (component IS NULL OR component = '');
