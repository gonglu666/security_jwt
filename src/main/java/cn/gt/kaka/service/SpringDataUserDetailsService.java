package cn.gt.kaka.service;


import cn.gt.kaka.dao.UserDao;
import cn.gt.kaka.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class SpringDataUserDetailsService implements IUserService {

    @Autowired
    UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;




    public UserDetails loadUserByUsername1(String username) throws UsernameNotFoundException {

        //将来连接数据库根据账号查询用户信息
        UserDto userDto = userDao.getUserByUsername(username);
        if(userDto == null){
            //如果用户查不到，返回null，由provider来抛出异常
            return null;
        }
        //根据用户的id查询用户的权限
        List<String> permissions = userDao.findPermissionsByUserId(userDto.getId());
        //将permissions转成数组
        String[] permissionArray = new String[permissions.size()];
        permissions.toArray(permissionArray);
        UserDetails userDetails = User.withUsername(userDto.getUsername()).password(userDto.getPassword()).authorities(permissionArray).build();
        return userDetails;
    }

    @Override
    public Optional<UserDto> loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userDao.getUserByUsername(username);
        return Optional.ofNullable(userDto);
    }

    @Override
    public Optional<UserDto> getByNameAndPassword(String username, String password) {
        UserDto userDto = userDao.getUserByUsername(username);
        if (userDto == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return Optional.ofNullable(userDto);
    }
}
