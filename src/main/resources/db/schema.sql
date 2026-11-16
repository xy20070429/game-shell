-- =============================================================
-- game_shell_db 建表脚本（第一阶段）
-- 数据库需预先创建：CREATE DATABASE IF NOT EXISTS game_shell_db
-- 字符集：utf8mb4，兼容中文与表情符号
-- =============================================================

-- 玩家表：后续阶段扩展属性（背包、属性、存档等）
CREATE TABLE IF NOT EXISTS player (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '玩家 ID',
    name        VARCHAR(50)  NOT NULL                COMMENT '玩家昵称',
    level       INT          NOT NULL DEFAULT 1      COMMENT '等级',
    gold        INT          NOT NULL DEFAULT 0      COMMENT '持有金币',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '玩家表';
