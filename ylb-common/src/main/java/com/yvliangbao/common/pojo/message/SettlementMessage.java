package com.yvliangbao.common.pojo.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderNo;
    private Long merchantId;
    private BigDecimal settlementAmount;
    private String productName;
    private Integer quantity;
    private LocalDateTime createTime;
    private Integer retryCount;

    public static SettlementMessage of(Long orderId, String orderNo, Long merchantId, 
                                        BigDecimal amount, String productName, Integer quantity) {
        return SettlementMessage.builder()
                .orderId(orderId)
                .orderNo(orderNo)
                .merchantId(merchantId)
                .settlementAmount(amount)
                .productName(productName)
                .quantity(quantity)
                .createTime(LocalDateTime.now())
                .retryCount(0)
                .build();
    }
}
