package com.yvliangbao.common.sercurity;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 通配符权限投票器
 * 支持 *:* 格式的通配符权限匹配
 */
@Component
public class WildcardPermissionVoter implements AccessDecisionVoter<Object> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute.getAttribute() != null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            return ACCESS_DENIED;
        }

        int result = ACCESS_ABSTAIN;
        
        for (ConfigAttribute attribute : attributes) {
            String requiredPermission = attribute.getAttribute();
            
            if (requiredPermission == null) {
                continue;
            }

            // 提取 hasAuthority 或 hasAnyPermission 中的权限字符串
            if (requiredPermission.startsWith("hasAuthority('") || requiredPermission.startsWith("hasPermission(")) {
                String permission = extractPermission(requiredPermission);
                if (permission != null && hasWildcardMatch(authentication, permission)) {
                    return ACCESS_GRANTED;
                }
            }
        }

        return result;
    }

    /**
     * 从表达式中提取权限字符串
     */
    private String extractPermission(String expression) {
        // hasAuthority('xxx') -> xxx
        int start = expression.indexOf("'");
        int end = expression.lastIndexOf("'");
        if (start != -1 && end != -1 && end > start) {
            return expression.substring(start + 1, end);
        }
        return null;
    }

    /**
     * 检查用户是否拥有匹配的通配符权限
     */
    private boolean hasWildcardMatch(Authentication authentication, String requiredPermission) {
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
     */
    private boolean matchWildcard(String pattern, String permission) {
        // *:* 匹配所有权限
        if ("*:*".equals(pattern)) {
            return true;
        }

        String[] patternParts = pattern.split(":");
        String[] permissionParts = permission.split(":");

        if (patternParts.length != permissionParts.length) {
            return false;
        }

        for (int i = 0; i < patternParts.length; i++) {
            if ("*".equals(patternParts[i])) {
                continue;
            }
            if (!patternParts[i].equals(permissionParts[i])) {
                return false;
            }
        }

        return true;
    }
}
