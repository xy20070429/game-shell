package com.example.gameshell.repository;

import com.example.gameshell.entity.Player;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * 玩家数据访问层。
 * 基于 JdbcTemplate 完成最基础的增、查，验证数据库读写链路打通。
 */
@Repository
public class PlayerRepository {

    private final JdbcTemplate jdbcTemplate;

    /** 构造器注入：Spring 自动装配容器中的 JdbcTemplate Bean */
    public PlayerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 行映射：把一行 ResultSet 封装为 Player 对象 */
    private static final RowMapper<Player> PLAYER_ROW_MAPPER = (rs, rowNum) -> {
        Player player = new Player();
        player.setId(rs.getLong("id"));
        player.setName(rs.getString("name"));
        player.setLevel(rs.getInt("level"));
        player.setGold(rs.getInt("gold"));
        player.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return player;
    };

    /** 查询玩家总数 */
    public int count() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM player", Integer.class);
        return count == null ? 0 : count;
    }

    /** 查询全部玩家，按 id 升序 */
    public List<Player> findAll() {
        return jdbcTemplate.query("SELECT id, name, level, gold, created_at FROM player ORDER BY id ASC",
                PLAYER_ROW_MAPPER);
    }

    /** 按 ID 查询玩家，不存在时返回空 Optional */
    public Optional<Player> findById(Long id) {
        List<Player> list = jdbcTemplate.query(
                "SELECT id, name, level, gold, created_at FROM player WHERE id = ?",
                PLAYER_ROW_MAPPER, id);
        return list.stream().findFirst();
    }

    /**
     * 新增玩家，并把数据库生成的自增主键回填到实体对象。
     *
     * @param player 待保存的玩家（id 可为空）
     * @return 回填主键后的玩家
     */
    public Player insert(Player player) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO player (name, level, gold) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, player.getName());
            ps.setInt(2, player.getLevel());
            ps.setInt(3, player.getGold());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            player.setId(key.longValue());
        }
        return player;
    }
}
