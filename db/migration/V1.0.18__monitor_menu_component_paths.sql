-- 监控管理（menu_id=4500）下二级菜单组件路径

UPDATE sys_menu SET component = 'monitor/operationsdesk/index', path = 'scene'
WHERE menu_id = 4511;

UPDATE sys_menu SET component = 'monitor/lock/index', path = 'lock'
WHERE menu_id = 4512;

-- 兜底：历史 component 路径
UPDATE sys_menu SET component = 'monitor/operationsdesk/index'
WHERE component = 'ops/monitor/operationsdesk/index';

UPDATE sys_menu SET component = 'monitor/lock/index'
WHERE component = 'ops/monitor/lock/index';

UPDATE sys_menu SET component = 'monitor/live/index'
WHERE component = 'ops/monitor/live/index';
