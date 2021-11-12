package cn.gt.kaka.exception;

/**
 * JwtTokenMalformedException will be thrown when
 * Jwt token is invalid.
 * Created by vishal.domale
 * @version 0.0.1
 */
public class JwtTokenMalformedException extends RuntimeException {

	private static final long serialVersionUID = 6456515847032214078L;

	public JwtTokenMalformedException(String string) {
		super(string);
	}

}
