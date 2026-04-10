package com.yvliangbao.common.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.mapper.admin.AdminInfoMapper;
import com.yvliangbao.common.pojo.dto.admin.AccountDTO;
import com.yvliangbao.common.pojo.entity.admin.AdminInfo;
import com.yvliangbao.common.pojo.entity.admin.RoleInfo;
import com.yvliangbao.common.pojo.vo.admin.AccountVO;
import com.yvliangbao.common.pojo.vo.admin.RoleVO;
import com.yvliangbao.common.service.admin.AdminAccountService;
import com.yvliangbao.common.service.admin.RoleInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminAccountServiceImpl extends ServiceImpl<AdminInfoMapper, AdminInfo> implements AdminAccountService {

    @Autowired
    private RoleInfoService roleInfoService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public IPage<AccountVO> pageList(Integer status, Long roleId, String keyword, int page, int size) {
        Page<AdminInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AdminInfo> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(AdminInfo::getStatus, status);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(AdminInfo::getUsername, keyword)
                    .or().like(AdminInfo::getRealName, keyword)
                    .or().like(AdminInfo::getPhone, keyword));
        }
        wrapper.orderByDesc(AdminInfo::getCreateTime);
        IPage<AdminInfo> result = page(pageParam, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public AccountVO getDetail(Long id) {
        AdminInfo admin = getById(id);
        if (admin == null) return null;
        AccountVO vo = toVO(admin);
        List<RoleInfo> roles = roleInfoService.getByAdminId(id);
        vo.setRoles(roles.stream().map(this::toRoleVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public Long createAccount(AccountDTO dto) {
        AdminInfo admin = new AdminInfo();
        BeanUtils.copyProperties(dto, admin);
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setPasswordUpdateTime(LocalDateTime.now());
        admin.setAdminNo("ADM" + System.currentTimeMillis());
        admin.setRoleId(Long.valueOf(dto.getRoleIds().split(",")[0]));
        save(admin);
        return admin.getId();
    }

    @Override
    public void updateAccount(AccountDTO dto) {
        AdminInfo admin = getById(dto.getId());
        BeanUtils.copyProperties(dto, admin, "password", "username");
        updateById(admin);
    }

    @Override
    public void deleteAccount(Long id) {
        AdminInfo admin = getById(id);
        if (admin.getRoleId() == 1L) throw new RuntimeException("超级管理员账号不可删除");
        removeById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        AdminInfo admin = getById(id);
        admin.setStatus(status);
        updateById(admin);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        AdminInfo admin = getById(id);
        admin.setPassword(passwordEncoder.encode(newPassword));
        admin.setPasswordUpdateTime(LocalDateTime.now());
        updateById(admin);
    }

    @Override
    public void assignRoles(Long id, String roleIds) {
        AdminInfo admin = getById(id);
        admin.setRoleIds(roleIds);
        updateById(admin);
    }

    @Override
    public AdminInfo getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<AdminInfo>().eq(AdminInfo::getUsername, username));
    }

    private AccountVO toVO(AdminInfo entity) {
        AccountVO vo = new AccountVO();
        BeanUtils.copyProperties(entity, vo);
        List<RoleInfo> roles = roleInfoService.getByAdminId(entity.getId());
        vo.setRoleNames(roles.stream().map(RoleInfo::getRoleName).collect(Collectors.joining(",")));
        return vo;
    }

    private RoleVO toRoleVO(RoleInfo entity) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
