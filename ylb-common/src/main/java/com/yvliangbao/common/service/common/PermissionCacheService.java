package com.yvliangbao.common.service.common;

import java.util.Set;

/**
 * 权限缓存服务接口
 *
 * @author 余量宝
 */
public interface PermissionCacheService {

    /**
     * 获取用户权限列表（带缓存）
     *
     * @param adminId 管理员ID
     * @return 权限代码集合
     */
    Set<String> getPermissions(Long adminId);

    /**
     * 使权限缓存失效
     *
     * @param adminId 管理员ID
     */
    void invalidateCache(Long adminId);

    /**
     * 检查用户是否拥有指定权限
     *
     * @param adminId 管理员ID
     * @param permission 权限代码
     * @return 是否拥有权限
     */
    boolean hasPermission(Long adminId, String permission);
}
