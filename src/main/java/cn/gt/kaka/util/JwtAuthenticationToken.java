package cn.gt.kaka.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

/**
 * This is {@link org.springframework.security.core.Authentication }wrapper for string jwt token
 * Created by vishal.domale
 * @version 0.0.1
 */
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = 1L;

	//String format jwt token
	private String token;

	//default constructor with empty token.
	public JwtAuthenticationToken() {
		super(new Principal(){
			@Override
			public String getName() {
				return "";
			}}, "");
	}

    /**
     * token assign to token of the JwtAuthenticationToken
     * Create JwtAuthenticationToken for authentication.
     * @param token
     */
	public JwtAuthenticationToken(String token) {
		super(new Principal(){
			@Override
			public String getName() {
				return token;
			}}, token);
		this.token = token;
	}

    /**
     * If Jwt token  is Authenticated
     * then create trusted token object.
     * @param principal
     * @param credentials
     * @param authorities
     */
	public JwtAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal,credentials,authorities);
		this.eraseCredentials();
	}

	public String getToken() {
		return token;
	}

	@Override
	public String getName() {
		return token;
	}
	
}
