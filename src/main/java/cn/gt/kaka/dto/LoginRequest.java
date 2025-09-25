package cn.gt.kaka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户登录请求数据传输对象
 * 
 * 用于接收客户端发送的登录请求数据，包含用户名和密码
 * 该类定义了登录接口的请求格式，并提供基本的数据验证
 * 
 * 请求格式示例：
 * {
 *   "username": "admin",
 *   "password": "123456"
 * }
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 用户名
     * 
     * 用户的登录标识，必须提供且不能为空
     * 对应JSON请求中的"username"字段
     */
    @JsonProperty(value = "username", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    private String username;

    /**
     * 密码
     * 
     * 用户的登录密码，必须提供且不能为空
     * 对应JSON请求中的"password"字段
     * 注意：这里接收的是明文密码，将在服务端进行加密验证
     */
    @JsonProperty(value = "password", required = true)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    /**
     * 验证请求数据是否有效
     * 
     * @return true表示数据有效，false表示数据无效
     */
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() 
            && password != null && !password.trim().isEmpty();
    }

    /**
     * 获取清理后的用户名
     * 
     * 移除用户名前后的空格，确保数据的一致性
     * 
     * @return 清理后的用户名
     */
    public String getTrimmedUsername() {
        return username != null ? username.trim() : null;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}