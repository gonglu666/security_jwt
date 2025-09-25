package cn.gt.kaka.util;

import cn.gt.kaka.dto.TokenType;
import cn.gt.kaka.exception.JwtExpiredTokenException;
import cn.gt.kaka.exception.JwtTokenMalformedException;
import cn.gt.kaka.model.UserDto;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JWT工具类
 * 
 * 提供JWT令牌的生成、解析、验证等功能
 * 支持访问令牌(Access Token)和刷新令牌(Refresh Token)两种类型
 * 使用HS256算法进行签名，确保令牌的安全性
 * 
 * 主要功能：
 * 1. 生成访问令牌和刷新令牌
 * 2. 解析和验证JWT令牌
 * 3. 处理令牌过期和格式错误等异常情况
 * 
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT签名密钥
     * 从配置文件中读取，用于HS256算法的签名和验证
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 访问令牌过期时间（秒）
     * 默认为1小时，用于控制访问令牌的有效期
     */
    @Value("${jwt.userAccessToken.expireTime}")
    private long userAccessTokenExpireTime;
    
    /**
     * 刷新令牌过期时间（秒）
     * 默认为2小时，用于控制刷新令牌的有效期
     */
    @Value("${jwt.userRefreshToken.expireTime}")
    private long userRefreshTokenExpireTime;

    /**
     * 生成访问令牌
     * 
     * 为指定用户生成访问令牌，包含用户ID和令牌类型信息
     * 访问令牌用于日常的API调用认证，有效期相对较短
     * 
     * @param userDto 用户信息对象，包含用户ID等基本信息
     * @return 生成的JWT访问令牌字符串
     * @throws JwtTokenMalformedException 当令牌创建过程中发生编码错误时抛出
     */
    public String generateAccessToken(UserDto userDto) {
        return generateToken(userDto, TokenType.Access, userAccessTokenExpireTime);
    }
    
    /**
     * 生成刷新令牌
     * 
     * 为指定用户生成刷新令牌，用于在访问令牌过期后获取新的访问令牌
     * 刷新令牌的有效期通常比访问令牌更长
     * 
     * @param userDto 用户信息对象，包含用户ID等基本信息
     * @return 生成的JWT刷新令牌字符串
     * @throws JwtTokenMalformedException 当令牌创建过程中发生编码错误时抛出
     */
    public String generateRefreshToken(UserDto userDto) {
        return generateToken(userDto, TokenType.Refresh, userRefreshTokenExpireTime);
    }

    /**
     * 生成JWT令牌的通用方法
     * 
     * 根据用户信息、令牌类型和过期时间生成JWT令牌
     * 使用HS256算法进行签名，确保令牌的完整性和真实性
     * 
     * @param userDto 用户信息对象
     * @param tokenType 令牌类型（访问令牌或刷新令牌）
     * @param expireTime 过期时间（秒）
     * @return 生成的JWT令牌字符串
     * @throws JwtTokenMalformedException 当令牌创建过程中发生编码错误时抛出
     */
    private String generateToken(UserDto userDto, TokenType tokenType, long expireTime) {
        try {
            // 创建JWT声明
            Claims claims = Jwts.claims().setSubject(userDto.getId());
            claims.put(Constants.USER_ID, userDto.getId());
            claims.put(Constants.TOKEN_TYPE, tokenType);
            
            // 计算过期时间
            LocalDateTime currentTime = LocalDateTime.now();
            Date expirationDate = Date.from(currentTime
                    .plusSeconds(expireTime)
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
            
            // 构建并返回JWT令牌
            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS256, secret.getBytes("UTF-8"))
                    .setExpiration(expirationDate)
                    .compact();
                    
        } catch (UnsupportedEncodingException e) {
            log.error("JWT令牌生成失败，编码异常: {}", e.getMessage(), e);
            throw new JwtTokenMalformedException("无效的JWT令牌: " + e.getMessage());
        }
    }

    /**
     * 解析JWT令牌
     * 
     * 使用系统密钥解析JWT令牌，验证签名并返回声明信息
     * 支持各种异常情况的处理，包括令牌过期、格式错误、签名无效等
     * 
     * @param authToken 待解析的JWT令牌字符串
     * @return 解析后的JWT声明对象
     * @throws BadCredentialsException 当令牌格式错误或签名无效时抛出
     * @throws JwtExpiredTokenException 当令牌已过期时抛出
     * @throws JwtTokenMalformedException 当令牌编码错误时抛出
     */
    public Jws<Claims> parseClaims(String authToken) {
        log.debug("开始解析JWT令牌: {}", authToken.substring(0, Math.min(20, authToken.length())) + "...");
        
        try {
            return Jwts.parser()
                    .setSigningKey(secret.getBytes("UTF-8"))
                    .parseClaimsJws(authToken);
                    
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException e) {
            log.error("JWT令牌验证失败: {}, 令牌: {}", e.getMessage(), authToken);
            throw new BadCredentialsException("无效的JWT令牌", e);
            
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期: {}", e.getMessage());
            throw new JwtExpiredTokenException("JWT令牌已过期", e);
            
        } catch (UnsupportedEncodingException e) {
            log.error("JWT令牌解析失败，编码异常: {}", e.getMessage(), e);
            throw new JwtTokenMalformedException("无效的JWT令牌: " + e.getMessage());
        }
    }

    /**
     * 验证令牌是否有效
     * 
     * 尝试解析令牌，如果解析成功则认为令牌有效
     * 
     * @param authToken 待验证的JWT令牌
     * @return true表示令牌有效，false表示令牌无效
     */
    public boolean validateToken(String authToken) {
        try {
            parseClaims(authToken);
            return true;
        } catch (Exception e) {
            log.debug("令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中提取用户ID
     * 
     * @param authToken JWT令牌
     * @return 用户ID，如果提取失败则返回null
     */
    public String getUserIdFromToken(String authToken) {
        try {
            Claims claims = parseClaims(authToken).getBody();
            return claims.get(Constants.USER_ID, String.class);
        } catch (Exception e) {
            log.warn("从令牌中提取用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中提取令牌类型
     * 
     * @param authToken JWT令牌
     * @return 令牌类型，如果提取失败则返回null
     */
    public TokenType getTokenTypeFromToken(String authToken) {
        try {
            Claims claims = parseClaims(authToken).getBody();
            String tokenTypeStr = claims.get(Constants.TOKEN_TYPE, String.class);
            return TokenType.valueOf(tokenTypeStr);
        } catch (Exception e) {
            log.warn("从令牌中提取令牌类型失败: {}", e.getMessage());
            return null;
        }
    }

    // ======================= 兼容性方法 =======================
    // 为了保持向后兼容性，保留原有的方法名

    /**
     * @deprecated 使用 {@link #generateAccessToken(UserDto)} 替代
     */
    @Deprecated
    public String generateToken(UserDto userDto) {
        return generateAccessToken(userDto);
    }
}
