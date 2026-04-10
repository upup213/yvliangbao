package com.yvliangbao.gateway.controller.common;

import com.yvliangbao.common.pojo.dto.payment.MockPaymentNotifyDTO;
import com.yvliangbao.common.pojo.dto.payment.PaymentCreateRequest;
import com.yvliangbao.common.pojo.entity.order.PaymentRecord;
import com.yvliangbao.common.pojo.enums.PaymentStatus;
import com.yvliangbao.common.pojo.vo.payment.PaymentCreateVO;
import com.yvliangbao.common.service.order.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟支付控制器
 * 提供模拟微信支付的完整流程
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "模拟支付接口")
@RestController
public class MockPaymentController {

    private final PaymentService paymentService;

    public MockPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 发起支付
     * 创建支付单，返回支付参数
     */
    @ApiOperation("发起支付（创建支付单）")
    @PostMapping("/api/payment/create")
    public ResponseEntity<Map<String, Object>> createPayment(
            @Validated @RequestBody PaymentCreateRequest request) {
        log.info("发起支付: orderNo={}, amount={}", request.getOrderNo(), request.getAmount());

        try {
            PaymentCreateVO vo = paymentService.createPayment(request);
            return success(vo);
        } catch (Exception e) {
            log.error("创建支付单失败: {}", e.getMessage(), e);
            return fail(e.getMessage());
        }
    }

    /**
     * 模拟支付回调
     * 模拟微信支付回调通知
     */
    @ApiOperation("模拟支付确认（回调通知）")
    @PostMapping("/mock-payment/notify")
    public ResponseEntity<Map<String, Object>> mockPayNotify(
            @Validated @RequestBody MockPaymentNotifyDTO dto) {
        log.info("模拟支付确认: paymentNo={}, mockPayCode={}", dto.getPaymentNo(), dto.getMockPayCode());

        try {
            String paymentNo = paymentService.mockPayNotify(dto);

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("paymentNo", paymentNo);
            data.put("orderNo", dto.getOrderNo());
            data.put("message", "支付成功");

            return success(data);

        } catch (Exception e) {
            log.error("模拟支付确认失败: {}", e.getMessage(), e);
            return fail(e.getMessage());
        }
    }

    /**
     * 查询支付状态
     */
    @ApiOperation("查询支付状态")
    @GetMapping("/api/payment/query/{paymentNo}")
    public ResponseEntity<Map<String, Object>> queryPayment(
            @PathVariable String paymentNo) {
        PaymentRecord record = paymentService.queryPayment(paymentNo);
        if (record == null) {
            return fail("支付记录不存在");
        }
        return success(record);
    }

    /**
     * 根据订单号查询支付记录
     */
    @ApiOperation("根据订单号查询支付记录")
    @GetMapping("/api/payment/order/{orderNo}")
    public ResponseEntity<Map<String, Object>> queryByOrderNo(
            @PathVariable String orderNo) {
        PaymentRecord record = paymentService.queryByOrderNo(orderNo);
        if (record == null) {
            return fail("支付记录不存在");
        }
        return success(record);
    }

    /**
     * 关闭支付单
     */
    @ApiOperation("关闭支付单")
    @PostMapping("/api/payment/close/{paymentNo}")
    public ResponseEntity<Map<String, Object>> closePayment(
            @PathVariable String paymentNo) {
        boolean success = paymentService.closePayment(paymentNo);
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        return success(data);
    }

    // ==================== 模拟支付页面（测试用） ====================

    /**
     * 模拟支付页面（GET请求，用于测试）
     */
    @ApiOperation("模拟支付页面（测试用）")
    @GetMapping("/mock-payment/pay")
    public String mockPayPage(
            @RequestParam String paymentNo,
            @RequestParam(required = false) String mockPayCode) {

        PaymentRecord record = paymentService.queryPayment(paymentNo);
        if (record == null) {
            return "<h2>支付单不存在</h2>";
        }

        // 如果已支付
        if (PaymentStatus.SUCCESS.getCode().equals(record.getStatus())) {
            return buildResultPage("支付成功", record, true);
        }

        // 如果已过期
        if (record.getExpireTime() != null && java.time.LocalDateTime.now().isAfter(record.getExpireTime())) {
            return buildResultPage("支付已过期", record, false);
        }

        // 如果已有支付码，直接显示确认页面
        if (mockPayCode != null && !mockPayCode.isEmpty()) {
            return buildPayConfirmPage(record, mockPayCode);
        }

        // 否则显示输入支付码页面
        return buildInputCodePage(record);
    }

