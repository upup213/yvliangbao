package com.yvliangbao.common.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 余额变动类型枚举
 *
 * @author 余量宝
 */
@Getter
@AllArgsConstructor
public enum BalanceChangeType {

    RECHARGE(1, "充值"),
    CONSUME(2, "消费"),
    REFUND(3, "退款"),
    SYSTEM_GIFT(4, "系统赠送"),
    ORDER_SETTLEMENT(5, "订单结算");

    private final Integer code;
    private final String desc;

    public static BalanceChangeType getByCode(Integer code) {
        for (BalanceChangeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
