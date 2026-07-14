-- 地图模型极简化：仅保留点(Point)和路径(Path)。
-- 删除 Block/Location 相关菜单与表，避免运行时继续查询 tcs_block/tcs_location。

DELETE FROM sys_role_menu
WHERE menu_id IN (
    SELECT menu_id
    FROM sys_menu
    WHERE perms IN (
        'factory:block:list',
        'factory:block:add',
        'factory:block:edit',
        'factory:block:remove',
        'opentcs:locationType:list',
        'opentcs:locationType:add',
        'opentcs:locationType:edit',
        'opentcs:locationType:remove',
        'opentcs:locationType:query'
    )
    OR path IN ('areas', 'location-type', 'location')
    OR component IN (
        'map/scene/areas/index',
        'map/scene/location-type/index',
        'deploy/factory/location-type/index'
    )
);

DELETE FROM sys_menu
WHERE perms IN (
    'factory:block:list',
    'factory:block:add',
    'factory:block:edit',
    'factory:block:remove',
    'opentcs:locationType:list',
    'opentcs:locationType:add',
    'opentcs:locationType:edit',
    'opentcs:locationType:remove',
    'opentcs:locationType:query'
)
OR path IN ('areas', 'location-type', 'location')
OR component IN (
    'map/scene/areas/index',
    'map/scene/location-type/index',
    'deploy/factory/location-type/index'
);

DROP TABLE IF EXISTS tcs_block;
DROP TABLE IF EXISTS tcs_location;
DROP TABLE IF EXISTS tcs_location_type;
