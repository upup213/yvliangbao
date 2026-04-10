package com.yvliangbao.gateway.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yvliangbao.common.pojo.dto.admin.AccountDTO;
import com.yvliangbao.common.pojo.vo.admin.AccountVO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.admin.AdminAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "账号管理")
@RestController
@RequestMapping("/admin/account")
public class AdminAccountController {

    @Autowired
    private AdminAccountService adminAccountService;

    @ApiOperation("获取账号列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:account:view')")
    public Result<IPage<AccountVO>> list(@RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) Long roleId,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminAccountService.pageList(status, roleId, keyword, page, size));
    }

    @ApiOperation("获取账号详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:account:view')")
    public Result<AccountVO> getDetail(@PathVariable Long id) {
        return Result.success(adminAccountService.getDetail(id));
    }

    @ApiOperation("创建账号")
    @PostMapping
    @PreAuthorize("hasAuthority('system:account:add')")
    public Result<Long> create(@RequestBody AccountDTO dto) {
        return Result.success(adminAccountService.createAccount(dto));
    }

    @ApiOperation("更新账号")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:account:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody AccountDTO dto) {
        dto.setId(id);
        adminAccountService.updateAccount(dto);
        return Result.success();
    }

    @ApiOperation("删除账号")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:account:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        adminAccountService.deleteAccount(id);
        return Result.success();
    }

    @ApiOperation("更新账号状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:account:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminAccountService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("重置密码")
    @PutMapping("/{id}/reset-pwd")
    @PreAuthorize("hasAuthority('system:account:edit')")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        adminAccountService.resetPassword(id, body.get("newPassword"));
        return Result.success();
    }

    @ApiOperation("分配角色")
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:account:edit')")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String, String> body) {
        adminAccountService.assignRoles(id, body.get("roleIds"));
        return Result.success();
    }
}
