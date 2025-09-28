package cn.gt.kaka.filter;

import cn.gt.kaka.util.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by gonglu
 * 2021/6/28
 */
@Component
public class RestAuthenticationEntryPoint  implements AuthenticationEntryPoint {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String errorMessage = authException.getMessage();
        int httpStatusValue = HttpStatus.UNAUTHORIZED.value();
        CommonResponse<Object> objectCommonResponse = CommonResponse.failure(httpStatusValue, "1234", errorMessage);
        objectMapper.writeValue(response.getWriter(), objectCommonResponse);
    }
}
