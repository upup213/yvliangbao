package com.yvliangbao.common.service.message;

import com.yvliangbao.common.config.RabbitMQConfig;
import com.yvliangbao.common.pojo.message.SettlementMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class SettlementProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSettlementMessage(Long orderId, String orderNo, Long merchantId, 
                                       BigDecimal amount, String productName, Integer quantity) {
        SettlementMessage message = SettlementMessage.of(orderId, orderNo, merchantId, amount, productName, quantity);
        
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SETTLEMENT_EXCHANGE,
                    RabbitMQConfig.SETTLEMENT_ROUTING_KEY,
                    message
            );
            log.info("分账消息发送成功: orderNo={}, merchantId={}, amount={}", orderNo, merchantId, amount);
        } catch (Exception e) {
            log.error("分账消息发送失败: orderNo={}, error={}", orderNo, e.getMessage(), e);
            throw new RuntimeException("分账消息发送失败", e);
        }
    }
}
