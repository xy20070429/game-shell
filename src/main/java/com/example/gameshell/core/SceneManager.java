package com.example.gameshell.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 场景切换管理器。
 *
 * 职责：持有主舞台（Stage），根据 FXML 路径加载界面并切换场景。
 * 关键集成点：通过 {@link FXMLLoader#setControllerFactory} 把 Controller 的创建权
 * 交给 Spring 容器，这样 FXML Controller 才能用 @Autowired / 构造器注入 Service、Clock 等 Bean。
 */
@Component
public class SceneManager {

    private final ApplicationContext applicationContext;

    /** 主舞台，由 MainApp 启动时传入 */
    private Stage primaryStage;

    public SceneManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /** 绑定主舞台 */
    public void init(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * 切换到指定 FXML 场景。
     *
     * @param fxmlPath  classpath 下的 FXML 路径，如 /fxml/main-view.fxml
     * @param title     窗口标题
     */
    public void switchScene(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        // Controller 由 Spring 创建：保证依赖注入生效
        loader.setControllerFactory(applicationContext::getBean);

        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
