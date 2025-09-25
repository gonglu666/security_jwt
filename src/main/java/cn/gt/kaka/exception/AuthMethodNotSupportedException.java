package cn.gt.kaka.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 认证方法不支持异常
 * 
 * 当客户端使用不支持的HTTP方法访问认证端点时抛出此异常
 * 继承自Spring Security的AuthenticationException，
 * 确保与Spring Security的认证流程正确集成
 * 
 * 典型场景：
 * 1. 使用GET方法访问登录接口（应该使用POST）
 * 2. 使用PUT、DELETE等方法访问认证端点
 * 3. 其他不符合API设计规范的HTTP方法调用
 * 
 * 设计目的：
 * - 提供清晰的错误提示，指导客户端使用正确的HTTP方法
 * - 增强API的安全性，拒绝非预期的请求方式
 * - 统一异常处理，改善用户体验
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
public class AuthMethodNotSupportedException extends AuthenticationException {
    
    private static final long serialVersionUID = 3705043083010304496L;

    /**
     * 构造一个新的认证方法不支持异常
     * 
     * @param message 异常详细信息，通常包含支持的HTTP方法提示
     */
    public AuthMethodNotSupportedException(String message) {
        super(message);
    }

    /**
     * 构造一个新的认证方法不支持异常
     * 
     * @param message 异常详细信息，通常包含支持的HTTP方法提示
     * @param cause 引发此异常的根本原因
     */
    public AuthMethodNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}