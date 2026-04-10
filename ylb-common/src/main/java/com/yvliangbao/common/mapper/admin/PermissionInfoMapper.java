package com.yvliangbao.common.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.admin.PermissionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface PermissionInfoMapper extends BaseMapper<PermissionInfo> {

    /**
     * 根据角色ID查询权限列表
     */
    List<PermissionInfo> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询所有启用的权限（树形结构）
     */
    List<PermissionInfo> selectAllEnabled();

    /**
     * 根据管理员ID查询权限列表
     */
    List<PermissionInfo> selectByAdminId(@Param("adminId") Long adminId);
}
