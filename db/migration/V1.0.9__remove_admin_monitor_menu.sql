-- 系统监控：移除 Admin 监控菜单

DELETE FROM sys_role_menu WHERE menu_id = 117;
DELETE FROM sys_menu WHERE menu_id = 117 OR parent_id = 117;
