package com.yvliangbao.gateway.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yvliangbao.common.pojo.dto.admin.RoleDTO;
import com.yvliangbao.common.pojo.vo.admin.RoleVO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.admin.RoleInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/role")
public class AdminRoleController {

    @Autowired
    private RoleInfoService roleInfoService;

    @ApiOperation("获取角色列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:view')")
    public Result<IPage<RoleVO>> list(@RequestParam(required = false) Integer status,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return Result.success(roleInfoService.pageList(status, keyword, page, size));
    }

    @ApiOperation("获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:view')")
    public Result<RoleVO> getDetail(@PathVariable Long id) {
        return Result.success(roleInfoService.getDetail(id));
    }

    @ApiOperation("创建角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Long> create(@RequestBody RoleDTO dto) {
        return Result.success(roleInfoService.createRole(dto));
    }

    @ApiOperation("更新角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody RoleDTO dto) {
        dto.setId(id);
        roleInfoService.updateRole(dto);
        return Result.success();
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        roleInfoService.deleteRole(id);
        return Result.success();
    }

    @ApiOperation("更新角色状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        roleInfoService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("分配权限")
    @PutMapping("/{id}/perms")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permIds) {
        roleInfoService.assignPermissions(id, permIds);
        return Result.success();
    }
}
