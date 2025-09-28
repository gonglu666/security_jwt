package cn.gt.kaka.filter.handler;


import cn.gt.kaka.exception.AuthMethodNotSupportedException;
import cn.gt.kaka.util.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * Handle unauthenticated request
 * and write error message in response
 * Created by vishal.domale
 * @version 0.0.1
 */
@Component
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginAuthenticationFailureHandler.class);

    // ObjectMapper will use to  write error response in json format.
    private final ObjectMapper mapper;

    @Autowired
    public LoginAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     *
     * This will call when user and jwt token is unauthorised.
     * @param request
     * @param response
     * @param e
     * @throws IOException
     * @throws ServletException
     *
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException e) throws IOException, ServletException {
        logger.info("Authentication failed!");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String message = e.getMessage();
        Integer subCode  = 500;
        if(e instanceof AuthMethodNotSupportedException){
            subCode = 401;
            message = "校验方法不支持";
        }else if(e instanceof UsernameNotFoundException){
            subCode = 400;
            message = "该用户不存在";
        }else if(e instanceof DisabledException){
            subCode = 400;
            message = "用户已删除";
        }
        CommonResponse commonResponse =  CommonResponse.failure(subCode,"error code",message);
        mapper.writeValue(response.getWriter(), commonResponse);
    }
}