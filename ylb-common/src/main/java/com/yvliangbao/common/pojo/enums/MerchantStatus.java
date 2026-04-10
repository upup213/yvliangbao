package com.yvliangbao.common.pojo.enums;

import lombok.Getter;

/**
 * 商户状态枚举
 *
 * @author 余量宝
 */
@Getter
public enum MerchantStatus {

    /**
     * 待审核
     */
    PENDING_AUDIT(0, "待审核"),

    /**
     * 正常
     */
    NORMAL(1, "正常"),

    /**
     * 已驳回
     */
    REJECTED(2, "已驳回"),

    /**
     * 已禁用
     */
    DISABLED(3, "已禁用");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    MerchantStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static MerchantStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MerchantStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否正常状态
     */
    public boolean isNormal() {
        return this == NORMAL;
    }

    /**
     * 判断是否禁用状态
     */
    public boolean isDisabled() {
        return this == DISABLED;
    }
}
