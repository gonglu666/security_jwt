package cn.gt.kaka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限数据传输对象
 * 
 * 该类用于表示系统中的权限信息，包含权限的基本属性
 * 权限用于控制用户对系统资源的访问，实现细粒度的访问控制
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {

    /**
     * 权限唯一标识符
     * 作为权限的主键，在权限表中唯一标识一个权限
     */
    private String id;
    
    /**
     * 权限代码
     * 权限的唯一编码，用于在代码中标识和验证权限
     * 例如：USER_READ、USER_WRITE、ADMIN_DELETE 等
     */
    private String code;
    
    /**
     * 权限描述
     * 权限的详细描述信息，便于管理员理解权限的具体作用
     */
    private String description;
    
    /**
     * 权限关联的URL
     * 该权限控制的具体URL路径，用于URL级别的权限控制
     * 支持通配符和路径模式匹配
     */
    private String url;
}
