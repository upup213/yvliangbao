package com.yvliangbao.common.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.admin.RoleInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface RoleInfoMapper extends BaseMapper<RoleInfo> {

    /**
     * 根据管理员ID查询角色列表
     */
    List<RoleInfo> selectByAdminId(@Param("adminId") Long adminId);

    /**
     * 查询角色的权限ID列表
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
