-- 任务管理（menu_id=3000）下二级菜单组件路径

UPDATE sys_menu SET component = 'task/operation/index', path = 'operation'
WHERE menu_id = 3011;

UPDATE sys_menu SET component = 'task/template/index', path = 'template'
WHERE menu_id = 3012;

-- 兜底：历史 component 路径
UPDATE sys_menu SET component = 'task/operation/index'
WHERE component IN ('ops/order/index');

UPDATE sys_menu SET component = 'task/template/index'
WHERE component IN ('deploy/task-config/template/index');
