package com.yvliangbao.common.service.impl.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvliangbao.common.config.WechatPayConfig;
import com.yvliangbao.common.mapper.order.PaymentRecordMapper;
import com.yvliangbao.common.pojo.dto.payment.WechatPayNotifyDTO;
import com.yvliangbao.common.pojo.dto.payment.WechatPayRequest;
import com.yvliangbao.common.pojo.entity.order.PaymentRecord;
import com.yvliangbao.common.pojo.enums.PaymentStatus;
import com.yvliangbao.common.pojo.enums.PaymentType;
import com.yvliangbao.common.pojo.vo.payment.WechatPayVO;
import com.yvliangbao.common.service.order.WechatPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * 微信支付服务实现
 * 完整模拟微信支付7步流程
 *
 * 生产环境说明：
 * - mockMode=true: 使用模拟支付（开发测试）
 * - mockMode=false: 调用真实微信支付API（需配置商户证书等）
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class WechatPayServiceImpl implements WechatPayService {

    private final WechatPayConfig config;
    private final PaymentRecordMapper paymentRecordMapper;
    private final ObjectMapper objectMapper;

    private PaySuccessCallback paySuccessCallback;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    public WechatPayServiceImpl(WechatPayConfig config,
                                 PaymentRecordMapper paymentRecordMapper,
                                 ObjectMapper objectMapper) {
        this.config = config;
        this.paymentRecordMapper = paymentRecordMapper;
        this.objectMapper = objectMapper;
    }

    // ==================== 步骤2: 统一下单 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WechatPayVO createPayment(WechatPayRequest request) {
        log.info("【微信支付】创建支付订单开始: orderNo={}, amount={}, openid={}",
                request.getOrderNo(), request.getAmount(), request.getOpenid());

        // 1. 检查是否已有待支付的支付单
        PaymentRecord existRecord = paymentRecordMapper.selectByOrderNo(request.getOrderNo());
        if (existRecord != null) {
            // 已有创建中或待支付的记录，检查是否有效
            if (PaymentStatus.CREATING.getCode().equals(existRecord.getStatus())) {
                // 正在创建中，等待或拒绝
                log.warn("【微信支付】支付单正在创建中: paymentNo={}", existRecord.getPaymentNo());
                throw new RuntimeException("支付单正在创建中，请稍后重试");
            }
            if (PaymentStatus.PENDING.getCode().equals(existRecord.getStatus())) {
                if (existRecord.getExpireTime() != null && LocalDateTime.now().isBefore(existRecord.getExpireTime())) {
                    log.info("【微信支付】支付单已存在: paymentNo={}", existRecord.getPaymentNo());
                    return buildWechatPayVO(existRecord);
                }
                // 已过期，关闭旧支付单
                existRecord.setStatus(PaymentStatus.CLOSED.getCode());
                paymentRecordMapper.updateById(existRecord);
            }
        }

        // 2. 生成支付单号
        String paymentNo = generatePaymentNo();

        // ⚠️ 关键修改：先插入"创建中"状态的记录，防止定时任务取消订单
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo(paymentNo);
        record.setOrderNo(request.getOrderNo());
        record.setUserId(request.getUserId());
        record.setOpenid(request.getOpenid());
        record.setMerchantId(request.getMerchantId());
        record.setAmount(request.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue()); // 元转分
        record.setStatus(PaymentStatus.CREATING.getCode()); // 创建中
        record.setPayType(PaymentType.WECHAT.getCode());
        record.setTradeType("JSAPI");
        record.setNotifyUrl(config.getNotifyUrl());
        record.setExpireTime(LocalDateTime.now().plusMinutes(30));
        record.setNotifyStatus(0);
        record.setNotifyCount(0);

        paymentRecordMapper.insert(record);
        log.info("【微信支付】插入创建中记录: paymentNo={}", paymentNo);

        // 3. 调用微信统一下单API
        String prepayId;
        try {
            if (config.isMockMode()) {
                // 模拟模式：生成模拟的prepay_id
                prepayId = generateMockPrepayId();
                log.info("【微信支付】模拟模式，生成prepay_id: {}", prepayId);
            } else {
                // 生产模式：调用真实微信API
                prepayId = callWechatUnifiedOrder(request, paymentNo);
            }
        } catch (Exception e) {
            // 调用失败，更新状态为失败
            record.setStatus(PaymentStatus.FAILED.getCode());
            paymentRecordMapper.updateById(record);
            log.error("【微信支付】调用微信API失败: {}", e.getMessage(), e);
            throw e;
        }

        // 4. 更新支付记录状态为"待支付"，并保存prepay_id
        record.setStatus(PaymentStatus.PENDING.getCode());
        record.setPrepayId(prepayId);
        paymentRecordMapper.updateById(record);

        // 5. 生成前端唤起支付的签名参数
        WechatPayVO vo = buildWechatPayVO(record);
        vo.setPaymentNo(paymentNo);
        vo.setOrderNo(request.getOrderNo());
        vo.setAmount(request.getAmount().toString());
        vo.setExpireTime(record.getExpireTime()
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());

        log.info("【微信支付】创建支付订单成功: paymentNo={}, prepayId={}", paymentNo, prepayId);
        return vo;
    }

    // ==================== 步骤6-7: 处理回调 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleNotify(String notifyData) {
        log.info("【微信支付】收到回调通知: {}", notifyData);

        try {
            WechatPayNotifyDTO notify;
            WechatPayNotifyDTO.Transaction transaction;

            if (config.isMockMode()) {
                // 模拟模式：解析模拟数据
                notify = objectMapper.readValue(notifyData, WechatPayNotifyDTO.class);
                transaction = parseMockTransaction(notifyData);
            } else {
                // 生产模式：解密回调数据
                notify = objectMapper.readValue(notifyData, WechatPayNotifyDTO.class);
                transaction = decryptNotifyData(notify.getResource());
            }

            // 验证交易状态
            if (!"SUCCESS".equals(transaction.getTradeState())) {
                log.warn("【微信支付】交易状态非成功: {}", transaction.getTradeState());
                return buildFailResponse("交易状态非成功");
            }

            // 查询支付记录
            String orderNo = transaction.getOutTradeNo();
            PaymentRecord record = paymentRecordMapper.selectByOrderNo(orderNo);
            if (record == null) {
                log.warn("【微信支付】支付记录不存在: orderNo={}", orderNo);
                return buildFailResponse("支付记录不存在");
            }

            // 验证金额（防止篡改）
            if (!transaction.getAmount().getTotal().equals(record.getAmount())) {
                log.error("【微信支付】金额不一致: 订单={}, 回调={}", record.getAmount(), transaction.getAmount().getTotal());
                return buildFailResponse("金额不一致");
            }

            // 原子更新支付状态为成功（防并发）
            int rows = paymentRecordMapper.updateToPaidByWechat(
                    orderNo,
                    transaction.getTransactionId(),
                    PaymentStatus.SUCCESS.getCode(),
                    LocalDateTime.now()
            );

            if (rows == 0) {
                log.warn("【微信支付】订单已处理过: orderNo={}", orderNo);
                return buildSuccessResponse(); // 返回成功避免微信重复回调
            }

            // 更新通知状态
            paymentRecordMapper.updateNotifyStatus(record.getPaymentNo());

            // 触发业务回调
            if (paySuccessCallback != null) {
                try {
                    paySuccessCallback.onPaySuccess(
                            orderNo,
                            transaction.getTransactionId(),
                            transaction.getPayer().getOpenid(),
                            record.getAmount()
                    );
                    log.info("【微信支付】业务回调执行成功: orderNo={}", orderNo);
                } catch (Exception e) {
                    log.error("【微信支付】业务回调执行失败: orderNo={}, error={}", orderNo, e.getMessage(), e);
                    // 不回滚，因为微信支付已成功
                }
            }

            log.info("【微信支付】回调处理成功: orderNo={}, transactionId={}",
                    orderNo, transaction.getTransactionId());
            return buildSuccessResponse();

        } catch (Exception e) {
            log.error("【微信支付】回调处理异常: {}", e.getMessage(), e);
            return buildFailResponse(e.getMessage());
        }
    }

    @Override
    public PaymentRecord queryPayment(String orderNo) {
        return paymentRecordMapper.selectByOrderNo(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closePayment(String orderNo) {
        PaymentRecord record = paymentRecordMapper.selectByOrderNo(orderNo);
        if (record == null) {
            return false;
        }

        if (PaymentStatus.PENDING.getCode().equals(record.getStatus())) {
            record.setStatus(PaymentStatus.CLOSED.getCode());
            paymentRecordMapper.updateById(record);
            log.info("【微信支付】支付单已关闭: orderNo={}", orderNo);
            return true;
        }

        return false;
    }

    @Override
    public void registerPaySuccessCallback(PaySuccessCallback callback) {
        this.paySuccessCallback = callback;
    }

    // ==================== 私有方法 ====================

    /**
     * 调用微信统一下单API（生产环境）
     */
    private String callWechatUnifiedOrder(WechatPayRequest request, String paymentNo) {
        // TODO: 生产环境实现
        // 1. 构建请求参数
        // 2. 使用商户私钥签名
        // 3. 发送HTTP POST请求到 https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi
        // 4. 解析响应，获取prepay_id
        throw new UnsupportedOperationException("生产环境请实现微信统一下单API调用");
    }

    /**
     * 解密回调数据（生产环境）
     */
    private WechatPayNotifyDTO.Transaction decryptNotifyData(WechatPayNotifyDTO.Resource resource) {
        // TODO: 生产环境实现
        // 1. 使用API密钥解密 resource.ciphertext
        // 2. 解析JSON得到 Transaction 对象
        throw new UnsupportedOperationException("生产环境请实现回调数据解密");
    }

    /**
     * 构建微信支付参数（返回给前端）
     * 步骤4: 后端生成前端唤起支付的签名参数
     */
    private WechatPayVO buildWechatPayVO(PaymentRecord record) {
        String appId = config.getAppId();
        String prepayId = record.getPrepayId();
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString().replace("-", "");

        // 生成签名
        String paySign = generatePaySign(appId, timeStamp, nonceStr, prepayId);

        return WechatPayVO.create(appId, prepayId, paySign, nonceStr, timeStamp);
    }

    /**
     * 生成支付签名
     * 签名规则：使用商户API密钥对特定字符串进行签名
     *
     * 签名串格式：
     * 应用ID\n
     * 时间戳\n
     * 随机串\n
     * 预支付交易会话标识\n
     */
    private String generatePaySign(String appId, String timeStamp, String nonceStr, String prepayId) {
        String message = appId + "\n"
                + timeStamp + "\n"
                + nonceStr + "\n"
                + "prepay_id=" + prepayId + "\n";

        if (config.isMockMode()) {
            // 模拟模式：简单的签名
            return "mock_sign_" + md5(message + config.getApiKey());
        } else {
            // 生产模式：使用RSA私钥签名
            return rsaSign(message);
        }
    }

    /**
     * RSA签名（生产环境）
     */
    private String rsaSign(String message) {
        // TODO: 生产环境使用商户私钥签名
        try {
            // 示例代码，实际需加载私钥文件
            // Signature signature = Signature.getInstance("SHA256withRSA");
            // signature.initSign(privateKey);
            // signature.update(message.getBytes(StandardCharsets.UTF_8));
            // return Base64.getEncoder().encodeToString(signature.sign());
            return "RSA_SIGN_PLACEHOLDER";
        } catch (Exception e) {
            throw new RuntimeException("RSA签名失败", e);
        }
    }

    /**
     * MD5哈希
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5计算失败", e);
        }
    }

    /**
     * 生成支付单号
     */
    private String generatePaymentNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = String.format("%06d", RANDOM.nextInt(1000000));
        return "PAY" + dateStr + randomStr;
    }

    /**
     * 生成模拟的prepay_id
     */
    private String generateMockPrepayId() {
        return "mock_prepay_id_" + System.currentTimeMillis();
    }

    /**
     * 解析模拟回调数据
     */
    private WechatPayNotifyDTO.Transaction parseMockTransaction(String notifyData) {
        try {
            // 假设模拟数据直接包含Transaction信息
            return objectMapper.readValue(notifyData, WechatPayNotifyDTO.Transaction.class);
        } catch (Exception e) {
            // 创建一个默认的成功交易
            WechatPayNotifyDTO.Transaction tx = new WechatPayNotifyDTO.Transaction();
            tx.setTradeState("SUCCESS");
            tx.setTransactionId("MOCK_TX_" + System.currentTimeMillis());

            WechatPayNotifyDTO.Amount amount = new WechatPayNotifyDTO.Amount();
            amount.setTotal(1);
            tx.setAmount(amount);

            WechatPayNotifyDTO.Payer payer = new WechatPayNotifyDTO.Payer();
            payer.setOpenid("mock_openid");
            tx.setPayer(payer);

            return tx;
        }
    }

    /**
     * 构建成功响应（返回给微信）
     */
    private String buildSuccessResponse() {
        return "{\"code\":\"SUCCESS\",\"message\":\"成功\"}";
    }

    /**
     * 构建失败响应（返回给微信）
     */
    private String buildFailResponse(String message) {
        return "{\"code\":\"FAIL\",\"message\":\"" + message + "\"}";
    }
}
