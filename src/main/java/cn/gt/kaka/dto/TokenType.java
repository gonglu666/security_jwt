package cn.gt.kaka.dto;

/**
 * JWT令牌类型枚举
 * 
 * 定义系统中使用的JWT令牌类型，用于区分不同用途的令牌
 * 在JWT的claims中使用，帮助服务端识别令牌的具体用途
 * 
 * 令牌类型说明：
 * - Access: 访问令牌，用于日常的API调用认证，有效期较短
 * - Refresh: 刷新令牌，用于获取新的访问令牌，有效期较长
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
public enum TokenType {
    
    /**
     * 访问令牌
     * 
     * 用于API调用的身份认证，通常有效期为1-2小时
     * 客户端在每次API请求时需要在Authorization头中携带此令牌
     */
    Access("access_token", "访问令牌"),
    
    /**
     * 刷新令牌
     * 
     * 用于在访问令牌过期后获取新的访问令牌，通常有效期为几天到几周
     * 只能用于令牌刷新接口，不能用于其他API调用
     */
    Refresh("refresh_token", "刷新令牌");

    /**
     * 令牌类型的英文标识
     */
    private final String value;
    
    /**
     * 令牌类型的中文描述
     */
    private final String description;

    /**
     * 构造函数
     * 
     * @param value 令牌类型的英文标识
     * @param description 令牌类型的中文描述
     */
    TokenType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取令牌类型的英文标识
     * 
     * @return 英文标识字符串
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取令牌类型的中文描述
     * 
     * @return 中文描述字符串
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据英文标识获取对应的令牌类型
     * 
     * @param value 英文标识
     * @return 对应的TokenType枚举值，如果找不到则返回null
     */
    public static TokenType fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        for (TokenType type : TokenType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 检查是否为访问令牌类型
     * 
     * @return true表示是访问令牌类型
     */
    public boolean isAccessToken() {
        return this == Access;
    }

    /**
     * 检查是否为刷新令牌类型
     * 
     * @return true表示是刷新令牌类型
     */
    public boolean isRefreshToken() {
        return this == Refresh;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", name(), description);
    }
}
