package cn.gt.kaka.service;

import cn.gt.kaka.model.UserDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 * 
 * 定义用户相关的业务服务方法，包括用户查询、认证等核心功能
 * 该接口为用户管理模块提供标准的服务契约
 * 
 * 主要功能：
 * 1. 用户信息查询服务
 * 2. 用户身份认证服务
 * 3. 用户权限查询服务
 * 
 * @author 龚璐 (gonglu)
 * @version 2.0
 * @since 2021/6/29
 */
public interface IUserService {

    /**
     * 根据用户名加载用户信息
     * 
     * 根据提供的用户名查询并返回用户的详细信息
     * 该方法通常用于用户认证流程中获取用户基本信息
     * 
     * @param username 用户名，用户的唯一登录标识
     * @return Optional包装的用户信息，如果用户不存在则返回empty
     * @throws UsernameNotFoundException 当查询过程中发生异常时抛出
     */
    Optional<UserDto> loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * 根据用户名和密码验证用户身份
     * 
     * 验证提供的用户名和密码是否匹配，用于用户登录认证
     * 支持加密密码的验证，确保系统安全性
     * 
     * @param username 用户名，用户的登录标识
     * @param password 密码，用户输入的原始密码（未加密）
     * @return Optional包装的用户信息，验证成功返回用户信息，失败返回empty
     * @throws RuntimeException 当用户不存在或密码错误时抛出异常
     */
    Optional<UserDto> getByNameAndPassword(String username, String password);

    /**
     * 检查用户是否存在
     * 
     * 根据用户名检查用户是否在系统中存在
     * 可用于注册时的用户名重复检查等场景
     * 
     * @param username 用户名
     * @return true表示用户存在，false表示用户不存在
     */
    default boolean userExists(String username) {
        return loadUserByUsername(username).isPresent();
    }

    /**
     * 获取用户权限列表
     * 
     * 根据用户ID获取该用户拥有的所有权限代码列表
     * 用于权限控制和授权验证
     * 
     * @param userId 用户ID，用户的唯一标识
     * @return 用户的权限代码列表，如果用户没有权限则返回空列表
     */
    default List<String> getUserPermissions(String userId) {
        return List.of();
    }
}
