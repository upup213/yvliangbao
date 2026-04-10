package com.yvliangbao.gateway.controller.user;

import com.yvliangbao.common.pojo.dto.user.UserLoginDTO;
import com.yvliangbao.common.pojo.dto.user.UserInfoVO;
import com.yvliangbao.common.pojo.dto.user.UserUpdateDTO;
import com.yvliangbao.common.pojo.vo.user.UserStatsVO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.user.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端控制器
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "用户端-用户接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation("微信授权登录")
    @PostMapping("/login/wechat")
    public Result<UserInfoVO> wechatLogin(@Validated @RequestBody UserLoginDTO dto) {
        UserInfoVO vo = userInfoService.wechatLogin(dto);
        return Result.success(vo);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        UserInfoVO vo = userInfoService.getCurrentUserInfo();
        return Result.success(vo);
    }

    @ApiOperation("获取用户统计数据")
    @GetMapping("/stats")
    public Result<UserStatsVO> getUserStats() {
        UserStatsVO vo = userInfoService.getUserStats();
        return Result.success(vo);
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/update")
    public Result<UserInfoVO> updateUser(@Validated @RequestBody UserUpdateDTO dto) {
        UserInfoVO vo = userInfoService.updateUserInfo(dto);
        return Result.success(vo);
    }
}
