package com.yvliangbao.common.sercurity;


import com.yvliangbao.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * 从请求头中提取 Token，解析权限并设置到 SecurityContext
 *
 * @author 余量宝
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 1. 从请求头获取 Token
        String token = resolveToken(request);
        
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 2. 验证 Token 有效性
                if (JwtUtil.validateToken(token)) {
                    
                    // 3. 从 Token 中提取用户信息
                    Long userId = JwtUtil.getUserId(token);
                    String username = JwtUtil.getUsername(token);
                    List<String> permissions = JwtUtil.getPermissions(token);
                    
                    // 4. 构建权限列表（角色加 ROLE_ 前缀，权限直接使用）
                    List<SimpleGrantedAuthority> authorities = Collections.emptyList();
                    if (permissions != null && !permissions.isEmpty()) {
                        authorities = permissions.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                    }
                    
                    // 5. 创建 Authentication 对象
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userId,  // principal: 用户ID
                                    null,    // credentials: 密码（不需要）
                                    authorities  // authorities: 权限列表
                            );
                    
                    // 6. 设置到 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("JWT 认证成功: userId={}, username={}, permissions={}", 
                            userId, username, permissions);
                }
            } catch (Exception e) {
                log.error("JWT 认证失败: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头解析 Token
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
