# script/mysql — 历史归档（只读）

> **自 2026-07 起已废弃直接修改。** 新变更请写入 `db/migration/` 或 `db/repeatable/`。

本目录保留 OpenTCS Plus 在引入 Flyway 之前的增量 SQL，仅供追溯与对照。内容已整理进：

| 历史脚本 | Flyway 去向 |
|----------|-------------|
| `opentcs_system_ddl_v2.0.sql` 等全量 DDL | `db/migration/V1.0.0__baseline_schema.sql` |
| `opentcs_menu_v3.*.sql` | `db/repeatable/R__sys_menu.sql`、`V1.0.2__*.sql` |
| 各模块 ALTER / backfill | 对应 `V1.0.x` 迁移（按需补充） |

## 禁止事项

- 禁止在本目录新增或修改用于生产的 SQL
- 禁止在 Docker `init.d` 中继续合并本目录生成 `02_schema.sql`

## 新工作流

```bash
./script/db/new-migration.sh 1.0.3 your_change
./script/db/dev-db.sh migrate
./script/db/dev-db.sh snapshot
```

详见 `db/README.md` 与 `doc/adr/0001-database-flyway-governance.md`。
