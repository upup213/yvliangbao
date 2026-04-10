package com.yvliangbao.common.config;

import com.yuliangbao.common.service.order.OrderInfoService;
import com.yuliangbao.common.service.order.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 支付回调配置
 * 注册支付成功回调，更新订单状态
 *
 * @author 余量宝
 */
@Slf4j
@Configuration
public class PaymentCallbackConfig {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderInfoService orderInfoService;

    @PostConstruct
    public void registerPaySuccessCallback() {
        log.info("注册支付成功回调");
        
        paymentService.registerPaySuccessCallback((paymentNo, orderNo, userId) -> {
            log.info("支付成功回调: paymentNo={}, orderNo={}, userId={}", paymentNo, orderNo, userId);
            
            try {
                // 更新订单状态为已支付
                boolean success = orderInfoService.updateOrderStatusToPaid(orderNo, userId);
                
                if (success) {
                    log.info("订单状态更新成功: orderNo={}", orderNo);
                } else {
                    log.warn("订单状态更新失败: orderNo={}", orderNo);
                }
            } catch (Exception e) {
                log.error("订单状态更新异常: orderNo={}, error={}", orderNo, e.getMessage(), e);
            }
        });
    }
}
