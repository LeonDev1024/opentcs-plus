-- ============================================================
-- 开发环境系统引导数据（登录必需）
-- 默认账号：admin / admin123
-- 客户端 ID 须与前端 VITE_APP_CLIENT_ID 一致
-- ============================================================

USE opentcsplus;

-- 部门
INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time)
VALUES
    (100, 0, '0', 'OpenTCS', 0, NULL, NULL, NULL, '0', '0', 103, 1, NOW(), NULL, NULL),
    (103, 100, '0,100', '研发部门', 1, NULL, NULL, NULL, '0', '0', 103, 1, NOW(), NULL, NULL)
ON DUPLICATE KEY UPDATE dept_name = VALUES(dept_name);

-- OAuth 客户端（PC 端）
INSERT INTO sys_client (id, client_id, client_key, client_secret, grant_type, device_type, active_timeout, timeout, status, del_flag, create_dept, create_by, create_time, update_by, update_time)
VALUES
    (1, 'e5cd7e4891bf95d1d19206ce24a7b32e', 'pc', 'pc123', 'password,sms,email,social', 'pc', 1800, 604800, '0', '0', 103, 1, NOW(), 1, NOW())
ON DUPLICATE KEY UPDATE
    client_key = VALUES(client_key),
    grant_type = VALUES(grant_type),
    device_type = VALUES(device_type),
    status = VALUES(status);

-- 超级管理员（user_id=1 拥有全部权限）
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES
    (1, 103, 'admin', '管理员', 'sys_user', 'admin@opentcs.local', '15888888888', '1', NULL,
     '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NOW(), 103, 1, NOW(), 1, NOW(), '管理员')
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    status = VALUES(status);

-- 超级管理员角色
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES
    (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 103, 1, NOW(), NULL, NULL, '超级管理员')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- 角色菜单：授予全部菜单（非 superadmin 用户时需要；admin 用户 id=1 本身已 bypass）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu;
