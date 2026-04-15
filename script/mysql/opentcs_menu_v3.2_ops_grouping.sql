-- ============================================================
-- OpenTCS Plus 菜单迁移 v3.2  ⚠️ 此文件包含错误，请执行 v3.3 修复！
-- 错误说明：
--   步骤 2 的 UPDATE 指向了 menu_id=3001（子目录本身）而非叶子节点
--   (3011/3012)，执行后造成 management 双层嵌套，URL 变为
--   /ops/management/management/amr。请勿重复执行本文件。
--   修复脚本：opentcs_menu_v3.3_fix_ops_nesting.sql
-- ============================================================

-- 1. 新增"运维管理"二级子目录（使用未占用的 id 3004）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (3004, '运维管理', 3000, 1, 'management', NULL, '', 1, 0, 'M', '0', '0', '', 'tool',
  103, 1, NOW(), NULL, NULL, '运维管理子目录');

-- 2. ⚠️ 错误：应更新 3011/3012（叶子），实际更新了 3001（子目录）
--    正确写法见 v3.3 修复脚本
UPDATE sys_menu SET parent_id = 3004, order_num = 1 WHERE menu_id = 3001;
UPDATE sys_menu SET parent_id = 3004, order_num = 2 WHERE menu_id = 3002;

-- 3. 实时监控子目录 order_num 调整为 2
UPDATE sys_menu SET order_num = 2 WHERE menu_id = 3003;
