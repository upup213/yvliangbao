package com.yvliangbao.gateway.controller.common;


import com.yvliangbao.common.pojo.dto.payment.WechatPayRequest;
import com.yvliangbao.common.pojo.entity.order.OrderInfo;
import com.yvliangbao.common.pojo.entity.order.PaymentRecord;
import com.yvliangbao.common.pojo.enums.OrderStatus;
import com.yvliangbao.common.pojo.vo.payment.BalancePayVO;
import com.yvliangbao.common.pojo.vo.payment.PaymentCreateVO;
import com.yvliangbao.common.pojo.vo.payment.WechatPayVO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.order.OrderInfoService;
import com.yvliangbao.common.service.order.PaymentService;
import com.yvliangbao.common.service.order.WechatPayService;
import com.yvliangbao.common.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 * 每种支付方式独立接口，符合单一职责原则
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "支付接口")
@RestController
@RequestMapping("/pay")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private OrderInfoService orderInfoService;

    // ==================== 微信支付 ====================

    @ApiOperation("微信支付（发起支付）")
    @PostMapping("/wechat/{orderNo}")
    public Result<WechatPayVO> payByWechat(
            @PathVariable String orderNo,
            @RequestParam String openid) {

        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.failed("请先登录");
        }

        // 查询订单
        OrderInfo order = orderInfoService.getByOrderNo(orderNo, userId);
        if (order == null) {
            return Result.failed("订单不存在");
        }

        // 检查订单状态
        if (!OrderStatus.PENDING_PAYMENT.getCode().equals(order.getOrderStatus())) {
            String statusText = OrderStatus.getByCode(order.getOrderStatus()).getDesc();
            return Result.failed("订单状态异常，当前状态：" + statusText);
        }

        // 构建微信支付请求
        WechatPayRequest request = new WechatPayRequest();
        request.setOrderNo(orderNo);
        request.setAmount(order.getPayAmount());
        request.setOpenid(openid);
        request.setUserId(userId);
        request.setMerchantId(order.getMerchantId());
        request.setDescription("余量宝商品购买");

        WechatPayVO vo = wechatPayService.createPayment(request);
        return Result.success(vo);
    }

    // ==================== 余额支付 ====================

    @ApiOperation("余额支付（直接扣款）")
    @PostMapping("/balance/{orderNo}")
    public Result<BalancePayVO> payByBalance(@PathVariable String orderNo) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.failed("请先登录");
        }

        // 查询订单
        OrderInfo order = orderInfoService.getByOrderNo(orderNo, userId);
        if (order == null) {
            return Result.failed("订单不存在");
        }

        // 检查订单状态
        if (!OrderStatus.PENDING_PAYMENT.getCode().equals(order.getOrderStatus())) {
            String statusText = OrderStatus.getByCode(order.getOrderStatus()).getDesc();
            return Result.failed("订单状态异常，当前状态：" + statusText);
        }

        // 余额支付：直接完成
        orderInfoService.payOrder(orderNo, userId);

        return Result.success(BalancePayVO.success(orderNo, order.getPayAmount()));
    }

    // ==================== 模拟支付（测试用） ====================

    @ApiOperation("模拟支付（开发测试用）- 直接完成支付")
    @PostMapping("/mock/{orderNo}")
    public Result<PaymentCreateVO> payByMock(@PathVariable String orderNo) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.failed("请先登录");
        }

        // 查询订单
        OrderInfo order = orderInfoService.getByOrderNo(orderNo, userId);
        if (order == null) {
            return Result.failed("订单不存在");
        }

        // 检查订单状态
        if (!OrderStatus.PENDING_PAYMENT.getCode().equals(order.getOrderStatus())) {
            String statusText = OrderStatus.getByCode(order.getOrderStatus()).getDesc();
            return Result.failed("订单状态异常，当前状态：" + statusText);
        }

        // 模拟支付：直接完成订单支付（一步到位）
        orderInfoService.payOrder(orderNo, userId);

        log.info("模拟支付成功: orderNo={}, userId={}", orderNo, userId);

        // 返回成功结果
        PaymentCreateVO vo = new PaymentCreateVO();
        vo.setPaymentNo("MOCK_" + System.currentTimeMillis());
        vo.setOrderNo(orderNo);
        vo.setAmount(order.getPayAmount());
        vo.setStatus(1); // 支付成功
        vo.setSuccess(true);
        vo.setMessage("支付成功");

        return Result.success(vo);
    }

    // ==================== 查询支付状态 ====================

    @ApiOperation("查询支付状态")
    @GetMapping("/status/{orderNo}")
    public Result<PaymentRecord> queryPaymentStatus(@PathVariable String orderNo) {
        PaymentRecord record = paymentService.queryByOrderNo(orderNo);
        return Result.success(record);
    }
}
