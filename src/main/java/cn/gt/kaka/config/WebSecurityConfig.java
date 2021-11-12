package cn.gt.kaka.config;

import cn.gt.kaka.filter.PostAuthenticationFilter;
import cn.gt.kaka.filter.PreAuthenticationFilter;
import cn.gt.kaka.filter.SpogRequestURLMatcher;
import cn.gt.kaka.filter.SpogRequestURLMatcher.MatcherBuilder;
import cn.gt.kaka.filter.handler.LoginAuthenticationFailureHandler;
import cn.gt.kaka.filter.handler.LoginAuthenticationSuccessHandler;
import cn.gt.kaka.provider.PostAuthenticationProvider;
import cn.gt.kaka.provider.PreAuthenticationProvider;
import cn.gt.kaka.rbac.CloudConsoleAccessDecisionManager;
import cn.gt.kaka.rbac.CloudConsoleFilterSecurityInterceptor;
import cn.gt.kaka.rbac.CloudConsoleSecurityMetadataSource;
import cn.gt.kaka.security.config.WhiteList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author Administrator
 * @version 1.0
 **/
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;

    @Autowired
    private LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;

    @Autowired
    private PostAuthenticationProvider postAuthenticationProvider;

    @Autowired
    private PreAuthenticationProvider preAuthenticationProvider;

    @Autowired
    private CloudConsoleAccessDecisionManager cloudConsoleAccessDecisionManager;

    @Autowired
    private CloudConsoleSecurityMetadataSource cloudConsoleSecurityMetadataSource;

    //安全拦截机制（最重要）
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .exceptionHandling()
            .authenticationEntryPoint(this.authenticationEntryPoint)
            .and()
            .headers().frameOptions().sameOrigin()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
//            .antMatchers("/version").permitAll()
            .anyRequest().authenticated()//.permitAll()
            .accessDecisionManager(cloudConsoleAccessDecisionManager)

            .and()
                .addFilterBefore(preAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(postAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authorizationSecurityInterceptor(), FilterSecurityInterceptor.class);

    }

    private CloudConsoleFilterSecurityInterceptor authorizationSecurityInterceptor(){
        CloudConsoleFilterSecurityInterceptor filter = new CloudConsoleFilterSecurityInterceptor();
        filter.setAccessDecisionManager(cloudConsoleAccessDecisionManager);
        filter.setSecurityMetadataSource(cloudConsoleSecurityMetadataSource);
        return filter;
    }

    private PreAuthenticationFilter preAuthenticationFilter() {
        PreAuthenticationFilter filter = new PreAuthenticationFilter("/users/login",
                loginAuthenticationSuccessHandler, loginAuthenticationFailureHandler, objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    private PostAuthenticationFilter postAuthenticationFilter() {
        PostAuthenticationFilter filter = new PostAuthenticationFilter(authenticationManager,loginAuthenticationFailureHandler);
        filter.setRequiresAuthenticationRequestMatcher(requestMatcher());
        return filter;

    }

    private RequestMatcher requestMatcher() {
        MatcherBuilder builder = SpogRequestURLMatcher.getBuilderWithBase(null);
        RequestMatcher match = builder
                .addWhiteMatcher(WhiteList.AUTHENTICATE_ENDPOINT)
                .addWhiteMatcher(WhiteList.VERSION_ENDPOINT,"GET")
                .addBlackMatcher("/**")
                .build();
        return match;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider((AuthenticationProvider) preAuthenticationProvider)
                .authenticationProvider((AuthenticationProvider) postAuthenticationProvider);
    }
}
