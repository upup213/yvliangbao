package com.yvliangbao.gateway.controller.admin;

import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.common.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 平台数据统计控制器
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "数据统计接口")
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("获取数据概览")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        log.info("获取数据概览");
        Map<String, Object> data = statisticsService.getOverview();
        return Result.success(data);
    }

    @ApiOperation("获取趋势数据")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getTrend(
            @ApiParam(value = "天数", defaultValue = "7")
            @RequestParam(value = "days", defaultValue = "7") int days) {
        log.info("获取趋势数据, days={}", days);
        List<Map<String, Object>> data = statisticsService.getTrend(days);
        return Result.success(data);
    }

    @ApiOperation("获取用户统计数据")
    @GetMapping("/users")
    public Result<Map<String, Object>> getUserStats() {
        log.info("获取用户统计数据");
        Map<String, Object> data = statisticsService.getUserStats();
        return Result.success(data);
    }

    @ApiOperation("获取商户统计数据")
    @GetMapping("/merchants")
    public Result<Map<String, Object>> getMerchantStats() {
        log.info("获取商户统计数据");
        Map<String, Object> data = statisticsService.getMerchantStats();
        return Result.success(data);
    }

    @ApiOperation("获取区域统计数据")
    @GetMapping("/regions")
    public Result<List<Map<String, Object>>> getRegionStats() {
        log.info("获取区域统计数据");
        List<Map<String, Object>> data = statisticsService.getRegionStats();
        return Result.success(data);
    }

    @ApiOperation("获取GMV统计数据")
    @GetMapping("/gmv")
    public Result<Map<String, Object>> getGmvStats() {
        log.info("获取GMV统计数据");
        Map<String, Object> data = statisticsService.getGmvStats();
        return Result.success(data);
    }
}
