package com.yvliangbao.common.config;


import com.yvliangbao.common.util.JwtUtil;
import com.yvliangbao.common.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
 *
 * @author 余量宝
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 从请求头获取 token
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthException("请先登录");
        }

        String token = authHeader.replace("Bearer ", "");

        // 2. 解析 token（失败会抛出异常）
        Long userId = JwtUtil.getUserIdOrThrow(token);
        String openid = JwtUtil.getOpenidOrThrow(token);

        // 3. 存入上下文
        UserContext.setUserId(userId);
        UserContext.setOpenid(openid);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                  Object handler, Exception ex) {
        // 请求结束后清除上下文，防止内存泄漏
        UserContext.clear();
    }
}
