-- 将原顶部“系统管理”下的二级分组提升为侧边栏一级菜单。
-- 目标结构：
--   系统监控 (id=2)
--   系统管理 (id=5001)
-- 外层旧壳菜单 id=1 隐藏，避免侧边栏出现“系统管理 / 系统管理”双层嵌套。

-- 外层旧壳：仅作为历史容器保留，不在侧边栏显示。
UPDATE sys_menu
SET parent_id = 0,
    order_num = 99,
    visible = '1',
    status = '0',
    icon = 'system',
    remark = '系统管理历史外层目录'
WHERE menu_id = 1;

-- 系统监控提升为一级菜单，保留 /system/monitor/* 路由层级。
UPDATE sys_menu
SET parent_id = 0,
    order_num = 90,
    menu_name = '系统监控',
    path = 'system/monitor',
    component = NULL,
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'shishi-jiankong',
    remark = '系统监控目录'
WHERE menu_id = 2;

UPDATE sys_menu
SET parent_id = 2
WHERE menu_id IN (109, 113, 117, 120);

-- 系统管理分组提升为一级菜单，保留 /system/management/* 路由层级。
UPDATE sys_menu
SET parent_id = 0,
    order_num = 91,
    menu_name = '系统管理',
    path = 'system/management',
    component = NULL,
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'system',
    remark = '系统管理目录'
WHERE menu_id = 5001;

UPDATE sys_menu
SET parent_id = 5001
WHERE menu_id IN (100, 101, 102, 103, 104, 105, 106, 107, 108, 118, 123);
