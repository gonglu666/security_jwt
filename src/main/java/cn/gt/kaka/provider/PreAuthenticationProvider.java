package cn.gt.kaka.provider;

import cn.gt.kaka.model.UserDto;
import cn.gt.kaka.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


/**
 * This class support  UsernamePasswordAuthenticationToken.class object.
 * If authentication manager  will having that object.
 * it call this class's authenticate method.
 * Created by vishal.domale
 * @version 0.0.1
 */
@Component
public class PreAuthenticationProvider implements AuthenticationProvider {
	private static final Logger logger = LoggerFactory.getLogger(PreAuthenticationProvider.class);
	
	@Autowired
    private IUserService userService;


    /**
     * This method will check Username and Password is valid or not
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        logger.debug("{} login request", username);
        String password = (String) authentication.getCredentials();
        

        
		UserDto	user = userService.getByNameAndPassword(username, password)
        			.orElseThrow(() -> new BadCredentialsException(
						"Authentication Failed. Username or Password not valid."));

		return new UsernamePasswordAuthenticationToken(user, null, null);
    }
    
    
    /**
	 *
	 * This will return true if Authentication is object of
	 * UsernamePasswordAuthenticationToken class. otherwise return false.
	 * @param authentication
	 * @return true
	 */
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.equals(authentication));
    }
}