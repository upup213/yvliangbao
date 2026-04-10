package com.yvliangbao.gateway.controller.user;


import com.yvliangbao.common.pojo.entity.admin.BannerInfo;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.admin.BannerInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 轮播图接口（用户端）
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "用户端-轮播图接口")
@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerInfoService bannerInfoService;

    @ApiOperation("获取轮播图列表")
    @GetMapping("/list")
    public Result<List<BannerInfo>> list() {
        List<BannerInfo> list = bannerInfoService.listEnabled();
        return Result.success(list);
    }
}
