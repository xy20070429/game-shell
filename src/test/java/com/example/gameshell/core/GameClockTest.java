package com.example.gameshell.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * GameClock 时间推进逻辑的单元测试。
 * 与被测类同包，可访问包私有方法 tick()；不加载 Spring 上下文、不连接数据库。
 */
class GameClockTest {

    @Test
    void initialTimeShouldBeDay1_08_00() {
        GameClock clock = new GameClock(1);
        assertEquals(1, clock.getDay());
        assertEquals(8, clock.getHour());
        assertEquals(0, clock.getMinute());
        assertEquals("第 1 天  08:00", clock.getDisplayTime());
    }

    @Test
    void tickShouldAdvanceOneMinute() {
        GameClock clock = new GameClock(1);
        clock.tick();
        assertEquals(1, clock.getMinute());
        assertEquals("第 1 天  08:01", clock.getDisplayTime());
    }

    @Test
    void tickShouldCarryOverToNextHour() {
        // 每跳一次推进 60 分钟，应进位到下一小时，分钟归零
        GameClock clock = new GameClock(60);
        clock.tick();
        assertEquals(0, clock.getMinute());
        assertEquals(9, clock.getHour());
    }
}
