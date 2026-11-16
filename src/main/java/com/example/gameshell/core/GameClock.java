package com.example.gameshell.core;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 游戏内时钟（第一阶段：基础时间推进）。
 *
 * 设计要点：
 * 1. 基于 JavaFX {@link Timeline} 按现实秒数触发心跳，避免阻塞 UI 线程；
 * 2. 维护游戏内「天 / 时 / 分」，每跳一次按配置推进若干游戏分钟；
 * 3. 对外暴露 {@link #displayTimeProperty()}，UI 标签可直接绑定，时钟变化自动刷新界面；
 * 4. 支持暂停、继续、停止。
 */
@Component
public class GameClock {

    /** 界面绑定用的展示文本，格式：第 1 天  08:00 */
    private final StringProperty displayTime = new SimpleStringProperty();

    /** 游戏内第几天，从第 1 天开始 */
    private int day = 1;

    /** 游戏内小时（0-23），默认早晨 8 点开局 */
    private int hour = 8;

    /** 游戏内分钟（0-59） */
    private int minute = 0;

    /** 现实 1 秒对应推进的游戏分钟数，由 application.yml 配置 */
    private final int minutesPerTick;

    /** JavaFX 动画时钟，负责按节拍回调 tick() */
    private Timeline timeline;

    /** 暂停标记：暂停时心跳仍在触发，但不推进时间 */
    private boolean paused = false;

    public GameClock(@Value("${game.clock.minutes-per-real-second:1}") int minutesPerTick) {
        this.minutesPerTick = minutesPerTick;
        refreshDisplay();
    }

    /** 启动时钟：每秒触发一次心跳，无限循环 */
    public void start() {
        if (timeline == null) {
            timeline = new Timeline(
                    new KeyFrame(javafx.util.Duration.seconds(1), event -> tick()));
            timeline.setCycleCount(Animation.INDEFINITE);
        }
        timeline.play();
    }

    /** 暂停推进（心跳不停，仅冻结时间） */
    public void pause() {
        paused = true;
    }

    /** 继续推进 */
    public void resume() {
        paused = false;
    }

    /** 彻底停止时钟，退出应用时调用 */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /** 单次心跳：推进游戏时间并刷新展示文本（包私有，便于同包单元测试） */
    void tick() {
        if (paused) {
            return;
        }
        minute += minutesPerTick;

        // 分钟溢出进位到小时
        while (minute >= 60) {
            minute -= 60;
            hour++;
        }
        // 小时溢出进位到天
        while (hour >= 24) {
            hour -= 24;
            day++;
        }
        refreshDisplay();
    }

    /** 根据当前天/时/分刷新绑定属性 */
    private void refreshDisplay() {
        String text = String.format("第 %d 天  %02d:%02d", day, hour, minute);
        displayTime.set(text);
    }

    /** 供 UI 绑定的展示文本属性 */
    public StringProperty displayTimeProperty() {
        return displayTime;
    }

    public String getDisplayTime() {
        return displayTime.get();
    }

    // 以下 getter 供后续阶段做存档、事件判断使用
    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isPaused() {
        return paused;
    }
}
