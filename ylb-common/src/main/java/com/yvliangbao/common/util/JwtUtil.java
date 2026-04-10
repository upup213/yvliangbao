package com.yvliangbao.common.util;


import com.yvliangbao.common.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.security.auth.message.AuthException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT 工具类
 *
 * @author 余量宝
 */
@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;

    private static JwtProperties staticJwtProperties;

    @PostConstruct
    public void init() {
        staticJwtProperties = this.jwtProperties;
    }

    /**
     * 生成密钥
     */
    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(staticJwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     *
     * @param userId 用户ID
     * @param openid  微信openid
     * @return token
     */
    public static String generateToken(Long userId, String openid) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("openid", openid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + staticJwtProperties.getExpiration()))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成带权限的 Token（用于管理后台）
     *
     * @param userId      用户ID
     * @param username    用户名
     * @param roles       角色列表
     * @param permissions 权限列表
     * @return token
     */
    public static String generateTokenWithPermissions(Long userId, String username, 
                                                       List<String> roles, List<String> permissions) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + staticJwtProperties.getExpiration()))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成带权限和用户类型的 Token（用于管理后台）
     *
     * @param userId      用户ID
     * @param username    用户名
     * @param roles       角色列表
     * @param permissions 权限列表
     * @param userType    用户类型：1-用户，2-商户，3-平台
     * @return token
     */
    public static String generateTokenWithPermissions(Long userId, String username,
                                                       List<String> roles, List<String> permissions,
                                                       int userType) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + staticJwtProperties.getExpiration()))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成带用户类型的 Token
     *
     * @param userId   用户ID
     * @param openid   微信openid
     * @param userType 用户类型：1-用户，2-商户，3-平台
     * @return token
     */
    public static String generateToken(Long userId, String openid, int userType) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("openid", openid)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + staticJwtProperties.getExpiration()))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token
     *
     * @param token token
     * @return Claims
     * @throws AuthException token无效或已过期时抛出
     */
    public static Claims parseToken(String token) throws AuthException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("token已过期: {}", e.getMessage());
            throw new AuthException("token已过期");
        } catch (JwtException e) {
            log.error("解析token失败: {}", e.getMessage());
            throw new AuthException("token无效");
        }
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public static Long getUserId(String token) throws AuthException {
        Claims claims = parseToken(token);
        if (claims != null) {
            return Long.parseLong(claims.getSubject());
        }
        return null;
    }

    /**
     * 从 Token 中获取 openid
     *
     * @param token token
     * @return openid
     */
    public static String getOpenid(String token) throws AuthException {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("openid", String.class);
        }
        return null;
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token token
     * @return 用户名
     */
    public static String getUsername(String token) throws AuthException {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("username", String.class);
        }
        return null;
    }

    /**
     * 从 Token 中获取角色列表
     *
     * @param token token
     * @return 角色列表
     */
    @SuppressWarnings("unchecked")
    public static List<String> getRoles(String token) throws AuthException {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("roles", List.class);
        }
        return null;
    }

    /**
     * 从 Token 中获取权限列表
     *
     * @param token token
     * @return 权限列表
     */
    @SuppressWarnings("unchecked")
    public static List<String> getPermissions(String token) throws AuthException {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("permissions", List.class);
        }
        return null;
    }

    /**
     * 从 Token 中获取用户ID（失败抛异常）
     *
     * @param token token
     * @return 用户ID
     * @throws AuthException token无效时抛出
     */
    public static Long getUserIdOrThrow(String token) throws AuthException {
        Claims claims = parseToken(token);
        if (claims == null) {
            throw new AuthException("token无效或已过期");
        }
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从 Token 中获取 openid（失败抛异常）
     *
     * @param token token
     * @return openid
     * @throws AuthException token无效时抛出
     */
    public static String getOpenidOrThrow(String token) throws AuthException {
        Claims claims = parseToken(token);
        if (claims == null) {
            throw new AuthException("token无效或已过期");
        }
        return claims.get("openid", String.class);
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token token
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("token验证失败: {}", e.getMessage());
            return false;
        }
    }
}
