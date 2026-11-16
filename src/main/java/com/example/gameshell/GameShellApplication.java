package com.example.gameshell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 引导配置类。
 *
 * 说明：本项目是 JavaFX 桌面应用，真正的 UI 入口是 {@link com.example.gameshell.MainApp}；
 * 本类作为 Spring 上下文的配置源，由 MainApp 在启动时加载，负责扫描 Bean、装配数据源与 JdbcTemplate。
 */
@SpringBootApplication
public class GameShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameShellApplication.class, args);
    }
}
