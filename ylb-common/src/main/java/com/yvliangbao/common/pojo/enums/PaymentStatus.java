package com.yvliangbao.common.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举
 *
 * @author 余量宝
 */
@Getter
@AllArgsConstructor
public enum PaymentStatus {

    CREATING(-1, "创建中"),    // ⚠️ 新增：调用微信API前插入记录，防止并发取消
    PENDING(0, "待支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),
    CLOSED(3, "已关闭");

    private final Integer code;
    private final String desc;

    public static PaymentStatus getByCode(Integer code) {
        for (PaymentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
