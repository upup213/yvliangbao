package com.yvliangbao.gateway.controller.admin;

import com.yvliangbao.common.mapper.admin.PermissionInfoMapper;
import com.yvliangbao.common.pojo.entity.admin.AdminInfo;
import com.yvliangbao.common.pojo.entity.admin.PermissionInfo;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.admin.AdminInfoService;
import com.yvliangbao.common.service.admin.BannerInfoService;
import com.yvliangbao.common.service.admin.RoleInfoService;
import com.yvliangbao.common.util.JwtUtil;
import com.yvliangbao.common.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 平台管理控制器
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "平台管理接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminInfoService adminInfoService;

    @Autowired
    private BannerInfoService bannerInfoService;

    @Autowired
    private RoleInfoService roleInfoService;

    @Autowired
    private PermissionInfoMapper permissionInfoMapper;

    /**
     * 管理员登录
     */
    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        AdminInfo admin = adminInfoService.login(username, password);

        // 查询用户角色
        List<RoleInfo> roles = roleInfoService.getByAdminId(admin.getId());
        List<String> roleCodes = roles.stream()
                .map(RoleInfo::getRoleCode)
                .collect(Collectors.toList());

        // 查询用户权限
        List<PermissionInfo> permissions = permissionInfoMapper.selectByAdminId(admin.getId());
        List<String> permCodes = permissions.stream()
                .map(PermissionInfo::getPermissionCode)
                .distinct()
                .collect(Collectors.toList());

        // 如果有 *:* 权限，加载所有权限（支持通配符匹配）
        if (permCodes.contains("*:*")) {
            List<PermissionInfo> allPermissions = permissionInfoMapper.selectAllEnabled();
            permCodes = allPermissions.stream()
                    .map(PermissionInfo::getPermissionCode)
                    .distinct()
                    .collect(Collectors.toList());
            // 保留 *:* 用于前端判断超级管理员
            permCodes.add(0, "*:*");
        }

        // 生成包含权限的 Token (平台类型=3)
        String token = JwtUtil.generateTokenWithPermissions(
                admin.getId(), 
                admin.getUsername(), 
                roleCodes, 
                permCodes,
                3
        );

        // 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("admin", admin);
        data.put("roles", roleCodes);
        data.put("permissions", permCodes);

        return Result.success(data);
    }

    /**
     * 获取管理员信息
     */
    @ApiOperation("获取管理员信息")
    @GetMapping("/info")
    public Result<AdminInfo> getAdminInfo() {
        Long adminId = UserContext.getUserId();
        AdminInfo admin = adminInfoService.getById(adminId);
        admin.setPassword(null); // 不返回密码
        return Result.success(admin);
    }
}
