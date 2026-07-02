#!/usr/bin/env python3
"""更新 MyBatis Mapper XML 中的业务表名为 tcs_ 前缀。"""
from __future__ import annotations

import re
from pathlib import Path

REPLACEMENTS = [
    ("navigation_map_history", "tcs_navigation_map_history"),
    ("cross_layer_connection", "tcs_cross_layer_connection"),
    ("factory_layer_group", "tcs_factory_layer_group"),
    ("navigation_map", "tcs_navigation_map"),
    ("factory_layer", "tcs_factory_layer"),
    ("factory_model", "tcs_factory_model"),
    ("location_type", "tcs_location_type"),
    ("elevator_schedule", "tcs_elevator_schedule"),
    ("transport_order", "tcs_transport_order"),
    ("vehicle_type", "tcs_vehicle_type"),
    ("visual_layout", "tcs_visual_layout"),
    ("vehicle", "tcs_vehicle"),
    ("location", "tcs_location"),
    ("point", "tcs_point"),
    ("path", "tcs_path"),
    ("block", "tcs_block"),
    ("brand", "tcs_brand"),
]

def transform(sql: str) -> str:
    for old, new in REPLACEMENTS:
        for prefix in ("FROM", "JOIN", "INTO", "UPDATE"):
            sql = re.sub(rf"\b{prefix}\s+{old}\b", f"{prefix} {new}", sql, flags=re.IGNORECASE)
    return sql


def main() -> None:
    root = Path(__file__).resolve().parents[2]
    for path in root.rglob("*Mapper.xml"):
        if "target" in path.parts or "opentcs-infrastructure" not in str(path):
            continue
        original = path.read_text(encoding="utf-8")
        updated = transform(original)
        if updated != original:
            path.write_text(updated, encoding="utf-8")
            print(f"updated: {path}")


if __name__ == "__main__":
    main()
