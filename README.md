# game-shell · 游戏空壳框架（第一阶段）

基于 **Spring Boot 2.7 + JavaFX 8 + MySQL** 的桌面游戏项目骨架。本阶段目标：搭建可运行的空壳游戏窗口，并打通数据库读写链路。

## 一、第一阶段交付对照

| 任务要求 | 落地实现 |
| --- | --- |
| 创建 Maven 项目，配置 Spring Boot / MySQL / JavaFX 依赖 | `pom.xml`：spring-boot-starter-jdbc、mysql-connector-java；JavaFX 8 随 JDK 8 内置 |
| 建立 `application.yml` 数据库连接 | `src/main/resources/application.yml` 数据源配置 |
| 设计并执行建表 SQL，准备初始化数据 | `db/schema.sql`（player 表）、`db/data.sql`（幂等初始化数据） |
| 搭建 JavaFX 主窗口、场景切换框架 | `MainApp`、`core/SceneManager`、`fxml/main-view.fxml` |
| 打通 Spring ↔ JavaFX Controller 集成 | `SceneManager` 通过 `controllerFactory` 交由 Spring 创建 Controller |
| 实现 `GameClock` 基础时间推进 | `core/GameClock`：Timeline 心跳推进游戏内天/时/分，支持暂停、继续、停止 |
| **交付物**：可运行的空壳游戏窗口 + 数据库可读写 | 主窗口显示实时时钟；「测试数据库连接」按钮完成一次读→写→读闭环 |

## 二、技术栈与版本

| 组件 | 版本 / 说明 |
| --- | --- |
| JDK | 1.8（Spring Boot 2.7 是最后一个支持 Java 8 的大版本） |
| Spring Boot | 2.7.18 |
| JavaFX | 8（JDK 8 内置，无需单独下载 SDK） |
| 数据访问 | Spring JDBC（JdbcTemplate + HikariCP） |
| 数据库 | MySQL 5.7 / 8.x，驱动 mysql-connector-java 8.x |
| 构建工具 | Maven 3.6+（IDEA 内置 Maven 亦可） |

## 三、整体架构

```
┌────────────────────────── JavaFX 桌面层 ──────────────────────────┐
│ MainApp(Application) ── SceneManager ── MainController ── 主窗口 FXML │
│      │                     │                  ▲                     │
│      │                     └ controllerFactory ┘ Spring 创建 Controller │
└──────┼──────────────────────────────────────┼───────────────────────┘
       │ 构建 / 关闭 Spring 上下文               │ 依赖注入
┌──────▼──────────────── Spring 容器层 ────────▼─────────────────────┐
│ GameShellApplication   GameClock   PlayerService   PlayerRepository │
└──────┼──────────────────────────────────────────────────────────────┘
       │ JdbcTemplate / HikariCP
┌──────▼────────────────────── MySQL 8.x ─────────────────────────────┐
│ player 表（schema.sql 建表，data.sql 初始化数据）                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 启动流程

1. JVM 调用 `MainApp.main()` → JavaFX `launch()` 进入 UI 生命周期；
2. `MainApp.start()` 中通过 `SpringApplication.run()` 构建 Spring 上下文（自动执行建表与初始化脚本）；
3. 从容器取出 `SceneManager`、`GameClock`，加载主场景并启动时钟；
4. 窗口关闭时 `stop()` 停止时钟、关闭上下文、退出 JavaFX。

## 四、目录结构

```
game-shell/
├── pom.xml                          # Maven 构建与依赖配置
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/example/gameshell/
    │   │   ├── MainApp.java                 # JavaFX 入口（UI 启动类）
    │   │   ├── GameShellApplication.java    # Spring Boot 配置源 / 上下文引导
    │   │   ├── config/                      # 预留：Spring 配置类
    │   │   ├── core/
    │   │   │   ├── SceneManager.java        # 场景切换 + Spring/JavaFX 集成
    │   │   │   └── GameClock.java           # 游戏内时钟（时间推进）
    │   │   ├── controller/
    │   │   │   └── MainController.java      # 主窗口控制器
    │   │   ├── entity/
    │   │   │   └── Player.java              # 玩家实体
    │   │   ├── repository/
    │   │   │   └── PlayerRepository.java    # JdbcTemplate 数据访问
    │   │   └── service/
    │   │       └── PlayerService.java        # 业务逻辑 / 数据库读写自检
    │   └── resources/
    │       ├── application.yml               # 数据源与应用配置
    │       ├── db/schema.sql                 # 建表脚本
    │       ├── db/data.sql                   # 初始化数据（幂等）
    │       ├── fxml/main-view.fxml           # 主窗口界面
    │       └── css/style.css                 # 界面样式
    └── test/java/com/example/gameshell/core/
        └── GameClockTest.java               # 时钟逻辑单元测试（不依赖数据库）
