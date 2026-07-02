# OpenTCS Plus 数据库迁移（Flyway）

本目录为 **Flyway + 全量快照** 治理体系的唯一写入入口。

## 实施状态（Phase 1～4）

| 阶段 | 内容 | 状态 |
|------|------|------|
| Phase 1 | `db/` 骨架、Flyway Maven Plugin、`generate/validate-snapshot.sh`、CI `db-validate` | ✅ |
| Phase 2 | `script/mysql/` 归档说明、迁移链 `V1.0.x`、`baseline-prod.sh` | ✅ |
| Phase 3 | `deploy.yml` 发布前 migrate、Compose `flyway` 服务 | ✅ |
| Phase 4 | Testcontainers `FlywayMigrationIT`、`dev-db-up.sh` | ✅ |

## 命名规范

| 类型 | 规则 | 示例 |
|------|------|------|
| 数据库 | `opentcsplus` | — |
| RuoYi 系统表 | 保持 `sys_*` 不变 | `sys_user`, `sys_menu` |
| 业务表 | 统一 `tcs_` 前缀 | `tcs_vehicle`, `tcs_factory_model` |

## 本地开发库（默认）

| 项 | 值 |
|----|-----|
| 主机 | `127.0.0.1:3306` |
| 用户 | `root` |
| 密码 | **无** |
| 库名 | `opentcsplus` |

## 一键本地环境

```bash
cd opentcs-plus

# Docker MySQL + Flyway 迁移（推荐新同学）
./script/db/dev-db-up.sh

# 删库重建 + 引导数据（admin/admin123）
./script/db/dev-db-up.sh --rebuild
```

## 常用命令

```bash
./script/db/dev-db.sh migrate     # 增量迁移
./script/db/dev-db.sh info        # 查看状态
./script/db/dev-db.sh snapshot    # 生成快照
./script/db/dev-db.sh validate    # 校验快照
./script/db/new-migration.sh 1.0.3 your_change   # 新建迁移

mvn -N flyway:migrate             # 也可直接用 Maven
```

自定义连接：复制 `script/db/local.env.example` 为 `local.env`。

## 生产存量库 baseline

已有表结构但无 `flyway_schema_history` 时（**先备份**）：

```bash
FLYWAY_HOST=... FLYWAY_PASSWORD=... ./script/db/baseline-prod.sh 1.0.2
./script/db/dev-db.sh migrate
```

## 目录结构

```
db/
├── migration/          # V{version}__{desc}.sql
├── repeatable/         # R__{desc}.sql（菜单等）
├── seed/               # 演示数据，不进入 Flyway
├── rollback/           # 手工回滚脚本（不自动执行）
└── snapshot/           # 自动生成的全量 schema 快照
```

## PR Checklist（含 DB 变更时）

- [ ] 新增 `db/migration/Vx.y.z__*.sql`，**禁止修改**已发布版本
- [ ] `./script/db/dev-db.sh migrate` 成功
- [ ] `./script/db/dev-db.sh snapshot` 并提交 `db/snapshot/`
- [ ] CI `db-validate` 通过
- [ ] 菜单/字典优先放 `repeatable/`

## 相关文档

- 治理方案：`../../doc/OpenTCS-Plus-数据库持续集成治理方案.md`
- ADR：`../doc/adr/0001-database-flyway-governance.md`
- 历史 SQL 归档：`../script/mysql/README.md`
