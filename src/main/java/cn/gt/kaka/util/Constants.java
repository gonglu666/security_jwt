
package cn.gt.kaka.util;

/**
 * Spring security related constants.
 * Created by vishal.domale
 * @version 0.0.1
 */
public class Constants {

    //Login response json key for access token
	public static final String ACCESS_TOKEN = "token";
	//Login response json key for refresh token
	public static final String REFRESH_TOKEN = "refresh_token";
	//JWt token payload prefix.
	public static final String HEADER_PREFIX = "Bearer ";
    //Jwt token header key
	public static final String AUTH_HEADER_NAME = "Authorization";
	//Jwt Claim Key.
	public static final String USER_ID = "user_id";
	//Jwt Claim Key.
	public static final String ROLE_ID = "role_id";
	//Jwt Claim Key.
	public static final String FIRST_LOGIN = "first_login";
	//Jwt Claim Key.
	public static final String TOKEN_TYPE = "token_type";

}
