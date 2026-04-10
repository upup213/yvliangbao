package com.yvliangbao.gateway.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yvliangbao.common.mapper.order.OrderInfoMapper;
import com.yvliangbao.common.pojo.entity.order.OrderInfo;
import com.yvliangbao.common.pojo.vo.order.OrderAdminVO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.order.OrderInfoService;
import com.yvliangbao.common.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 平台端订单管理控制器
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "平台端订单管理接口")
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderInfoService orderInfoService;
    
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    /**
     * 订单列表（多维度筛选）
     */
    @ApiOperation("订单列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('order:list') or hasAuthority('*:*')")
    public Result<Map<String, Object>> orderList(
            @RequestParam(required = false) String orderNo,      // 订单号
            @RequestParam(required = false) String phone,        // 用户手机号
            @RequestParam(required = false) Long merchantId,    // 商户ID
            @RequestParam(required = false) Integer status,      // 订单状态
            @RequestParam(required = false) String startDate,    // 开始日期
            @RequestParam(required = false) String endDate,      // 结束日期
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<OrderAdminVO> pageParam = new Page<>(page, size);
        IPage<OrderAdminVO> orders = orderInfoMapper.selectAdminOrderList(
                (org.springframework.data.domain.Page<OrderAdminVO>) pageParam, orderNo, phone, merchantId, status, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("records", orders.getRecords());
        result.put("total", orders.getTotal());
        result.put("page", page);
        result.put("size", size);

        return Result.success(result);
    }

    /**
     * 订单详情
     */
    @ApiOperation("订单详情")
    @GetMapping("/{orderNo}")
    @PreAuthorize("hasAuthority('order:list:view') or hasAuthority('*:*')")
    public Result<OrderAdminVO> orderDetail(@PathVariable String orderNo) {
        OrderAdminVO order = orderInfoMapper.selectAdminOrderDetail(orderNo);
        if (order == null) {
            return Result.failed(404, "订单不存在");
        }
        return Result.success(order);
    }

    /**
     * 强制退款
     */
    @ApiOperation("强制退款")
    @PostMapping("/refund/{orderNo}")
    @PreAuthorize("hasAuthority('order:list:refund') or hasAuthority('*:*')")
    public Result<OrderInfo> forceRefund(
            @PathVariable String orderNo,
            @RequestParam(required = false, defaultValue = "平台强制退款") String reason) {

        Long adminId = UserContext.getUserId();
        log.info("管理员[{}]强制退款订单: {}, 原因: {}", adminId, orderNo, reason);

        OrderInfo order = orderInfoService.getByOrderNo(orderNo);
        if (order == null) {
            return Result.failed(404, "订单不存在");
        }

        // 只有已支付、待取餐、退款中状态可以强制退款
        // 订单状态：0-待支付，1-已支付，2-待取餐，3-已完成，4-已取消，5-已退款，6-退款中
        if (order.getOrderStatus() != 1 && order.getOrderStatus() != 2 && order.getOrderStatus() != 6) {
            return Result.failed(400, "当前订单状态不允许退款");
        }

        // 直接执行退款逻辑
        OrderInfo refundedOrder = orderInfoService.adminForceRefund(orderNo, reason);

        return Result.success(refundedOrder);
    }

    /**
     * 强制取消
     */
    @ApiOperation("强制取消")
    @PostMapping("/cancel/{orderNo}")
    @PreAuthorize("hasAuthority('order:list:cancel') or hasAuthority('*:*')")
    public Result<OrderInfo> forceCancel(
            @PathVariable String orderNo,
            @RequestParam(required = false, defaultValue = "平台强制取消") String reason) {

        Long adminId = UserContext.getUserId();
        log.info("管理员[{}]强制取消订单: {}, 原因: {}", adminId, orderNo, reason);

        OrderInfo order = orderInfoService.getByOrderNo(orderNo);
        if (order == null) {
            return Result.failed(404, "订单不存在");
        }

        // 只有待支付状态可以强制取消
        // 订单状态：0-待支付，1-已支付，2-待取餐，3-已完成，4-已取消，5-已退款，6-退款中
        if (order.getOrderStatus() != 0) {
            return Result.failed(400, "当前订单状态不允许取消");
        }

        // 执行取消逻辑
        OrderInfo cancelledOrder = orderInfoService.adminForceCancel(orderNo, reason);

        return Result.success(cancelledOrder);
    }

    /**
     * 订单统计概览
     */
    @ApiOperation("订单统计概览")
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('order:list') or hasAuthority('*:*')")
    public Result<Map<String, Object>> orderStats() {
        Map<String, Object> stats = new HashMap<>();

        // 各状态订单数量
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getDeleted, 0);

        // 总订单数
        stats.put("total", orderInfoMapper.selectCount(wrapper));

        // 待支付（状态0）
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOrderStatus, 0).eq(OrderInfo::getDeleted, 0);
        stats.put("pending", orderInfoMapper.selectCount(wrapper));

        // 已支付（状态1）
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOrderStatus, 1).eq(OrderInfo::getDeleted, 0);
        stats.put("paid", orderInfoMapper.selectCount(wrapper));

        // 待取餐（状态2）
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOrderStatus, 2).eq(OrderInfo::getDeleted, 0);
        stats.put("ready", orderInfoMapper.selectCount(wrapper));

        // 已完成（状态3）
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOrderStatus, 3).eq(OrderInfo::getDeleted, 0);
        stats.put("completed", orderInfoMapper.selectCount(wrapper));

        // 已取消（状态4）
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOrderStatus, 4).eq(OrderInfo::getDeleted, 0);
        stats.put("cancelled", orderInfoMapper.selectCount(wrapper));

        // 退款中（状态6）
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOrderStatus, 6).eq(OrderInfo::getDeleted, 0);
        stats.put("refunding", orderInfoMapper.selectCount(wrapper));

        // 已退款（状态5）
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOrderStatus, 5).eq(OrderInfo::getDeleted, 0);
        stats.put("refunded", orderInfoMapper.selectCount(wrapper));

        return Result.success(stats);
    }
}
