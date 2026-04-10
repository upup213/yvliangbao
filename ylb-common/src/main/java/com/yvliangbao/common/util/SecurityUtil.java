package com.yvliangbao.common.util;


import com.yvliangbao.common.exception.AuthException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 用于获取当前登录用户信息
 *
 * @author 余量宝
 */
public class SecurityUtil {

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     * @throws AuthException 如果用户未登录
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException("用户未登录");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        
        // 如果 principal 是字符串（比如用户名），尝试解析
        if (principal instanceof String) {
            try {
                return Long.parseLong((String) principal);
            } catch (NumberFormatException e) {
                throw new AuthException("无法获取用户ID");
            }
        }
        
        throw new AuthException("无法获取用户ID");
    }

    /**
     * 获取当前登录用户ID（可能为null）
     *
     * @return 用户ID，未登录返回null
     */
    public static Long getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (AuthException e) {
            return null;
        }
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     * @throws AuthException 如果用户未登录
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException("用户未登录");
        }
        return authentication.getName();
    }

    /**
     * 判断当前用户是否已登录
     *
     * @return 是否已登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
