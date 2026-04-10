package com.yvliangbao.common.service.impl.common;


import com.yvliangbao.common.service.admin.PermissionInfoService;
import com.yvliangbao.common.service.common.PermissionCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 权限缓存服务实现
 *
 * @author 余量宝
 */
@Service
@RequiredArgsConstructor
public class PermissionCacheServiceImpl implements PermissionCacheService {

    private static final String CACHE_PREFIX = "permission:admin:";
    private static final long CACHE_TTL = 2; // 2小时

    private final RedisTemplate<String, Object> redisTemplate;
    private final PermissionInfoService permissionInfoService;

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getPermissions(Long adminId) {
        String key = CACHE_PREFIX + adminId + ":permissions";
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return new HashSet<>((List<String>) cached);
        }
        List<String> permissions = permissionInfoService.getPermissionCodesByAdminId(adminId);
        Set<String> permSet = new HashSet<>(permissions);
        redisTemplate.opsForValue().set(key, new ArrayList<>(permSet), CACHE_TTL, TimeUnit.HOURS);
        return permSet;
    }

    @Override
    public void invalidateCache(Long adminId) {
        String key = CACHE_PREFIX + adminId + ":permissions";
        redisTemplate.delete(key);
    }

    @Override
    public boolean hasPermission(Long adminId, String permission) {
        Set<String> permissions = getPermissions(adminId);
        if (permissions.contains("*:*")) return true; // 超级管理员
        return permissions.contains(permission);
    }
}
