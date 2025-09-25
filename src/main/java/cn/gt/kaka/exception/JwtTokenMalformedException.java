package cn.gt.kaka.exception;

/**
 * JWT令牌格式错误异常
 * 
 * 当JWT令牌格式不正确、无法解析或验证失败时抛出此异常
 * 这是一个运行时异常，表示客户端提供的JWT令牌存在格式问题
 * 
 * 常见的触发场景：
 * 1. JWT令牌格式不符合标准
 * 2. JWT令牌签名验证失败
 * 3. JWT令牌编码/解码过程中出现异常
 * 4. JWT令牌的Claims结构不正确
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
public class JwtTokenMalformedException extends RuntimeException {

    private static final long serialVersionUID = 6456515847032214078L;

    /**
     * 构造一个新的JWT令牌格式错误异常
     * 
     * @param message 异常详细信息，描述令牌格式错误的具体原因
     */
    public JwtTokenMalformedException(String message) {
        super(message);
    }

    /**
     * 构造一个新的JWT令牌格式错误异常
     * 
     * @param message 异常详细信息，描述令牌格式错误的具体原因
     * @param cause 引发此异常的根本原因
     */
    public JwtTokenMalformedException(String message, Throwable cause) {
        super(message, cause);
    }
}
