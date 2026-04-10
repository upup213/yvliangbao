package com.yvliangbao.common.service.message;

import com.yvliangbao.common.config.RabbitMQConfig;
import com.yvliangbao.common.mapper.merchant.CapitalFlowMapper;
import com.yvliangbao.common.pojo.entity.merchant.CapitalFlow;
import com.yvliangbao.common.pojo.message.SettlementMessage;
import com.yvliangbao.common.service.common.SettlementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class SettlementConsumer {

    private static final int MAX_RETRY_COUNT = 3;
    private static final Integer FLOW_TYPE_INCOME = 1;  // 收入

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private CapitalFlowMapper capitalFlowMapper;

    @RabbitListener(queues = RabbitMQConfig.SETTLEMENT_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void handleSettlementMessage(SettlementMessage message) {
        log.info("收到分账消息: orderNo={}, merchantId={}, amount={}, retryCount={}",
                message.getOrderNo(), message.getMerchantId(), 
                message.getSettlementAmount(), message.getRetryCount());

        try {
            doSettlement(message);
            log.info("分账处理成功: orderNo={}, merchantId={}", 
                    message.getOrderNo(), message.getMerchantId());
        } catch (Exception e) {
            log.error("分账处理失败: orderNo={}, error={}", 
                    message.getOrderNo(), e.getMessage(), e);
            throw e;
        }
    }

    private void doSettlement(SettlementMessage message) {
        Long merchantId = message.getMerchantId();
        // 将 BigDecimal 转换为 Long（分）
        Long amountInCents = message.getSettlementAmount() != null 
                ? message.getSettlementAmount().multiply(new BigDecimal(100)).longValue()
                : 0L;

        CapitalFlow incomeFlow = new CapitalFlow();
        incomeFlow.setFlowNo(generateFlowNo());
        incomeFlow.setMerchantId(merchantId);
        incomeFlow.setFlowType(FLOW_TYPE_INCOME);
        incomeFlow.setAmount(amountInCents);
        incomeFlow.setRelatedNo(message.getOrderNo());
        incomeFlow.setRemark("订单结算：" + message.getProductName() + " x" + message.getQuantity());
        incomeFlow.setCreateTime(message.getCreateTime());
        capitalFlowMapper.insert(incomeFlow);

        settlementService.updateMerchantBalance(merchantId, amountInCents);

        log.info("分账明细写入完成: merchantId={}, amount={}, orderNo={}", 
                merchantId, amountInCents, message.getOrderNo());
    }

    private String generateFlowNo() {
        return "CF" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }
}
