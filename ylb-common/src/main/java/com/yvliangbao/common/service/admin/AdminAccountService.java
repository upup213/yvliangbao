package com.yvliangbao.common.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.dto.admin.AccountDTO;
import com.yvliangbao.common.pojo.entity.admin.AdminInfo;
import com.yvliangbao.common.pojo.vo.admin.AccountVO;


public interface AdminAccountService extends IService<AdminInfo> {
    IPage<AccountVO> pageList(Integer status, Long roleId, String keyword, int page, int size);
    AccountVO getDetail(Long id);
    Long createAccount(AccountDTO dto);
    void updateAccount(AccountDTO dto);
    void deleteAccount(Long id);
    void updateStatus(Long id, Integer status);
    void resetPassword(Long id, String newPassword);
    void assignRoles(Long id, String roleIds);
    AdminInfo getByUsername(String username);
}