    /**
     * 构建输入支付码页面
     */
    private String buildInputCodePage(PaymentRecord record) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<title>模拟支付</title>" +
                "<style>" +
                "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;max-width:400px;margin:50px auto;padding:20px;background:#f5f5f5;}" +
                ".card{background:#fff;padding:30px;border-radius:12px;box-shadow:0 2px 12px rgba(0,0,0,0.08);}" +
                "h2{color:#333;margin-bottom:20px;text-align:center;}" +
                ".info{margin:12px 0;color:#666;font-size:14px;}" +
                ".amount{font-size:36px;color:#e74c3c;font-weight:bold;margin:25px 0;text-align:center;}" +
                "input{width:100%;padding:14px;margin:10px 0;border:2px solid #e0e0e0;border-radius:8px;box-sizing:border-box;font-size:20px;text-align:center;letter-spacing:8px;}" +
                "input:focus{border-color:#07c160;outline:none;}" +
                "button{width:100%;padding:16px;background:#07c160;color:#fff;border:none;border-radius:8px;font-size:18px;font-weight:500;cursor:pointer;margin-top:20px;}" +
                "button:hover{background:#06ad56;}" +
                ".tips{color:#999;font-size:12px;text-align:center;margin-top:15px;}" +
                "</style></head><body>" +
                "<div class='card'>" +
                "<h2>🛒 模拟支付</h2>" +
                "<div class='info'>订单号：" + record.getOrderNo() + "</div>" +
                "<div class='info'>支付单号：" + record.getPaymentNo() + "</div>" +
                "<div class='amount'>¥ " + String.format("%.2f", record.getAmount() / 100.0) + "</div>" +
                "<form action='/mock-payment/pay' method='get'>" +
                "<input type='hidden' name='paymentNo' value='" + record.getPaymentNo() + "'>" +
                "<input type='text' name='mockPayCode' placeholder='请输入6位支付码' maxlength='6' required autofocus>" +
                "<button type='submit'>确认支付码</button>" +
                "</form>" +
                "<div class='tips'>请向用户索取6位支付码</div>" +
                "</div></body></html>";
    }

    /**
     * 构建支付确认页面
     */
    private String buildPayConfirmPage(PaymentRecord record, String mockPayCode) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<title>确认支付</title>" +
                "<style>" +
                "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;max-width:400px;margin:50px auto;padding:20px;background:#f5f5f5;}" +
                ".card{background:#fff;padding:30px;border-radius:12px;box-shadow:0 2px 12px rgba(0,0,0,0.08);}" +
                "h2{color:#333;margin-bottom:20px;text-align:center;}" +
                ".info{margin:10px 0;color:#666;font-size:14px;}" +
                ".amount{font-size:40px;color:#e74c3c;font-weight:bold;margin:30px 0;text-align:center;}" +
                ".pay-code{font-size:28px;color:#07c160;background:#f0fff0;padding:20px;border-radius:10px;margin:20px 0;text-align:center;letter-spacing:8px;font-weight:bold;}" +
                "button{width:100%;padding:16px;border:none;border-radius:8px;font-size:18px;font-weight:500;cursor:pointer;margin-top:12px;}" +
                ".btn-success{background:#07c160;color:#fff;}" +
                ".btn-success:hover{background:#06ad56;}" +
                ".btn-cancel{background:#f5f5f5;color:#666;}" +
                ".btn-cancel:hover{background:#eee;}" +
                "#result{display:none;text-align:center;padding:30px 0;}" +
                ".success-icon{font-size:60px;color:#07c160;}" +
                ".fail-icon{font-size:60px;color:#e74c3c;}" +
                "</style>" +
                "<script>" +
                "function doPay(result) {" +
                "  document.getElementById('loading').style.display='block';" +
                "  fetch('/mock-payment/notify', {" +
                "    method: 'POST'," +
                "    headers: {'Content-Type': 'application/json'}," +
                "    body: JSON.stringify({" +
                "      paymentNo: '" + record.getPaymentNo() + "'," +
                "      orderNo: '" + record.getOrderNo() + "'," +
                "      mockPayCode: '" + mockPayCode + "'," +
                "      result: result" +
                "    })" +
                "  })" +
                "  .then(r => r.json())" +
                "  .then(data => {" +
                "    document.getElementById('loading').style.display='none';" +
                "    document.getElementById('form').style.display = 'none';" +
                "    document.getElementById('result').style.display = 'block';" +
                "    if(data.code === 0 && data.data && data.data.success) {" +
                "      document.getElementById('result').innerHTML = '<div class=\"success-icon\">✓</div><h2 style=\"color:#07c160\">支付成功</h2><p>订单号：" + record.getOrderNo() + "</p><p>支付单号：" + record.getPaymentNo() + "</p>';" +
                "    } else {" +
                "      document.getElementById('result').innerHTML = '<div class=\"fail-icon\">✗</div><h2 style=\"color:#e74c3c\">支付失败</h2><p>' + (data.message || '未知错误') + '</p>';" +
                "    }" +
                "  })" +
                "  .catch(e => {" +
                "    document.getElementById('loading').style.display='none';" +
                "    alert('请求失败: ' + e);" +
                "  });" +
                "}" +
                "</script></head><body>" +
                "<div class='card'>" +
                "<h2>🛒 确认支付</h2>" +
                "<div class='info'>订单号：" + record.getOrderNo() + "</div>" +
                "<div class='info'>支付单号：" + record.getPaymentNo() + "</div>" +
                "<div class='amount'>¥ " + String.format("%.2f", record.getAmount() / 100.0) + "</div>" +
                "<div class='pay-code'>支付码：" + mockPayCode + "</div>" +
                "<div id='form'>" +
                "<button class='btn-success' onclick='doPay(\"success\")'>✓ 确认支付</button>" +
                "<button class='btn-cancel' onclick='doPay(\"fail\")'>✗ 取消支付</button>" +
                "</div>" +
                "<div id='loading' style='display:none;text-align:center;padding:20px;color:#666;'>处理中...</div>" +
                "<div id='result'></div>" +
                "</div></body></html>";
    }

    /**
     * 构建结果页面
     */
    private String buildResultPage(String title, PaymentRecord record, boolean success) {
        String icon = success ? "✓" : "✗";
        String color = success ? "#07c160" : "#e74c3c";
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<title>" + title + "</title>" +
                "<style>" +
                "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;max-width:400px;margin:100px auto;padding:20px;background:#f5f5f5;}" +
                ".card{background:#fff;padding:40px;border-radius:12px;box-shadow:0 2px 12px rgba(0,0,0,0.08);text-align:center;}" +
                ".icon{font-size:60px;color:" + color + ";margin-bottom:20px;}" +
                "h2{color:" + color + ";margin-bottom:15px;}" +
                ".info{color:#666;margin:10px 0;}" +
                "</style></head><body>" +
                "<div class='card'>" +
                "<div class='icon'>" + icon + "</div>" +
                "<h2>" + title + "</h2>" +
                "<div class='info'>订单号：" + record.getOrderNo() + "</div>" +
                "<div class='info'>支付单号：" + record.getPaymentNo() + "</div>" +
                "</div></body></html>";
    }

    // ==================== 工具方法 ====================

    private ResponseEntity<Map<String, Object>> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", "success");
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<Map<String, Object>> fail(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", -1);
        result.put("message", message);
        result.put("data", null);
        return ResponseEntity.ok(result);
    }
}
