package com.yvliangbao.common.service.impl.order;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yvliangbao.common.mapper.order.PaymentRecordMapper;
import com.yvliangbao.common.pojo.dto.payment.MockPaymentNotifyDTO;
import com.yvliangbao.common.pojo.dto.payment.PaymentCreateRequest;
import com.yvliangbao.common.pojo.entity.order.PaymentRecord;
import com.yvliangbao.common.pojo.enums.PaymentStatus;
import com.yvliangbao.common.pojo.enums.PaymentType;
import com.yvliangbao.common.pojo.vo.payment.PaymentCreateVO;
import com.yvliangbao.common.service.order.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 支付服务实现
 * 支持微信支付、余额支付、模拟支付
 *
 * 流程：
 * 1. 用户发起支付 -> createPayment() 创建支付单
 * 2. 返回支付参数（含模拟支付码或微信支付参数）
 * 3. 前端调用支付确认 -> mockPayNotify() 或微信回调
 * 4. 更新支付状态 -> 回调通知业务系统
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements PaymentService {

    private final PaymentRecordMapper paymentRecordMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    /**
     * 支付成功回调（由外部设置）
     */
    private PaySuccessCallback paySuccessCallback;

    public PaymentServiceImpl(PaymentRecordMapper paymentRecordMapper) {
        this.paymentRecordMapper = paymentRecordMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentCreateVO createPayment(PaymentCreateRequest request) {
        log.info("创建支付单: orderNo={}, amount={}, payType={}",
                request.getOrderNo(), request.getAmount(), request.getPayType());

        // 检查是否已有待支付的支付单
        PaymentRecord existRecord = paymentRecordMapper.selectByOrderNo(request.getOrderNo());
        if (existRecord != null && PaymentStatus.PENDING.getCode().equals(existRecord.getStatus())) {
            // 如果未过期，返回已有的支付单
            if (existRecord.getExpireTime() != null && LocalDateTime.now().isBefore(existRecord.getExpireTime())) {
                log.info("支付单已存在: paymentNo={}", existRecord.getPaymentNo());
                return buildPaymentVO(existRecord);
            }
            // 已过期，关闭旧支付单
            existRecord.setStatus(PaymentStatus.CLOSED.getCode());
            existRecord.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.updateById(existRecord);
        }

        // 生成支付单号
        String paymentNo = generatePaymentNo();

        // 根据支付方式创建支付单
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo(paymentNo);
        record.setOrderNo(request.getOrderNo());
        record.setUserId(request.getUserId());
        record.setMerchantId(request.getMerchantId());
        record.setAmount(request.getAmount().multiply(BigDecimal.valueOf(100)).longValue()); // 元转分
        record.setStatus(PaymentStatus.PENDING.getCode());
        record.setPayType(request.getPayType());
        record.setChannel(request.getChannel());
        record.setRemark(request.getRemark());

        if (PaymentType.MOCK.getCode().equals(request.getPayType())) {
            // 模拟支付：生成支付码
            record.setMockPayCode(generateMockPayCode());
            record.setExpireTime(LocalDateTime.now().plusMinutes(5));
        } else if (PaymentType.WECHAT.getCode().equals(request.getPayType())) {
            // 微信支付：调用微信API创建预支付订单（这里模拟）
            record.setExpireTime(LocalDateTime.now().plusMinutes(30));
        } else {
            // 余额支付：无过期时间
            record.setExpireTime(LocalDateTime.now().plusMinutes(5));
        }

        paymentRecordMapper.insert(record);
        log.info("支付单创建成功: paymentNo={}, mockPayCode={}", paymentNo, record.getMockPayCode());

        return buildPaymentVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String mockPayNotify(MockPaymentNotifyDTO dto) {
        log.info("模拟支付回调: paymentNo={}, orderNo={}, result={}",
                dto.getPaymentNo(), dto.getOrderNo(), dto.getResult());

        // 查询支付记录
        PaymentRecord record = this.lambdaQuery()
                .eq(PaymentRecord::getPaymentNo, dto.getPaymentNo())
                .eq(PaymentRecord::getOrderNo, dto.getOrderNo())
                .one();

        if (record == null) {
            log.warn("支付记录不存在: paymentNo={}", dto.getPaymentNo());
            throw new RuntimeException("支付记录不存在");
        }

        // 校验模拟支付码
        if (!dto.getMockPayCode().equals(record.getMockPayCode())) {
            log.warn("模拟支付码错误: expected={}, actual={}", record.getMockPayCode(), dto.getMockPayCode());
            throw new RuntimeException("支付码错误");
        }

        // 校验支付状态
        if (!PaymentStatus.PENDING.getCode().equals(record.getStatus())) {
            log.warn("支付状态异常: paymentNo={}, status={}", dto.getPaymentNo(), record.getStatus());
            throw new RuntimeException("支付状态异常");
        }

        // 校验是否过期
        if (record.getExpireTime() != null && LocalDateTime.now().isAfter(record.getExpireTime())) {
            log.warn("支付已过期: paymentNo={}", dto.getPaymentNo());
            throw new RuntimeException("支付已过期，请重新发起");
        }

        if ("success".equals(dto.getResult())) {
            // 原子更新支付状态为成功
            int rows = paymentRecordMapper.updateToPaid(dto.getPaymentNo(), dto.getMockPayCode());
            if (rows == 0) {
                log.warn("支付状态更新失败（可能已支付）: paymentNo={}", dto.getPaymentNo());
                throw new RuntimeException("支付状态已变更");
            }

            // 更新通知状态
            paymentRecordMapper.updateNotifyStatus(dto.getPaymentNo());

            // 触发回调
            if (paySuccessCallback != null) {
                try {
                    paySuccessCallback.onPaySuccess(dto.getPaymentNo(), dto.getOrderNo(), record.getUserId());
                    log.info("支付成功回调执行完成: orderNo={}", dto.getOrderNo());
                } catch (Exception e) {
                    log.error("支付成功回调执行失败: orderNo={}, error={}", dto.getOrderNo(), e.getMessage(), e);
                }
            }

            return dto.getPaymentNo();

        } else {
            // 支付失败
            record.setStatus(PaymentStatus.FAILED.getCode());
            record.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.updateById(record);
            log.info("支付失败: paymentNo={}, orderNo={}", dto.getPaymentNo(), dto.getOrderNo());
            throw new RuntimeException("用户取消支付");
        }
    }

    @Override
    public PaymentRecord queryPayment(String paymentNo) {
        return this.lambdaQuery()
                .eq(PaymentRecord::getPaymentNo, paymentNo)
                .one();
    }

    @Override
    public PaymentRecord queryByOrderNo(String orderNo) {
        return paymentRecordMapper.selectByOrderNo(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closePayment(String paymentNo) {
        PaymentRecord record = this.lambdaQuery()
                .eq(PaymentRecord::getPaymentNo, paymentNo)
                .one();

        if (record == null) {
            return false;
        }

        if (PaymentStatus.PENDING.getCode().equals(record.getStatus())) {
            record.setStatus(PaymentStatus.CLOSED.getCode());
            record.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.updateById(record);
            log.info("支付单已关闭: paymentNo={}", paymentNo);
            return true;
        }

        return false;
    }

    @Override
    public void registerPaySuccessCallback(PaySuccessCallback callback) {
        this.paySuccessCallback = callback;
    }

    /**
     * 构建支付返回VO
     */
    private PaymentCreateVO buildPaymentVO(PaymentRecord record) {
        Long expireTimestamp = record.getExpireTime() != null
                ? record.getExpireTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                : null;

        return PaymentCreateVO.mock(
                record.getPaymentNo(),
                record.getOrderNo(),
                BigDecimal.valueOf(record.getAmount()).divide(BigDecimal.valueOf(100)),
                record.getMockPayCode(),
                expireTimestamp
        );
    }

    /**
     * 生成支付单号
     * 格式：PAY + 日期时间 + 6位随机数
     */
    private String generatePaymentNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = String.format("%06d", RANDOM.nextInt(1000000));
        return "PAY" + dateStr + randomStr;
    }

    /**
     * 生成模拟支付码（6位数字）
     */
    private String generateMockPayCode() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }
}
