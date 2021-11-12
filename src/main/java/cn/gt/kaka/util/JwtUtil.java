package cn.gt.kaka.util;

import cn.gt.kaka.dto.TokenType;
import cn.gt.kaka.exception.JwtExpiredTokenException;
import cn.gt.kaka.exception.JwtTokenMalformedException;
import cn.gt.kaka.model.UserDto;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    //Read jwt secret from .properties file
    //private key use in hash algorithm(HS256).
    @Value("${jwt.secret}")
    private String secret;

    // user access token  expire time, default is 1 hours(in second)
    @Value("${jwt.userAccessToken.expireTime}")
    private long userAccessTokenExpireTime;
    
    // user refresh token  expire time, default is 2 hours(in second)
    @Value("${jwt.userRefreshToken.expireTime}")
    private long userRefreshTokenExpireTime;


    /**
     * This method will create jwt token.
     * and add claims in jwt token
     * @param userDto
     * @return
    */
    public String generateToken(UserDto userDto) {
        Claims claims = Jwts.claims().setSubject(userDto.getId().toString());
        claims.put(Constants.USER_ID,userDto.getId());
        claims.put(Constants.TOKEN_TYPE, TokenType.Access);
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes("UTF-8"))
                .setExpiration(Date.from(currentTime
                        .plusSeconds(userAccessTokenExpireTime)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .compact();
            
        } catch (UnsupportedEncodingException encodingEx) {
            logger.error("Exception in building JWT. " + encodingEx);
            throw new JwtTokenMalformedException("Invalid JWT token: " + encodingEx.getMessage());
        }
    }
    
    /**
     * This method will create jwt refresh token.
     * and add claims in jwt token
     * @param userDto
     * @return
    */
    public String generateRefreshToken(UserDto userDto) {
        Claims claims = Jwts.claims().setSubject(userDto.getId().toString());
        claims.put(Constants.USER_ID,userDto.getId());
        claims.put(Constants.TOKEN_TYPE, TokenType.Refresh);
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS256, secret.getBytes("UTF-8"))
                    .setExpiration(Date.from(currentTime
                            .plusSeconds(userRefreshTokenExpireTime)
                            .atZone(ZoneId.systemDefault()).toInstant()))
                    .compact();
        } catch (UnsupportedEncodingException encodingEx) {
            logger.error("Exception in building JWT. " + encodingEx);
            throw new JwtTokenMalformedException("Invalid JWT token: " + encodingEx.getMessage());
        }
    }

    /**
     * This method will parse jwt token by using secret key
     *  and return jws claims
     * @param authToken
     * @return Jws<Claims>
     *
     */
    public Jws<Claims> parseClaims(String authToken) {
        logger.debug("parseClaims...");
        try {
            return Jwts.parser().setSigningKey(secret.getBytes("UTF-8")).parseClaimsJws(authToken);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
        	logger.error("Validating user authToken :: " + authToken);
            logger.error("Exception in parsing claim. " + ex);
            throw new BadCredentialsException("Invalid JWT token: ", ex);
        } catch (ExpiredJwtException expiredEx) {
            logger.error("Exception in parsing claim. " + expiredEx);
            throw new JwtExpiredTokenException("JWT Token expired", expiredEx);
        } catch (UnsupportedEncodingException encodingEx) {
            logger.error("Exception in parsing claim. " + encodingEx);
            throw new JwtTokenMalformedException("Invalid JWT token: " + encodingEx.getMessage());
        }
    }
}
