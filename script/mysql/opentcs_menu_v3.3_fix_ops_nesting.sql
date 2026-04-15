-- ============================================================
-- OpenTCS Plus 菜单修复 v3.3
-- 修复 v3.2 引入的"运维管理"子目录双层嵌套问题
--
-- 问题来源：v3.2 错误地将 menu_id=3001（子目录本身）移到 3004 下，
--   导致路径变成 /ops/management/management/amr（URL 双倍）
--   且侧边栏出现两层"运维管理"标题。
--
-- 修复目标：
--   3000 (ops) → 3004 (management) → 3011 (amr)
--                                  → 3012 (order)
--              → 3003 (monitor)   → 3031 (live)
--                                  → 3032 (lock)
-- ============================================================

-- 1. 将叶子节点直接挂到 3004（去掉中间的 3001 层）
UPDATE sys_menu SET parent_id = 3004, order_num = 1 WHERE menu_id = 3011;
UPDATE sys_menu SET parent_id = 3004, order_num = 2 WHERE menu_id = 3012;

-- 2. 删除被错误嵌套的旧子目录（3001 已无子节点，可安全删除）
DELETE FROM sys_menu WHERE menu_id = 3001;
