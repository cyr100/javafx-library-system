# 图书管理系统

一个基于JavaFX和MySQL的图书管理系统，支持用户管理、图书管理和借阅记录管理等功能。

## 功能特点

- 用户管理：支持管理员和普通用户两种角色，管理员可添加、编辑和删除用户
- 图书管理：实现图书的添加、编辑、删除和查询功能
- 借阅管理：记录图书借阅和归还信息，支持分页查询
- 用户头像：支持用户上传和显示头像，包含默认头像功能
- 权限控制：根据用户角色动态显示/隐藏功能按钮

## 环境要求

- Java JDK 8 或更高版本
- JavaFX SDK
- MySQL 5.7 或更高版本
- IntelliJ IDEA (推荐) 或其他Java IDE

## 安装步骤

1. 克隆或下载项目到本地

2. 导入MySQL数据库
   ```
   mysql -u username -p < sql/library_db.sql
   ```

3. 配置数据库连接
   修改 `dao/DBConnection.java` 文件中的数据库连接信息：
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC"; 
   private static final String USER = "your_username"; 
   private static final String PASSWORD = "your_password"; 
   ```

4. 在IDE中配置JavaFX SDK

5. 添加MySQL驱动
   项目已包含 `lib/mysql-connector-j-8.0.33.jar`，需在IDE中添加为库依赖

## 使用说明

1. 运行 `Main.java` 启动应用程序

2. 登录系统
   - 管理员账户: admin/admin
   - 普通用户可通过注册功能创建

3. 功能操作
   - 管理员：可进行用户管理、图书管理和借阅记录管理
   - 普通用户：可查询图书、借阅和归还图书

## 项目结构

```
javafx-library-system/
├── controller/       # 控制器类
├── dao/              # 数据访问对象
├── model/            # 模型类
├── views/            # FXML视图文件
├── lib/              # 依赖库
├── sql/              # 数据库脚本
├── avatars/          # 用户头像存储
└── bkimages/         # 图书封面存储
```

## 常见问题

- **Q: 无法连接数据库？**
  A: 检查数据库服务是否启动，连接信息是否正确

- **Q: 头像上传失败？**
  A: 确保应用程序有文件系统写入权限，检查avatars目录是否存在

- **Q: JavaFX相关类找不到？**
  A: 确保已正确配置JavaFX SDK路径



