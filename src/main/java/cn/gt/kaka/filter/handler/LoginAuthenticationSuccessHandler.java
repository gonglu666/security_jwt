package cn.gt.kaka.filter.handler;



import cn.gt.kaka.dto.LoginResponse;
import cn.gt.kaka.model.UserDto;
import cn.gt.kaka.util.CommonResponse;
import cn.gt.kaka.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Component
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginAuthenticationSuccessHandler.class);

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected ObjectMapper mapper;


    
    /**
     *
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     *
     * This method will call when user successfully authenticated
     *  and in this method  Create Jwt token and write that  jwt token in to response.
     *  Store Authenticated user's user context in to session.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
    	logger.debug("User successfully authenticated.");
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(generateToken(authentication));
        loginResponse.setRefreshToken(generateRefreshToken(authentication));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        CommonResponse<LoginResponse> loginResponseCommonResponse = CommonResponse.success(loginResponse);
        mapper.writeValue(response.getWriter(), loginResponseCommonResponse);
        clearAuthenticationAttributes(request);
    }

    
    /**
     * generate user token
     * @param authentication
     * @return
     */
    protected String generateToken(Authentication authentication) {
        UserDto userDto = (UserDto) authentication.getPrincipal();
    	return jwtUtil.generateToken(userDto);
    }
    
    /**
     * generate user refresh token
     * @param authentication
     * @return
     */
    protected String generateRefreshToken(Authentication authentication) {
        UserDto userDto = (UserDto) authentication.getPrincipal();
    	return jwtUtil.generateRefreshToken(userDto);
    }

    /**
     * Removing attribute from http session if present.
     * @param request
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
    
}