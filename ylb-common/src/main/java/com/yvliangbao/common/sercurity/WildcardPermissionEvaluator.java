package com.yvliangbao.common.sercurity;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义权限评估器，支持通配符权限匹配
 * 
 * 权限格式：module:action 或 *:*
 * - *:* 匹配所有权限
 * - system:* 匹配 system 模块下的所有权限
 * - *:view 匹配所有模块的 view 权限
 */
@Component
public class WildcardPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return hasPermission(authentication, permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, permission);
    }

    private boolean hasPermission(Authentication authentication, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }

        String requiredPermission = permission.toString();

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String userPermission = authority.getAuthority();
            
            // 完全匹配
            if (userPermission.equals(requiredPermission)) {
                return true;
            }
            
            // 通配符匹配
            if (matchWildcard(userPermission, requiredPermission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 通配符权限匹配
     * 
     * @param pattern 用户拥有的权限模式（可能包含 *）
     * @param permission 需要检查的权限
     * @return 是否匹配
     */
    private boolean matchWildcard(String pattern, String permission) {
        // *:* 匹配所有权限
        if ("*:*".equals(pattern)) {
            return true;
        }

        String[] patternParts = pattern.split(":");
        String[] permissionParts = permission.split(":");

        // 权限格式不一致
        if (patternParts.length != permissionParts.length) {
            return false;
        }

        // 逐段匹配
        for (int i = 0; i < patternParts.length; i++) {
            String patternPart = patternParts[i];
            String permissionPart = permissionParts[i];

            // 通配符匹配任意值
            if ("*".equals(patternPart)) {
                continue;
            }

            // 精确匹配
            if (!patternPart.equals(permissionPart)) {
                return false;
            }
        }

        return true;
    }
}
