-- 移除一级菜单：任务编排、运维管理；以及运维管理下 AMR管理、订单管理

-- 1. 雪花 ID 子菜单（运维管理下）
DELETE FROM sys_role_menu WHERE menu_id IN (2033796119204540417, 2033796453461209089);
DELETE FROM sys_menu WHERE menu_id IN (2033796119204540417, 2033796453461209089);

-- 2. 雪花 ID 一级菜单
DELETE FROM sys_role_menu WHERE menu_id IN (2033795781630177282, 2033794847118274562);
DELETE FROM sys_menu WHERE menu_id IN (2033795781630177282, 2033794847118274562);

-- 3. 历史运维结构中间目录
DELETE FROM sys_role_menu WHERE menu_id IN (3001, 3004);
DELETE FROM sys_menu WHERE menu_id IN (3001, 3004);

-- 4. 历史运维结构叶子（仅 AMR运维管理 / 订单任务管理，不碰「任务管理」下的 3011/3012）
DELETE FROM sys_role_menu
WHERE menu_id IN (SELECT menu_id FROM (
  SELECT menu_id FROM sys_menu
  WHERE menu_name IN ('AMR运维管理', '订单任务管理')
    AND path IN ('amr', 'order')
) t);
DELETE FROM sys_menu
WHERE menu_name IN ('AMR运维管理', '订单任务管理')
  AND path IN ('amr', 'order');

-- 5. 一级「运维管理」（menu_id=3000 且名称仍为运维管理时）
DELETE FROM sys_role_menu
WHERE menu_id = 3000
  AND EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3000 AND menu_name = '运维管理');
DELETE FROM sys_menu WHERE menu_id = 3000 AND menu_name = '运维管理';

-- 6. 按名称兜底：一级任务编排 / 运维管理
DELETE FROM sys_role_menu
WHERE menu_id IN (SELECT menu_id FROM (
  SELECT menu_id FROM sys_menu WHERE menu_name IN ('任务编排', '运维管理') AND parent_id = 0
) t);
DELETE FROM sys_menu WHERE menu_name IN ('任务编排', '运维管理') AND parent_id = 0;

-- 7. 运维管理下 AMR管理 / 订单管理（任意 parent 为一级运维管理）
DELETE rm FROM sys_role_menu rm
INNER JOIN sys_menu m ON rm.menu_id = m.menu_id
INNER JOIN sys_menu p ON m.parent_id = p.menu_id
WHERE p.menu_name = '运维管理' AND p.parent_id = 0
  AND m.menu_name IN ('AMR管理', '订单管理', 'AMR运维管理', '订单任务管理');

DELETE m FROM sys_menu m
INNER JOIN sys_menu p ON m.parent_id = p.menu_id
WHERE p.menu_name = '运维管理' AND p.parent_id = 0
  AND m.menu_name IN ('AMR管理', '订单管理', 'AMR运维管理', '订单任务管理');

-- 8. 任务编排下残留子菜单
DELETE rm FROM sys_role_menu rm
INNER JOIN sys_menu m ON rm.menu_id = m.menu_id
INNER JOIN sys_menu p ON m.parent_id = p.menu_id
WHERE p.menu_name = '任务编排' AND p.parent_id = 0;

DELETE m FROM sys_menu m
INNER JOIN sys_menu p ON m.parent_id = p.menu_id
WHERE p.menu_name = '任务编排' AND p.parent_id = 0;
