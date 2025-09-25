package cn.gt.kaka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户数据传输对象
 * 
 * 该类用于在各层之间传输用户相关数据，包含用户的基本信息
 * 使用 Lombok 注解简化样板代码，提高代码可读性和维护性
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    /**
     * 用户唯一标识符
     * 作为用户的主键，在整个系统中唯一标识一个用户
     */
    private String id;
    
    /**
     * 用户登录名
     * 用户用于登录系统的唯一用户名，不可重复
     */
    private String username;
    
    /**
     * 用户密码
     * 存储用户的加密密码，用于身份验证
     * 注意：实际存储时应为加密后的密码
     */
    private String password;
    
    /**
     * 用户真实姓名
     * 用户的完整姓名，用于显示和身份识别
     */
    private String fullname;
    
    /**
     * 用户手机号码
     * 用户的联系电话，可用于找回密码、接收验证码等功能
     */
    private String mobile;

    /**
     * 仅包含用户ID的构造函数
     * 
     * 当只需要用户ID时使用此构造函数，常用于权限验证等场景
     * 
     * @param id 用户唯一标识符
     */
    public UserDto(String id) {
        this.id = id;
    }
}
