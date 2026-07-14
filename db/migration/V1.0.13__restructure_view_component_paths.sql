-- 前端视图按领域分层：vehicle / task

UPDATE sys_menu SET component = 'vehicle/brand/index'
WHERE component IN ('deploy/device/brand/index', 'opentcs/vehicle/brand/index');

UPDATE sys_menu SET component = 'vehicle/type/index'
WHERE component IN ('deploy/device/type/index', 'opentcs/vehicle/type/index');

UPDATE sys_menu SET component = 'vehicle/list/index'
WHERE component IN ('deploy/device/list/index', 'opentcs/vehicle/index');

UPDATE sys_menu SET component = 'task/template/index'
WHERE component IN ('deploy/task-config/template/index');

UPDATE sys_menu SET component = 'task/operation/index'
WHERE component IN ('ops/order/index');
