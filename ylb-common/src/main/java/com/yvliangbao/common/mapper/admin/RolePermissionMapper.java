package com.yvliangbao.common.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.admin.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色权限关联Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 删除角色的所有权限
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限
     */
    int batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") java.util.List<Long> permissionIds);
}
