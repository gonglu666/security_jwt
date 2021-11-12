package cn.gt.kaka.provider;


import cn.gt.kaka.model.UserDto;
import cn.gt.kaka.util.Constants;
import cn.gt.kaka.util.JwtAuthenticationToken;
import cn.gt.kaka.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * This class support  JwtAuthenticationToken.class object.
 * When authentication manager will  have JwtAuthenticationToken  object
 * then it will call this class's authenticate method.
 * Created by vishal.domale
 *
 * @version 0.0.1
 */
@Component
public class PostAuthenticationProvider implements AuthenticationProvider {
	private static final Logger logger = LoggerFactory.getLogger(PostAuthenticationProvider.class);

    // use for  parse a jwt token.
    @Autowired
    protected JwtUtil jwtUtil;

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.equals(authentication));
    }

    /**
     * This method will parsing jwt token
     * and validate that token
     *
     * @param authentication
     * @return
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;
        Claims claims = validateAccessToken(authToken.getToken());
        UserDto userDto;
        // user token
        String userId = getUseId(claims);
        logger.debug("User: {} has authorities: {}", userId);
        userDto = new UserDto(userId);
		Set<GrantedAuthority> accesses=new HashSet<GrantedAuthority>(AuthorityUtils.commaSeparatedStringToAuthorityList("user"));
        return new JwtAuthenticationToken(userDto, null, null);
    }

    /**
     * This  method will return
     * a username form claims object.
     *
     * @param claims
     * @return
     */
    private String getUseId(Claims claims) {
        return (String) claims.get(Constants.USER_ID);
    }

    /**
     * This method will parse string jwt token and
     * return a claims(payload of JWT TOKEN)
     *
     * @param authToken
     * @return Claims
     */
    protected Claims validateAccessToken(String authToken) {
        logger.debug("Validating access token");
        logger.debug("Signing JWT token ");
        Jws<Claims> jwsClaims = jwtUtil.parseClaims(authToken);
        logger.debug("Signed JWT token ");
        Claims claims = jwsClaims.getBody();
        //Check claim

        //Check validity date of auth token
//        Date expiryDate = claims.getExpiration();
//        if (expiryDate.before(new Date())) {
//            throw new JwtExpiredTokenException("JWT Token expired");
//        }
        logger.debug("Validation of access token is successfully done ");
        return claims;
    }

}