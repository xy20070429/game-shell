package com.example.gameshell.controller;

import com.example.gameshell.core.GameClock;
import com.example.gameshell.service.PlayerService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

/**
 * 主窗口控制器。
 *
 * 由 Spring 容器创建（见 SceneManager 的 controllerFactory），
 * 因此可通过构造器注入 GameClock 与 PlayerService。
 */
@Component
public class MainController {

    /** 时钟展示标签，文本与 GameClock 的属性双向绑定 */
    @FXML
    private Label clockLabel;

    /** 数据库自检结果展示标签 */
    @FXML
    private Label dbStatusLabel;

    /** 暂停 / 继续时钟按钮 */
    @FXML
    private Button togglePauseBtn;

    private final GameClock gameClock;
    private final PlayerService playerService;

    /** 暂停状态标记 */
    private boolean paused = false;

    public MainController(GameClock gameClock, PlayerService playerService) {
        this.gameClock = gameClock;
        this.playerService = playerService;
    }

    /** FXML 加载完成后回调：绑定时钟文本属性 */
    @FXML
    public void initialize() {
        clockLabel.textProperty().bind(gameClock.displayTimeProperty());
        dbStatusLabel.setText("尚未测试数据库，点击下方按钮进行读写自检。");
    }

    /** 「测试数据库连接」按钮：调用服务层完成一次读写闭环 */
    @FXML
    private void onTestDb() {
        try {
            dbStatusLabel.setText(playerService.selfCheck());
        } catch (Exception ex) {
            dbStatusLabel.setText("数据库连接失败：" + ex.getMessage());
        }
    }

    /** 「暂停 / 继续」按钮：切换时钟推进状态 */
    @FXML
    private void onTogglePause() {
        if (paused) {
            gameClock.resume();
            togglePauseBtn.setText("暂停时间");
        } else {
            gameClock.pause();
            togglePauseBtn.setText("继续时间");
        }
        paused = !paused;
    }
}
