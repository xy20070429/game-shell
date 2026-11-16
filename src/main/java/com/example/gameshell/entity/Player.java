package com.example.gameshell.entity;

import java.time.LocalDateTime;

/**
 * 玩家实体，对应数据库 player 表。
 * 第一阶段仅承载基础字段，后续阶段可在此扩展背包、属性、存档状态等。
 */
public class Player {

    /** 玩家 ID，数据库自增主键 */
    private Long id;

    /** 玩家昵称 */
    private String name;

    /** 等级 */
    private Integer level;

    /** 持有金币 */
    private Integer gold;

    /** 创建时间，由数据库默认值填充 */
    private LocalDateTime createdAt;

    public Player() {
    }

    /** 新增玩家时使用：不传入 id 与 createdAt，由数据库生成 */
    public Player(String name, Integer level, Integer gold) {
        this.name = name;
        this.level = level;
        this.gold = gold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Player{id=" + id + ", name='" + name + "', level=" + level + ", gold=" + gold + '}';
    }
}
