-- ============================================================
-- OpenTCS Plus 菜单重构 v3.4
-- 系统管理顶部导航下新增两个二级子目录：
--   /system/management → 原 /system 下所有菜单
--   /system/monitor    → 原 /monitor 下所有菜单（id=2 移入 id=1）
--
-- 执行前提：v3.0 已执行（id=1 order_num=5, id=2 order_num=6）
-- ============================================================

-- 1. 新增"系统管理"子目录（id=5001），归入顶级系统管理（id=1）下
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (5001, '系统管理', 1, 1, 'management', NULL, '', 1, 0, 'M', '0', '0', '', 'system',
  103, 1, NOW(), NULL, NULL, '系统管理子目录');

-- 2. 将原 id=1 的直接子菜单（100-108、118、123）移入新子目录 5001
UPDATE sys_menu
SET parent_id = 5001
WHERE menu_id IN (100, 101, 102, 103, 104, 105, 106, 107, 108, 118, 123);

-- 3. 将"系统监控"(id=2) 从顶级移入 id=1 下，作为二级子目录
--    path 保持 'monitor'，order_num 改为 2（在 5001 之后）
UPDATE sys_menu
SET parent_id = 1, order_num = 2
WHERE menu_id = 2;
