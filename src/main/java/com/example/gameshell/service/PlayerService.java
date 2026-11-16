package com.example.gameshell.service;

import com.example.gameshell.entity.Player;
import com.example.gameshell.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 玩家业务层。
 * 第一阶段负责演示「数据库可读、可写」的完整闭环：
 * 读取玩家列表 -> 必要时写入初始数据 -> 再次读取并汇总结果。
 */
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /** 查询全部玩家 */
    public List<Player> listAll() {
        return playerRepository.findAll();
    }

    /** 新增玩家 */
    public Player addPlayer(String name) {
        return playerRepository.insert(new Player(name, 1, 0));
    }

    /**
     * 数据库读写自检：
     * 1. 读取当前玩家数量；
     * 2. 若表为空，写入一条默认玩家（验证写入）；
     * 3. 再次读取并拼接结果摘要（验证读取）。
     *
     * @return 可直接展示在界面上的结果文本
     */
    public String selfCheck() {
        int before = playerRepository.count();
        if (before == 0) {
            playerRepository.insert(new Player("自检角色", 1, 50));
        }
        List<Player> players = playerRepository.findAll();
        Player first = players.isEmpty() ? null : players.get(0);

        StringBuilder sb = new StringBuilder();
        sb.append("数据库连接成功 ✔").append('\n');
        sb.append("自检前玩家数：").append(before).append('\n');
        sb.append("当前玩家数：").append(players.size()).append('\n');
        if (first != null) {
            sb.append("首个玩家：").append(first.getName())
              .append("（等级 ").append(first.getLevel())
              .append("，金币 ").append(first.getGold()).append("）");
        }
        return sb.toString();
    }
}
