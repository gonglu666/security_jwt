package cn.gt.kaka.dao;

import cn.gt.kaka.model.PermissionDto;
import cn.gt.kaka.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户数据访问对象
 * 
 * 负责用户相关的数据库操作，包括用户查询、权限查询等功能
 * 使用Spring JDBC Template进行数据库访问，提供简洁的数据访问接口
 * 
 * 主要功能：
 * 1. 根据用户名和密码查询用户信息
 * 2. 根据用户名查询用户信息
 * 3. 根据用户ID查询用户权限列表
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
@Slf4j
@Repository
public class UserDao {

    /**
     * Spring JDBC模板，用于执行数据库操作
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据用户名和密码查询用户信息
     * 
     * 该方法主要用于用户登录验证，通过用户名和密码匹配查找用户
     * 注意：在实际应用中，密码应该是加密存储的，此方法可能需要配合密码加密使用
     * 
     * @param username 用户名，用户的登录名
     * @param password 密码，用户的登录密码（可能是加密后的）
     * @return 返回匹配的用户信息，如果没有找到则返回Optional.empty()
     * @throws RuntimeException 当数据库访问出现异常时抛出
     */
    public Optional<UserDto> findByUsernameAndPassword(String username, String password) {
        log.debug("根据用户名和密码查询用户: username={}", username);
        
        final String sql = "SELECT id, username, password, fullname, mobile " +
                          "FROM t_user " +
                          "WHERE username = ? AND password = ?";
        
        try {
            List<UserDto> users = jdbcTemplate.query(
                sql, 
                new Object[]{username, password}, 
                new BeanPropertyRowMapper<>(UserDto.class)
            );
            
            if (CollectionUtils.isEmpty(users)) {
                log.debug("未找到匹配的用户: username={}", username);
                return Optional.empty();
            }
            
            if (users.size() > 1) {
                log.warn("发现多个同名用户，这可能是数据异常: username={}, count={}", username, users.size());
            }
            
            UserDto user = users.get(0);
            log.debug("成功查询到用户信息: userId={}, username={}", user.getId(), user.getUsername());
            return Optional.of(user);
            
        } catch (EmptyResultDataAccessException e) {
            log.debug("未找到匹配的用户: username={}", username);
            return Optional.empty();
        } catch (Exception e) {
            log.error("查询用户信息时发生异常: username={}, error={}", username, e.getMessage(), e);
            throw new RuntimeException("查询用户信息失败", e);
        }
    }

    /**
     * 根据用户名查询用户信息
     * 
     * 通过用户名查找用户信息，通常用于用户认证流程中获取用户的完整信息
     * 包括用户ID、用户名、加密密码、真实姓名和手机号码
     * 
     * @param username 用户名，用户的唯一登录标识
     * @return 返回匹配的用户信息，如果没有找到则返回Optional.empty()
     * @throws RuntimeException 当数据库访问出现异常时抛出
     */
    public Optional<UserDto> findByUsername(String username) {
        log.debug("根据用户名查询用户: username={}", username);
        
        final String sql = "SELECT id, username, password, fullname, mobile " +
                          "FROM t_user " +
                          "WHERE username = ?";
        
        try {
            List<UserDto> users = jdbcTemplate.query(
                sql, 
                new Object[]{username}, 
                new BeanPropertyRowMapper<>(UserDto.class)
            );
            
            if (CollectionUtils.isEmpty(users)) {
                log.debug("未找到用户: username={}", username);
                return Optional.empty();
            }
            
            if (users.size() > 1) {
                log.warn("发现多个同名用户，这可能是数据异常: username={}, count={}", username, users.size());
            }
            
            UserDto user = users.get(0);
            log.debug("成功查询到用户信息: userId={}, username={}", user.getId(), user.getUsername());
            return Optional.of(user);
            
        } catch (EmptyResultDataAccessException e) {
            log.debug("未找到用户: username={}", username);
            return Optional.empty();
        } catch (Exception e) {
            log.error("查询用户信息时发生异常: username={}, error={}", username, e.getMessage(), e);
            throw new RuntimeException("查询用户信息失败", e);
        }
    }

    /**
     * 根据用户ID查询用户的权限列表
     * 
     * 通过复杂的关联查询获取用户的所有权限信息
     * 查询路径：用户 -> 用户角色关联 -> 角色权限关联 -> 权限信息
     * 
     * 查询逻辑：
     * 1. 根据用户ID在用户角色表中查找用户的所有角色
     * 2. 根据角色ID在角色权限表中查找角色的所有权限
     * 3. 根据权限ID在权限表中查找权限的详细信息
     * 4. 提取权限代码列表返回
     * 
     * @param userId 用户ID，用户的唯一标识
     * @return 返回用户的权限代码列表，如果用户没有权限则返回空列表
     * @throws RuntimeException 当数据库访问出现异常时抛出
     */
    public List<String> findPermissionsByUserId(String userId) {
        log.debug("查询用户权限: userId={}", userId);
        
        final String sql = "SELECT p.* FROM t_permission p " +
                          "WHERE p.id IN (" +
                          "    SELECT rp.permission_id FROM t_role_permission rp " +
                          "    WHERE rp.role_id IN (" +
                          "        SELECT ur.role_id FROM t_user_role ur " +
                          "        WHERE ur.user_id = ?" +
                          "    )" +
                          ")";
        
        try {
            List<PermissionDto> permissions = jdbcTemplate.query(
                sql, 
                new Object[]{userId}, 
                new BeanPropertyRowMapper<>(PermissionDto.class)
            );
            
            List<String> permissionCodes = permissions.stream()
                    .map(PermissionDto::getCode)
                    .collect(Collectors.toList());
            
            log.debug("用户权限查询完成: userId={}, permissionCount={}, permissions={}", 
                     userId, permissionCodes.size(), permissionCodes);
            
            return permissionCodes;
            
        } catch (Exception e) {
            log.error("查询用户权限时发生异常: userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("查询用户权限失败", e);
        }
    }

    // ======================= 兼容性方法 =======================
    // 为了保持向后兼容性，保留原有的方法名

    /**
     * @deprecated 使用 {@link #findByUsernameAndPassword(String, String)} 替代
     */
    @Deprecated
    public UserDto getByNameAndPassword(String username, String password) {
        return findByUsernameAndPassword(username, password).orElse(null);
    }

    /**
     * @deprecated 使用 {@link #findByUsername(String)} 替代
     */
    @Deprecated
    public UserDto getUserByUsername(String username) {
        return findByUsername(username).orElse(null);
    }
}
