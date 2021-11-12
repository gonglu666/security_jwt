package cn.gt.kaka.service;

import cn.gt.kaka.model.UserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

/**
 * Created by gonglu
 * 2021/6/29
 */
public interface IUserService {

    Optional<UserDto> loadUserByUsername(String username) throws UsernameNotFoundException;

    Optional<UserDto> getByNameAndPassword(String username, String password);


}
