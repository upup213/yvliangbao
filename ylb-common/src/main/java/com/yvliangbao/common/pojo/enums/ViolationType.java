package com.yvliangbao.common.pojo.enums;

import lombok.Getter;

/**
 * 违规类型枚举
 *
 * @author 余量宝
 */
@Getter
public enum ViolationType {

    /**
     * 商品违规
     */
    PRODUCT(1, "商品违规"),

    /**
     * 订单违规
     */
    ORDER(2, "订单违规"),

    /**
     * 服务违规
     */
    SERVICE(3, "服务违规"),

    /**
     * 其他
     */
    OTHER(4, "其他");

    private final Integer code;
    private final String desc;

    ViolationType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ViolationType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ViolationType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
