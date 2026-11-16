-- =============================================================
-- 初始化数据（幂等：重复执行不会重复插入）
-- 依据昵称判断是否已存在，存在则跳过
-- =============================================================

INSERT INTO player (name, level, gold)
SELECT '初始角色', 1, 100
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM player WHERE name = '初始角色');

INSERT INTO player (name, level, gold)
SELECT '冒险者', 2, 260
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM player WHERE name = '冒险者');
