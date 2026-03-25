-- =============================================================================
-- 现象：接口 JSON 里 meta.title 等为 "AMRç®¡ç†" 类乱码，而 msg「操作成功」正常。
-- 原因：多为「UTF-8 字节被按 latin1 读成错字，再被 UTF-8 编码写入」的双层错码。
--       表仍是 utf8mb4，但磁盘上的字节不是「正常中文 UTF-8」。
--       以 HEX 为准：正确「管理」应为 E7AEA1E79086；错码常为 C3A7C2AE...（更长）。
--       客户端若仍显示像中文，请以 HEX 与接口返回对照，勿仅凭肉眼。
--       JDBC 的 SET NAMES 不能纠正已写错的字节。
--
-- 执行前务必备份：
--   mysqldump -u root -p --default-character-set=utf8mb4 opentcs sys_menu > sys_menu_backup.sql
--
-- 客户端请使用：mysql --default-character-set=utf8mb4 -u root -p opentcs
-- =============================================================================

-- 1) 表结构：menu_name / remark 应为 utf8mb4
-- SHOW CREATE TABLE sys_menu\G

-- 2) 抽样看 HEX。正常「管理」UTF-8 的 HEX 约为 E7AEA1E79086（6 字节）
-- SELECT menu_id, menu_name, HEX(menu_name) AS hex_name FROM sys_menu ORDER BY menu_id DESC LIMIT 15;

-- 3) 预览修复（不修改数据）：确认 `fixed` 列是否为预期中文，再考虑第 4 步
--    若本来就是正常中文的行，fixed 会变乱，切勿对全表盲目 UPDATE
SELECT menu_id,
       menu_name AS current_value,
       CONVERT(CAST(CONVERT(menu_name USING latin1) AS BINARY) USING utf8mb4) AS fixed_preview
FROM sys_menu
WHERE menu_name IS NOT NULL AND menu_name <> ''
LIMIT 30;

-- 4) 仅在第 3 步预览正确时执行（可先 START TRANSACTION; ... ROLLBACK; 试跑）
--    若库中「部分行已是正常中文」，不要全表 UPDATE，可只对疑似乱码行加 WHERE（示例，需自行调整）：
--    WHERE menu_name REGEXP '[çåäæèéêëìíîïðñòóôõö]' OR HEX(menu_name) LIKE '%C3%A7%';
-- START TRANSACTION;
-- UPDATE sys_menu
-- SET menu_name = CONVERT(CAST(CONVERT(menu_name USING latin1) AS BINARY) USING utf8mb4),
--     remark = CASE
--         WHEN remark IS NULL OR remark = '' THEN remark
--         ELSE CONVERT(CAST(CONVERT(remark USING latin1) AS BINARY) USING utf8mb4)
--     END;
-- COMMIT;

-- 5) 若表/列仍为 latin1，在数据修复后可统一（按需）
-- ALTER TABLE sys_menu CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