```

## 五、环境准备与运行

### 1. 准备 MySQL

启动本机 MySQL 后，先创建数据库（账号密码在 `application.yml` 中按实际修改）：

```sql
CREATE DATABASE IF NOT EXISTS game_shell_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;
```

> 首次启动应用时，`schema.sql` 与 `data.sql` 会自动执行；脚本为幂等设计，重复运行不会重复插入。

### 2. 修改数据库连接

打开 `src/main/resources/application.yml`，按本机实际情况填写 `username`、`password`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/game_shell_db?...
    username: root
    password: root
```

### 3. 导入与运行（IDEA）

1. IDEA → `Open` → 选择本项目根目录 `game-shell/`，识别为 Maven 项目；
2. 等待依赖下载完成；
3. 找到 `MainApp.java`，右键 `Run 'MainApp'` 启动；
4. 主窗口弹出后：时钟自动走时；点击「测试数据库连接」，状态区显示读写自检结果。

### 4. 命令行构建（需已安装 Maven 3.6+）

```bash
# 编译 + 运行测试
mvn clean test

# 打包
mvn clean package

# 运行
java -jar target/game-shell-0.1.0.jar
```

## 六、提交到 GitHub

在项目根目录执行（首次使用 Git 需先配置提交身份，只需配置一次）：

```bash
# 配置提交身份（换成你自己的昵称与邮箱）
git config --global user.name "你的昵称"
git config --global user.email "你的邮箱@example.com"

# 初始化仓库并提交
git init
git add .
git commit -m "feat: 第一阶段项目搭建与基础框架（Spring Boot + JavaFX + MySQL）"
git branch -M main
git remote add origin <你的仓库地址>
git push -u origin main
```

`.gitignore` 已排除 `target/`、IDE 工程文件、日志与含密码的本地配置文件。

## 七、关键设计说明

- **Spring 与 JavaFX 如何打通**：`FXMLLoader` 默认用无参构造创建 Controller，无法注入 Bean；`SceneManager` 调用 `setControllerFactory(applicationContext::getBean)`，把 Controller 交给 Spring 容器创建，从而支持构造器注入 `GameClock`、`PlayerService`。
- **时钟不阻塞 UI**：`GameClock` 基于 JavaFX `Timeline` 按秒回调，时间推进在 UI 线程的动画节拍内完成；后续阶段若接入耗时数据库操作，应改为后台线程 + 界面回调。
- **读写自检闭环**：`PlayerService.selfCheck()` 先读玩家数量，表为空时写入一条默认数据，再次读取并汇总结果，用于验证数据库「可读、可写」。

## 八、后续阶段预留

- `config/` 包预留 Spring 配置类（事务、拦截器、Bean 定制等）；
- `entity/` 可扩展背包、属性、存档状态；
- `SceneManager` 已支持多 FXML 场景切换，后续可新增游戏主场景、菜单场景等；
- `GameClock` 已暴露天/时/分 getter，可供事件触发、存档与昼夜逻辑使用。
