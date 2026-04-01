-- OpenTCS Plus
-- v2.3 历史数据 layout 回填脚本（幂等）
-- 目标：将 point/path/location 的历史数据补齐到 layout，统一布局真源

SET NAMES utf8mb4;

-- ============================================================
-- point: layout 为空时，从结构化字段回填
-- ============================================================
UPDATE point
SET layout = JSON_OBJECT(
    'layerId', layer_id,
    'x', x_position,
    'y', y_position,
    'z', z_position,
    'editorProps', JSON_OBJECT(
        'radius', radius,
        'label', label
    )
)
WHERE (layout IS NULL OR JSON_LENGTH(layout) = 0)
  AND del_flag = '0';

-- ============================================================
-- location: layout 为空时，从结构化字段回填
-- ============================================================
UPDATE location
SET layout = JSON_OBJECT(
    'layerId', layer_id,
    'x', position_x,
    'y', position_y,
    'z', position_z
)
WHERE (layout IS NULL OR JSON_LENGTH(layout) = 0)
  AND del_flag = '0';

-- ============================================================
-- path: layout 为空时，从结构化字段回填最小布局
-- 说明：旧数据若无控制点，先写入 connectionType，controlPoints 置空数组
-- ============================================================
UPDATE path
SET layout = JSON_OBJECT(
    'layerId', layer_id,
    'connectionType', 'DIRECT',
    'controlPoints', JSON_ARRAY()
)
WHERE (layout IS NULL OR JSON_LENGTH(layout) = 0)
  AND del_flag = '0';

