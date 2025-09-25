package cn.gt.kaka.security.config;

import java.util.Arrays;
import java.util.List;

/**
 * 安全白名单配置类
 * 
 * 定义系统中不需要进行身份认证就可以访问的URL端点
 * 这些端点通常包括登录接口、公共API、健康检查等
 * 
 * 白名单的作用：
 * 1. 提供用户登录入口，允许未认证用户进行身份验证
 * 2. 暴露必要的公共API，如版本信息、健康检查等
 * 3. 允许静态资源的访问，如CSS、JS、图片等
 * 4. 提供系统监控和管理接口的访问路径
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
public final class WhiteList {

    /**
     * 私有构造函数，防止实例化
     */
    private WhiteList() {
        throw new AssertionError("WhiteList配置类不应被实例化");
    }

    // ======================== 认证相关端点 ========================

    /**
     * 用户登录端点
     * 允许用户提交用户名和密码进行身份认证
     */
    public static final String AUTHENTICATE_ENDPOINT = "/users/login";

    /**
     * 令牌刷新端点
     * 允许用户使用刷新令牌获取新的访问令牌
     */
    public static final String REFRESH_TOKEN_ENDPOINT = "/users/refresh";

    /**
     * 用户注册端点
     * 允许新用户注册账户（如果开放注册功能）
     */
    public static final String REGISTER_ENDPOINT = "/users/register";

    // ======================== 公共API端点 ========================

    /**
     * 版本信息端点
     * 提供系统版本、构建信息等公共信息
     */
    public static final String VERSION_ENDPOINT = "/version";

    /**
     * 健康检查端点
     * 用于系统监控和负载均衡器的健康检查
     */
    public static final String HEALTH_ENDPOINT = "/health";

    /**
     * API文档端点
     * 提供Swagger API文档访问（开发环境）
     */
    public static final String API_DOCS_ENDPOINT = "/swagger-ui/**";

    /**
     * 错误页面端点
     * Spring Boot默认错误处理页面
     */
    public static final String ERROR_ENDPOINT = "/error";

    // ======================== 静态资源端点 ========================

    /**
     * 静态资源路径
     * CSS、JavaScript、图片等静态资源
     */
    public static final String STATIC_RESOURCES = "/static/**";

    /**
     * 公共资源路径
     * 公开访问的资源文件
     */
    public static final String PUBLIC_RESOURCES = "/public/**";

    /**
     * 网站图标
     * 浏览器自动请求的favicon.ico
     */
    public static final String FAVICON = "/favicon.ico";

    // ======================== 监控和管理端点 ========================

    /**
     * Actuator端点（如果启用）
     * Spring Boot Actuator提供的监控端点
     */
    public static final String ACTUATOR_ENDPOINTS = "/actuator/**";

    // ======================== 便捷方法 ========================

    /**
     * 获取所有认证相关的白名单端点
     * 
     * @return 认证相关端点列表
     */
    public static List<String> getAuthEndpoints() {
        return Arrays.asList(
                AUTHENTICATE_ENDPOINT,
                REFRESH_TOKEN_ENDPOINT,
                REGISTER_ENDPOINT
        );
    }

    /**
     * 获取所有公共API端点
     * 
     * @return 公共API端点列表
     */
    public static List<String> getPublicEndpoints() {
        return Arrays.asList(
                VERSION_ENDPOINT,
                HEALTH_ENDPOINT,
                API_DOCS_ENDPOINT,
                ERROR_ENDPOINT
        );
    }

    /**
     * 获取所有静态资源端点
     * 
     * @return 静态资源端点列表
     */
    public static List<String> getStaticResourceEndpoints() {
        return Arrays.asList(
                STATIC_RESOURCES,
                PUBLIC_RESOURCES,
                FAVICON
        );
    }

    /**
     * 获取所有白名单端点
     * 
     * @return 完整的白名单端点列表
     */
    public static List<String> getAllWhiteListEndpoints() {
        return Arrays.asList(
                // 认证端点
                AUTHENTICATE_ENDPOINT,
                REFRESH_TOKEN_ENDPOINT,
                REGISTER_ENDPOINT,
                // 公共API端点
                VERSION_ENDPOINT,
                HEALTH_ENDPOINT,
                API_DOCS_ENDPOINT,
                ERROR_ENDPOINT,
                // 静态资源端点
                STATIC_RESOURCES,
                PUBLIC_RESOURCES,
                FAVICON,
                // 监控端点
                ACTUATOR_ENDPOINTS
        );
    }

    /**
     * 检查指定路径是否在白名单中
     * 
     * @param path 要检查的路径
     * @return true表示在白名单中，false表示不在白名单中
     */
    public static boolean isWhiteListed(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }

        return getAllWhiteListEndpoints().stream()
                .anyMatch(endpoint -> {
                    if (endpoint.endsWith("/**")) {
                        String prefix = endpoint.substring(0, endpoint.length() - 3);
                        return path.startsWith(prefix);
                    } else {
                        return path.equals(endpoint);
                    }
                });
    }
}
