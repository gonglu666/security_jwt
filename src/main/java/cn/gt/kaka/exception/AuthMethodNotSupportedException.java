package cn.gt.kaka.exception;

import org.springframework.security.core.AuthenticationException;

/**
 *  AuthMethodNotSupportedException will  be thrown when we get
 *  /login  api  request with type other than  post.
 * Created by vishal.domale
 * @version 0.0.1
 */
public class AuthMethodNotSupportedException extends AuthenticationException {
    private static final long serialVersionUID = 3705043083010304496L;

    public AuthMethodNotSupportedException(String msg) {
        super(msg);
    }
}