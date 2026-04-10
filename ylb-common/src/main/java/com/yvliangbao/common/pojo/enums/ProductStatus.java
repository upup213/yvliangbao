package com.yvliangbao.common.pojo.enums;

import lombok.Getter;

/**
 * 商品状态枚举
 *
 * @author 余量宝
 */
@Getter
public enum ProductStatus {

    /**
     * 已下架
     */
    OFFLINE(0, "已下架"),

    /**
     * 在售
     */
    ONLINE(1, "在售"),

    /**
     * 已售罄
     */
    SOLD_OUT(2, "已售罄");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    ProductStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static ProductStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ProductStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否在售
     */
    public boolean isOnline() {
        return this == ONLINE;
    }
}
