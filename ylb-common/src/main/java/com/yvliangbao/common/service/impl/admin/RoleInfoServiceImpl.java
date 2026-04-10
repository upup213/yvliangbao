package com.yvliangbao.common.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.mapper.admin.PermissionInfoMapper;
import com.yvliangbao.common.mapper.admin.RoleInfoMapper;
import com.yvliangbao.common.mapper.admin.RolePermissionMapper;
import com.yvliangbao.common.pojo.dto.admin.RoleDTO;
import com.yvliangbao.common.pojo.entity.admin.PermissionInfo;
import com.yvliangbao.common.pojo.entity.admin.RoleInfo;
import com.yvliangbao.common.pojo.vo.admin.PermissionVO;
import com.yvliangbao.common.pojo.vo.admin.RoleVO;
import com.yvliangbao.common.service.admin.RoleInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleInfoServiceImpl extends ServiceImpl<RoleInfoMapper, RoleInfo> implements RoleInfoService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionInfoMapper permissionInfoMapper;

    @Override
    public IPage<RoleVO> pageList(Integer status, String keyword, int page, int size) {
        Page<RoleInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<RoleInfo> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(RoleInfo::getStatus, status);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(RoleInfo::getRoleName, keyword)
                    .or().like(RoleInfo::getRoleCode, keyword));
        }
        wrapper.orderByDesc(RoleInfo::getCreateTime);
        IPage<RoleInfo> result = page(pageParam, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public RoleVO getDetail(Long id) {
        RoleInfo role = getById(id);
        if (role == null) return null;
        RoleVO vo = toVO(role);
        List<PermissionInfo> permissions = permissionInfoMapper.selectByRoleId(id);
        vo.setPermissions(permissions.stream().map(this::toPermissionVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional
    public Long createRole(RoleDTO dto) {
        RoleInfo role = new RoleInfo();
        BeanUtils.copyProperties(dto, role);
        role.setIsPreset(0);
        save(role);
        if (!CollectionUtils.isEmpty(dto.getPermissionIds())) {
            rolePermissionMapper.batchInsert(role.getId(), dto.getPermissionIds());
        }
        return role.getId();
    }

    @Override
    @Transactional
    public void updateRole(RoleDTO dto) {
        RoleInfo role = getById(dto.getId());
        if (role.getIsPreset() == 1) throw new RuntimeException("预设角色不可修改");
        BeanUtils.copyProperties(dto, role);
        updateById(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        RoleInfo role = getById(id);
        if (role.getIsPreset() == 1) throw new RuntimeException("预设角色不可删除");
        removeById(id);
        rolePermissionMapper.deleteByRoleId(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        RoleInfo role = getById(id);
        role.setStatus(status);
        updateById(role);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);
        if (!CollectionUtils.isEmpty(permissionIds)) {
            rolePermissionMapper.batchInsert(roleId, permissionIds);
        }
    }

    @Override
    public List<RoleInfo> getByAdminId(Long adminId) {
        return baseMapper.selectByAdminId(adminId);
    }

    private RoleVO toVO(RoleInfo entity) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private PermissionVO toPermissionVO(PermissionInfo entity) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
