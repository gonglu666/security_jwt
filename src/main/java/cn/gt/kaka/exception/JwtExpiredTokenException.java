package cn.gt.kaka.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * JWT令牌过期异常
 * 
 * 当JWT令牌已过期且无法继续使用时抛出此异常
 * 继承自Spring Security的AuthenticationException，
 * 确保与Spring Security的认证流程正确集成
 * 
 * 触发场景：
 * 1. 访问令牌(Access Token)超过有效期
 * 2. 刷新令牌(Refresh Token)超过有效期
 * 3. 令牌中的exp声明时间早于当前时间
 * 
 * 处理建议：
 * - 对于访问令牌过期：尝试使用刷新令牌获取新的访问令牌
 * - 对于刷新令牌过期：要求用户重新登录
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
public class JwtExpiredTokenException extends AuthenticationException {
    
    private static final long serialVersionUID = -5959543783324224864L;

    /**
     * 构造一个新的JWT令牌过期异常
     * 
     * @param message 异常详细信息，描述令牌过期的具体情况
     */
    public JwtExpiredTokenException(String message) {
        this(message, null);
    }

    /**
     * 构造一个新的JWT令牌过期异常
     * 
     * @param message 异常详细信息，描述令牌过期的具体情况
     * @param cause 引发此异常的根本原因，通常是Jackson的ExpiredJwtException
     */
    public JwtExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}