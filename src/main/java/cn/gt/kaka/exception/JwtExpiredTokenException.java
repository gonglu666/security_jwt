package cn.gt.kaka.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * JwtExpiredTokenException will be thrown when
 * Jwt token get expired.
 * Created by vishal.domale
 * @version 0.0.1
 */
public class JwtExpiredTokenException extends AuthenticationException {
    private static final long serialVersionUID = -5959543783324224864L;
    

    public JwtExpiredTokenException(String msg) {
        this(msg, null);
    }

    public JwtExpiredTokenException(String msg, Throwable t) {
        super(msg, t);
    }

}