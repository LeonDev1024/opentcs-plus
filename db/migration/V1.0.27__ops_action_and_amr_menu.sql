-- I3: OpsAction 持久化 + 恢复 AMR 运维菜单

CREATE TABLE IF NOT EXISTS tcs_ops_action (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    action_id VARCHAR(64) NOT NULL COMMENT '动作业务ID',
    request_id VARCHAR(64) NULL COMMENT '客户端幂等键',
    trace_id VARCHAR(64) NOT NULL COMMENT '链路追踪ID',
    vehicle_name VARCHAR(128) NOT NULL COMMENT '车辆名称',
    action_category VARCHAR(32) NOT NULL COMMENT 'MODE_SWITCH/MAP_SWITCH/GO_CHARGE/MOVE',
    action_type VARCHAR(64) NOT NULL COMMENT 'VDA actionType',
    request_payload JSON NULL COMMENT '请求参数',
    execute_status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/ACCEPTED/RUNNING/SUCCEEDED/FAILED/TIMEOUT/REJECTED',
    reason_code VARCHAR(64) NULL,
    reason_message VARCHAR(512) NULL,
    operator_name VARCHAR(64) NULL COMMENT '操作人',
    operated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at DATETIME NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_ops_action PRIMARY KEY (id),
    CONSTRAINT uk_ops_action_id UNIQUE (action_id),
    CONSTRAINT uk_ops_action_request UNIQUE (vehicle_name, request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AMR运维动作审计表';

CREATE INDEX idx_ops_action_vehicle_time ON tcs_ops_action(vehicle_name, operated_at);
CREATE INDEX idx_ops_action_status ON tcs_ops_action(execute_status);

-- 车辆管理下恢复 AMR 运维动作台
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (2014, 'AMR运维管理', 2000, 4, 'amr', 'vehicle/amr/index', '', 1, 0, 'C', '0', '0', 'ops:amr:list', 'robot',
  103, 1, NOW(), NULL, NULL, 'AMR运维动作台')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  perms = VALUES(perms),
  visible = '0',
  status = '0',
  remark = VALUES(remark);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES
(20141, '模式切换', 2014, 1, '', '', '', 1, 0, 'F', '0', '0', 'ops:amr:mode', '#', 103, 1, NOW(), NULL, NULL, ''),
(20142, '地图切换', 2014, 2, '', '', '', 1, 0, 'F', '0', '0', 'ops:amr:map', '#', 103, 1, NOW(), NULL, NULL, ''),
(20143, '去充电', 2014, 3, '', '', '', 1, 0, 'F', '0', '0', 'ops:amr:charge', '#', 103, 1, NOW(), NULL, NULL, ''),
(20144, '移动/重定位', 2014, 4, '', '', '', 1, 0, 'F', '0', '0', 'ops:amr:move', '#', 103, 1, NOW(), NULL, NULL, '')
ON DUPLICATE KEY UPDATE perms = VALUES(perms), menu_name = VALUES(menu_name);

-- 告警中心入口（监控管理下）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (4513, '告警中心', 4500, 3, 'alarm', 'monitor/alarm/index', '', 1, 0, 'C', '0', '0', 'ops:monitor:alarm', 'message',
  103, 1, NOW(), NULL, NULL, '运维告警中心')
ON DUPLICATE KEY UPDATE
  component = VALUES(component),
  path = VALUES(path),
  perms = VALUES(perms),
  visible = '0',
  status = '0';

-- 超管角色授权新菜单
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
(1, 2014), (1, 20141), (1, 20142), (1, 20143), (1, 20144), (1, 4513);
