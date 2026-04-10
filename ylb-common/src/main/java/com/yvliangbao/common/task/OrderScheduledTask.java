package com.yvliangbao.common.task;


import com.yvliangbao.common.mapper.order.OrderInfoMapper;
import com.yvliangbao.common.pojo.entity.order.OrderInfo;
import com.yvliangbao.common.pojo.enums.OrderStatus;
import com.yvliangbao.common.service.merchant.ProductInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 订单定时任务
 *
 * 功能：
 * 1. 超时未支付订单自动取消
 * 2. 超时退款申请自动批准
 *
 * @author 余量宝
 */
@Slf4j
@Component
public class OrderScheduledTask {

    private static final String LOCK_KEY = "lock:order:timeout";
    private static final long LOCK_EXPIRE_SECONDS = 50;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 处理超时未支付订单
     *
     * 执行时间：每分钟执行一次
     * 功能：
     * 1. 查询超时未支付的订单（排除正在支付的订单）
     * 2. 原子更新订单状态为已取消
     * 3. 释放锁定的库存（幂等性保证）
     *
     * 并发安全说明：
     * - cancelTimeoutOrder SQL 会检查是否存在待支付的支付记录
     * - 如果存在，说明用户正在支付，不会取消订单
     * - releaseStock 方法有幂等性检查，防止重复释放库存
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void processTimeoutOrders() {
        String lockValue = UUID.randomUUID().toString();
        
        try {
            Boolean acquired = stringRedisTemplate.opsForValue()
                    .setIfAbsent(LOCK_KEY, lockValue, LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
            
            if (!Boolean.TRUE.equals(acquired)) {
                log.debug("获取分布式锁失败，另一实例正在执行");
                return;
            }
            
            log.debug("开始处理超时未支付订单");

            // 1. 查询超时订单（最多处理100条）
            // SQL 已排除存在待支付支付记录的订单
            List<OrderInfo> timeoutOrders = orderInfoMapper.selectTimeoutOrders(LocalDateTime.now(), 100);

            if (timeoutOrders.isEmpty()) {
                log.debug("没有超时未支付的订单");
                return;
            }

            log.info("发现{}笔超时未支付订单", timeoutOrders.size());

            int successCount = 0;
            int payingCount = 0; // 正在支付的订单数

            // 2. 逐笔处理
            for (OrderInfo order : timeoutOrders) {
                try {
                    // 原子取消订单（SQL会检查是否存在待支付的支付记录）
                    int updatedRows = orderInfoMapper.cancelTimeoutOrder(
                            order.getOrderNo(),
                            "订单超时自动取消",
                            LocalDateTime.now()
                    );

                    if (updatedRows > 0) {
                        // 释放锁定的库存（幂等性由 releaseStock 方法保证）
                        productInfoService.releaseStock(
                                order.getProductId(),
                                order.getQuantity(),
                                order.getOrderNo()
                        );
                        log.info("超时订单取消成功: orderNo={}, productId={}, quantity={}",
                                order.getOrderNo(), order.getProductId(), order.getQuantity());
                        successCount++;
                    } else {
                        // 取消失败，可能是：
                        // 1. 订单状态已变更（用户已取消或已支付）
                        // 2. 存在待支付的支付记录（用户正在支付）
                        log.warn("超时订单取消失败（可能用户正在支付或订单状态已变更）: orderNo={}", order.getOrderNo());
                        payingCount++;
                    }
                } catch (Exception e) {
                    log.error("处理超时订单异常: orderNo={}, error={}", order.getOrderNo(), e.getMessage(), e);
                }
            }

            log.info("超时订单处理完成: 总数={}, 成功取消={}, 跳过={}", timeoutOrders.size(), successCount, payingCount);

        } catch (Exception e) {
            log.error("处理超时订单任务异常: error={}", e.getMessage(), e);
        }
    }

    /**
     * 处理超时退款申请
     *
     * 执行时间：每5分钟执行一次
     * 功能：
     * 1. 查询超过24小时未处理的退款申请
     * 2. 自动批准退款
     * 3. 恢复库存
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void processTimeoutRefunds() {
        log.debug("开始处理超时退款申请");

        try {
            // 1. 查询超过24小时未处理的退款申请
            List<OrderInfo> timeoutOrders = orderInfoMapper.selectTimeoutRefundingOrders(24);

            if (timeoutOrders.isEmpty()) {
                log.debug("没有超时的退款申请");
                return;
            }

            log.info("发现{}笔超时退款申请", timeoutOrders.size());

            int successCount = 0;

            // 2. 逐笔处理自动退款
            for (OrderInfo order : timeoutOrders) {
                try {
                    // 原子更新订单状态为已退款
                    int updatedRows = orderInfoMapper.autoRefund(
                            order.getOrderNo(),
                            OrderStatus.REFUNDED.getCode(),
                            LocalDateTime.now()
                    );

                    if (updatedRows > 0) {
                        // 释放库存（退款时库存已释放，这里不需要再释放）
                        log.info("超时退款自动批准: orderNo={}, productId={}",
                                order.getOrderNo(), order.getProductId());
                        successCount++;
                    } else {
                        log.warn("超时退款处理失败（状态已变更）: orderNo={}", order.getOrderNo());
                    }
                } catch (Exception e) {
                    log.error("处理超时退款异常: orderNo={}, error={}", order.getOrderNo(), e.getMessage(), e);
                }
            }

            log.info("超时退款处理完成: 总数={}, 成功={}", timeoutOrders.size(), successCount);

        } catch (Exception e) {
            log.error("处理超时退款任务异常: error={}", e.getMessage(), e);
        }
    }
}
