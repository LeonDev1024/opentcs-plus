#!/usr/bin/env python3
"""将业务表重命名为 tcs_ 前缀，并将 USE opentcs 改为 opentcsplus。"""
from __future__ import annotations

import re
import sys
from pathlib import Path

# 先匹配长表名，避免 navigation_map 误伤 navigation_map_history
TCS_TABLES = [
    "navigation_map_history",
    "cross_layer_connection",
    "factory_layer_group",
    "navigation_map",
    "factory_layer",
    "factory_model",
    "location_type",
    "elevator_schedule",
    "transport_order",
    "vehicle_type",
    "visual_layout",
    "vehicle",
    "location",
    "point",
    "path",
    "block",
    "brand",
]


def rename_tables(sql: str) -> str:
    sql = re.sub(r"\bUSE\s+opentcs\b", "USE opentcsplus", sql, flags=re.IGNORECASE)
    sql = re.sub(r"\bINSERT\s+INTO\s+opentcs\.", "INSERT INTO ", sql, flags=re.IGNORECASE)

    for table in TCS_TABLES:
        new = f"tcs_{table}"
        patterns = [
            (rf"`{table}`", f"`{new}`"),
            (rf"\bDROP TABLE IF EXISTS {table}\b", f"DROP TABLE IF EXISTS {new}"),
            (rf"\bCREATE TABLE IF NOT EXISTS {table}\b", f"CREATE TABLE IF NOT EXISTS {new}"),
            (rf"\bCREATE TABLE {table}\b", f"CREATE TABLE {new}"),
            (rf"\bALTER TABLE {table}\b", f"ALTER TABLE {new}"),
            (rf"\bREFERENCES {table}\(", f"REFERENCES {new}("),
            (rf"\bON {table}\(", f"ON {new}("),
            (rf"\bINSERT INTO {table}\b", f"INSERT INTO {new}"),
            (rf"\bFROM {table}\b", f"FROM {new}"),
            (rf"\bJOIN {table}\b", f"JOIN {new}"),
            (rf"\bUPDATE {table}\b", f"UPDATE {new}"),
            (rf"table `{table}`", f"table `{new}`"),
        ]
        for pattern, repl in patterns:
            sql = re.sub(pattern, repl, sql)
    return sql


def process_file(path: Path) -> None:
    original = path.read_text(encoding="utf-8")
    updated = rename_tables(original)
    if updated != original:
        path.write_text(updated, encoding="utf-8")
        print(f"updated: {path}")


def main() -> int:
    root = Path(__file__).resolve().parents[2]
    targets = [
        root / "db/migration/V1.0.0__baseline_schema.sql",
        root / "script/db/patches/V1.0.0_extensions.sql",
        root / "db/seed/demo_factory_data.sql",
        root / "db/repeatable/R__sys_menu.sql",
        root / "script/mysql/opentcs_vehicle_ddl_v2.0.sql",
        root / "script/mysql/opentcs_factory_model_ddl_v2.0.sql",
        root / "script/mysql/opentcs_transport_order_ddl.sql",
        root / "script/mysql/opentcs_factory_model_ddl_v2.1_raster.sql",
        root / "script/mysql/opentcs_factory_model_ddl_v2.4_base_layer.sql",
        root / "script/mysql/opentcs_map_version_ddl.sql",
        root / "script/mysql/opentcs_factory_model_add_path_layout_ddl.sql",
    ]
    for target in targets:
        if target.exists():
            process_file(target)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
