package cn.gt.kaka.rbac;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 云控制台安全元数据源
 * 
 * 实现Spring Security的FilterInvocationSecurityMetadataSource接口，
 * 负责为每个HTTP请求提供所需的权限配置信息
 * 
 * 该类是基于角色的访问控制(RBAC)系统的核心组件之一，
 * 它决定了访问特定资源所需要的权限要求
 * 
 * 工作流程：
 * 1. 接收HTTP请求的信息（URL、方法等）
 * 2. 根据请求信息查找对应的权限要求
 * 3. 返回访问该资源所需的权限配置
 * 4. Spring Security使用这些配置进行权限验证
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
@Slf4j
@Component
public class CloudConsoleSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    /**
     * 获取访问指定资源所需的权限配置
     * 
     * 根据HTTP请求的详细信息（URL路径、HTTP方法等），
     * 确定访问该资源所需要的权限要求
     * 
     * 当前实现为简化版本，对所有请求都要求"user"权限
     * 在实际生产环境中，应该根据具体的URL和方法返回相应的权限要求
     * 
     * @param object FilterInvocation对象，包含HTTP请求的详细信息
     * @return 访问该资源所需的权限配置集合
     * @throws IllegalArgumentException 当传入的对象类型不正确时抛出
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if (!(object instanceof FilterInvocation)) {
            throw new IllegalArgumentException("不支持的对象类型: " + object.getClass());
        }

        FilterInvocation filterInvocation = (FilterInvocation) object;
        String requestUrl = filterInvocation.getRequestUrl();
        String httpMethod = filterInvocation.getRequest().getMethod();
        
        log.debug("获取资源权限配置: {} {}", httpMethod, requestUrl);

        // 创建权限配置集合
        Set<ConfigAttribute> attributes = new HashSet<>();
        
        // 当前简化实现：所有请求都需要"user"权限
        // 在实际项目中，这里应该根据URL和方法的具体组合返回相应的权限要求
        // 例如：
        // - /admin/** 需要 ADMIN 权限
        // - /user/** 需要 USER 权限
        // - /public/** 不需要任何权限
        attributes.add(new SecurityConfig("user"));
        
        log.debug("资源 {} {} 需要权限: {}", httpMethod, requestUrl, attributes);
        return attributes;
    }

    /**
     * 获取系统中所有的权限配置
     * 
     * 返回系统中定义的所有可能的权限配置，
     * Spring Security在启动时会调用此方法来验证权限配置的完整性
     * 
     * 当前实现返回null，表示不进行启动时的权限配置验证
     * 在复杂的权限系统中，可以返回所有可能的权限配置用于验证
     * 
     * @return 所有可能的权限配置集合，null表示不进行验证
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        log.debug("获取所有权限配置 - 当前实现不返回具体配置");
        
        // 返回null表示不进行启动时的权限配置验证
        // 如果需要验证，可以返回系统中所有可能的权限配置
        return null;
    }

    /**
     * 检查是否支持指定的安全对象类型
     * 
     * Spring Security会调用此方法来确认当前的SecurityMetadataSource
     * 是否能够处理特定类型的安全对象
     * 
     * 本实现支持FilterInvocation类型的对象，即HTTP请求相关的安全检查
     * 
     * @param clazz 要检查的安全对象类型
     * @return true表示支持该类型，false表示不支持
     */
    @Override
    public boolean supports(Class<?> clazz) {
        boolean supported = FilterInvocation.class.isAssignableFrom(clazz);
        log.debug("检查是否支持安全对象类型 {}: {}", clazz.getSimpleName(), supported);
        return supported;
    }
}
