package com.yvliangbao.common.pojo.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 *
 * @author 余量宝
 */
@Getter
public enum OrderStatus {

    /**
     * 待支付
     */
    PENDING_PAYMENT(0, "待支付"),

    /**
     * 已支付
     */
    PAID(1, "已支付"),

    /**
     * 待取餐
     */
    PENDING_PICKUP(2, "待取餐"),

    /**
     * 已完成
     */
    COMPLETED(3, "已完成"),

    /**
     * 已取消
     */
    CANCELED(4, "已取消"),

    /**
     * 已退款
     */
    REFUNDED(5, "已退款"),

    /**
     * 退款中
     */
    REFUNDING(6, "退款中");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    OrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static OrderStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为待支付状态
     */
    public boolean isPendingPayment() {
        return this == PENDING_PAYMENT;
    }

    /**
     * 判断是否为已支付状态
     */
    public boolean isPaid() {
        return this == PAID;
    }

    /**
     * 判断是否为已完成状态
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
