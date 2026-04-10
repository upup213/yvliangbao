package com.yvliangbao.common.service.admin;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.entity.admin.PermissionInfo;
import com.yvliangbao.common.pojo.vo.admin.PermissionVO;


import java.util.List;

public interface PermissionInfoService extends IService<PermissionInfo> {
    List<PermissionVO> getPermissionTree();
    List<PermissionVO> getPermissionList(Integer permissionType);
    List<PermissionInfo> getByRoleId(Long roleId);
    List<PermissionInfo> getByAdminId(Long adminId);
    List<String> getPermissionCodesByAdminId(Long adminId);
}
