package com.example.gameshell;

import com.example.gameshell.core.GameClock;
import com.example.gameshell.core.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * JavaFX 桌面应用入口。
 *
 * 启动流程：
 * 1. JVM 调用 {@link #main(String[])} -> {@link Application#launch} 启动 JavaFX 生命周期；
 * 2. {@link #start(Stage)} 中先构建 Spring 容器（装配数据源、JdbcTemplate、Service 等 Bean）；
 * 3. 从容器取出 SceneManager 与 GameClock，加载主场景并启动时钟；
 * 4. 窗口关闭时在 {@link #stop()} 中停止时钟、关闭 Spring 上下文、退出 JavaFX。
 */
public class MainApp extends Application {

    /** Spring 容器上下文，贯穿整个桌面应用生命周期 */
    private ConfigurableApplicationContext context;

    private SceneManager sceneManager;

    private GameClock gameClock;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. 启动 Spring 上下文（同时执行建表与初始化数据脚本）
        String[] args = getParameters().getRaw().toArray(new String[0]);
        context = SpringApplication.run(GameShellApplication.class, args);

        // 2. 从容器获取核心组件
        sceneManager = context.getBean(SceneManager.class);
        gameClock = context.getBean(GameClock.class);

        // 3. 初始化舞台并加载主窗口场景
        sceneManager.init(primaryStage);
        sceneManager.switchScene("/fxml/main-view.fxml", "游戏空壳 · 第一阶段");

        // 4. 启动游戏时钟
        gameClock.start();
    }

    @Override
    public void stop() throws Exception {
        // 先停止时钟，再关闭 Spring 容器，最后退出 JavaFX
        if (gameClock != null) {
            gameClock.stop();
        }
        if (context != null) {
            context.close();
        }
        Platform.exit();
        super.stop();
    }
}
