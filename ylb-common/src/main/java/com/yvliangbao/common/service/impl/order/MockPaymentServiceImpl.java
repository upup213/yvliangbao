package com.yvliangbao.common.service.impl.order;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yvliangbao.common.exception.BusinessException;
import com.yvliangbao.common.mapper.order.OrderInfoMapper;
import com.yvliangbao.common.mapper.order.PaymentRecordMapper;
import com.yvliangbao.common.pojo.dto.payment.MockPaymentNotifyDTO;
import com.yvliangbao.common.pojo.entity.order.OrderInfo;
import com.yvliangbao.common.pojo.entity.order.PaymentRecord;
import com.yvliangbao.common.pojo.enums.OrderStatus;
import com.yvliangbao.common.pojo.vo.payment.PaymentCreateVO;
import com.yvliangbao.common.service.order.MockPaymentService;
import com.yvliangbao.common.service.order.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 模拟支付服务实现
 * 模拟真实微信支付流程
 *
 * 流程：
 * 1. 用户发起支付 -> createPayment() 创建支付单
 * 2. 返回支付参数（含模拟支付码）
 * 3. 前端调用模拟支付确认 -> mockPayNotify()
 * 4. 更新支付状态 -> 回调通知订单服务更新订单状态
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class MockPaymentServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements MockPaymentService {

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    @Lazy
    private OrderInfoService orderInfoService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentCreateVO createPayment(OrderInfo order) {
        log.info("创建支付单: orderNo={}, amount={}", order.getOrderNo(), order.getPayAmount());

        // 检查订单状态
        if (!OrderStatus.PENDING_PAYMENT.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException("订单状态异常，无法支付");
        }

        // 生成支付单号（模拟微信支付订单号格式）
        String paymentNo = generatePaymentNo();

        // 生成模拟支付码（6位数字，用户需要输入此码确认支付）
        String mockPayCode = generateMockPayCode();

        // 计算过期时间（5分钟）
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(5);

        // 创建支付记录
        PaymentRecord record = new PaymentRecord();

        record.setPaymentNo(paymentNo);
        record.setOrderNo(order.getOrderNo());
        record.setUserId(order.getUserId());
        record.setMerchantId(order.getMerchantId());
        record.setAmount(order.getPayAmount().multiply(BigDecimal.valueOf(100)).longValue()); // 元转分
        record.setStatus(0); // 待支付
        record.setPayType(3); // 模拟支付
        record.setExpireTime(expireTime);
        record.setNotifyStatus(0); // 未通知
        record.setMockPayCode(mockPayCode);

        paymentRecordMapper.insert(record);

        log.info("支付单创建成功: paymentNo={}, mockPayCode={}", paymentNo, mockPayCode);

        // 返回支付参数
        return PaymentCreateVO.mock(
                paymentNo,
                order.getOrderNo(),
                order.getPayAmount(),
                mockPayCode,
                expireTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean mockPayNotify(MockPaymentNotifyDTO dto) {
        log.info("模拟支付回调: paymentNo={}, orderNo={}, result={}",
                dto.getPaymentNo(), dto.getOrderNo(), dto.getResult());

        // 查询支付记录
        PaymentRecord record = this.lambdaQuery()
                .eq(PaymentRecord::getPaymentNo, dto.getPaymentNo())
                .eq(PaymentRecord::getOrderNo, dto.getOrderNo())
                .one();

        if (record == null) {
            log.warn("支付记录不存在: paymentNo={}", dto.getPaymentNo());
            throw new BusinessException("支付记录不存在");
        }

        // 校验模拟支付码
        if (!dto.getMockPayCode().equals(record.getMockPayCode())) {
            log.warn("模拟支付码错误: expected={}, actual={}", record.getMockPayCode(), dto.getMockPayCode());
            throw new BusinessException("支付码错误");
        }

        // 校验支付状态
        if (record.getStatus() != 0) {
            log.warn("支付状态异常: paymentNo={}, status={}", dto.getPaymentNo(), record.getStatus());
            throw new BusinessException("支付状态异常");
        }

        // 校验是否过期
        if (record.getExpireTime() != null && LocalDateTime.now().isAfter(record.getExpireTime())) {
            log.warn("支付已过期: paymentNo={}", dto.getPaymentNo());
            throw new BusinessException("支付已过期，请重新发起");
        }

        if ("success".equals(dto.getResult())) {
            // 原子更新支付状态为成功
            int rows = paymentRecordMapper.updateToPaid(dto.getPaymentNo(), dto.getMockPayCode());
            if (rows == 0) {
                log.warn("支付状态更新失败（可能已支付）: paymentNo={}", dto.getPaymentNo());
                throw new BusinessException("支付状态已变更");
            }

            // 回调通知：更新订单状态
            try {
                // 查询订单
                OrderInfo order = orderInfoMapper.selectById(record.getOrderNo());
                if (order != null && OrderStatus.PENDING_PAYMENT.getCode().equals(order.getOrderStatus())) {
                    // 调用订单服务完成支付
                    orderInfoService.payOrder(order.getOrderNo(), order.getUserId());
                    log.info("订单支付成功: orderNo={}", order.getOrderNo());
                }

                // 更新通知状态
                paymentRecordMapper.updateNotifyStatus(dto.getPaymentNo());

            } catch (Exception e) {
                log.error("订单支付回调处理失败: orderNo={}, error={}", record.getOrderNo(), e.getMessage(), e);
                // 支付成功但回调失败，记录日志，后续可手动处理
            }

            return true;

        } else {
            // 支付失败
            record.setStatus(2); // 支付失败
            record.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.updateById(record);

            log.info("支付失败: paymentNo={}, orderNo={}", dto.getPaymentNo(), dto.getOrderNo());
            return false;
        }
    }

    @Override
    public PaymentRecord queryPayment(String paymentNo) {
        return this.lambdaQuery()
                .eq(PaymentRecord::getPaymentNo, paymentNo)
                .one();
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

        if (record.getStatus() == 0) {
            record.setStatus(3); // 已关闭
            record.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.updateById(record);
            log.info("支付单已关闭: paymentNo={}", paymentNo);
            return true;
        }

        return false;
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
