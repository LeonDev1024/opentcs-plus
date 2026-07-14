-- ============================================================
-- OpenTCS Plus 菜单结构重构 v3.7
-- 归并历史车辆菜单，兼容早期库中的超长 ID vehicle 菜单。
-- ============================================================

SET @legacy_vehicle_root_id := (
  SELECT MIN(menu_id)
  FROM sys_menu
  WHERE parent_id = 0
    AND path = 'vehicle'
    AND menu_id <> 2000
);

SET @vehicle_root_id := COALESCE(@legacy_vehicle_root_id, 2000);

-- 如果存在历史 vehicle 根菜单，移除 V1.0.2/V1.0.3 可能插入的固定 ID 车辆菜单，避免重复。
DELETE FROM sys_role_menu
WHERE @legacy_vehicle_root_id IS NOT NULL
  AND menu_id IN (2000, 2011, 2012, 2013);

DELETE FROM sys_menu
WHERE @legacy_vehicle_root_id IS NOT NULL
  AND menu_id IN (2011, 2012, 2013, 2000);

UPDATE sys_menu
SET menu_name = '车辆管理',
    parent_id = 0,
    order_num = 1,
    path = 'vehicle',
    component = NULL,
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    icon = 'agv',
    remark = '车辆管理目录'
WHERE menu_id = @vehicle_root_id;

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
SELECT 2011, '品牌管理', @vehicle_root_id, 1, 'brand', 'vehicle/brand/index', '',
  1, 0, 'C', '0', '0', 'vehicle:brand:list', 'pinpai',
  103, 1, NOW(), NULL, NULL, '品牌管理菜单'
WHERE NOT EXISTS (
  SELECT 1
  FROM sys_menu
  WHERE parent_id = @vehicle_root_id
    AND (component IN ('vehicle/brand/index', 'opentcs/vehicle/brand/index')
      OR menu_name IN ('AMR品牌', '品牌管理'))
);

UPDATE sys_menu
SET parent_id = @vehicle_root_id,
    order_num = 1,
    menu_name = '品牌管理',
    path = 'brand',
    component = 'vehicle/brand/index',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'vehicle:brand:list',
    icon = 'pinpai',
    remark = '品牌管理菜单'
WHERE parent_id = @vehicle_root_id
  AND (component IN ('vehicle/brand/index', 'opentcs/vehicle/brand/index')
    OR menu_name IN ('AMR品牌', '品牌管理'));

UPDATE sys_menu
SET parent_id = @vehicle_root_id,
    order_num = 2,
    menu_name = '车辆型号',
    path = 'type',
    component = 'vehicle/type/index',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'vehicle:type:list',
    icon = 'model',
    remark = '车辆型号菜单'
WHERE parent_id = @vehicle_root_id
  AND (component IN ('vehicle/type/index', 'opentcs/vehicle/type/index')
    OR menu_name IN ('AMR型号', 'AMR类型', '车辆类型', '车辆型号'));

UPDATE sys_menu
SET parent_id = @vehicle_root_id,
    order_num = 3,
    menu_name = '机器人列表',
    path = 'list',
    component = 'vehicle/list/index',
    menu_type = 'C',
    visible = '0',
    status = '0',
    perms = 'vehicle:list:list',
    icon = 'jiqi-ren',
    remark = '机器人列表菜单'
WHERE parent_id = @vehicle_root_id
  AND (component IN ('vehicle/list/index', 'opentcs/vehicle/index')
    OR menu_name IN ('AMR列表', '机器人管理', '机器人列表'));
