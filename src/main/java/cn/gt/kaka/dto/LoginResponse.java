package cn.gt.kaka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录响应数据传输对象
 * 
 * 用于向客户端返回登录成功后的认证信息，包含访问令牌和刷新令牌
 * 该类定义了登录接口成功响应的数据格式
 * 
 * 响应格式示例：
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...",
 *   "refresh_token": "eyJhbGciOiJIUzI1NiJ9...",
 *   "expires_in": 3600,
 *   "token_type": "Bearer"
 * }
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    /**
     * 访问令牌
     * 
     * 用于后续API调用的身份认证令牌，有效期相对较短
     * 客户端需要在Authorization头中携带此令牌进行API调用
     * 对应JSON响应中的"token"字段
     */
    @JsonProperty(value = "token", required = true)
    private String token;

    /**
     * 刷新令牌
     * 
     * 用于在访问令牌过期后获取新的访问令牌，有效期相对较长
     * 客户端应该安全存储此令牌，并在访问令牌过期时使用它来刷新令牌
     * 对应JSON响应中的"refresh_token"字段
     */
    @JsonProperty(value = "refresh_token", required = true)
    private String refreshToken;

    /**
     * 令牌过期时间（秒）
     * 
     * 访问令牌的有效期，单位为秒
     * 客户端可以根据此值来判断何时需要刷新令牌
     */
    @JsonProperty(value = "expires_in")
    private Long expiresIn;

    /**
     * 令牌类型
     * 
     * 令牌的类型，通常为"Bearer"
     * 客户端在Authorization头中使用时需要加上此前缀
     */
    @JsonProperty(value = "token_type")
    private String tokenType;

    /**
     * 创建包含基本信息的登录响应
     * 
     * @param token 访问令牌
     * @param refreshToken 刷新令牌
     * @return 登录响应对象
     */
    public static LoginResponse of(String token, String refreshToken) {
        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    /**
     * 创建包含完整信息的登录响应
     * 
     * @param token 访问令牌
     * @param refreshToken 刷新令牌
     * @param expiresIn 过期时间（秒）
     * @return 登录响应对象
     */
    public static LoginResponse of(String token, String refreshToken, Long expiresIn) {
        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .tokenType("Bearer")
                .build();
    }

    /**
     * 检查响应数据是否完整
     * 
     * @return true表示包含必要的令牌信息，false表示数据不完整
     */
    public boolean isValid() {
        return token != null && !token.trim().isEmpty() 
            && refreshToken != null && !refreshToken.trim().isEmpty();
    }
}
