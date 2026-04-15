-- ============================================================
-- OpenTCS Plus 菜单组件路径更新 v3.5
-- 与前端 views 目录重构同步：
--   system/* → system/management/*
--   monitor/* → system/monitor/*
-- ============================================================

-- 1. 系统管理（100-108、118、123）: system/* → system/management/*
UPDATE sys_menu SET component = 'system/management/user/index' WHERE menu_id = 100;
UPDATE sys_menu SET component = 'system/management/role/index' WHERE menu_id = 101;
UPDATE sys_menu SET component = 'system/management/menu/index' WHERE menu_id = 102;
UPDATE sys_menu SET component = 'system/management/dept/index' WHERE menu_id = 103;
UPDATE sys_menu SET component = 'system/management/post/index' WHERE menu_id = 104;
UPDATE sys_menu SET component = 'system/management/dict/index' WHERE menu_id = 105;
UPDATE sys_menu SET component = 'system/management/config/index' WHERE menu_id = 106;
UPDATE sys_menu SET component = 'system/management/notice/index' WHERE menu_id = 107;
UPDATE sys_menu SET component = 'system/management/oss/index' WHERE menu_id = 118;
UPDATE sys_menu SET component = 'system/management/client/index' WHERE menu_id = 123;

-- 2. 系统监控（2、109、113、117、120、500、501）: monitor/* → system/monitor/*
UPDATE sys_menu SET component = 'system/monitor/online/index' WHERE menu_id = 109;
UPDATE sys_menu SET component = 'system/monitor/cache/index' WHERE menu_id = 113;
UPDATE sys_menu SET component = 'system/monitor/admin/index' WHERE menu_id = 117;
UPDATE sys_menu SET component = 'system/monitor/snailjob/index' WHERE menu_id = 120;
UPDATE sys_menu SET component = 'system/monitor/operlog/index' WHERE menu_id = 500;
UPDATE sys_menu SET component = 'system/monitor/logininfor/index' WHERE menu_id = 501;

-- 3. 日志管理（108）: 移动到 system/management 下后 component 路径保持 system/*
--    （108 是目录，不是叶子，不需要更新 component）

-- 4. 租户管理（121、122）: system/tenant* → system/management/tenant*
UPDATE sys_menu SET component = 'system/management/tenant/index' WHERE menu_id = 121;
UPDATE sys_menu SET component = 'system/management/tenantPackage/index' WHERE menu_id = 122;

-- 5. 业务页面（130-133）子菜单: system/* → system/management/*
UPDATE sys_menu SET component = 'system/management/role/authUser' WHERE menu_id = 130;
UPDATE sys_menu SET component = 'system/management/user/authRole' WHERE menu_id = 131;
UPDATE sys_menu SET component = 'system/management/dict/data' WHERE menu_id = 132;
UPDATE sys_menu SET component = 'system/management/oss/config' WHERE menu_id = 133;