package com.yvliangbao.gateway.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yvliangbao.common.pojo.dto.user.UserInfoVO;
import com.yvliangbao.common.pojo.entity.user.UserInfo;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.user.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 平台端用户管理控制器
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "平台端-用户管理")
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 用户列表（分页）
     */
    @ApiOperation("用户列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('user:list') or hasAuthority('*:*')")
    public Result<IPage<UserInfoVO>> getUserList(
            @ApiParam(value = "手机号") @RequestParam(required = false) String phone,
            @ApiParam(value = "昵称") @RequestParam(required = false) String nickname,
            @ApiParam(value = "状态：1-正常，0-禁用") @RequestParam(required = false) Integer status,
            @ApiParam(value = "当前页码") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页数量") @RequestParam(defaultValue = "10") int size) {

        Page<UserInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();

        if (phone != null && !phone.isEmpty()) {
            wrapper.like(UserInfo::getPhone, phone);
        }
        if (nickname != null && !nickname.isEmpty()) {
            wrapper.like(UserInfo::getNickname, nickname);
        }
        if (status != null) {
            wrapper.eq(UserInfo::getStatus, status);
        }
        wrapper.orderByDesc(UserInfo::getCreateTime);

        IPage<UserInfo> userPage = userInfoService.page(pageParam, wrapper);

        // 转换为VO
        IPage<UserInfoVO> voPage = userPage.convert(user -> {
            UserInfoVO vo = new UserInfoVO();
            vo.setId(user.getId());
            vo.setPhone(user.getPhone());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
            vo.setBalance(user.getBalance());
            vo.setSavedFoodWeight(user.getSavedFoodWeight());
            vo.setCarbonReduction(user.getCarbonReduction());
            vo.setStatus(user.getStatus());
            vo.setCreateTime(user.getCreateTime() != null ? user.getCreateTime().toString() : null);
            return vo;
        });

        return Result.success(voPage);
    }

    /**
     * 禁用用户
     */
    @ApiOperation("禁用用户")
    @PostMapping("/disable/{userId}")
    public Result<Void> disableUser(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        UserInfo user = userInfoService.getById(userId);
        if (user != null) {
            user.setStatus(0);
            userInfoService.updateById(user);
            log.info("禁用用户: userId={}", userId);
        }
        return Result.success();
    }

    /**
     * 启用用户
     */
    @ApiOperation("启用用户")
    @PostMapping("/enable/{userId}")
    public Result<Void> enableUser(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        UserInfo user = userInfoService.getById(userId);
        if (user != null) {
            user.setStatus(1);
            userInfoService.updateById(user);
            log.info("启用用户: userId={}", userId);
        }
        return Result.success();
    }
}
