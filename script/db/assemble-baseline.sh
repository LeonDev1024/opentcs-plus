#!/usr/bin/env bash
# ============================================================
# 组装 Flyway V1.0.0 基线脚本
# 从 script/mysql 源文件合并，供首次初始化或 regenerate 使用
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
MYSQL_DIR="$PROJECT_ROOT/script/mysql"
PATCHES_DIR="$SCRIPT_DIR/patches"
OUT_FILE="$PROJECT_ROOT/db/migration/V1.0.0__baseline_schema.sql"

mkdir -p "$(dirname "$OUT_FILE")"

{
  cat <<'HEADER'
-- ============================================================
-- OpenTCS Plus Flyway Baseline Schema V1.0.0
-- 由 script/db/assemble-baseline.sh 自动生成，请勿手工编辑
-- 源: script/mysql + script/db/patches/V1.0.0_extensions.sql
-- ============================================================

SET NAMES utf8mb4;

HEADER

  for file in \
    opentcs_system_ddl_v2.0.sql \
    opentcs_vehicle_ddl_v2.0.sql \
    opentcs_factory_model_ddl_v2.0.sql \
    opentcs_transport_order_ddl.sql
  do
    echo ""
    echo "-- ---- ${file} ----"
    cat "$MYSQL_DIR/$file"
    echo ""
  done

  echo "-- ---- V1.0.0_extensions.sql ----"
  cat "$PATCHES_DIR/V1.0.0_extensions.sql"
} > "$OUT_FILE"

echo "Baseline assembled: $OUT_FILE"
