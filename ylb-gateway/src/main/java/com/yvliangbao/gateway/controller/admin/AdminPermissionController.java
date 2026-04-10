package com.yvliangbao.gateway.controller.admin;


import com.yvliangbao.common.pojo.vo.admin.PermissionVO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.admin.PermissionInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "权限管理")
@RestController
@RequestMapping("/admin/permission")
public class AdminPermissionController {

    @Autowired
    private PermissionInfoService permissionInfoService;

    @ApiOperation("获取权限树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:role:view')")
    public Result<List<PermissionVO>> getTree() {
        return Result.success(permissionInfoService.getPermissionTree());
    }

    @ApiOperation("获取权限列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:view')")
    public Result<List<PermissionVO>> getList(@RequestParam(required = false) Integer permissionType) {
        return Result.success(permissionInfoService.getPermissionList(permissionType));
    }
}
