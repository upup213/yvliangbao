package com.yvliangbao.common.sercurity;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * 自定义安全表达式根类
 * 支持 hasWildcardAuthority 的通配符匹配
 */
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    /**
     * 支持通配符的权限检查
     */
    public boolean hasWildcardAuthority(String requiredPermission) {
        if (this.getAuthentication() == null) {
            return false;
        }

        for (GrantedAuthority authority : this.getAuthentication().getAuthorities()) {
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
     * 支持通配符的多权限检查
     */
    public boolean hasAnyWildcardAuthority(String... authorities) {
        for (String authority : authorities) {
            if (hasWildcardAuthority(authority)) {
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

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    public void setThis(Object target) {
        this.target = target;
    }
}
