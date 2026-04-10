package com.yvliangbao.gateway.controller.admin;


import com.yvliangbao.common.pojo.entity.admin.BannerInfo;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.admin.BannerInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图管理接口（管理后台）
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "轮播图管理")
@RestController
@RequestMapping("/admin/banner")
public class AdminBannerController {

    @Autowired
    private BannerInfoService bannerInfoService;

    @ApiOperation("获取轮播图列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('banner:view')")
    public Result<List<BannerInfo>> list() {
        return Result.success(bannerInfoService.listAll());
    }

    @ApiOperation("创建轮播图")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('banner:add')")
    public Result<Long> create(@RequestBody BannerInfo banner) {
        return Result.success(bannerInfoService.createBanner(banner));
    }

    @ApiOperation("更新轮播图")
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('banner:edit')")
    public Result<Void> update(@RequestBody BannerInfo banner) {
        bannerInfoService.updateBanner(banner);
        return Result.success();
    }

    @ApiOperation("删除轮播图")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('banner:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        bannerInfoService.deleteBanner(id);
        return Result.success();
    }

    @ApiOperation("更新轮播图状态")
    @PutMapping("/status/{id}")
    @PreAuthorize("hasAuthority('banner:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        bannerInfoService.updateStatus(id, status);
        return Result.success();
    }
}
