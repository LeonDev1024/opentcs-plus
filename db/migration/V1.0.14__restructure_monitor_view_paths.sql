-- 前端监控视图：ops/monitor → monitor

UPDATE sys_menu SET component = 'monitor/operationsdesk/index'
WHERE component = 'ops/monitor/operationsdesk/index';

UPDATE sys_menu SET component = 'monitor/lock/index'
WHERE component = 'ops/monitor/lock/index';

UPDATE sys_menu SET component = 'monitor/live/index'
WHERE component = 'ops/monitor/live/index';
