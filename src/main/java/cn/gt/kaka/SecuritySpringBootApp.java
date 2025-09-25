package cn.gt.kaka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * JWT安全认证系统Spring Boot启动类
 * 
 * 这是基于Spring Boot的JWT认证和授权系统的主启动类
 * 系统提供了完整的用户认证、JWT令牌管理和基于角色的访问控制功能
 * 
 * 系统主要功能：
 * 1. 用户登录认证和JWT令牌生成
 * 2. JWT令牌验证和用户身份识别
 * 3. 基于角色的资源访问控制(RBAC)
 * 4. 令牌刷新机制
 * 5. 统一的异常处理和响应格式
 * 
 * 技术栈：
 * - Spring Boot 2.1.3
 * - Spring Security 5.1.4
 * - JWT (JSON Web Token)
 * - MySQL数据库
 * - Lombok简化代码
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
@Slf4j
@SpringBootApplication
public class SecuritySpringBootApp {

    /**
     * 应用程序入口方法
     * 
     * 启动Spring Boot应用，初始化所有配置和组件
     * 包括安全配置、数据库连接、JWT工具等
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            log.info("========================================");
            log.info("JWT安全认证系统正在启动...");
            log.info("========================================");
            
            SpringApplication app = new SpringApplication(SecuritySpringBootApp.class);
            Environment env = app.run(args).getEnvironment();
            
            logApplicationStartup(env);
            
        } catch (Exception e) {
            log.error("应用启动失败", e);
            System.exit(1);
        }
    }

    /**
     * 记录应用启动信息
     * 
     * @param env Spring环境配置
     */
    private static void logApplicationStartup(Environment env) {
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String hostAddress = "localhost";
        
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("无法获取主机地址，使用默认值: localhost");
        }
        
        log.info("\n----------------------------------------------------------\n" +
                "\t应用启动成功！相关信息如下:\n" +
                "\t应用名称: \t{}\n" +
                "\t本地访问: \thttp://localhost:{}{}\n" +
                "\t外部访问: \thttp://{}:{}{}\n" +
                "\t配置文件: \t{}\n" +
                "\t活跃配置: \t{}\n" +
                "----------------------------------------------------------",
                env.getProperty("spring.application.name", "JWT Security System"),
                serverPort,
                contextPath,
                hostAddress,
                serverPort,
                contextPath,
                env.getProperty("spring.config.location", "默认配置"),
                env.getActiveProfiles().length == 0 ? "default" : String.join(", ", env.getActiveProfiles())
        );
        
        // 输出重要的安全提示
        log.info("========================================");
        log.info("安全提示:");
        log.info("1. 请确保JWT密钥的安全性");
        log.info("2. 建议在生产环境中使用HTTPS");
        log.info("3. 定期更新依赖版本以修复安全漏洞");
        log.info("========================================");
    }
}
