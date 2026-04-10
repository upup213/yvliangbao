package com.yvliangbao.common.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.dto.admin.RoleDTO;
import com.yvliangbao.common.pojo.entity.admin.RoleInfo;
import com.yvliangbao.common.pojo.vo.admin.RoleVO;


import java.util.List;

public interface RoleInfoService extends IService<RoleInfo> {
    IPage<RoleVO> pageList(Integer status, String keyword, int page, int size);
    RoleVO getDetail(Long id);
    Long createRole(RoleDTO dto);
    void updateRole(RoleDTO dto);
    void deleteRole(Long id);
    void updateStatus(Long id, Integer status);
    void assignPermissions(Long roleId, List<Long> permissionIds);
    List<RoleInfo> getByAdminId(Long adminId);
}
