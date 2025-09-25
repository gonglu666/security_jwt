# JWT 安全认证系统

## 项目简介

这是一个基于 Spring Boot + Spring Security + JWT 的企业级用户认证和授权系统。系统提供完整的用户登录认证、JWT令牌管理和基于角色的访问控制(RBAC)功能。

## 技术栈

- **框架**: Spring Boot 2.1.3, Spring Security 5.1.4
- **认证**: JWT (JSON Web Token)
- **数据库**: MySQL 5.7+
- **连接池**: HikariCP
- **代码简化**: Lombok
- **数据验证**: JSR-303 Bean Validation
- **构建工具**: Maven 3.6+
- **JDK版本**: Java 8+

## 项目特性

### 🔐 安全认证
- JWT 访问令牌和刷新令牌机制
- 密码加密存储 (BCrypt)
- 令牌自动过期和刷新
- 防止令牌伪造和篡改

### 🛡️ 权限控制
- 基于角色的访问控制 (RBAC)
- 细粒度的URL权限配置
- 动态权限验证
- 白名单机制

### 🏗️ 架构设计
- 分层架构：Controller -> Service -> DAO
- 统一响应格式封装
- 全局异常处理
- 详细的日志记录

### 💼 企业级特性
- 完整的中文注释文档
- 参数验证和异常处理
- 配置文件管理
- 监控和健康检查

## 快速开始

### 环境要求

- JDK 8 或更高版本
- Maven 3.6+
- MySQL 5.7+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 创建必要的表结构：
```sql
-- 用户表
CREATE TABLE t_user (
    id VARCHAR(32) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    fullname VARCHAR(100),
    mobile VARCHAR(20)
);

-- 权限表
CREATE TABLE t_permission (
    id VARCHAR(32) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200),
    url VARCHAR(200)
);

-- 角色表
CREATE TABLE t_role (
    id VARCHAR(32) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200)
);

-- 用户角色关联表
CREATE TABLE t_user_role (
    user_id VARCHAR(32),
    role_id VARCHAR(32),
    PRIMARY KEY (user_id, role_id)
);

-- 角色权限关联表
CREATE TABLE t_role_permission (
    role_id VARCHAR(32),
    permission_id VARCHAR(32),
    PRIMARY KEY (role_id, permission_id)
);
```

### 配置文件

修改 `src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/user_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT配置
jwt.secret=your_jwt_secret_key
jwt.userAccessToken.expireTime=3600
jwt.userRefreshToken.expireTime=7200
```

### 启动应用

```bash
# 克隆项目
git clone https://github.com/gonglu666/security_jwt.git

# 进入项目目录
cd security_jwt

# 编译并运行
mvn spring-boot:run
```

应用启动后访问：http://localhost:8080

## API 接口

### 用户认证

#### 登录接口
```http
POST /users/login
Content-Type: application/json

{
    "username": "admin",
    "password": "123456"
}
```

响应：
```json
{
    "code": 0,
    "msg": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "refresh_token": "eyJhbGciOiJIUzI1NiJ9...",
        "expires_in": 3600,
        "token_type": "Bearer"
    }
}
```

#### 令牌使用
在后续的API请求中，需要在请求头中携带访问令牌：

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 权限控制

系统中的受保护资源需要相应的权限才能访问。权限验证基于：
1. JWT令牌的有效性
2. 用户的角色和权限配置
3. 请求的URL和HTTP方法

## 项目结构

```
src/main/java/cn/gt/kaka/
├── config/                 # 配置类
│   └── WebSecurityConfig.java
├── dao/                    # 数据访问层
│   └── UserDao.java
├── dto/                    # 数据传输对象
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── TokenType.java
├── exception/              # 异常定义
│   ├── AuthMethodNotSupportedException.java
│   ├── JwtExpiredTokenException.java
│   └── JwtTokenMalformedException.java
├── filter/                 # 安全过滤器
│   ├── PreAuthenticationFilter.java
│   ├── PostAuthenticationFilter.java
│   └── ...
├── model/                  # 实体模型
│   ├── UserDto.java
│   └── PermissionDto.java
├── provider/               # 认证提供者
│   ├── PreAuthenticationProvider.java
│   └── PostAuthenticationProvider.java
├── rbac/                   # 权限控制
│   └── CloudConsoleSecurityMetadataSource.java
├── security/               # 安全配置
│   └── config/
│       └── WhiteList.java
├── service/                # 业务服务层
│   ├── IUserService.java
│   └── SpringDataUserDetailsService.java
├── util/                   # 工具类
│   ├── CommonResponse.java
│   ├── Constants.java
│   ├── JacksonUtil.java
│   └── JwtUtil.java
└── SecuritySpringBootApp.java  # 启动类
```

## 核心组件说明

### JWT工具类 (JwtUtil)
- 生成访问令牌和刷新令牌
- 解析和验证JWT令牌
- 提取令牌中的用户信息

### 用户服务 (SpringDataUserDetailsService)
- 用户认证和授权
- 密码验证
- 权限加载

### 通用响应 (CommonResponse)
- 统一的API响应格式
- 支持链式调用
- 成功/失败状态封装

### 安全配置 (WebSecurityConfig)
- Spring Security配置
- 认证过滤器链配置
- 权限控制配置

## 安全最佳实践

1. **JWT密钥安全**：生产环境请使用强密钥并定期更换
2. **HTTPS传输**：生产环境建议使用HTTPS加密传输
3. **令牌过期策略**：合理设置令牌过期时间
4. **密码强度**：实施密码复杂度要求
5. **日志监控**：监控异常登录和权限访问

## 开发指南

### 添加新的权限控制

1. 在数据库中添加权限记录
2. 在 `CloudConsoleSecurityMetadataSource` 中配置URL权限要求
3. 为用户分配相应的角色和权限

### 扩展用户信息

1. 修改 `UserDto` 类添加新字段
2. 更新数据库表结构
3. 修改 `UserDao` 中的查询语句

### 自定义异常处理

1. 创建新的异常类继承相应的基类
2. 在全局异常处理器中添加处理逻辑
3. 返回统一格式的错误响应

## 常见问题

### Q: 令牌过期后如何处理？
A: 客户端应该捕获401错误，然后使用刷新令牌调用刷新接口获取新的访问令牌。

### Q: 如何添加新的白名单URL？
A: 在 `WhiteList` 类中添加新的常量，并在 `WebSecurityConfig` 中配置。

### Q: 如何自定义JWT Claims？
A: 在 `JwtUtil.generateToken()` 方法中添加自定义的claims信息。

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证。详情请见 [LICENSE](LICENSE) 文件。

## 联系信息

- 作者：龚璐 (gonglu)
- 邮箱：your-email@example.com
- 项目链接：https://github.com/gonglu666/security_jwt

## 更新日志

### v2.0.0 (2024-XX-XX)
- 完整的代码重构，添加详细中文注释
- 引入 Lombok 简化代码
- 优化异常处理和日志记录
- 增强配置文件和启动信息
- 提升代码可读性和维护性

### v1.0.0 (2021-06-29)
- 初始版本发布
- 基础的JWT认证功能
- 简单的权限控制机制