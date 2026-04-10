package com.yvliangbao.gateway.controller.user;

import com.yvliangbao.common.pojo.dto.order.OrderCreateDTO;
import com.yvliangbao.common.pojo.entity.order.OrderInfo;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.order.OrderInfoService;
import com.yvliangbao.common.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端订单控制器
 *
 * 支付相关接口请直接调用 PaymentController:
 * - 微信支付: POST /api/pay/wechat/{orderNo}?openid=xxx
 * - 余额支付: POST /api/pay/balance/{orderNo}
 * - 模拟支付: POST /api/pay/mock/{orderNo}
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "用户端-订单接口")
@RestController
@RequestMapping("/order")
public class UserOrderController {

    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation("创建订单（下单）")
    @PostMapping("/create")
    @Transactional
    public Result<OrderInfo> createOrder(@Validated @RequestBody OrderCreateDTO dto) {
        Long userId = UserContext.getUserId();
        OrderInfo order = orderInfoService.createOrder(dto, userId);
        return Result.success(order);
    }

    @ApiOperation("我的订单")
    @GetMapping("/my")
    public Result<Map<String, Object>> myOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = UserContext.getUserId();

        List<OrderInfo> orders = orderInfoService.getMyOrders(userId, status, keyword, page, size);
        long total = orderInfoService.countMyOrders(userId, status, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("list", orders);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return Result.success(result);
    }

    @ApiOperation("订单详情")
    @GetMapping("/detail/{orderNo}")
    public Result<OrderInfo> getOrderDetail(@PathVariable String orderNo) {
        Long userId = UserContext.getUserId();
        OrderInfo order = orderInfoService.getByOrderNo(orderNo, userId);
        return Result.success(order);
    }

    @ApiOperation("取消订单")
    @PostMapping("/cancel/{orderNo}")
    public Result<OrderInfo> cancelOrder(@PathVariable String orderNo) {
        Long userId = UserContext.getUserId();
        OrderInfo order = orderInfoService.cancelOrder(orderNo, userId);
        return Result.success(order);
    }

    @ApiOperation("申请退款（已支付/待取餐→退款中）")
    @PostMapping("/refund/{orderNo}")
    public Result<OrderInfo> applyRefund(
            @PathVariable String orderNo,
            @RequestParam(required = false, defaultValue = "用户申请退款") String reason) {
        Long userId = UserContext.getUserId();
        OrderInfo order = orderInfoService.applyRefund(orderNo, userId, reason);
        return Result.success(order);
    }
}
