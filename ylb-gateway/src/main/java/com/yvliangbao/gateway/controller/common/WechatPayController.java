package com.yvliangbao.gateway.controller.common;


import com.yvliangbao.common.pojo.dto.payment.WechatPayRequest;
import com.yvliangbao.common.pojo.entity.order.PaymentRecord;
import com.yvliangbao.common.pojo.vo.payment.WechatPayVO;
import com.yvliangbao.common.service.order.WechatPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 微信支付控制器
 * 完整模拟微信支付7步流程
 *
 * API列表：
 * 1. POST /wechat-pay/create      - 步骤2: 创建支付订单（统一下单）
 * 2. POST /wechat-pay/notify      - 步骤6: 微信回调通知
 * 3. GET  /wechat-pay/query       - 查询支付状态
 * 4. POST /wechat-pay/close       - 关闭支付订单
 * 5. GET  /wechat-pay/mock-pay    - 模拟支付页面（测试用）
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/wechat-pay")
public class WechatPayController {

    @Autowired
    private WechatPayService wechatPayService;

    // ==================== 步骤1-2: 创建支付订单 ====================

    @ApiOperation("发起支付（统一下单）")
    @PostMapping("/create")
    public ResponseEntity<WechatPayVO> createPayment(@Validated @RequestBody WechatPayRequest request) {
        log.info("【步骤1-2】发起微信支付: orderNo={}, amount={}", request.getOrderNo(), request.getAmount());

        // 步骤2: 后端向微信支付统一下单接口请求支付参数
        WechatPayVO vo = wechatPayService.createPayment(request);

        // 步骤4: 返回前端唤起支付的签名参数
        log.info("【步骤4】返回支付参数: paymentNo={}, prepayId={}", vo.getPaymentNo(), vo.getPackageStr());
        return ResponseEntity.ok(vo);
    }

    // ==================== 步骤6-7: 微信回调通知 ====================

    @ApiOperation("微信支付回调通知（微信调用）")
    @PostMapping("/notify")
    public ResponseEntity<String> handleNotify(@RequestBody String notifyData) {
        log.info("【步骤6】收到微信回调通知");

        // 步骤7: 后端处理回调并响应微信
        String response = wechatPayService.handleNotify(notifyData);

        log.info("【步骤7】回调处理完成，响应: {}", response);
        return ResponseEntity.ok(response);
    }

    // ==================== 辅助接口 ====================

    @ApiOperation("查询支付状态")
    @GetMapping("/query")
    public ResponseEntity<PaymentRecord> queryPayment(
            @ApiParam("订单号") @RequestParam String orderNo) {
        PaymentRecord record = wechatPayService.queryPayment(orderNo);
        return ResponseEntity.ok(record);
    }

    @ApiOperation("关闭支付订单")
    @PostMapping("/close")
    public ResponseEntity<Boolean> closePayment(
            @ApiParam("订单号") @RequestParam String orderNo) {
        boolean result = wechatPayService.closePayment(orderNo);
        return ResponseEntity.ok(result);
    }

    // ==================== 模拟支付（测试用） ====================

    @ApiOperation("模拟支付页面（测试用）")
    @GetMapping("/mock-pay")
    public ResponseEntity<String> mockPayPage(
            @ApiParam("支付单号") @RequestParam String paymentNo,
            @ApiParam("订单号") @RequestParam String orderNo) {
        // 返回一个简单的HTML页面用于模拟支付
        String html = buildMockPayPage(paymentNo, orderNo);
        return ResponseEntity.ok()
                .header("Content-Type", "text/html;charset=UTF-8")
                .body(html);
    }

    @ApiOperation("模拟支付确认（测试用）")
    @PostMapping("/mock-pay/confirm")
    public ResponseEntity<String> mockPayConfirm(
            @ApiParam("支付单号") @RequestParam String paymentNo,
            @ApiParam("订单号") @RequestParam String orderNo,
            @ApiParam("结果：success/fail") @RequestParam(defaultValue = "success") String result) {

        log.info("【模拟支付】确认支付: paymentNo={}, result={}", paymentNo, result);

        // 构建模拟回调数据
        String mockNotifyData = buildMockNotifyData(orderNo, result);

        // 调用回调处理
        String response = wechatPayService.handleNotify(mockNotifyData);

        return ResponseEntity.ok(response);
    }

    /**
     * 构建模拟支付页面HTML
     */
    private String buildMockPayPage(String paymentNo, String orderNo) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>模拟微信支付</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; padding: 20px; text-align: center; }\n" +
                "        .container { max-width: 400px; margin: 0 auto; }\n" +
                "        .btn { padding: 15px 40px; margin: 10px; font-size: 16px; cursor: pointer; border: none; border-radius: 5px; }\n" +
                "        .btn-success { background: #07c160; color: white; }\n" +
                "        .btn-fail { background: #ff4d4f; color: white; }\n" +
                "        .info { margin: 20px 0; padding: 15px; background: #f5f5f5; border-radius: 5px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>🧪 模拟微信支付</h1>\n" +
                "        <div class=\"info\">\n" +
                "            <p><strong>支付单号：</strong>" + paymentNo + "</p>\n" +
                "            <p><strong>订单号：</strong>" + orderNo + "</p>\n" +
                "        </div>\n" +
                "        <p>请选择支付结果：</p>\n" +
                "        <button class=\"btn btn-success\" onclick=\"confirmPay('success')\">✅ 支付成功</button>\n" +
                "        <button class=\"btn btn-fail\" onclick=\"confirmPay('fail')\">❌ 支付失败</button>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        function confirmPay(result) {\n" +
                "            fetch('/wechat-pay/mock-pay/confirm?paymentNo=" + paymentNo + "&orderNo=" + orderNo + "&result=' + result, {\n" +
                "                method: 'POST'\n" +
                "            })\n" +
                "            .then(r => r.text())\n" +
                "            .then(data => {\n" +
                "                alert('支付结果: ' + data);\n" +
                "                if (result === 'success') {\n" +
                "                    window.close();\n" +
                "                }\n" +
                "            })\n" +
                "            .catch(err => alert('请求失败: ' + err));\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * 构建模拟回调数据
     */
    private String buildMockNotifyData(String orderNo, String result) {
        if ("success".equals(result)) {
            return "{\n" +
                    "  \"id\": \"EV-" + System.currentTimeMillis() + "\",\n" +
                    "  \"event_type\": \"TRANSACTION.SUCCESS\",\n" +
                    "  \"summary\": \"支付成功\",\n" +
                    "  \"out_trade_no\": \"" + orderNo + "\",\n" +
                    "  \"transaction_id\": \"MOCK_TX_" + System.currentTimeMillis() + "\",\n" +
                    "  \"trade_state\": \"SUCCESS\",\n" +
                    "  \"trade_state_desc\": \"支付成功\",\n" +
                    "  \"success_time\": \"" + java.time.LocalDateTime.now() + "\",\n" +
                    "  \"amount\": { \"total\": 1, \"currency\": \"CNY\" },\n" +
                    "  \"payer\": { \"openid\": \"mock_openid\" }\n" +
                    "}";
        } else {
            return "{\n" +
                    "  \"id\": \"EV-" + System.currentTimeMillis() + "\",\n" +
                    "  \"event_type\": \"TRANSACTION.FAIL\",\n" +
                    "  \"summary\": \"支付失败\",\n" +
                    "  \"out_trade_no\": \"" + orderNo + "\",\n" +
                    "  \"trade_state\": \"PAYERROR\",\n" +
                    "  \"trade_state_desc\": \"支付失败\"\n" +
                    "}";
        }
    }
}
