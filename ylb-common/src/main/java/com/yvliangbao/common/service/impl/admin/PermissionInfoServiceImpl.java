package com.yvliangbao.common.service.impl.admin;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yvliangbao.common.mapper.admin.PermissionInfoMapper;
import com.yvliangbao.common.pojo.entity.admin.PermissionInfo;
import com.yvliangbao.common.pojo.vo.admin.PermissionVO;
import com.yvliangbao.common.service.admin.PermissionInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionInfoServiceImpl extends ServiceImpl<PermissionInfoMapper, PermissionInfo> implements PermissionInfoService {

    @Override
    public List<PermissionVO> getPermissionTree() {
        List<PermissionInfo> all = baseMapper.selectAllEnabled();
        List<PermissionVO> voList = all.stream().map(this::toVO).collect(Collectors.toList());
        return buildTree(voList, null);
    }

    @Override
    public List<PermissionVO> getPermissionList(Integer permissionType) {
        List<PermissionInfo> list = baseMapper.selectAllEnabled();
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<PermissionInfo> getByRoleId(Long roleId) {
        return baseMapper.selectByRoleId(roleId);
    }

    @Override
    public List<PermissionInfo> getByAdminId(Long adminId) {
        return baseMapper.selectByAdminId(adminId);
    }

    @Override
    public List<String> getPermissionCodesByAdminId(Long adminId) {
        List<PermissionInfo> permissions = getByAdminId(adminId);
        return permissions.stream().map(PermissionInfo::getPermissionCode).collect(Collectors.toList());
    }

    private List<PermissionVO> buildTree(List<PermissionVO> all, Long parentId) {
        List<PermissionVO> tree = new ArrayList<>();
        for (PermissionVO vo : all) {
            // 顶级节点的 parentId 为 null 或 0
            boolean isRoot = parentId == null && (vo.getParentId() == null || vo.getParentId() == 0L);
            boolean isChild = parentId != null && parentId.equals(vo.getParentId());
            
            if (isRoot || isChild) {
                vo.setChildren(buildTree(all, vo.getId()));
                tree.add(vo);
            }
        }
        return tree;
    }

    private PermissionVO toVO(PermissionInfo entity) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
