-- OpenTCS Plus 车辆管理菜单命名与图标语义优化
-- 将原“部署管理 / 设备管理”下的 AMR 菜单收敛到“车辆管理”下：
--   AMR品牌 -> 品牌管理
--   AMR型号 -> 车辆型号
--   AMR列表 -> 机器人列表

UPDATE sys_menu
SET menu_name = '车辆管理',
    icon = 'agv',
    path = 'vehicle',
    component = NULL,
    remark = '车辆管理目录'
WHERE menu_id = 2000;

UPDATE sys_menu
SET parent_id = 2000,
    order_num = 1,
    menu_name = '品牌管理',
    path = 'brand',
    component = 'deploy/device/brand/index',
    perms = 'vehicle:brand:list',
    icon = 'pinpai',
    remark = '品牌管理菜单'
WHERE menu_id = 2011;

UPDATE sys_menu
SET parent_id = 2000,
    order_num = 2,
    menu_name = '车辆型号',
    path = 'type',
    component = 'deploy/device/type/index',
    perms = 'vehicle:type:list',
    icon = 'model',
    remark = '车辆型号菜单'
WHERE menu_id = 2012;

UPDATE sys_menu
SET parent_id = 2000,
    order_num = 3,
    menu_name = '机器人列表',
    path = 'list',
    component = 'deploy/device/list/index',
    perms = 'vehicle:list:list',
    icon = 'jiqi-ren',
    remark = '机器人列表菜单'
WHERE menu_id = 2013;
