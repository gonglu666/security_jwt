
package cn.gt.kaka.util;

/**
 * 系统常量定义类
 * 
 * 定义Spring Security和JWT相关的常量，统一管理系统中使用的魔法字符串
 * 避免硬编码，提高代码的可维护性和可读性
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
public final class Constants {

    /**
     * 私有构造函数，防止实例化
     */
    private Constants() {
        throw new AssertionError("Constants类不应被实例化");
    }

    // ======================== JWT Token 相关常量 ========================

    /**
     * 登录响应JSON中访问令牌的键名
     * 用于在登录成功后返回给客户端的访问令牌字段名
     */
    public static final String ACCESS_TOKEN = "token";

    /**
     * 登录响应JSON中刷新令牌的键名
     * 用于在登录成功后返回给客户端的刷新令牌字段名
     */
    public static final String REFRESH_TOKEN = "refresh_token";

    /**
     * JWT令牌头部前缀
     * 在Authorization头部中，JWT令牌前需要添加的前缀
     * 符合Bearer Token的标准格式
     */
    public static final String HEADER_PREFIX = "Bearer ";

    /**
     * JWT令牌请求头名称
     * 客户端发送JWT令牌时使用的HTTP头部名称
     */
    public static final String AUTH_HEADER_NAME = "Authorization";

    // ======================== JWT Claims 相关常量 ========================

    /**
     * JWT声明中用户ID的键名
     * 在JWT payload中存储用户ID信息的字段名
     */
    public static final String USER_ID = "user_id";

    /**
     * JWT声明中角色ID的键名
     * 在JWT payload中存储用户角色ID信息的字段名
     */
    public static final String ROLE_ID = "role_id";

    /**
     * JWT声明中首次登录标识的键名
     * 用于标识用户是否为首次登录，可用于强制修改密码等场景
     */
    public static final String FIRST_LOGIN = "first_login";

    /**
     * JWT声明中令牌类型的键名
     * 用于区分访问令牌（Access Token）和刷新令牌（Refresh Token）
     */
    public static final String TOKEN_TYPE = "token_type";

}
