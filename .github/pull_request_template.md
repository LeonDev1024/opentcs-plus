## Database changes

若本 PR 修改 `db/**` 或 `script/db/**`：

- [ ] 已新增 `db/migration/V*__*.sql`（未改写已合并的旧版本）
- [ ] 本地 `./script/db/dev-db.sh migrate` 通过
- [ ] 已运行 `./script/db/dev-db.sh snapshot` 并提交 `db/snapshot/`
- [ ] 已在描述中说明升级影响（锁表、停服、手工步骤）
