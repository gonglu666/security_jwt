package cn.gt.kaka.filter;

import cn.gt.kaka.util.Constants;
import cn.gt.kaka.util.JwtAuthenticationToken;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ObjectUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter  will be activated for /api/** url pattern.
 * and check for jwt token in request header and authorise that token.
 * Created by vishal.domale
 * @version 0.0.1
 */

public class PostAuthenticationFilter extends BasicAuthenticationFilter {
	private static final Logger logger = LoggerFactory.getLogger(PostAuthenticationFilter.class);

	// Call when jwt token is unauthorised.
	private final AuthenticationFailureHandler failureHandler;

    private RequestMatcher requiresAuthenticationRequestMatcher;

    public RequestMatcher getRequiresAuthenticationRequestMatcher() {
        return requiresAuthenticationRequestMatcher;
    }

    public void setRequiresAuthenticationRequestMatcher(RequestMatcher requiresAuthenticationRequestMatcher) {
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
    }

    public AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }

    public PostAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationFailureHandler failureHandler) {
        super(authenticationManager);
        this.failureHandler = failureHandler;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String tokenPayload = extractToken(request.getHeader(Constants.AUTH_HEADER_NAME));
            JwtAuthenticationToken authRequest  = new JwtAuthenticationToken(tokenPayload);
            Authentication authResult = getAuthenticationManager().authenticate(authRequest);
            onSuccessfulAuthentication(request, response, authResult);
        }
        catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
            onUnsuccessfulAuthentication(request, response, failed);
            return;
        }
        chain.doFilter(request, response);

    }

    /**
     *
     * Extracting jwt token from request header
     * and check for that jwt token valid or not.
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */


    protected void onUnsuccessfulAuthentication(HttpServletRequest request,
                                                HttpServletResponse response, AuthenticationException failed)
            throws IOException {
        SecurityContextHolder.clearContext();
        try {
            failureHandler.onAuthenticationFailure(request, response, failed);
        } catch (ServletException e) {
            throw new AuthenticationServiceException("异常处理失败");
        }
    }

    public void onSuccessfulAuthentication(HttpServletRequest request,
                                           HttpServletResponse response, Authentication authResult) {
        if (authResult.getPrincipal() != null) {
            logger.debug("Authorization successful!");
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);
        }

    }

    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        JwtAuthenticationToken authRequest;
        String tokenPayload = extractToken(request.getHeader(Constants.AUTH_HEADER_NAME));
        authRequest = new JwtAuthenticationToken(tokenPayload);
        return getAuthenticationManager().authenticate(authRequest);
    }

    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        if (authResult.getPrincipal() != null) {
            logger.debug("Authorization successful!");
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);
        }
        chain.doFilter(request, response);
    }

	protected void unsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed)
			throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }


    /**
     *
     * Check for that token payload has prefix value as "Bearer "
     * Extract jwt token from token payload mean get without prefix.
     * @param tokenHeader
     * @return
     */
    public String extractToken(String tokenHeader) {
        if (ObjectUtils.isEmpty(tokenHeader)) {
            throw new AuthenticationServiceException("Authorization header cannot be blank!");
        }
        if (!tokenHeader.startsWith(Constants.HEADER_PREFIX)) {
            throw new AuthenticationServiceException("Invalid authorization header.");
        }
        return tokenHeader.substring(Constants.HEADER_PREFIX.length(), tokenHeader.length());
    }

    private boolean requiresAuthentication(HttpServletRequest request,
                                             HttpServletResponse response) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }
}