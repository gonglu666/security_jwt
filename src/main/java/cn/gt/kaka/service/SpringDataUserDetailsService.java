package cn.gt.kaka.service;

import cn.gt.kaka.dao.UserDao;
import cn.gt.kaka.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Spring Security用户详情服务实现类
 * 
 * 实现了Spring Security的用户详情服务接口和自定义的用户服务接口
 * 负责用户认证过程中的用户信息查询、密码验证、权限加载等功能
 * 
 * 主要功能：
 * 1. 根据用户名加载用户信息，供Spring Security认证使用
 * 2. 验证用户名和密码的有效性
 * 3. 加载用户的权限信息，用于授权控制
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
@Slf4j
@Service
public class SpringDataUserDetailsService implements IUserService {

    /**
     * 用户数据访问对象，用于查询用户信息
     */
    @Autowired
    private UserDao userDao;

    /**
     * 密码编码器，用于密码的加密和验证
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据用户名加载用户详情信息（Spring Security接口方法）
     * 
     * 此方法为Spring Security框架提供用户详情信息，包括用户名、密码和权限列表
     * 在用户认证过程中被Spring Security自动调用
     * 
     * @param username 用户名，用户的登录标识
     * @return UserDetails对象，包含用户的完整认证和授权信息
     * @throws UsernameNotFoundException 当用户不存在时抛出此异常
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Spring Security请求加载用户详情: username={}", username);

        // 参数验证
        if (!StringUtils.hasText(username)) {
            log.warn("用户名为空，无法加载用户详情");
            throw new UsernameNotFoundException("用户名不能为空");
        }

        // 查询用户基本信息
        Optional<UserDto> userOptional = userDao.findByUsername(username);
        if (!userOptional.isPresent()) {
            log.warn("用户不存在: username={}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        UserDto user = userOptional.get();
        log.debug("找到用户信息: userId={}, username={}", user.getId(), user.getUsername());

        // 查询用户权限
        List<String> permissions = userDao.findPermissionsByUserId(user.getId());
        String[] authorities = permissions.toArray(new String[0]);

        log.debug("用户权限加载完成: username={}, permissions={}", username, permissions);

        // 构建Spring Security的UserDetails对象
        UserDetails userDetails = User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

        log.debug("用户详情构建完成: username={}", username);
        return userDetails;
    }

    /**
     * 根据用户名查询用户信息（自定义接口方法）
     * 
     * 提供简单的用户查询功能，返回Optional包装的用户信息
     * 主要用于业务逻辑中需要获取用户信息的场景
     * 
     * @param username 用户名，用户的登录标识
     * @return Optional包装的用户信息，如果用户不存在则返回empty
     * @throws UsernameNotFoundException 当查询过程中发生异常时抛出
     */
    @Override
    public Optional<UserDto> loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("查询用户信息: username={}", username);

        // 参数验证
        if (!StringUtils.hasText(username)) {
            log.warn("用户名为空，无法查询用户信息");
            return Optional.empty();
        }

        try {
            Optional<UserDto> userOptional = userDao.findByUsername(username);
            if (userOptional.isPresent()) {
                log.debug("用户查询成功: userId={}, username={}", 
                         userOptional.get().getId(), userOptional.get().getUsername());
            } else {
                log.debug("用户不存在: username={}", username);
            }
            return userOptional;
            
        } catch (Exception e) {
            log.error("查询用户信息时发生异常: username={}, error={}", username, e.getMessage(), e);
            throw new UsernameNotFoundException("查询用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证用户名和密码
     * 
     * 根据用户名和密码验证用户身份，支持加密密码的验证
     * 用于用户登录时的身份验证流程
     * 
     * @param username 用户名，用户的登录标识
     * @param password 密码，用户输入的原始密码（未加密）
     * @return Optional包装的用户信息，验证成功返回用户信息，失败返回empty
     * @throws RuntimeException 当用户不存在或密码错误时抛出异常
     */
    @Override
    public Optional<UserDto> getByNameAndPassword(String username, String password) {
        log.debug("验证用户名和密码: username={}", username);

        // 参数验证
        if (!StringUtils.hasText(username)) {
            log.warn("用户名为空，无法进行密码验证");
            throw new RuntimeException("用户名不能为空");
        }

        if (!StringUtils.hasText(password)) {
            log.warn("密码为空，无法进行密码验证: username={}", username);
            throw new RuntimeException("密码不能为空");
        }

        try {
            // 查询用户信息
            Optional<UserDto> userOptional = userDao.findByUsername(username);
            if (!userOptional.isPresent()) {
                log.warn("用户不存在，密码验证失败: username={}", username);
                throw new RuntimeException("用户不存在");
            }

            UserDto user = userOptional.get();

            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("密码验证失败: username={}", username);
                throw new RuntimeException("密码错误");
            }

            log.debug("用户名和密码验证成功: userId={}, username={}", user.getId(), user.getUsername());
            return Optional.of(user);

        } catch (RuntimeException e) {
            // 重新抛出运行时异常
            throw e;
        } catch (Exception e) {
            log.error("验证用户名和密码时发生异常: username={}, error={}", username, e.getMessage(), e);
            throw new RuntimeException("密码验证失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查用户是否存在
     * 
     * @param username 用户名
     * @return true表示用户存在，false表示用户不存在
     */
    public boolean userExists(String username) {
        return loadUserByUsername(username).isPresent();
    }

    /**
     * 获取用户的权限列表
     * 
     * @param userId 用户ID
     * @return 用户的权限代码列表
     */
    public List<String> getUserPermissions(String userId) {
        log.debug("获取用户权限: userId={}", userId);
        
        if (!StringUtils.hasText(userId)) {
            log.warn("用户ID为空，无法获取权限");
            return List.of();
        }
        
        try {
            List<String> permissions = userDao.findPermissionsByUserId(userId);
            log.debug("权限获取成功: userId={}, permissionCount={}", userId, permissions.size());
            return permissions;
        } catch (Exception e) {
            log.error("获取用户权限时发生异常: userId={}, error={}", userId, e.getMessage(), e);
            return List.of();
        }
    }

    // ======================= 兼容性方法 =======================
    // 为了保持向后兼容性，保留原有的方法名

    /**
     * @deprecated 使用 {@link #loadUserByUsername(String)} 替代
     */
    @Deprecated
    public UserDetails loadUserByUsername1(String username) throws UsernameNotFoundException {
        return loadUserByUsername(username);
    }
}
