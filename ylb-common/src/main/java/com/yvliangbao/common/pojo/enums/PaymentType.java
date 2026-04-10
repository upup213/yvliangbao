package com.yvliangbao.common.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式枚举
 *
 * @author 余量宝
 */
@Getter
@AllArgsConstructor
public enum PaymentType {

    WECHAT(1, "微信支付"),
    BALANCE(2, "余额支付"),
    MOCK(3, "模拟支付");

    private final Integer code;
    private final String desc;

    public static PaymentType getByCode(Integer code) {
        for (PaymentType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
